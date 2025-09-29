"""
BixArena Battle Page

Adapted from FastChat's gradio_web_server_multi.py and
simplified to a single function for a single-page LLM comparison arena.
"""

import json
import logging
import random
import time
from typing import List, Dict, Any
import html
import gradio as gr

from config.constants import (
    MODERATION_MSG,
    CONVERSATION_LIMIT_MSG,
    SLOW_MODEL_MSG,
    INPUT_CHAR_LEN_LIMIT,
    CONVERSATION_TURN_LIMIT,
)
from config.prompt_examples import get_prompt_manager

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

# Global state for prompt examples
prompt_manager = get_prompt_manager()


def create_suggested_prompts(num_prompts=3):
    """Create suggested prompts cards"""
    available_prompts = prompt_manager.get_all_prompts()
    num_to_sample = min(num_prompts, len(available_prompts))
    prompts = random.sample(available_prompts, num_to_sample)

    prompt_cards = []

    for i, prompt in enumerate(prompts):
        # Truncate very long prompts for display
        display_text = prompt if len(prompt) <= 120 else prompt[:120] + "..."

        btn = gr.Button(
            value=display_text,
            elem_classes=["suggested-prompt-card"],
        )

        prompt_cards.append((btn, prompt))

    return prompt_cards


def load_demo_side_by_side_anony(models_, _):
    global models
    models = models_

    states = (None,) * num_sides
    selector_updates = (
        gr.Markdown(visible=True),
        gr.Markdown(visible=True),
    )

    return states + selector_updates


def vote_last_response(states, vote_type, model_selectors, _: gr.Request):
    # Log the exact same data to console instead of file
    data = {
        "tstamp": round(time.time(), 4),
        "type": vote_type,
        "models": list(model_selectors),
        "states": [x.dict() for x in states],
    }
    logger.info(f"Vote data: {json.dumps(data)}")

    if ":" not in model_selectors[0]:
        for _ in range(5):
            names = (
                "### Model A: " + states[0].model_name,
                "### Model B: " + states[1].model_name,
            )
            yield (
                names
                + (
                    gr.update(
                        interactive=False, placeholder="Ready for the next battle?"
                    ),
                )
                + (disable_btn,) * 3
                + (enable_btn,)
            )
            time.sleep(0.1)
    else:
        names = (
            "### Model A: " + states[0].model_name,
            "### Model B: " + states[1].model_name,
        )
        yield (
            names
            + (gr.update(interactive=False, placeholder="Ready for the next battle?"),)
            + (disable_btn,) * 3
            + (enable_btn,)
        )


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
        [None] * num_sides  # states
        + [None] * num_sides  # chatbots
        + anony_names  # model_selectors
        + [
            gr.update(
                value="", interactive=True, placeholder="Ask anything biomedical..."
            )
        ]  # re-enable textbox
        + [invisible_btn] * 3  # voting buttons (leftvote, rightvote, tie)
        + [disable_btn] * 1  # clear button
        + [""]  # slow_warning
        + [gr.Group(visible=False)]  # hide battle_interface
        + [gr.Row(visible=False)]  # hide voting_row
        + [gr.Row(visible=False)]  # hide next_battle_row
        + [gr.Column(visible=True)]  # show suggested_prompts_group
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
            + [""]  # slow_warning
            + [gr.Group(visible=False)]  # keep battle_interface hidden
            + [gr.Row(visible=False)]  # keep voting_row hidden
            + [gr.Row(visible=False)]  # keep next_battle_row hidden
            + [gr.Column(visible=True)]  # keep suggested_prompts_group visible
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
            + [""]  # slow_warning
            + [gr.Group(visible=True)]  # show battle_interface (conversation exists)
            + [gr.Row(visible=True)]  # show voting_row
            + [gr.Row(visible=True)]  # show next_battle_row
            + [gr.Column(visible=False)]  # hide suggested_prompts_group
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
        + [gr.update(value="", placeholder="Ask followups...")]
        + [
            disable_btn,
        ]
        * 4
        + [hint_msg]
        + [gr.Group(visible=True)]  # show battle_interface
        + [gr.Row(visible=True)]  # show voting_row
        + [gr.Row(visible=True)]  # show next_battle_row
        + [gr.Column(visible=False)]  # hide suggested_prompts_group
    )


def build_side_by_side_ui_anony(num_example_prompts=3):
    # Page header with title and custom styles
    page_header_html = """
    <div style="text-align: center; padding: 0px;">
        <h1 style="font-size: 3rem; margin-bottom: 0.5rem;">BixArena</h1>
        <p style="font-size: 1.2rem; color: #666; margin: 0;">Benchmarking LLMs for Biomedical Breakthroughs</p>
    </div>
    <style>
    .gradio-container .suggested-prompt-card {
        background: rgba(255, 255, 255, 0.05);
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 8px;
        padding: 12px 16px;
        text-align: left;
        font-size: 14px;
        transition: all 0.2s ease;
        white-space: normal;
        width: 80%;
        margin: 0 auto;
    }

    .gradio-container .suggested-prompt-card:hover {
        background: rgba(255, 255, 255, 0.08);
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
    
    #input_box.prompt_input {
        background: var(--background-fill-primary);
    }
    
    #input_box.prompt_input textarea {
        border-radius: 12px;
    }
    
    .form:has(.prompt_input) {
        border: none;
        box-shadow: none;
    }
    </style>
    

    """

    states = [gr.State() for _ in range(num_sides)]
    model_selectors = []
    chatbots = []

    # Page content
    with gr.Column(elem_classes=["content-wrapper"]):
        gr.HTML(page_header_html, elem_id="title_section")
        # Suggested prompts
        with gr.Column(
            elem_id="suggested_prompts_section", visible=True
        ) as suggested_prompts_group:
            prompt_buttons_data = create_suggested_prompts(num_example_prompts)

        # Battle interface - will appear once a prompt is submitted
        with gr.Group(elem_id="share-region-anony", visible=False) as battle_interface:
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

        # Voting buttons
        with gr.Row(visible=False) as voting_row:
            leftvote_btn = gr.Button(
                value="A is better 👈 ", visible=False, interactive=False
            )
            tie_btn = gr.Button(value="🤝 Tie", visible=False, interactive=False)

            rightvote_btn = gr.Button(
                value="👉 B is better", visible=False, interactive=False
            )

        # Prompt input - always visible
        with gr.Row():
            textbox = gr.Textbox(
                show_label=False,
                placeholder="Ask anything biomedical...",
                elem_id="input_box",
                elem_classes=["prompt_input"],
            )

        # Next Round button
        with gr.Row(visible=False) as next_battle_row:
            with gr.Column(scale=2):
                gr.HTML("")
            with gr.Column(scale=1):
                clear_btn = gr.Button(value="🎯 Next Battle", interactive=False)
            with gr.Column(scale=2):
                gr.HTML("")

    # Register listeners (keep all existing functionality)
    btn_list = [
        leftvote_btn,
        rightvote_btn,
        tie_btn,
        clear_btn,
    ]
    leftvote_btn.click(
        leftvote_last_response,
        states + model_selectors,
        model_selectors + [textbox] + btn_list,
    )
    rightvote_btn.click(
        rightvote_last_response,
        states + model_selectors,
        model_selectors + [textbox] + btn_list,
    )
    tie_btn.click(
        tievote_last_response,
        states + model_selectors,
        model_selectors + [textbox] + btn_list,
    )
    clear_btn.click(
        clear_history,
        None,
        states
        + chatbots
        + model_selectors
        + [textbox]
        + btn_list
        + [slow_warning]
        + [battle_interface, voting_row, next_battle_row, suggested_prompts_group],
    )

    # Direct JavaScript functions for enter key control
    disable_enter_js = """
    () => {
        console.log('Disabling Enter key');
        const textbox = document.querySelector('#input_box textarea');
        if (textbox) {
            textbox._enterDisabled = true;
            textbox.placeholder = 'Type your next message... (Enter disabled while generating)';
            
            if (!textbox._enterHandler) {
                textbox._enterHandler = function(event) {
                    if (event.key === 'Enter' && !event.shiftKey && textbox._enterDisabled) {
                        console.log('Enter key blocked!');
                        event.preventDefault();
                        event.stopPropagation();
                        event.stopImmediatePropagation();
                        return false;
                    }
                };
                textbox.addEventListener('keydown', textbox._enterHandler, true);
            }
        }
        return [];
    }
    """

    enable_enter_js = """
    () => {
        console.log('Enabling Enter key');
        const textbox = document.querySelector('#input_box textarea');
        if (textbox) {
            textbox._enterDisabled = false;
            textbox.placeholder = 'Ask anything biomedical...';
        }
        return [];
    }
    """

    textbox.submit(
        add_text,
        states + model_selectors + [textbox],
        states
        + chatbots
        + [textbox]
        + btn_list
        + [slow_warning]
        + [battle_interface, voting_row, next_battle_row, suggested_prompts_group],
    ).then(
        lambda: None,  # Disable enter key
        [],
        [],
        js=disable_enter_js,
    ).then(
        bot_response_multi,
        states,
        states + chatbots + btn_list,
    ).then(
        flash_buttons,
        [],
        btn_list,
    ).then(
        lambda: None,  # Enable enter key
        [],
        [],
        js=enable_enter_js,
    )

    # Set up suggested prompt click handlers to automatically submit
    for btn, prompt_text in prompt_buttons_data:

        def create_handler(text):
            def handler():
                return text

            return handler

        btn.click(create_handler(prompt_text), outputs=[textbox]).then(
            add_text,
            states + model_selectors + [textbox],
            states
            + chatbots
            + [textbox]
            + btn_list
            + [slow_warning]
            + [battle_interface, voting_row, next_battle_row, suggested_prompts_group],
        ).then(
            lambda: None,  # Disable enter key
            [],
            [],
            js=disable_enter_js,
        ).then(
            bot_response_multi,
            states,
            states + chatbots + btn_list,
        ).then(
            flash_buttons,
            [],
            btn_list,
        ).then(
            lambda: None,  # Enable enter key
            [],
            [],
            js=enable_enter_js,
        )

    return states + model_selectors


def build_battle_page(
    register_api_endpoint_file=None,
    moderate=False,
    num_example_prompts=3,
):
    """Build the battle page with configurable number of example prompts

    Args:
        register_api_endpoint_file: File for API endpoint registration
        moderate (bool): Enable content moderation
        num_example_prompts (int): Number of suggested prompts to display (default: 3)
    """
    # Set global variables
    set_global_vars_anony(moderate)

    # Load models once and only for text-only models
    models, _ = get_model_list(
        register_api_endpoint_file,
        False,  # Disable multimodal models for now
    )

    # Initialize the demo (this sets up global variables in the original module)
    load_demo_side_by_side_anony(models, {})

    with gr.Blocks(title="BixArena - Biomedical LLM Battle") as battle_page:
        build_side_by_side_ui_anony(num_example_prompts)

    return battle_page
