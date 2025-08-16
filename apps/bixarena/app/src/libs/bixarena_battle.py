import gradio as gr


def build_battle_page():
    with gr.Column() as battle_page:
        gr.Markdown("# Battle Page")
    return battle_page
