"""Call API providers."""

import logging
import os

import gradio as gr
from bixarena_api_client import ModelApi, ModelErrorCreateRequest
from openai import OpenAI

from bixarena_app.api.api_client_helper import create_authenticated_api_client
from bixarena_app.auth.user_state import get_user_state
from bixarena_app.model.error_handler import (
    get_empty_response_message,
    get_finish_error_message,
    handle_api_error_message,
)

logger = logging.getLogger(__name__)


def _process_streaming_response(
    res,
    model_name: str,
    model_api_dict: dict,
    battle_session,
    cookies: dict[str, str] | None,
):
    """
    Process streaming response from API provider.

    Handles chunk processing, finish_reason validation, and empty response detection.
    """
    text = ""
    finish_reason = None
    error_details = None

    for chunk in res:
        if len(chunk.choices) > 0:
            text += chunk.choices[0].delta.content or ""
            # Capture finish_reason and error details from the final chunk
            if chunk.choices[0].finish_reason is not None:
                finish_reason = chunk.choices[0].finish_reason
                if finish_reason == "error":
                    error_details = getattr(chunk.choices[0], "error", None)
            yield {
                "text": text,
                "error_code": 0,
                "finish_reason": finish_reason,
            }

    # Handle errors for different finish_reason
    if finish_reason == "error":
        error_msg = getattr(error_details, "message", "Unknown error")
        logger.error(f"Model {model_name} finish_reason='error': {error_msg}")
        # Report error to backend
        report_error = (
            error_details
            if error_details
            else Exception(f"finish_reason={finish_reason}: {error_msg}")
        )
        report_model_error(model_api_dict, report_error, battle_session, cookies)
        yield {
            "text": get_finish_error_message(),
            "error_code": 1,
        }
        return

    # Validate that the model generated a response
    if not text.strip():
        logger.error(
            f"Empty response from model: {model_name}. "
            f"The model completed streaming but generated no output."
        )
        # Report error to backend
        report_model_error(
            model_api_dict,
            Exception(f"finish_reason={finish_reason}: Empty response"),
            battle_session,
            cookies,
        )
        yield {
            "text": get_empty_response_message(),
            "error_code": 1,
        }
        return


def report_model_error(
    model_api_dict: dict,
    error: Exception,
    battle_session=None,
    cookies: dict[str, str] | None = None,
) -> None:
    """
    Report a model error to the backend API.

    Args:
        model_api_dict: Model information dict containing model_id and other details
        error: The exception that occurred
        battle_session: BattleSession object containing battle_id and round_id
        cookies: Optional session cookies for authentication
    """
    try:
        # Validate required fields
        model_id = model_api_dict.get("model_id")
        if (
            not model_id
            or not battle_session
            or not battle_session.battle_id
            or not battle_session.round_id
        ):
            logger.warning("⚠️ Cannot report error: missing required fields")
            return

        # Create error request
        error_request = ModelErrorCreateRequest(
            code=getattr(error, "code", None),
            message=getattr(error, "message", str(error)),
            battle_id=battle_session.battle_id,
            round_id=battle_session.round_id,
        )

        # Report to backend
        with create_authenticated_api_client(cookies) as api_client:
            model_api = ModelApi(api_client)
            response = model_api.create_model_error(model_id, error_request)
            logger.info(f"✅ Model error reported: {response}")
    except Exception as e:
        # Don't let error reporting break the user experience
        logger.warning(f"❌ Failed to report model error: {e}")


# Only support OpenAI API for now
def get_api_provider_stream_iter(
    conv,
    model_api_dict,
    temperature,
    top_p,
    max_new_tokens,
    battle_session=None,
    cookies: dict[str, str] | None = None,
    request: gr.Request | None = None,
):
    if model_api_dict["api_type"] == "openai":
        prompt = conv.to_openai_api_messages()
        stream_iter = openai_api_stream_iter(
            model_api_dict["model_name"],
            prompt,
            temperature,
            top_p,
            max_new_tokens,
            api_base=model_api_dict.get("api_base"),
            api_key=model_api_dict.get("api_key"),
            model_api_dict=model_api_dict,
            battle_session=battle_session,
            cookies=cookies,
            request=request,
        )
    else:
        raise NotImplementedError()

    return stream_iter


def openai_api_stream_iter(
    model_name,
    messages,
    temperature,
    top_p,
    max_new_tokens,
    api_base=None,
    api_key=None,
    model_api_dict: dict | None = None,
    battle_session=None,
    cookies: dict[str, str] | None = None,
    request: gr.Request | None = None,
):
    assert model_api_dict is not None, "model_api_dict is required"

    # Get OPENROUTER_API_KEY from environment if not provided
    if not api_key:
        api_key = os.getenv("OPENROUTER_API_KEY")
        if not api_key:
            raise ValueError("API_KEY environment variable is required but not set")

    client = OpenAI(
        base_url=api_base or "https://api.openai.com/v1",
        api_key=api_key,
        timeout=60.0,
        max_retries=3,
    )

    # Make requests
    gen_params = {
        "model": model_name,
        "prompt": messages,
        "temperature": temperature,
        "top_p": top_p,
        "max_new_tokens": max_new_tokens,
    }

    username = None
    state = get_user_state(request)
    current = state.get_current_user()
    if current:
        username = current.get("userName")

    logger.info("==== request ====\n%s", gen_params)

    try:
        create_kwargs = {
            "model": model_name,
            "messages": messages,
            "temperature": temperature,
            "max_tokens": max_new_tokens,
            "stream": True,
        }

        if username:
            create_kwargs["user"] = username

        res = client.chat.completions.create(**create_kwargs)
        yield from _process_streaming_response(
            res, model_name, model_api_dict, battle_session, cookies
        )
    except Exception as e:
        # Retry without system message if provider doesn't support it
        error_str = str(e).lower()
        is_system_error = any(
            pattern in error_str
            for pattern in ["developer instruction", "system role", "system message"]
        )
        has_system = messages and messages[0].get("role") == "system"
        is_400 = getattr(e, "status_code", None) == 400

        if is_400 and is_system_error and has_system:
            logger.warning(f"Retrying {model_name} without system message...")
            # Merge system message into first user message
            system_msg = messages[0]["content"]
            retry_messages = messages[1:]
            if retry_messages and retry_messages[0].get("role") == "user":
                user_msg = retry_messages[0]["content"]
                retry_messages[0]["content"] = f"{system_msg}\n\n{user_msg}"

            try:
                res = client.chat.completions.create(
                    model=model_name,
                    messages=retry_messages,
                    temperature=temperature,
                    max_tokens=max_new_tokens,
                    stream=True,
                )
                yield from _process_streaming_response(
                    res, model_name, model_api_dict, battle_session, cookies
                )
                return
            except Exception as retry_e:
                logger.error(f"Retry also failed: {retry_e}", exc_info=True)
                e = retry_e

        # Handle error
        logger.error(f"OpenAI API error: {e}", exc_info=True)

        # Report error to backend
        report_model_error(model_api_dict, e, battle_session, cookies)

        display_error_msg = handle_api_error_message(e)
        yield {
            "text": display_error_msg,
            "error_code": 1,
        }
