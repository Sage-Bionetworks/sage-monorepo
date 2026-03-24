import logging
from uuid import UUID

import gradio as gr

from bixarena_app.auth.request_auth import get_session_cookie
from bixarena_app.config.constants import PROMPT_USE_LIMIT
from bixarena_app.config.conversation import (
    CONTINUATION_PROMPT,
    Conversation,
    create_system_message_html,
)
from bixarena_app.model.api_provider import get_api_provider_stream_iter
from bixarena_app.model.error_handler import get_user_error_message

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


class BattleSession:
    """Track the active battle and round identifiers for the Gradio session."""

    def __init__(self):
        self.battle_id: UUID | None = None
        self.round_id: UUID | None = None
        self.last_prompt: str | None = None
        self.prompt_use_remaining: int = PROMPT_USE_LIMIT

    def reset(self):
        """Reset battle/round IDs while preserving prompt history for reuse."""
        self.battle_id = None
        self.round_id = None


def bot_response(
    state,
    battle_session: BattleSession | None = None,
    cookies: dict[str, str] | None = None,
):
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

    # Stream from backend SSE endpoint
    stream_iter = get_api_provider_stream_iter(
        model_api_dict,
        battle_session,
        cookies=cookies,
    )

    conv.update_last_message("▌")
    yield (state, state.to_gradio_chatbot())

    try:
        for data in stream_iter:
            output = data["text"].strip()
            state.has_error = False
            conv.update_last_message(output + "▌")
            yield (state, state.to_gradio_chatbot())

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
        display_error_msg = get_user_error_message(e)
        error_content = create_system_message_html(display_error_msg)
        conv.update_last_message(error_content)
        state.has_error = True
        yield (state, state.to_gradio_chatbot())
        return


def bot_response_multi(
    state0,
    state1,
    battle_session: BattleSession,
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
            gr.update(),  # new_battle_same_prompt_btn: unchanged
            gr.update(),  # new_battle_btn: unchanged
        )
        return

    states = [state0, state1]
    gen = []
    cookies = get_session_cookie(request) if request else None
    for i in range(num_sides):
        gen.append(
            bot_response(
                states[i],
                battle_session=battle_session,
                cookies=cookies,
            )
        )

    chatbots = [None] * num_sides
    while True:
        stop = True
        for i in range(num_sides):
            try:
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
            + [gr.update()]  # new_battle_same_prompt_btn: unchanged
            + [gr.update()]  # new_battle_btn: unchanged
        )
        if stop:
            break

    # State 3B: Error occurred
    if any(state.has_error for state in states):
        # Show "New Battle Same Prompt" button on error so user can retry
        can_reuse = (
            battle_session.last_prompt is not None
            and battle_session.prompt_use_remaining > 0
        )
        yield (
            states  # state0, state1: error state
            + [battle_session]  # battle_session: unchanged
            + chatbots  # chatbot0, chatbot1: show error message
            + [gr.Row(visible=False)]  # voting_row: hide on error
            + [gr.Row(visible=True)]  # next_battle_row: show
            + [gr.HTML(visible=False)]  # page_header: hide
            + [gr.Row(visible=True)]  # textbox_row: show
            + [
                gr.Button(
                    value=f"New Battle\nSame Prompt ({battle_session.prompt_use_remaining} left)",
                    variant="primary",
                    visible=can_reuse,
                    interactive=can_reuse,
                )
            ]  # new_battle_same_prompt_btn: show on error
            + [
                gr.Button(variant="secondary" if can_reuse else "primary")
            ]  # new_battle_btn: adjust variant
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
            + [gr.update()]  # new_battle_same_prompt_btn: unchanged
            + [gr.update()]  # new_battle_btn: unchanged
        )
