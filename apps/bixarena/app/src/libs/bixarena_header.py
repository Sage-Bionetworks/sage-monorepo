import gradio as gr


def build_header():
    """Build the header with proper alignment - clean solution"""

    with gr.Row(elem_id="header-row") as header:
        with gr.Column(scale=4):
            gr.HTML("""
                <div style="display: flex; align-items: center; height: 40px;">
                    <h1 style="margin: 0; padding: 0; font-size: 1.5rem;">
                        <a href="/" style="text-decoration: none; color: inherit; cursor: pointer;">
                            🧬 BixArena
                        </a>
                    </h1>
                </div>
            """)

        with gr.Column(scale=1):
            battle_btn = gr.Button("Battle", variant="secondary")
        with gr.Column(scale=1):
            leaderboard_btn = gr.Button("Leaderboard", variant="secondary")
        with gr.Column(scale=1):
            login_btn = gr.Button("Login", variant="primary")

    # CSS to align the row items
    gr.HTML("""
        <style>
        #header-row {
            align-items: center;
        }
        </style>
    """)

    return header, battle_btn, leaderboard_btn, login_btn
