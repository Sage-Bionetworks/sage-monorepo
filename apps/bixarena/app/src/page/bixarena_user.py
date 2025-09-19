"""
page/bixarena_user.py - User profile page with logout functionality
"""

import gradio as gr
from config.oauth_handler import get_current_user, logout_user


def build_user_page():
    """Build user profile page with name and logout button"""
    current_user = get_current_user()
    print(f"Debug - build_user_page called: current_user = {current_user}")  # Debug

    # Get user's display name
    if current_user:
        username = current_user.get("firstName", current_user.get("userName", "User"))
        print(f"Debug - Found username: {username}")  # Debug
    else:
        username = "Guest"
        print("Debug - No user found, using Guest")  # Debug

    with gr.Column() as user_page:
        with gr.Row():
            gr.HTML(f"""
                <div style="text-align: center; padding: 40px;">
                    <h2>Welcome, {username}!</h2>
                </div>
            """)

        with gr.Row():
            logout_btn = gr.Button("Logout", variant="primary", size="lg")

    return user_page, logout_btn


def handle_logout():
    """Handle user logout and return to home page"""
    logout_user()
    return gr.update()
