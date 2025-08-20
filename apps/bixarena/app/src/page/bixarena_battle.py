"""
BixArena Battle Page

Adapted from FastChat's gradio_web_server_multi.py and
simplified to a single function for a single-page LLM comparison arena.
"""

import json
import logging
import time

import gradio as gr


from config.constants import (
    MODERATION_MSG,
    CONVERSATION_LIMIT_MSG,
    SLOW_MODEL_MSG,
    INPUT_CHAR_LEN_LIMIT,
    CONVERSATION_TURN_LIMIT,
)

from model.model_selection import get_battle_pair, moderation_filter


from model.model_response import (
    State,
    get_model_list,
    set_global_vars_anony,
    bot_response_multi,
    no_change_btn,
    enable_btn,
    disable_btn,
    invisible_btn,
)


logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

num_sides = 2
anony_names = ["", ""]
models = []


def load_demo_side_by_side_anony(models_, url_params):
    global models
    models = models_

    states = (None,) * num_sides
    selector_updates = (
        gr.Markdown(visible=True),
        gr.Markdown(visible=True),
    )

    return states + selector_updates


def vote_last_response(states, vote_type, model_selectors, request: gr.Request):
    # Log the exact same data to console instead of file
    data = {
        "tstamp": round(time.time(), 4),
        "type": vote_type,
        "models": [x for x in model_selectors],
        "states": [x.dict() for x in states],
    }
    logger.info(f"Vote data: {json.dumps(data)}")

    if ":" not in model_selectors[0]:
        for i in range(5):
            names = (
                "### Model A: " + states[0].model_name,
                "### Model B: " + states[1].model_name,
            )
            yield names + ("",) + (disable_btn,) * 3
            time.sleep(0.1)
    else:
        names = (
            "### Model A: " + states[0].model_name,
            "### Model B: " + states[1].model_name,
        )
        yield names + ("",) + (disable_btn,) * 3


def leftvote_last_response(
    state0, state1, model_selector0, model_selector1, request: gr.Request
):
    logger.info("leftvote (anony).")
    for x in vote_last_response(
        [state0, state1], "leftvote", [model_selector0, model_selector1], request
    ):
        yield x


def rightvote_last_response(
    state0, state1, model_selector0, model_selector1, request: gr.Request
):
    logger.info("rightvote (anony).")
    for x in vote_last_response(
        [state0, state1], "rightvote", [model_selector0, model_selector1], request
    ):
        yield x


def tievote_last_response(
    state0, state1, model_selector0, model_selector1, request: gr.Request
):
    logger.info("tievote (anony).")
    for x in vote_last_response(
        [state0, state1], "tievote", [model_selector0, model_selector1], request
    ):
        yield x


def clear_history(request: gr.Request):
    logger.info("clear_history (anony).")
    return (
        [None] * num_sides
        + [None] * num_sides
        + anony_names
        + [""]
        + [invisible_btn] * 3
        + [disable_btn] * 1
        + [""]
    )


def flash_buttons():
    btn_updates = [
        [disable_btn] * 3 + [enable_btn] * 1,
        [enable_btn] * 4,
    ]
    for i in range(4):
        yield btn_updates[i % 2]
        time.sleep(0.3)


def add_text(
    state0, state1, model_selector0, model_selector1, text, request: gr.Request
):
    logger.info(f"add_text (anony). len: {len(text)}")
    states = [state0, state1]
    model_selectors = [model_selector0, model_selector1]

    # Init states if necessary
    if states[0] is None:
        assert states[1] is None

        model_left, model_right = get_battle_pair(models)
        states = [
            State(model_left),
            State(model_right),
        ]

    if len(text) <= 0:
        for i in range(num_sides):
            states[i].skip_next = True
        return (
            states
            + [x.to_gradio_chatbot() for x in states]
            + [""]
            + [
                no_change_btn,
            ]
            * 4
            + [""]
        )

    model_list = [states[i].model_name for i in range(num_sides)]
    flagged = moderation_filter(text, model_list)
    if flagged:
        logger.info(f"violate moderation (anony). text: {text}")
        # overwrite the original text
        text = MODERATION_MSG

    conv = states[0].conv
    if (len(conv.messages) - conv.offset) // 2 >= CONVERSATION_TURN_LIMIT:
        logger.info(f"conversation turn limit. text: {text}")
        for i in range(num_sides):
            states[i].skip_next = True
        return (
            states
            + [x.to_gradio_chatbot() for x in states]
            + [CONVERSATION_LIMIT_MSG]
            + [
                no_change_btn,
            ]
            * 4
            + [""]
        )

    text = text[:INPUT_CHAR_LEN_LIMIT]  # Hard cut-off
    for i in range(num_sides):
        states[i].conv.append_message(states[i].conv.roles[0], text)
        states[i].conv.append_message(states[i].conv.roles[1], None)  # type: ignore
        states[i].skip_next = False

    hint_msg = ""
    for i in range(num_sides):
        if "deluxe" in states[i].model_name:
            hint_msg = SLOW_MODEL_MSG
    return (
        states
        + [x.to_gradio_chatbot() for x in states]
        + [""]
        + [
            disable_btn,
        ]
        * 4
        + [hint_msg]
    )


def build_side_by_side_ui_anony():
    notice_markdown = """
# ⚔️  BixArena: Benchmarking LLMs for Biomedical Breakthroughs

## 📜 Rules
- Ask biomedical questions to two anonymous models and vote for the better one!
- You can continue chatting until you identify a winner.

## 👇 Chat now!
"""

    states = [gr.State() for _ in range(num_sides)]
    model_selectors = []
    chatbots = []

    gr.Markdown(notice_markdown, elem_id="notice_markdown")

    with gr.Group(elem_id="share-region-anony"):
        with gr.Row():
            for i in range(num_sides):
                label = "Model A" if i == 0 else "Model B"
                with gr.Column():
                    chatbot = gr.Chatbot(
                        label=label,
                        elem_id="chatbot",
                        height=550,
                        show_copy_button=True,
                    )
                    chatbots.append(chatbot)

        with gr.Row():
            for i in range(num_sides):
                with gr.Column():
                    model_selector = gr.Markdown(
                        anony_names[i], elem_id="model_selector_md"
                    )
                    model_selectors.append(model_selector)
        with gr.Row():
            slow_warning = gr.Markdown("", elem_id="notice_markdown")

    with gr.Row():
        leftvote_btn = gr.Button(
            value="👈  A is better", visible=False, interactive=False
        )
        tie_btn = gr.Button(value="🤝  Tie", visible=False, interactive=False)

        rightvote_btn = gr.Button(
            value="👉  B is better", visible=False, interactive=False
        )

    with gr.Row():
        textbox = gr.Textbox(
            show_label=False,
            placeholder="👉 Enter your biomedical prompt and press ENTER",
            elem_id="input_box",
        )
        send_btn = gr.Button(value="Send", variant="primary", scale=0)
    with gr.Row():
        with gr.Column(scale=2):
            pass
        with gr.Column(scale=1):
            clear_btn = gr.Button(value="🎯 Next Battle", interactive=False)
        with gr.Column(scale=2):
            pass

    # Register listeners
    btn_list = [
        leftvote_btn,
        rightvote_btn,
        tie_btn,
        clear_btn,
    ]
    leftvote_btn.click(
        leftvote_last_response,
        states + model_selectors,
        model_selectors + [textbox, leftvote_btn, rightvote_btn, tie_btn],
    )
    rightvote_btn.click(
        rightvote_last_response,
        states + model_selectors,
        model_selectors + [textbox, leftvote_btn, rightvote_btn, tie_btn],
    )
    tie_btn.click(
        tievote_last_response,
        states + model_selectors,
        model_selectors + [textbox, leftvote_btn, rightvote_btn, tie_btn],
    )
    clear_btn.click(
        clear_history,
        None,
        states + chatbots + model_selectors + [textbox] + btn_list + [slow_warning],
    )

    textbox.submit(
        add_text,
        states + model_selectors + [textbox],
        states + chatbots + [textbox] + btn_list + [slow_warning],
    ).then(
        bot_response_multi,
        states,
        states + chatbots + btn_list,
    ).then(
        flash_buttons,
        [],
        btn_list,
    )

    send_btn.click(
        add_text,
        states + model_selectors + [textbox],
        states + chatbots + [textbox] + btn_list,
    ).then(
        bot_response_multi,
        states,
        states + chatbots + btn_list,
    ).then(flash_buttons, [], btn_list)

    return states + model_selectors


def build_battle_page(
    register_api_endpoint_file=None,
    moderate=False,
):
    # Set global variables
    set_global_vars_anony(moderate)

    # Load models once and only for text-only models
    models, all_models = get_model_list(
        register_api_endpoint_file,
        False,
    )

    # Initialize the demo (this sets up global variables in the original module)
    load_demo_side_by_side_anony(models, {})

    with gr.Blocks(title="BixArena - Biomedical LLM Battle") as battle_page:
        build_side_by_side_ui_anony()

    return battle_page
