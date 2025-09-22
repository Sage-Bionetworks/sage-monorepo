import gradio as gr
from config.auth_service import get_auth_service


def build_user_page():
    """Build self-contained user page that manages its own state"""

    with gr.Column() as user_container:
        welcome_display = gr.HTML("")
        with gr.Row():
            with gr.Column(scale=1):
                logout_btn = gr.Button("Logout", visible=False, variant="primary")
            with gr.Column(scale=1):
                gr.HTML("")
            with gr.Column(scale=1):
                gr.HTML("")

        def update_user_display_internal(user_info):
            """Internal function to update welcome message and logout button"""
            if user_info and user_info.get("authenticated", False):
                username = user_info.get("firstName") or user_info.get(
                    "userName", "User"
                )
                welcome_html = f"<h2>Welcome, {username}!</h2>"
                return (
                    gr.HTML(welcome_html),
                    gr.Button("Logout", visible=True, variant="secondary"),
                )
            else:
                login_prompt_html = "<p>Please log in to access this page.</p>"
                return (gr.HTML(login_prompt_html), gr.Button("Logout", visible=False))

    return user_container, welcome_display, logout_btn, update_user_display_internal


def handle_logout_click(navigator, update_login_button, update_user_page):
    """Handle logout and redirect to home page"""
    auth_service = get_auth_service()

    if not auth_service.is_user_authenticated():
        updated_login_btn = update_login_button()
        user_info = update_user_page()
        home_pages = navigator.show_page(0)
        return home_pages + [updated_login_btn] + list(user_info)

    auth_service.logout_user()

    updated_login_btn = update_login_button()
    user_info = update_user_page()
    home_pages = navigator.show_page(0)

    return home_pages + [updated_login_btn] + list(user_info)


def get_current_user_info():
    """Get current user information for passing to user page"""
    auth_service = get_auth_service()

    if auth_service.is_user_authenticated():
        user_data = auth_service.get_current_user()
        if user_data:
            user_data["authenticated"] = True
            return user_data

    return {"authenticated": False}


def update_user_page():
    """Function that returns tuple of user info for components"""
    user_info = get_current_user_info()

    if user_info and user_info.get("authenticated", False):
        username = user_info.get("firstName") or user_info.get("userName", "User")
        welcome_html = f"<h2>Welcome, {username}!</h2>"
        return (
            gr.HTML(welcome_html),
            gr.Button("Logout", visible=True, variant="primary"),
        )
    else:
        login_prompt_html = "<p>Please log in to access this page.</p>"
        return (gr.HTML(login_prompt_html), gr.Button("Logout", visible=False))
