"""
BixArena Battle Page

Adapted from FastChat's gradio_web_server_multi.py and
simplified to a single function for a single-page LLM comparison arena.
"""

import gradio as gr

from fastchat.serve.gradio_block_arena_anony import (
    set_global_vars_anony,
    leftvote_last_response,
    rightvote_last_response,
    tievote_last_response,
    bothbad_vote_last_response,
    regenerate,
    clear_history,
    share_click,
    add_text,
    bot_response_multi,
    load_demo_side_by_side_anony,  # Add this import
)
from fastchat.serve.gradio_web_server import (
    get_model_list,
)

from fastchat.serve.gradio_block_arena_named import flash_buttons
from fastchat.serve.gradio_web_server import (
    acknowledgment_md,
)
from fastchat.utils import (
    build_logger,
)

logger = build_logger("bixarena_battle", "bixarena_battle.log")

num_sides = 2
anony_names = ["", ""]


def build_side_by_side_ui_anony(models):
    notice_markdown = """
# ⚔️  BixArena: Benchmarking LLMs for Biomedical Breakthroughs

## 📜 Rules
- Ask biomedical questions to two anonymous models and vote for the better one!
- You can continue chatting until you identify a winner.

## 👇 Chat now!
"""

    states = [gr.State() for _ in range(num_sides)]
    model_selectors = [None] * num_sides
    chatbots = [None] * num_sides

    gr.Markdown(notice_markdown, elem_id="notice_markdown")

    with gr.Group(elem_id="share-region-anony"):
        with gr.Row():
            for i in range(num_sides):
                label = "Model A" if i == 0 else "Model B"
                with gr.Column():
                    chatbots[i] = gr.Chatbot(
                        label=label,
                        elem_id="chatbot",
                        height=550,
                        show_copy_button=True,
                    )

        with gr.Row():
            for i in range(num_sides):
                with gr.Column():
                    model_selectors[i] = gr.Markdown(
                        anony_names[i], elem_id="model_selector_md"
                    )
        with gr.Row():
            slow_warning = gr.Markdown("", elem_id="notice_markdown")

    with gr.Row():
        leftvote_btn = gr.Button(
            value="👈  A is better", visible=False, interactive=False
        )
        rightvote_btn = gr.Button(
            value="👉  B is better", visible=False, interactive=False
        )
        tie_btn = gr.Button(value="🤝  Tie", visible=False, interactive=False)
        bothbad_btn = gr.Button(
            value="👎  Both are bad", visible=False, interactive=False
        )

    with gr.Row():
        textbox = gr.Textbox(
            show_label=False,
            placeholder="👉 Enter your biomedical prompt and press ENTER",
            elem_id="input_box",
        )
        send_btn = gr.Button(value="Send", variant="primary", scale=0)

    with gr.Row() as button_row:
        clear_btn = gr.Button(value="🎲 New Round", interactive=False)
        regenerate_btn = gr.Button(value="🔄  Regenerate", interactive=False)
        share_btn = gr.Button(value="📷  Share")

    with gr.Accordion("Parameters", open=False) as parameter_row:
        temperature = gr.Slider(
            minimum=0.0,
            maximum=1.0,
            value=0.7,
            step=0.1,
            interactive=True,
            label="Temperature",
        )
        top_p = gr.Slider(
            minimum=0.0,
            maximum=1.0,
            value=1.0,
            step=0.1,
            interactive=True,
            label="Top P",
        )
        max_output_tokens = gr.Slider(
            minimum=16,
            maximum=2048,
            value=1024,
            step=64,
            interactive=True,
            label="Max output tokens",
        )

    gr.Markdown(acknowledgment_md, elem_id="ack_markdown")

    # Register listeners
    btn_list = [
        leftvote_btn,
        rightvote_btn,
        tie_btn,
        bothbad_btn,
        regenerate_btn,
        clear_btn,
    ]
    leftvote_btn.click(
        leftvote_last_response,
        states + model_selectors,
        model_selectors + [textbox, leftvote_btn, rightvote_btn, tie_btn, bothbad_btn],
    )
    rightvote_btn.click(
        rightvote_last_response,
        states + model_selectors,
        model_selectors + [textbox, leftvote_btn, rightvote_btn, tie_btn, bothbad_btn],
    )
    tie_btn.click(
        tievote_last_response,
        states + model_selectors,
        model_selectors + [textbox, leftvote_btn, rightvote_btn, tie_btn, bothbad_btn],
    )
    bothbad_btn.click(
        bothbad_vote_last_response,
        states + model_selectors,
        model_selectors + [textbox, leftvote_btn, rightvote_btn, tie_btn, bothbad_btn],
    )
    regenerate_btn.click(
        regenerate, states, states + chatbots + [textbox] + btn_list
    ).then(
        bot_response_multi,
        states + [temperature, top_p, max_output_tokens],
        states + chatbots + btn_list,
    ).then(flash_buttons, [], btn_list)
    clear_btn.click(
        clear_history,
        None,
        states + chatbots + model_selectors + [textbox] + btn_list + [slow_warning],
    )

    share_js = """
function (a, b, c, d) {
    const captureElement = document.querySelector('#share-region-anony');
    html2canvas(captureElement)
        .then(canvas => {
            canvas.style.display = 'none'
            document.body.appendChild(canvas)
            return canvas
        })
        .then(canvas => {
            const image = canvas.toDataURL('image/png')
            const a = document.createElement('a')
            a.setAttribute('download', 'bixarena-battle.png')
            a.setAttribute('href', image)
            a.click()
            canvas.remove()
        });
    return [a, b, c, d];
}
"""
    share_btn.click(share_click, states + model_selectors, [], js=share_js)

    textbox.submit(
        add_text,
        states + model_selectors + [textbox],
        states + chatbots + [textbox] + btn_list + [slow_warning],
    ).then(
        bot_response_multi,
        states + [temperature, top_p, max_output_tokens],
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
        states + [temperature, top_p, max_output_tokens],
        states + chatbots + btn_list,
    ).then(flash_buttons, [], btn_list)

    return states + model_selectors


def build_battle_page(
    register_api_endpoint_file=None,
    controller_url=None,
    moderate=False,
):
    # Set global variables
    set_global_vars_anony(moderate)

    # Load models once and only for text-only models
    models, all_models = get_model_list(
        controller_url,
        register_api_endpoint_file,
        False,
    )

    # Initialize the demo (this sets up global variables in the original module)
    load_demo_side_by_side_anony(models, {})

    with gr.Blocks(title="BixArena - Biomedical LLM Battle") as battle_page:
        build_side_by_side_ui_anony(models)

    return battle_page
