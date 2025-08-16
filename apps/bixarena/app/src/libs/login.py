import gradio as gr


def build_login_page():
    with gr.Column() as login_page:
        gr.Markdown("# Login Page")
    return login_page
