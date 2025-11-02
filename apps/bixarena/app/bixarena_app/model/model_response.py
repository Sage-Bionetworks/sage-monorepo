import logging
from uuid import UUID

import gradio as gr
import requests
from bixarena_api_client import (
    BattleApi,
    BattleRoundUpdateRequest,
    MessageCreate,
    MessageRole,
)

from bixarena_app.api.api_client_helper import create_authenticated_api_client
from bixarena_app.auth.request_auth import get_session_cookie
from bixarena_app.config.constants import (
    DEFAULT_TEMPERATURE,
    DEFAULT_TOP_P,
    MAX_RESPONSE_TOKENS,
)
from bixarena_app.config.conversation import Conversation
from bixarena_app.model.api_provider import get_api_provider_stream_iter
from bixarena_app.model.error_handler import handle_error_message

logger = logging.getLogger(__name__)

no_change_btn = gr.Button()
enable_btn = gr.Button(interactive=True, visible=True)
disable_btn = gr.Button(interactive=False)
invisible_btn = gr.Button(interactive=False, visible=False)

api_endpoint_info = {}


class State:
    def __init__(self, model_name):
        # All models use OpenRouter/OpenAI API format with default roles
        self.conv = Conversation()
        self.skip_next = False
        self.model_name = model_name
        self.has_error = False

    def to_gradio_chatbot(self):
        return self.conv.to_gradio_chatbot()

    def last_assistant_message(self) -> MessageCreate | None:
        """Return the last completed assistant response as a MessageCreate."""
        assistant_role = self.conv.roles[1] if len(self.conv.roles) > 1 else "assistant"
        for role, content in reversed(self.conv.messages):
            if role == assistant_role and content:
                return MessageCreate(role=MessageRole.ASSISTANT, content=content)
        return None


class BattleSession:
    """Track the active battle and round identifiers for the Gradio session."""

    def __init__(self):
        self.battle_id: UUID | None = None
        self.round_id: UUID | None = None

    def reset(self):
        self.battle_id = None
        self.round_id = None


def _update_battle_round_with_responses(
    state1: State,
    state2: State,
    battle_session: BattleSession,
    cookies: dict[str, str] | None,
) -> None:
    """Persist both LLM responses to the current battle round."""
    battle_id = battle_session.battle_id
    round_id = battle_session.round_id
    if not battle_id or not round_id:
        return
    # Capture successful responses
    model1_message = state1.last_assistant_message()
    model2_message = state2.last_assistant_message()

    # When a model errors, add a system message so we still persist context
    # If the model errored and we didn't get any assistant content, persist a SYSTEM message
    # If there is an assistant message (e.g., a successful follow-up), keep it as ASSISTANT.
    if state1.has_error and not model1_message:
        error_content = "Model response unavailable due to an error."
        model1_message = MessageCreate(role=MessageRole.SYSTEM, content=error_content)
    if state2.has_error and not model2_message:
        error_content = "Model response unavailable due to an error."
        model2_message = MessageCreate(role=MessageRole.SYSTEM, content=error_content)

    if not model1_message and not model2_message:
        battle_session.round_id = None
        return

    try:
        with create_authenticated_api_client(cookies) as api_client:
            battle_api = BattleApi(api_client)
            battle_api.update_battle_round(
                battle_id,
                round_id,
                BattleRoundUpdateRequest(
                    model1_message=model1_message,
                    model2_message=model2_message,
                ),
            )
            logger.info(f"✅ Battle round updated: round={round_id}")
    except Exception as e:
        logger.warning(f"❌ Failed to update battle round: round={round_id} {e}")
    finally:
        battle_session.round_id = None


# NOTE: get_model_list() function has been removed
# Model information is now dynamically populated when battles are created.
# The backend randomly selects models and returns their full details in the
# battle creation response, which are then stored in api_endpoint_info.


def bot_response(
    state,
    temperature,
    top_p,
    max_new_tokens,
):
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
        for data in stream_iter:
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


def bot_response_multi(
    state0,
    state1,
    battle_session: BattleSession,
    temperature=DEFAULT_TEMPERATURE,
    top_p=DEFAULT_TOP_P,
    max_new_tokens=MAX_RESPONSE_TOKENS,
    request: gr.Request | None = None,
):
    num_sides = 2
    if state0 is None or state0.skip_next:
        # This generate call is skipped due to invalid inputs
        yield (
            state0,
            state1,
            battle_session,
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
        # State 2: Models streaming responses
        yield (
            states  # state0, state1: streaming
            + [battle_session]  # battle_session: unchanged
            + chatbots  # chatbot0, chatbot1: show streaming text with "▌"
            + [disable_btn] * 4  # leftvote, rightvote, tie, clear_btn: disable
            + [gr.Row(visible=False)]  # next_battle_row: hide
        )
        if stop:
            break

    cookies = get_session_cookie(request) if request else None
    _update_battle_round_with_responses(states[0], states[1], battle_session, cookies)

    # State 3B: Error occurred
    if any(state.has_error for state in states):
        yield (
            states  # state0, state1: error state
            + [battle_session]  # battle_session: unchanged
            + chatbots  # chatbot0, chatbot1: show error message
            + [
                disable_btn,
                disable_btn,
                disable_btn,
                enable_btn,
            ]  # leftvote, rightvote, tie: disable; clear_btn: enable
            + [gr.Row(visible=True)]  # next_battle_row: show
        )
    else:
        # State 3A: Both models succeeded
        yield (
            states  # state0, state1: complete
            + [battle_session]  # battle_session: unchanged
            + chatbots  # chatbot0, chatbot1: show complete responses
            + [enable_btn] * 4  # leftvote, rightvote, tie, clear_btn: enable
            + [gr.Row(visible=False)]  # next_battle_row: hide
        )
