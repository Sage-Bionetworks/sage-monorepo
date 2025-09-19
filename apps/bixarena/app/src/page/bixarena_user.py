import gradio as gr
from config.auth_service import get_auth_service


def build_user_page():
    """Build simple user profile page"""
    with gr.Column() as user_page:
        # Welcome message - will be updated reactively
        welcome_display = gr.HTML("")

        # Logout button
        logout_btn = gr.Button("Logout", variant="primary", size="lg", visible=False)

    return user_page, welcome_display, logout_btn


def get_user_welcome_message():
    """Get welcome message based on current authentication state"""
    auth_service = get_auth_service()

    if not auth_service.is_user_authenticated():
        return """
            <div style="text-align: center; padding: 40px;">
                <h2>Please log in to access your profile</h2>
                <p>You need to be logged in to view this page.</p>
            </div>
        """

    # User is authenticated - get user data
    user_data = auth_service.get_current_user()
    username = user_data.get("firstName", user_data.get("userName", "User"))

    return f"""
        <div style="text-align: center; padding: 40px;">
            <h2>Welcome, {username}!</h2>
        </div>
    """


def update_user_page():
    """Update user page content and logout button visibility"""
    auth_service = get_auth_service()

    welcome_html = get_user_welcome_message()
    logout_visible = auth_service.is_user_authenticated()

    return welcome_html, gr.Button(
        "Logout", variant="primary", size="lg", visible=logout_visible
    )


def handle_logout():
    """Handle user logout - delegates to auth service"""
    auth_service = get_auth_service()
    auth_service.logout_user()
    return gr.update()
