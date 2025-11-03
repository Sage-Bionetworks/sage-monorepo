"""Call API providers."""

import logging
import os

from openai import OpenAI

from bixarena_app.auth.user_state import get_user_state
from bixarena_app.model.error_handler import handle_error_message

logger = logging.getLogger(__name__)


# Only support OpenAI API for now
def get_api_provider_stream_iter(
    conv,
    model_name,
    model_api_dict,
    temperature,
    top_p,
    max_new_tokens,
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
):
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
    state = get_user_state()
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

        text = ""
        for chunk in res:
            if len(chunk.choices) > 0:
                text += chunk.choices[0].delta.content or ""
                data = {
                    "text": text,
                    "error_code": 0,
                }
                yield data
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
                text = ""
                for chunk in res:
                    if chunk.choices and chunk.choices[0].delta.content:
                        text += chunk.choices[0].delta.content
                        yield {"text": text, "error_code": 0}
                return
            except Exception as retry_e:
                logger.error(f"Retry also failed: {retry_e}", exc_info=True)
                e = retry_e

        # Handle error
        logger.error(f"OpenAI API error: {e}", exc_info=True)
        display_error_msg = handle_error_message(e)
        yield {
            "text": display_error_msg,
            "error_code": 1,
        }
