"""
BixArena Battle Page

Adapted from FastChat's gradio_web_server_multi.py and
simplified to a single function for a single-page LLM comparison arena.
"""

import gradio as gr

from fastchat.serve.gradio_block_arena_anony import (
    build_side_by_side_ui_anony,
    set_global_vars_anony,
)
from fastchat.serve.gradio_web_server import (
    get_model_list,
)


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

    with gr.Blocks(title="BixArena - Biomedical LLM Battle") as battle_page:
        build_side_by_side_ui_anony(models)

    return battle_page
