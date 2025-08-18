"""
The gradio demo server with multiple tabs.
It supports chatting with a single model or chatting with two models side-by-side.
"""

import gradio as gr

from fastchat.serve.gradio_block_arena_anony import (
    build_side_by_side_ui_anony,
    load_demo_side_by_side_anony,
    set_global_vars_anony,
)
from fastchat.serve.gradio_block_arena_named import (
    build_side_by_side_ui_named,
    load_demo_side_by_side_named,
    set_global_vars_named,
)
from fastchat.serve.gradio_block_arena_vision import (
    build_single_vision_language_model_ui,
)
from fastchat.serve.gradio_web_server import (
    set_global_vars,
    block_css,
    build_single_model_ui,
    build_about,
    get_model_list,
    load_demo_single,
    get_ip,
)
from fastchat.serve.monitor.monitor import build_leaderboard_tab
from fastchat.utils import (
    build_logger,
    get_window_url_params_js,
    get_window_url_params_with_tos_js,
)

from fastchat.serve.gradio_leaderboard_page import build_leaderboard_page


logger = build_logger("gradio_web_server_multi", "gradio_web_server_multi.log")


def load_demo(url_params, request: gr.Request):
    global models, all_models, vl_models

    ip = get_ip(request)
    logger.info(f"load_demo. ip: {ip}. params: {url_params}")

    selected = 0
    if "arena" in url_params:
        selected = 0
    elif "compare" in url_params:
        selected = 1
    elif "direct" in url_params or "model" in url_params:
        selected = 2
    elif "vision" in url_params:
        selected = 3
    elif "leaderboard" in url_params:
        selected = 4

    if args.model_list_mode == "reload":
        models, all_models = get_model_list(
            args.controller_url,
            args.register_api_endpoint_file,
            False,
        )

        vl_models, all_vl_models = get_model_list(
            args.controller_url,
            args.register_api_endpoint_file,
            True,
        )

    single_updates = load_demo_single(models, url_params)
    side_by_side_anony_updates = load_demo_side_by_side_anony(all_models, url_params)
    side_by_side_named_updates = load_demo_side_by_side_named(models, url_params)
    vision_language_updates = load_demo_single(vl_models, url_params)

    return (
        (gr.Tabs(selected=selected),)
        + single_updates
        + side_by_side_anony_updates
        + side_by_side_named_updates
        + vision_language_updates
    )


def build_header():
    """Build a minimal header with two buttons"""
    with gr.Row():
        with gr.Column(scale=1):
            gr.HTML("<h2>BixArena</h2>")
        with gr.Column(scale=1):
            with gr.Row():
                battle_btn = gr.Button("Battle")
                leaderboard_btn = gr.Button("Leaderboard")

    return battle_btn, leaderboard_btn


def build_demo(models, vl_models, elo_results_file, leaderboard_table_file):
    text_size = gr.themes.sizes.text_md
    if args.show_terms_of_use:
        load_js = get_window_url_params_with_tos_js
    else:
        load_js = get_window_url_params_js

    head_js = """
<script src="https://cdnjs.cloudflare.com/ajax/libs/html2canvas/1.4.1/html2canvas.min.js"></script>
"""
    if args.ga_id is not None:
        head_js += f"""
<script async src="https://www.googletagmanager.com/gtag/js?id={args.ga_id}"></script>
<script>
window.dataLayer = window.dataLayer || [];
function gtag(){{dataLayer.push(arguments);}}
gtag('js', new Date());

gtag('config', '{args.ga_id}');
window.__gradio_mode__ = "app";
</script>
        """

    with gr.Blocks(
        title="Chat with Open Large Language Models",
        theme=gr.themes.Default(text_size=text_size),
        css=block_css,
        head=head_js,
    ) as demo:
        battle_btn, leaderboard_btn = build_header()

        with gr.Tabs() as tabs:
            with gr.Tab("Arena (battle)", id=0):
                side_by_side_anony_list = build_side_by_side_ui_anony(models)

            with gr.Tab("Arena (side-by-side)", id=1):
                side_by_side_named_list = build_side_by_side_ui_named(models)

            with gr.Tab("Direct Chat", id=2):
                single_model_list = build_single_model_ui(
                    models, add_promotion_links=True
                )

            with gr.Tab(
                "Vision-Language Model Direct Chat", id=3, visible=args.multimodal
            ):
                single_vision_language_model_list = (
                    build_single_vision_language_model_ui(
                        vl_models, add_promotion_links=True
                    )
                )

            if elo_results_file:
                with gr.Tab("Leaderboard", id=4):
                    build_leaderboard_tab(elo_results_file, leaderboard_table_file)

            with gr.Tab("About Us", id=5):
                about = build_about()

        with gr.Column(visible=False) as leaderboard_page:
            build_leaderboard_page()
            leaderboard_btn.click(lambda: gr.Tabs(selected=4), outputs=tabs)

        battle_btn.click(
            lambda: [gr.Column(visible=True), gr.Column(visible=False)],
            outputs=[tabs, leaderboard_page],
        )
        leaderboard_btn.click(
            lambda: [gr.Column(visible=False), gr.Column(visible=True)],
            outputs=[tabs, leaderboard_page],
        )

        url_params = gr.JSON(visible=False)

        if args.model_list_mode not in ["once", "reload"]:
            raise ValueError(f"Unknown model list mode: {args.model_list_mode}")

        demo.load(
            load_demo,
            [url_params],
            [tabs]
            + single_model_list
            + side_by_side_anony_list
            + side_by_side_named_list
            + single_vision_language_model_list,
            js=load_js,
        )

    return demo


def build_battle_page(
    register_api_endpoint_file=None,
    controller_url=None,
    moderate=False,
):
    # Create simple args object with default values
    class Args:
        def __init__(self):
            self.controller_url = controller_url
            self.register_api_endpoint_file = register_api_endpoint_file
            self.moderate = moderate
            self.show_terms_of_use = False
            self.multimodal = False
            self.ga_id = None
            self.gradio_auth_path = None
            self.elo_results_file = None
            self.leaderboard_table_file = None
            self.model_list_mode = "once"

    # Make args available at module level for the other functions
    global args, models, all_models, vl_models
    args = Args()
    logger.info(f"args: {args}")

    # Set global variables
    set_global_vars(controller_url, moderate)
    set_global_vars_named(moderate)
    set_global_vars_anony(moderate)
    models, all_models = get_model_list(
        controller_url,
        register_api_endpoint_file,
        False,
    )

    vl_models, all_vl_models = get_model_list(
        controller_url,
        register_api_endpoint_file,
        True,
    )

    # Build the demo
    demo = build_demo(
        models,
        vl_models,
        args.elo_results_file,
        args.leaderboard_table_file,
    )

    return demo
