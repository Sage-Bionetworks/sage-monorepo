import logging

import gradio as gr

from bixarena_app.auth.request_auth import get_session_cookie
from bixarena_app.config.system_message import (
    CONTINUATION_PROMPT,
    create_system_message_html,
)
from bixarena_app.model.api_provider import get_api_provider_stream_iter
from bixarena_app.model.battle_state import BattleSession, State
from bixarena_app.model.error_handler import get_user_error_message

logger = logging.getLogger(__name__)


def bot_response(
    state: State,
    battle_session: BattleSession | None = None,
    cookies: dict[str, str] | None = None,
):
    if state.skip_next:
        state.skip_next = False
        yield (state, state.to_gradio_chatbot())
        return

    stream_iter = get_api_provider_stream_iter(
        state.model_id,
        battle_session,
        cookies=cookies,
    )

    state.update_last_message("▌")
    yield (state, state.to_gradio_chatbot())

    try:
        for data in stream_iter:
            output = data["text"].strip()
            state.has_error = False
            state.update_last_message(output + "▌")
            yield (state, state.to_gradio_chatbot())

        output = data["text"].strip()
        state.update_last_message(output)

        # Add continuation prompt if response was truncated
        finish_reason = data.get("finish_reason")
        if finish_reason == "length" and not state.has_error:
            logger.warning(
                "Response truncated due to max_tokens limit for model %s",
                state.model_name,
            )
            state.append_message("assistant", CONTINUATION_PROMPT)
            state.is_truncated = True

        yield (state, state.to_gradio_chatbot())
    except Exception as e:
        display_error_msg = get_user_error_message(e)
        error_content = create_system_message_html(display_error_msg)
        state.update_last_message(error_content)
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
