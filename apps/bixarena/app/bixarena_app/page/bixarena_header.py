import os

import gradio as gr

from bixarena_app.auth.auth_service import get_auth_service


def build_header():
    """Build header with navigation and login button"""
    with gr.Row(elem_id="header-row") as header:
        with gr.Column(scale=4):
            gr.HTML("""
                <div style="display: flex; align-items: center; height: 40px;">
                    <h1 style="margin: 0; padding: 0; font-size: 1.5rem;">
                        <a href="/" style="text-decoration: none; color: inherit;">
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
            login_btn = gr.Button("Login", variant="primary", link="")

    gr.HTML("""
        <style>
        #header-row {
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #e0e0e0;
            margin-bottom: 20px;
        }
        </style>
    """)

    return header, battle_btn, leaderboard_btn, login_btn


def update_login_button():
    """Update login button using backend OIDC start endpoint."""
    auth_service = get_auth_service()
    backend_base = os.environ.get("BACKEND_BASE_URL", "http://localhost:8112/v1")
    start_endpoint = f"{backend_base}/auth/oidc/start"
    if auth_service.is_authenticated():
        return gr.Button(auth_service.get_display_name(), variant="primary", link=None)
    else:
        return gr.Button("Login", variant="primary", link=start_endpoint)


def handle_login_click(navigator, update_login_button, update_user_page):
    """Handle login button click - must return all expected outputs"""
    auth_service = get_auth_service()
    user_info = update_user_page()

    updated_login_btn = update_login_button()

    if auth_service.is_authenticated():
        # Show user page
        return *navigator.show_page(3), updated_login_btn, *user_info
    else:
        # Show home page
        return *navigator.show_page(0), updated_login_btn, *user_info
