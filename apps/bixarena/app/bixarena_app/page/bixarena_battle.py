"""
BixArena Battle Page

Adapted from FastChat's gradio_web_server_multi.py and
simplified to a single function for a single-page LLM comparison arena.
"""

import json
import logging
import time

import gradio as gr
from bixarena_api_client import ApiClient, Configuration, ExamplePromptApi
from bixarena_api_client.models.example_prompt_search_query import (
    ExamplePromptSearchQuery,
)

from bixarena_app.config.constants import (
    CONVERSATION_LIMIT_MSG,
    CONVERSATION_TURN_LIMIT,
    INPUT_CHAR_LEN_LIMIT,
    MODERATION_MSG,
    SLOW_MODEL_MSG,
)
from bixarena_app.model.model_response import (
    State,
    bot_response_multi,
    disable_btn,
    enable_btn,
    get_model_list,
    invisible_btn,
    no_change_btn,
    set_global_vars_anony,
)
from bixarena_app.model.model_selection import get_battle_pair, moderation_filter

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

num_sides = 2
anony_names = ["", ""]
models = []


def example_prompt_cards(num_prompts=3):
    """Create prompt example cards"""
    try:
        # Configure the API client
        configuration = Configuration(host="http://bixarena-api:8112/v1")

        # Create API client and example prompt API instance
        with ApiClient(configuration) as api_client:
            api_instance = ExamplePromptApi(api_client)

            # Create search query to get random active prompts directly from backend
            search_query = ExamplePromptSearchQuery(
                page_size=num_prompts, sort="random"
            )

            # Fetch random example prompts - no client-side sampling needed!
            response = api_instance.list_example_prompts(
                example_prompt_search_query=search_query
            )

            # Extract questions - already randomly selected by backend
            display_prompts = [prompt.question for prompt in response.example_prompts]
        logger.info(f"✅ Fetched {len(display_prompts)} random example prompts")
    except Exception as e:
        # Fallback to dummy prompts if API fails
        logger.error(f"Error fetching example prompts: {e}")
        display_prompts = [
            "What are the main symptoms of Type 2 diabetes?",
            "How does chemotherapy affect cancer cells?",
            "What is the role of genetics in heart disease?",
        ]

    prompt_cards = []
    for question in display_prompts[:num_prompts]:
        with gr.Column(elem_classes=["prompt-card-container"]):
            btn = gr.Button(
                value=question,
                elem_classes=["prompt-card"],
            )
        prompt_cards.append((btn, question))
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


def build_side_by_side_ui_anony():
    # Page header with title and custom styles
    page_header_html = """
    <div style="text-align: center; padding: 0px;">
        <h1 style="font-size: 3rem; margin-bottom: 0.5rem;">BixArena</h1>
        <p style="font-size: 1.2rem; color: #666; margin: 0;">Benchmarking LLMs for Biomedical Breakthroughs</p>
    </div>
    <style>

    #prompt-card-section {
        display: flex;
        flex-direction: row;
        flex-wrap: wrap;
        justify-content: center;
        align-items: center;
        gap: 12px;
        max-width: 1200px;
        margin: 0 auto;
    }
    
    .prompt-card-container {
        padding: 12px 16px;
        border-radius: 8px;
        border: 1px solid rgba(255, 255, 255, 0.2);
        transition: all 0.2s ease;
        width: 30%;
        margin: 0 16px;
        flex: 1;
        min-width: 200px;
    }

    .gradio-container .prompt-card-container:hover {
        background: rgba(255, 255, 255, 0.08);
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    .gradio-container button.prompt-card {
        background: transparent !important;
        padding: 0px;
        text-align: left;
        font-size: 14px;
        line-height: 1.4;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 3;
        overflow: hidden;
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
        gr.HTML(page_header_html)
        # Example prompt
        with gr.Column(
            elem_id="prompt-card-section", visible=True
        ) as suggested_prompts_group:
            prompt_buttons_data = example_prompt_cards()

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
                clear_btn = gr.Button(value="🧪 Next Battle", interactive=False)
            with gr.Column(scale=2):
                gr.HTML("")

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
        const textbox = document.querySelector('#input_box textarea');
        if (textbox) {
            textbox._enterDisabled = true;
            
            if (!textbox._enterHandler) {
                textbox._enterHandler = function(event) {
                    if (event.key === 'Enter' && !event.shiftKey && textbox._enterDisabled) {
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
        const textbox = document.querySelector('#input_box textarea');
        if (textbox) {
            textbox._enterDisabled = false;
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
            lambda: None,
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
            lambda: None,
            [],
            [],
            js=enable_enter_js,
        )

    return states + model_selectors


def build_battle_page(
    moderate=False,
):
    """Build the battle page

    Args:
        moderate (bool): Enable content moderation
    """
    # Set global variables
    set_global_vars_anony(moderate)

    # Load models once (text-only models)
    models, _ = get_model_list()

    # Initialize the demo (this sets up global variables in the original module)
    load_demo_side_by_side_anony(models, {})

    with gr.Blocks(title="BixArena - Biomedical LLM Battle") as battle_page:
        build_side_by_side_ui_anony()

    return battle_page
