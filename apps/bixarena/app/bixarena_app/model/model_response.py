import logging
from uuid import UUID

import gradio as gr
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
from bixarena_app.config.conversation import (
    CONTINUATION_PROMPT,
    Conversation,
    create_system_message_html,
)
from bixarena_app.model.api_provider import get_api_provider_stream_iter
from bixarena_app.model.error_handler import (
    handle_api_error_message,
)

logger = logging.getLogger(__name__)

api_endpoint_info = {}


class State:
    def __init__(self, model_name):
        # All models use OpenRouter/OpenAI API format with default roles
        self.conv = Conversation()
        self.skip_next = False
        self.model_name = model_name
        self.has_error = False
        self.is_truncated = False

    def to_gradio_chatbot(self):
        return self.conv.to_gradio_chatbot()

    def get_message_for_persistence(self) -> MessageCreate | None:
        """Get the message to persist to database (handles both success and errors).

        Returns:
            - MessageCreate with SYSTEM role if error occurred
            - MessageCreate with ASSISTANT role if successful response
            - None if no message to persist
        """
        # If there was an error, return a system placeholder message
        if self.has_error:
            return MessageCreate(
                role=MessageRole.SYSTEM,
                content="Model response unavailable due to an error.",
            )

        # Otherwise, extract the last successful assistant message
        api_messages = self.conv.to_openai_api_messages()
        for msg in reversed(api_messages):
            if msg.get("role") == "assistant" and msg.get("content"):
                return MessageCreate(role=MessageRole.ASSISTANT, content=msg["content"])

        # No message found (e.g., model never responded)
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

    # Get messages for persistence (handles both success and error cases)
    # Successful responses: ASSISTANT role with actual LLM content
    # Error responses: SYSTEM role with placeholder message
    # Detailed error info tracked separately in model_error table
    model1_message = state1.get_message_for_persistence()
    model2_message = state2.get_message_for_persistence()

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
    battle_session: BattleSession | None = None,
    cookies: dict[str, str] | None = None,
    request: gr.Request | None = None,
):
    temperature = float(temperature)
    top_p = float(top_p)
    max_new_tokens = int(max_new_tokens)

    if state.skip_next:
        # This generate call is skipped due to invalid inputs
        state.skip_next = False
        yield (state, state.to_gradio_chatbot())
        return

    conv, model_name = state.conv, state.model_name
    model_api_dict = api_endpoint_info.get(model_name)

    # Only use API endpoints - no controller/worker logic
    if model_api_dict is None:
        logger.error(f"UNEXPECTED: Model {model_name} not in api_endpoint_info.")
        conv.update_last_message(f"Configuration error: Model {model_name} not found")
        yield (state, state.to_gradio_chatbot())
        return

    # Use API provider stream with battle context
    stream_iter = get_api_provider_stream_iter(
        conv,
        model_api_dict,
        temperature,
        top_p,
        max_new_tokens,
        battle_session=battle_session,
        cookies=cookies,
        request=request,
    )

    conv.update_last_message("▌")
    yield (state, state.to_gradio_chatbot())

    try:
        for data in stream_iter:
            if data["error_code"] == 0:
                output = data["text"].strip()
                # Reset error flag when receiving successful chunks
                state.has_error = False
                conv.update_last_message(output + "▌")
                yield (state, state.to_gradio_chatbot())
            else:
                output = data["text"]
                error_content = create_system_message_html(output)
                conv.update_last_message(error_content)
                state.has_error = True
                yield (state, state.to_gradio_chatbot())
                return
        output = data["text"].strip()
        conv.update_last_message(output)

        # Add continuation prompt if response was truncated
        finish_reason = data.get("finish_reason")
        if finish_reason == "length" and not state.has_error:
            logger.warning(
                f"Response truncated due to max_tokens limit for model {state.model_name}"
            )
            conv.append_message("assistant", CONTINUATION_PROMPT)
            state.is_truncated = True

        yield (state, state.to_gradio_chatbot())
    except Exception as e:
        display_error_msg = handle_api_error_message(e)
        error_content = create_system_message_html(display_error_msg)
        conv.update_last_message(error_content)
        state.has_error = True
        yield (state, state.to_gradio_chatbot())
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
    # Check if BOTH models should skip (e.g., round limit reached)
    if state0 and state0.skip_next and state1 and state1.skip_next:
        # State: Edge case - battle round limit reached
        yield (
            state0,
            state1,
            battle_session,
            state0.to_gradio_chatbot(),
            state1.to_gradio_chatbot(),
            gr.Row(visible=True),  # voting_row: show
            gr.Row(visible=False),  # next_battle_row: hide
            gr.HTML(visible=False),  # page_header: hide
            gr.Row(visible=False),  # textbox_row: hide
        )
        return

    states = [state0, state1]
    gen = []
    cookies = get_session_cookie(request) if request else None
    for i in range(num_sides):
        gen.append(
            bot_response(
                states[i],
                temperature,
                top_p,
                max_new_tokens,
                battle_session=battle_session,
                cookies=cookies,
                request=request,
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
            + [gr.Row(visible=False)]  # voting_row: hide during streaming
            + [gr.Row(visible=False)]  # next_battle_row: hide
            + [gr.HTML(visible=False)]  # page_header: hide
            + [gr.Row(visible=True)]  # textbox_row: show
        )
        if stop:
            break

    _update_battle_round_with_responses(states[0], states[1], battle_session, cookies)

    # State 3B: Error occurred
    if any(state.has_error for state in states):
        yield (
            states  # state0, state1: error state
            + [battle_session]  # battle_session: unchanged
            + chatbots  # chatbot0, chatbot1: show error message
            + [gr.Row(visible=False)]  # voting_row: hide on error
            + [gr.Row(visible=True)]  # next_battle_row: show
            + [gr.HTML(visible=False)]  # page_header: hide
            + [gr.Row(visible=True)]  # textbox_row: show
        )
    else:
        # State 3A: Both models succeeded
        yield (
            states  # state0, state1: complete
            + [battle_session]  # battle_session: unchanged
            + chatbots  # chatbot0, chatbot1: show complete responses
            + [gr.Row(visible=True)]  # voting_row: show on success
            + [gr.Row(visible=False)]  # next_battle_row: hide
            + [gr.HTML(visible=False)]  # page_header: hide
            + [gr.Row(visible=True)]  # textbox_row: show
        )
