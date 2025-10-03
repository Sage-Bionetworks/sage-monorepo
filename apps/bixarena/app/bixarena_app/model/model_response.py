import json
import logging
import os
import time
import uuid

import gradio as gr
import requests

from bixarena_app.config.constants import SERVER_ERROR_MSG, ErrorCode
from bixarena_app.fastchat.model.model_adapter import get_conversation_template
from bixarena_app.fastchat.serve.api_provider import get_api_provider_stream_iter

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

no_change_btn = gr.Button()
enable_btn = gr.Button(interactive=True, visible=True)
disable_btn = gr.Button(interactive=False)
invisible_btn = gr.Button(interactive=False, visible=False)

controller_url = ""
enable_moderation = False

api_endpoint_info = {}


class State:
    def __init__(self, model_name):
        self.conv = get_conversation_template(model_name)
        self.conv_id = uuid.uuid4().hex
        self.skip_next = False
        self.model_name = model_name

    def to_gradio_chatbot(self):
        return self.conv.to_gradio_chatbot()

    def dict(self):
        base = self.conv.dict()
        base.update(
            {
                "conv_id": self.conv_id,
                "model_name": self.model_name,
            }
        )
        return base


def set_global_vars_anony(enable_moderation_):
    global enable_moderation
    enable_moderation = enable_moderation_


def get_model_list():
    global api_endpoint_info
    models = []  # Initiate models list
    model_api_base = "http://localhost:8000"
    # Load models from the /models API endpoint (visible models only)
    try:
        # Fetch all models for development logging
        all_response = requests.get(
            f"{model_api_base}/models?visible_only=false", timeout=5
        )
        visible_response = requests.get(f"{model_api_base}/models", timeout=5)

        if all_response.status_code == 200 and visible_response.status_code == 200:
            all_models_data = all_response.json()
            db_models = visible_response.json()

            # Development logging: show all vs visible models
            all_model_names = [m["name"] for m in all_models_data]
            visible_model_names = [m["name"] for m in db_models]
            hidden_model_names = [
                name for name in all_model_names if name not in visible_model_names
            ]

            logger.info(
                f"📊 Total models in database: {len(all_model_names)} | Visible: {len(visible_model_names)} ✅ | Hidden: {len(hidden_model_names)} 🔒"
            )
            if hidden_model_names:
                logger.info(f"🔒 Hidden models: {hidden_model_names}")

            api_endpoint_info = {}

            for model in db_models:
                model_name = model["name"]

                # Check required fields for API configuration
                api_model_name = model.get("api_model_name")
                api_type = model.get("api_type", "openai")
                api_base = model.get("api_base", "https://openrouter.ai/api/v1")
                api_key = os.getenv("OPENAI_API_KEY", "")

                if not api_model_name:
                    logger.warning(
                        f"Skipping model '{model_name}' - missing api_model_name"
                    )
                    continue
                if not api_key:
                    logger.warning(
                        f"Skipping model '{model_name}' - missing OPENAI_API_KEY"
                    )
                    continue
                if not api_type:
                    logger.warning(f"Skipping model '{model_name}' - missing api_type")
                    continue
                if not api_base:
                    logger.warning(f"Skipping model '{model_name}' - missing api_base")
                    continue

                # Add model's display name to the model list
                models.append(model_name)

                # Convert to FastChat API-based format (text-only models)
                api_endpoint_info[model_name] = {
                    "api_type": api_type,
                    "api_base": api_base,
                    "api_key": api_key,
                    "model_name": api_model_name,
                    "anony_only": False,
                    "multimodal": False,
                }
        else:
            logger.warning(
                f"❌ Models service not available (status: {visible_response.status_code})"
            )

    except Exception as e:
        logger.error(f"Error loading models from database: {e}")
        models = []
        api_endpoint_info = {}

    # Sort models alphabetically
    models.sort()

    if models:
        logger.info(
            f"🔗 Configured API-based models: {len(api_endpoint_info)}/{len(models)} models"
        )
        logger.info(f"🚀 Available models: {models}")
    else:
        logger.warning("⚠️ No visible models found")

    # Return models twice for compatibility with FastChat interface expectations
    return models, models


def bot_response(
    state,
    temperature,
    top_p,
    max_new_tokens,
    request: gr.Request,
    apply_rate_limit=True,
):
    logger.info("bot_response. ")
    start_tstamp = time.time()
    temperature = float(temperature)
    top_p = float(top_p)
    max_new_tokens = int(max_new_tokens)

    if state.skip_next:
        # This generate call is skipped due to invalid inputs
        state.skip_next = False
        yield (state, state.to_gradio_chatbot()) + (no_change_btn,) * 4
        return

    conv, model_name = state.conv, state.model_name
    model_api_dict = (
        api_endpoint_info[model_name] if model_name in api_endpoint_info else None
    )

    # Only use API endpoints - no controller/worker logic
    if model_api_dict is None:
        logger.error(f"UNEXPECTED: Model {model_name} not in api_endpoint_info.")
        conv.update_last_message(f"Configuration error: Model {model_name} not found")
        yield (state, state.to_gradio_chatbot()) + (enable_btn,) * 4
        return

    # Use API provider stream
    stream_iter = get_api_provider_stream_iter(
        conv,
        model_name,
        model_api_dict,
        temperature,
        top_p,
        max_new_tokens,
    )

    conv.update_last_message("▌")
    yield (state, state.to_gradio_chatbot()) + (disable_btn,) * 4

    try:
        for i, data in enumerate(stream_iter):
            if data["error_code"] == 0:
                output = data["text"].strip()
                conv.update_last_message(output + "▌")
                yield (state, state.to_gradio_chatbot()) + (disable_btn,) * 4
            else:
                output = data["text"] + f"\n\n(error_code: {data['error_code']})"
                conv.update_last_message(output)
                yield (state, state.to_gradio_chatbot()) + (
                    disable_btn,
                    disable_btn,
                    disable_btn,
                    enable_btn,
                    enable_btn,
                )
                return
        output = data["text"].strip()
        conv.update_last_message(output)
        yield (state, state.to_gradio_chatbot()) + (enable_btn,) * 4
    except requests.exceptions.RequestException as e:
        conv.update_last_message(
            f"{SERVER_ERROR_MSG}\n\n(error_code: {ErrorCode.GRADIO_REQUEST_ERROR}, {e})"
        )
        yield (state, state.to_gradio_chatbot()) + (
            disable_btn,
            disable_btn,
            disable_btn,
            enable_btn,
            enable_btn,
        )
        return
    except Exception as e:
        conv.update_last_message(
            f"{SERVER_ERROR_MSG}\n\n"
            f"(error_code: {ErrorCode.GRADIO_STREAM_UNKNOWN_ERROR}, {e})"
        )
        yield (state, state.to_gradio_chatbot()) + (
            disable_btn,
            disable_btn,
            disable_btn,
            enable_btn,
            enable_btn,
        )
        return

    finish_tstamp = time.time()
    logger.info(f"{output}")

    # Log the exact same data to console instead of file
    data = {
        "tstamp": round(finish_tstamp, 4),
        "type": "chat",
        "model": model_name,
        "gen_params": {
            "temperature": temperature,
            "top_p": top_p,
            "max_new_tokens": max_new_tokens,
        },
        "start": round(start_tstamp, 4),
        "finish": round(finish_tstamp, 4),
        "state": state.dict(),
    }
    logger.info(f"Conversation data: {json.dumps(data)}")


def bot_response_multi(
    state0,
    state1,
    request: gr.Request,
    temperature=0.7,
    top_p=1.0,
    max_new_tokens=1024,
):
    logger.info("bot_response_multi (anony).")
    num_sides = 2
    if state0 is None or state0.skip_next:
        # This generate call is skipped due to invalid inputs
        yield (
            state0,
            state1,
            state0.to_gradio_chatbot(),
            state1.to_gradio_chatbot(),
        ) + (no_change_btn,) * 4
        return

    states = [state0, state1]
    gen = []
    for i in range(num_sides):
        gen.append(
            bot_response(
                states[i],
                temperature,
                top_p,
                max_new_tokens,
                request,
                apply_rate_limit=False,
            )
        )

    is_gemini = []
    for i in range(num_sides):
        is_gemini.append(states[i].model_name in ["gemini-pro", "gemini-pro-dev-api"])
    chatbots = [None] * num_sides
    iters = 0
    while True:
        stop = True
        iters += 1
        for i in range(num_sides):
            try:
                # yield gemini fewer times as its chunk size is larger
                # otherwise, gemini will stream too fast
                if not is_gemini[i] or (iters % 30 == 1 or iters < 3):
                    ret = next(gen[i])
                    states[i], chatbots[i] = ret[0], ret[1]
                stop = False
            except StopIteration:
                pass
        yield states + chatbots + [disable_btn] * 4
        if stop:
            break
