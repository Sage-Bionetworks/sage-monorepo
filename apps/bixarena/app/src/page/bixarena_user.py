import gradio as gr
from auth.auth_service import get_auth_service


def build_user_page():
    """Build user page with welcome message and logout button"""
    with gr.Column() as user_container:
        welcome_display = gr.HTML("")

        with gr.Row():
            with gr.Column(scale=1):
                gr.HTML("")  # Left spacer
            with gr.Column(scale=1):
                logout_btn = gr.Button("Logout", visible=False, variant="primary")
            with gr.Column(scale=1):
                gr.HTML("")  # Right spacer

    return user_container, welcome_display, logout_btn


def update_user_page():
    """Update user page based on authentication state"""
    auth_service = get_auth_service()

    if auth_service.is_authenticated():
        username = auth_service.get_display_name()
        welcome_html = f"<h2 style='text-align: center;'>Welcome, {username}!</h2>"
        return gr.HTML(welcome_html), gr.Button(
            "Logout", visible=True, variant="primary"
        )
    else:
        login_prompt = (
            "<p style='text-align: center;'>Please log in to access this page.</p>"
        )
        return gr.HTML(login_prompt), gr.Button("Logout", visible=False)


def handle_logout_click(navigator, update_login_button, update_user_page):
    """Handle logout and redirect to home - must return all expected outputs"""
    auth_service = get_auth_service()

    if auth_service.is_authenticated():
        auth_service.logout()

    updated_login_btn = update_login_button()
    user_info = update_user_page()
    home_pages = navigator.show_page(0)

    # Clear session cookie
    # Clear session cookie
    clear_cookie_script = """
    <script>
    document.cookie = "bixarena_session=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT; samesite=strict;";
    </script>
    """

    return *home_pages, updated_login_btn, *user_info, gr.HTML(clear_cookie_script)
