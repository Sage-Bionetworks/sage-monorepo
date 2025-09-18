"""
page/bixarena_header.py - Clean header component with dynamic login URL
"""

import gradio as gr
from config.oauth_handler import get_current_user, get_synapse_login_url

# Simple global state for current user
current_user = None


def build_header():
    """Build header with dynamic login state"""
    global current_user

    # Check current login status
    current_user = get_current_user()

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
            # Always create login button, but handle differently based on auth state
            if current_user is None:
                # For non-logged in users
                login_btn = gr.Button("Login", variant="primary")
            else:
                # Show username if logged in
                username = current_user.get(
                    "firstName", current_user.get("userName", "User")
                )
                login_btn = gr.Button(username, variant="primary")

    # CSS to align header items
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


def get_user_display_name():
    """Get current user's display name"""
    global current_user
    if current_user:
        return current_user.get("firstName", current_user.get("userName", "User"))
    return None


def is_user_logged_in():
    """Check if user is currently logged in"""
    global current_user
    return current_user is not None


def update_user_session(user_info):
    """Update the current user session"""
    global current_user
    current_user = user_info


def clear_user_session():
    """Clear the current user session"""
    global current_user
    current_user = None
