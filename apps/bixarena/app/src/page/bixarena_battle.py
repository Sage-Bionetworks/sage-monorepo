"""
BixArena Battle Page

Adapted from FastChat's gradio_web_server_multi.py and
simplified to a single function for a single-page LLM comparison arena.
"""

import json
import logging
import time
import threading
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

# Import our prompt examples manager
from config.prompt_examples import get_prompt_manager


logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

num_sides = 2
anony_names = ["", ""]
models = []

# Global state for prompt examples
prompt_manager = get_prompt_manager()
_state_lock = threading.Lock()
search_active = False
rolling_paused = False


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


# New functions for prompt examples functionality
def handle_search_change(search_query: str):
    """Handle search input changes"""
    global search_active, rolling_paused, _state_lock

    if search_query.strip():
        with _state_lock:
            search_active = True
            rolling_paused = True
        # Return search results
        results = prompt_manager.search_prompts(search_query)
        if results:
            # Create modern HTML for search results with proper escaping
            html_results = """
            <div style='
                max-height: 280px; 
                overflow-y: auto; 
                background: linear-gradient(135deg, rgba(79, 70, 229, 0.15), rgba(124, 58, 237, 0.12));
                border-radius: 0 0 12px 12px;
                padding: 16px;
                margin-top: 0;
                border: 1px solid rgba(79, 70, 229, 0.3);
                border-top: none;
                box-shadow: 0 4px 12px rgba(79, 70, 229, 0.2);
            '>
            """
            for i, result in enumerate(results[:5]):  # Show max 5 results
                # Properly escape HTML content
                escaped_text = html.escape(result["text"], quote=True)
                escaped_category = html.escape(result["category"], quote=True)
                truncated_text = result["text"][:120] + (
                    "..." if len(result["text"]) > 120 else ""
                )
                escaped_truncated = html.escape(truncated_text, quote=True)

                # Modern card-style result with enhanced styling
                html_results += f"""
                <div class='prompt-result' 
                     data-prompt-text='{escaped_text}'
                     style='
                        padding: 12px; 
                        margin: 8px 0; 
                        background: linear-gradient(135deg, rgba(56, 189, 248, 0.08), rgba(99, 102, 241, 0.06));
                        border-radius: 10px; 
                        cursor: pointer; 
                        border: 1px solid rgba(56, 189, 248, 0.2);
                        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                        position: relative;
                        overflow: hidden;
                     '
                     onmouseover='
                        this.style.background="linear-gradient(135deg, rgba(56, 189, 248, 0.15), rgba(99, 102, 241, 0.12))";
                        this.style.transform="translateY(-2px)";
                        this.style.boxShadow="0 8px 25px rgba(56, 189, 248, 0.15)";
                        this.style.borderColor="rgba(56, 189, 248, 0.4)";
                     '
                     onmouseout='
                        this.style.background="linear-gradient(135deg, rgba(56, 189, 248, 0.08), rgba(99, 102, 241, 0.06))";
                        this.style.transform="translateY(0)";
                        this.style.boxShadow="none";
                        this.style.borderColor="rgba(56, 189, 248, 0.2)";
                     '
                     onclick='
                        try {{
                            var inputBox = document.querySelector("#input_box textarea") || 
                                         document.querySelector("#input_box input") || 
                                         document.getElementById("input_box");
                            if (inputBox) {{
                                inputBox.value = this.getAttribute("data-prompt-text");
                                inputBox.focus();
                                inputBox.dispatchEvent(new Event("input", {{ bubbles: true }}));
                            }}
                        }} catch(e) {{
                            console.error("Error setting prompt:", e);
                        }}
                     '>
                    <div style='
                        display: flex; 
                        align-items: center; 
                        justify-content: space-between; 
                        margin-bottom: 8px;
                    '>
                        <span style='
                            color: #38bdf8; 
                            font-weight: 600; 
                            font-size: 12px; 
                            text-transform: uppercase; 
                            letter-spacing: 0.5px;
                            background: rgba(56, 189, 248, 0.1);
                            padding: 4px 8px;
                            border-radius: 20px;
                            border: 1px solid rgba(56, 189, 248, 0.3);
                        '>{escaped_category}</span>
                        <span style='
                            color: #64748b; 
                            font-size: 11px;
                        '>Click to use</span>
                    </div>
                    <p style='
                        color: #e2e8f0; 
                        font-size: 14px; 
                        line-height: 1.5; 
                        margin: 0;
                        font-weight: 400;
                    '>{escaped_truncated}</p>
                </div>
                """
            html_results += "</div>"
            return gr.HTML(value=html_results, visible=True), gr.Button(visible=True)
        else:
            return gr.HTML(
                value="""
                <div style='
                    background: linear-gradient(135deg, rgba(79, 70, 229, 0.15), rgba(124, 58, 237, 0.12));
                    border-radius: 0 0 12px 12px;
                    padding: 32px;
                    margin-top: 0;
                    border: 1px solid rgba(79, 70, 229, 0.3);
                    border-top: none;
                    text-align: center;
                '>
                    <div style='font-size: 24px; margin-bottom: 8px;'>🔍</div>
                    <p style='
                        color: #94a3b8; 
                        font-size: 16px; 
                        margin: 0;
                        font-weight: 500;
                    '>No matching prompts found</p>
                    <p style='
                        color: #64748b; 
                        font-size: 14px; 
                        margin: 8px 0 0 0;
                    '>Try a different search term or browse our examples</p>
                </div>
                """,
                visible=True,
            ), gr.Button(visible=True)
    else:
        with _state_lock:
            search_active = False
            rolling_paused = False
        return gr.HTML(visible=False), gr.Button(visible=True)


def update_rolling_prompt():
    """Update the rolling prompt - called periodically"""
    global search_active, rolling_paused, _state_lock

    # Get current prompt regardless of state for HTML display
    new_prompt = prompt_manager.get_rolling_prompt()

    return gr.HTML(
        value=f"""
        <div id="rolling_prompt" style="
            background: linear-gradient(135deg, #4f46e5, #7c3aed);
            border: none;
            border-radius: 12px;
            padding: 16px 24px;
            font-weight: 500;
            font-size: 16px;
            color: white;
            min-height: 60px;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            box-shadow: 0 4px 20px rgba(79, 70, 229, 0.3);
            width: 100%;
            margin-bottom: 4px;
            cursor: pointer;
            display: flex;
            flex-direction: column;
            align-items: flex-start;
            line-height: 1.4;
        " onclick="
            try {{
                var inputBox = document.querySelector('#input_box textarea') || 
                             document.querySelector('#input_box input') || 
                             document.getElementById('input_box');
                if (inputBox) {{
                    inputBox.value = '{new_prompt.replace("'", "\\'")}';
                    inputBox.focus();
                    inputBox.dispatchEvent(new Event('input', {{ bubbles: true }}));
                }}
            }} catch(e) {{
                console.error('Error setting prompt:', e);
            }}
        " onmouseover="
            this.style.transform='translateY(-2px)';
            this.style.boxShadow='0 8px 30px rgba(79, 70, 229, 0.4)';
            this.style.background='linear-gradient(135deg, #5b51f0, #8b47f7)';
        " onmouseout="
            this.style.transform='translateY(0)';
            this.style.boxShadow='0 4px 20px rgba(79, 70, 229, 0.3)';
            this.style.background='linear-gradient(135deg, #4f46e5, #7c3aed)';
        ">
            <div style="font-size: 14px; opacity: 0.9; margin-bottom: 8px;">
                💡 Don't know what to ask? Try this example:
            </div>
            <div style="font-size: 16px; font-weight: 400; font-style: italic; opacity: 0.95;">
                "{new_prompt}"
            </div>
        </div>
        """,
        visible=True,
    )


def click_rolling_prompt(button_value):
    """Handle clicking on the rolling prompt"""
    # Extract the prompt text from within the quotes
    if "💡 Don't know what to ask? Try this example:" in button_value:
        # Find the text between quotes
        start_quote = button_value.find('"')
        end_quote = button_value.rfind('"')
        if start_quote != -1 and end_quote != -1 and start_quote < end_quote:
            prompt_text = button_value[start_quote + 1 : end_quote]
        return prompt_text
    # Fallback for old format
    if button_value.startswith("💫 Try this: "):
        prompt_text = button_value[len("💫 Try this: ") :]
        return prompt_text
    return button_value


def build_side_by_side_ui_anony():
    notice_markdown = """
    # ⚔️  BixArena: Benchmarking LLMs for Biomedical Breakthroughs

    ## 📜 Rules
    - Ask biomedical questions to two anonymous models and vote for the better one!
    - You can continue chatting until you identify a winner.

    ## 👇 Chat now!
    
    <style>
        /* Styling for rolling prompt */
        #rolling_prompt {
            background: linear-gradient(135deg, #4f46e5, #7c3aed) !important;
            border: none !important;
            border-radius: 12px !important;
            padding: 16px 24px !important;
            font-weight: 500 !important;
            font-size: 16px !important;
            color: white !important;
            min-height: 60px !important;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
            box-shadow: 0 4px 20px rgba(79, 70, 229, 0.3) !important;
            width: 100% !important;
            margin-bottom: 4px !important;
            text-align: left !important;
            position: relative !important;
        }
        
        /* Style the rolling prompt to make quoted text distinct */
        #rolling_prompt {
            line-height: 1.4 !important;
            white-space: normal !important;
            word-wrap: break-word !important;
        }
        
        /* Use CSS to style the quoted portion differently */
        #rolling_prompt::after {
            content: "💬" !important;
            position: absolute !important;
            top: 12px !important;
            right: 16px !important;
            font-size: 16px !important;
            opacity: 0.6 !important;
        }
        
        #rolling_prompt:hover {
            transform: translateY(-2px) !important;
            box-shadow: 0 8px 30px rgba(79, 70, 229, 0.4) !important;
            background: linear-gradient(135deg, #5b51f0, #8b47f7) !important;
        }
        
        /* Remove spacing from search components */
        #prompt_search {
            margin: 0 !important;
        }
        
        #search_results {
            margin: 0 !important;
        }
        
        /* Force purple background for search results container */
        #search_results .gradio-html,
        #search_results > div,
        #search_results > div > div,
        [id*="component-"] div[style*="max-height: 280px"],
        div[style*="overflow-y: auto"][style*="max-height: 280px"] {
            background: linear-gradient(135deg, rgba(79, 70, 229, 0.15), rgba(124, 58, 237, 0.12)) !important;
            border: 1px solid rgba(79, 70, 229, 0.3) !important;
            box-shadow: 0 4px 12px rgba(79, 70, 229, 0.2) !important;
        }
        
        /* Target any element that looks like our search results container */
        div[style*="background: linear-gradient(135deg, rgba(30, 41, 59"],
        div[style*="background: linear-gradient(135deg, rgba(51, 65, 85"] {
            background: linear-gradient(135deg, rgba(79, 70, 229, 0.15), rgba(124, 58, 237, 0.12)) !important;
            border: 1px solid rgba(79, 70, 229, 0.3) !important;
            box-shadow: 0 4px 12px rgba(79, 70, 229, 0.2) !important;
        }
        
        /* Remove background and borders from the group container */
        #component-46,
        #component-46 .gr-group,
        #component-46 .styler {
            background: transparent !important;
            border: none !important;
            box-shadow: none !important;
            border-width: 0px !important;
            --block-border-width: 0px !important;
        }
        
        /* Ensure the group container blends with the app */
        .gr-group.svelte-1nguped {
            background: transparent !important;
            border: none !important;
        }
    </style>
    """

    states = [gr.State() for _ in range(num_sides)]
    model_selectors = []
    chatbots = []

    gr.Markdown(notice_markdown, elem_id="notice_markdown")

    # Group rolling prompt and search box to eliminate spacing
    with gr.Group():
        rolling_prompt_button = gr.HTML(
            value=f"""
            <div id="rolling_prompt" style="
                background: linear-gradient(135deg, #4f46e5, #7c3aed);
                border: none;
                border-radius: 12px;
                padding: 16px 24px;
                font-weight: 500;
                font-size: 16px;
                color: white;
                min-height: 60px;
                transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                box-shadow: 0 4px 20px rgba(79, 70, 229, 0.3);
                width: 100%;
                margin-bottom: 4px;
                cursor: pointer;
                display: flex;
                flex-direction: column;
                align-items: flex-start;
                line-height: 1.4;
            " onclick="
                try {{
                    var inputBox = document.querySelector('#input_box textarea') || 
                                 document.querySelector('#input_box input') || 
                                 document.getElementById('input_box');
                    if (inputBox) {{
                        inputBox.value = '{prompt_manager.get_rolling_prompt().replace("'", "\\'")}';
                        inputBox.focus();
                        inputBox.dispatchEvent(new Event('input', {{ bubbles: true }}));
                    }}
                }} catch(e) {{
                    console.error('Error setting prompt:', e);
                }}
            " onmouseover="
                this.style.transform='translateY(-2px)';
                this.style.boxShadow='0 8px 30px rgba(79, 70, 229, 0.4)';
                this.style.background='linear-gradient(135deg, #5b51f0, #8b47f7)';
            " onmouseout="
                this.style.transform='translateY(0)';
                this.style.boxShadow='0 4px 20px rgba(79, 70, 229, 0.3)';
                this.style.background='linear-gradient(135deg, #4f46e5, #7c3aed)';
            ">
                <div style="font-size: 14px; opacity: 0.9; margin-bottom: 8px;">
                    💡 Don't know what to ask? Try this example:
                </div>
                <div style="font-size: 16px; font-weight: 400; font-style: italic; opacity: 0.95;">
                    "{prompt_manager.get_rolling_prompt()}"
                </div>
            </div>
            """,
            visible=True,
        )

        search_box = gr.Textbox(
            placeholder="Search biomedical examples: cancer, drug discovery...",
            show_label=False,
            container=False,
            elem_id="prompt_search",
        )

        # Search results display (hidden by default)
        search_results_display = gr.HTML(visible=False, elem_id="search_results")

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
            gr.HTML("")
        with gr.Column(scale=1):
            clear_btn = gr.Button(value="🎯 Next Battle", interactive=False)
        with gr.Column(scale=2):
            gr.HTML("")

    # Event handlers for prompt examples
    search_box.change(
        handle_search_change,
        inputs=[search_box],
        outputs=[search_results_display, rolling_prompt_button],
    )

    # HTML component handles click via inline onclick

    # Add timer for rolling prompts (updates every 4 seconds)
    timer = gr.Timer(4.0)  # 4 second interval
    timer.tick(update_rolling_prompt, outputs=[rolling_prompt_button])

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
    models, _ = get_model_list(
        register_api_endpoint_file,
        False,  # Disable multimodal models for now
    )

    # Initialize the demo (this sets up global variables in the original module)
    load_demo_side_by_side_anony(models, {})

    with gr.Blocks(title="BixArena - Biomedical LLM Battle") as battle_page:
        build_side_by_side_ui_anony()

    return battle_page
