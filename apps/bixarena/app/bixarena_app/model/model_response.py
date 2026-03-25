import logging
import queue
import threading
import time

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
        prev_len = 0
        for data in stream_iter:
            output = data["text"].strip()
            state.has_error = False
            new_text = output[prev_len:]
            prev_len = len(output)

            # Drip-feed large chunks word-by-word for smooth display.
            # Some providers (e.g., Gemini) send hundreds of chars at once.
            words = new_text.split(" ")
            displayed = output[: len(output) - len(new_text)]
            for j, word in enumerate(words):
                if j > 0:
                    displayed += " "
                displayed += word
                state.update_last_message(displayed.strip() + "▌")
                yield (state, state.to_gradio_chatbot())
                if len(words) > 3:
                    time.sleep(0.02)

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
    cookies = get_session_cookie(request) if request else None
    chatbots = [None] * num_sides

    # Stream both models in parallel using threads + queue.
    # Each thread runs bot_response() independently and puts updates into the
    # shared queue. The main generator drains the queue and yields to Gradio.
    # This prevents one slow model from blocking the other's display.
    _SENTINEL = object()
    q = queue.Queue()

    def _stream_model(i):
        try:
            for state, chatbot in bot_response(
                states[i],
                battle_session=battle_session,
                cookies=cookies,
            ):
                q.put((i, state, chatbot))
        finally:
            q.put((i, _SENTINEL, None))

    for i in range(num_sides):
        t = threading.Thread(target=_stream_model, args=(i,), daemon=True)
        t.start()

    done = 0
    while done < num_sides:
        try:
            i, state_or_sentinel, chatbot = q.get(timeout=120)
        except queue.Empty:
            logger.error("Streaming timed out waiting for model updates")
            break

        if state_or_sentinel is _SENTINEL:
            done += 1
            continue

        states[i] = state_or_sentinel
        chatbots[i] = chatbot

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
