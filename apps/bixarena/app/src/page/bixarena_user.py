import gradio as gr
from config.auth_service import get_auth_service


def build_user_page():
    """Build self-contained user page that manages its own state"""

    with gr.Column() as user_container:
        # Internal components managed by this page
        welcome_display = gr.HTML("")
        logout_btn = gr.Button("Logout", visible=False, variant="secondary")

        # Internal logout handler - this is not used directly anymore
        def handle_internal_logout():
            """Handle logout within user page - simplified version"""
            # This function is no longer used since logout is handled in main.py
            # Keeping it for backward compatibility but it doesn't need to do anything
            return (
                gr.HTML(""),  # Clear welcome display
                gr.Button("Logout", visible=False),  # Hide logout button
            )

        # Wire up logout button to internal handler
        logout_btn.click(
            fn=handle_internal_logout, outputs=[welcome_display, logout_btn]
        )

        # Function to update user page components based on user info
        def update_user_display_internal(user_info):
            """Internal function to update welcome message and logout button"""
            if user_info and user_info.get("authenticated", False):
                # User is authenticated - show simple welcome message and logout button
                username = user_info.get("firstName") or user_info.get(
                    "userName", "User"
                )
                welcome_html = f"<h2>Welcome, {username}!</h2>"
                return (
                    gr.HTML(welcome_html),
                    gr.Button("Logout", visible=True, variant="secondary"),
                )
            else:
                # User not authenticated - show simple login prompt
                login_prompt_html = "<p>Please log in to access this page.</p>"
                return (gr.HTML(login_prompt_html), gr.Button("Logout", visible=False))

    # Return the container, components, and the update function
    return user_container, welcome_display, logout_btn, update_user_display_internal


def get_current_user_info():
    """Get current user information for passing to user page"""
    auth_service = get_auth_service()

    if auth_service.is_user_authenticated():
        user_data = auth_service.get_current_user()
        if user_data:
            # Add authenticated flag for easy checking
            user_data["authenticated"] = True
            return user_data

    return {"authenticated": False}


def update_user_page():
    """Function that returns tuple of user info for components"""
    user_info = get_current_user_info()

    if user_info and user_info.get("authenticated", False):
        # User is authenticated - return welcome message and visible logout button
        username = user_info.get("firstName") or user_info.get("userName", "User")
        welcome_html = f"<h2>Welcome, {username}!</h2>"
        return (
            gr.HTML(welcome_html),
            gr.Button("Logout", visible=True, variant="secondary"),
        )
    else:
        # User not authenticated - return login prompt and hidden logout button
        login_prompt_html = "<p>Please log in to access this page.</p>"
        return (gr.HTML(login_prompt_html), gr.Button("Logout", visible=False))


# Legacy function for backward compatibility (if needed elsewhere)
def handle_logout():
    """Legacy logout handler - now handled internally by user page"""
    auth_service = get_auth_service()
    auth_service.logout_user()
