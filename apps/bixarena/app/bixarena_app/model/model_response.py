import json
import logging
import os
import time
import uuid

import gradio as gr
import requests
from bixarena_api_client import ApiClient, Configuration, ModelApi, ModelSearchQuery
from bixarena_api_client.exceptions import ApiException

from bixarena_app.model.api_provider import get_api_provider_stream_iter
from bixarena_app.model.error_handler import handle_error_message
from bixarena_app.model.model_adapter import get_conversation_template

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
        self.has_error = False

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
    """Fetch models from the BixArena API using the proper API client"""
    global api_endpoint_info
    models = []  # Initiate models list

    try:
        # Configure the API client
        configuration = Configuration(host="http://bixarena-api:8112/v1")

        # Create API client and model API instance
        with ApiClient(configuration) as api_client:
            api_instance = ModelApi(api_client)

            # Fetch only visible models (100 for now)
            visible_models_search_query = ModelSearchQuery(
                page_size=100,
                active=True,
            )
            visible_models_response = api_instance.list_models(
                model_search_query=visible_models_search_query
            )

            api_endpoint_info = {}

            for model in visible_models_response.models:
                model_name = model.name

                # Check required fields for API configuration
                api_model_name = model.api_model_name
                api_base = model.api_base
                api_type = "openai"
                api_key = os.getenv("OPENAI_API_KEY", "")

                if not api_key:
                    logger.warning(
                        f"Skipping model '{model_name}' - missing OPENAI_API_KEY"
                    )
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
        logger.info(f"✅ Fetched {len(models)} visible models from BixArena API.")

    except ApiException as e:
        logger.error(f"❌ API Exception when calling ModelApi->list_models: {e}")
        models = []
        api_endpoint_info = {}
    except Exception as e:
        logger.error(f"❌ Unexpected error fetching models: {e}")
        models = []
        api_endpoint_info = {}

    # Sort models alphabetically
    models.sort()

    if models:
        logger.info(f"\n🚀 Available models: {models}")
    else:
        logger.warning("⚠️ No visible models found")

    # Return models twice for compatibility with FastChat interface expectations
    return models, models


def bot_response(
    state,
    temperature,
    top_p,
    max_new_tokens,
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
    model_api_dict = api_endpoint_info.get(model_name)

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
        for _i, data in enumerate(stream_iter):
            if data["error_code"] == 0:
                output = data["text"].strip()
                conv.update_last_message(output + "▌")
                yield (state, state.to_gradio_chatbot()) + (disable_btn,) * 4
            else:
                output = data["text"]
                conv.update_last_message(output)
                state.has_error = True
                yield (state, state.to_gradio_chatbot()) + (
                    disable_btn,
                    disable_btn,
                    disable_btn,
                    enable_btn,
                )
                return
        output = data["text"].strip()
        conv.update_last_message(output)
        yield (state, state.to_gradio_chatbot()) + (enable_btn,) * 4
    except requests.exceptions.RequestException as e:
        display_error_msg = handle_error_message(e)
        conv.update_last_message(display_error_msg)
        state.has_error = True  # Mark this state as having an error
        yield (state, state.to_gradio_chatbot()) + (
            disable_btn,
            disable_btn,
            disable_btn,
            enable_btn,
        )
        return
    except Exception as e:
        display_error_msg = handle_error_message(e)
        conv.update_last_message(display_error_msg)
        state.has_error = True  # Mark this state as having an error
        yield (state, state.to_gradio_chatbot()) + (
            disable_btn,
            disable_btn,
            disable_btn,
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

    # At least one model had an error -> keep voting buttons disabled
    if any(state.has_error for state in states):
        yield states + chatbots + [disable_btn, disable_btn, disable_btn, enable_btn]
    else:
        # Both models succeeded -> enable all buttons including voting
        yield states + chatbots + [enable_btn] * 4
