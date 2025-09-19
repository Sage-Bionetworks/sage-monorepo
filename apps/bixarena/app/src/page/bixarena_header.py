import gradio as gr
from config.auth_service import get_auth_service


def build_header():
    """Build header - login button will be managed reactively"""
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
            # Create reactive login button - will be updated by update_login_button
            login_btn = gr.Button("Login", variant="primary", link="")

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


def update_login_button():
    """Update login button state and link based on authentication"""
    auth_service = get_auth_service()

    if auth_service.is_user_authenticated():
        # User is logged in - show username, no external link
        username = auth_service.session.get_user_display_name()
        return gr.Button(username, variant="primary", link=None)
    else:
        # User not logged in - show login button with OAuth link
        login_url = auth_service.initiate_login()
        return gr.Button("Login", variant="primary", link=login_url)


def get_error_display():
    """Get error message display HTML"""
    auth_service = get_auth_service()
    error = auth_service.get_current_error()

    if error:
        return f"""
            <div style="background-color: #fee; border: 1px solid #fcc; 
                        color: #c00; padding: 10px; border-radius: 4px; margin: 10px 0;">
                ⚠️ {error}
            </div>
        """
    return ""


def get_login_button_state():
    """Get current login button state for updates"""
    auth_service = get_auth_service()
    button_config = auth_service.get_login_button_config()
    return gr.Button(button_config["value"], variant=button_config["variant"])
