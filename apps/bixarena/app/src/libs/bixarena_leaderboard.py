import gradio as gr


def build_leaderboard_page():
    with gr.Column() as leaderboard_page:
        gr.Markdown("# Leaderboard Page")
    return leaderboard_page
