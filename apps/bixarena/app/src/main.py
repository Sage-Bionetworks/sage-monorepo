"""
main.py - Clean OAuth integration using file-based redirect (like oauth_test.py)
"""

import argparse
import urllib.parse
import gradio as gr
import webbrowser
import os
from page.bixarena_header import build_header, update_user_session, clear_user_session
from page.bixarena_battle import build_battle_page
from page.bixarena_leaderboard import build_leaderboard_page
from page.bixarena_home import build_home_page
from page.bixarena_user import build_user_page, handle_logout
from config.oauth_handler import (
    handle_synapse_callback,
    get_synapse_login_url,
    get_current_user,
)


class PageNavigator:
    """Clean navigation class for managing page visibility"""

    def __init__(self, sections):
        self.sections = sections

    def show_page(self, page_index):
        """Show only the specified page, hide others"""
        return [gr.Column(visible=(i == page_index)) for i in range(len(self.sections))]


def handle_login_redirect():
    """Handle login button click - pure server-side redirect"""
    # Check if already logged in
    current_user = get_current_user()
    if current_user:
        # Already logged in, no action needed
        return ""

    # Generate login URL and perform server-side redirect
    login_url, state = get_synapse_login_url()
    print(f"🔗 Redirecting to Synapse: {login_url}")

    # Use meta refresh for server-side redirect (no JavaScript)
    redirect_html = f'<meta http-equiv="refresh" content="0;url={login_url}">'
    return redirect_html


def check_oauth_callback(request: gr.Request):
    """Check for OAuth callback in URL and process it"""
    if request is None:
        return "", get_login_button_state()

    # Extract URL parameters
    try:
        # Get the full URL from request
        if hasattr(request, "url"):
            parsed_url = urllib.parse.urlparse(str(request.url))
            url_params = urllib.parse.parse_qs(parsed_url.query)

            # Check for OAuth code
            if "code" in url_params:
                oauth_code = url_params["code"][0]
                print(f"🔍 Processing OAuth code: {oauth_code[:20]}...")

                # Process the OAuth callback
                user_profile = handle_synapse_callback(oauth_code)
                if user_profile:
                    # Update header state
                    update_user_session(user_profile)
                    username = user_profile.get(
                        "firstName", user_profile.get("userName", "User")
                    )
                    print(f"✅ Login successful for: {username}")

                    # Return updated button state
                    return "", gr.Button(username, variant="primary")
                else:
                    print("❌ Login failed")
    except Exception as e:
        print(f"Error processing OAuth callback: {e}")

    # Return current button state
    return "", get_login_button_state()


def get_login_button_state():
    """Get the current login button state based on user authentication"""
    current_user = get_current_user()
    if current_user:
        username = current_user.get("firstName", current_user.get("userName", "User"))
        return gr.Button(username, variant="primary")
    else:
        return gr.Button("Login", variant="primary")


def handle_user_logout_and_navigate(navigator):
    """Handle logout and navigate to home page"""
    handle_logout()
    clear_user_session()
    # Return home page visibility and updated login button
    return navigator.show_page(0) + [gr.Button("Login", variant="primary")]


def parse_args():
    """Parse command line arguments"""
    parser = argparse.ArgumentParser()
    parser.add_argument("--host", type=str, default="127.0.0.1")
    parser.add_argument("--port", type=int)
    parser.add_argument(
        "--share",
        action="store_true",
        help="Whether to generate a public, shareable link",
    )
    parser.add_argument(
        "--concurrency-count",
        type=int,
        default=10,
        help="The concurrency count of the gradio queue",
    )
    parser.add_argument(
        "--register-api-endpoint-file",
        type=str,
        help="Register API-based model endpoints from a JSON file",
    )
    parser.add_argument(
        "--moderate",
        action="store_true",
        help="Enable content moderation to block unsafe inputs",
    )
    parser.add_argument(
        "--gradio-root-path", type=str, help="Sets the gradio root path"
    )
    return parser.parse_args()


def build_app(register_api_endpoint_file=None, moderate=False):
    """Create the main application"""

    with gr.Blocks(title="BixArena - Biomedical LLM Evaluation") as demo:
        # OAuth status - only visible when needed
        oauth_status = gr.HTML("")

        # Header
        header, battle_btn, leaderboard_btn, login_btn = build_header()

        # Pages
        with gr.Column(visible=True) as home_page:
            home_content, cta_btn = build_home_page()

        with gr.Column(visible=False) as battle_page:
            battle_content = build_battle_page(register_api_endpoint_file, moderate)

        with gr.Column(visible=False) as leaderboard_page:
            leaderboard_content = build_leaderboard_page()

        with gr.Column(visible=False) as user_page:
            # Build a placeholder user page that will be updated
            with gr.Row():
                user_welcome = gr.HTML("")
            with gr.Row():
                logout_btn = gr.Button("Logout", variant="primary", size="lg")

        # Initialize navigator
        all_pages = [home_page, battle_page, leaderboard_page, user_page]
        navigator = PageNavigator(all_pages)

        # Page navigation
        battle_btn.click(lambda: navigator.show_page(1), outputs=all_pages)
        leaderboard_btn.click(lambda: navigator.show_page(2), outputs=all_pages)
        cta_btn.click(lambda: navigator.show_page(1), outputs=all_pages)

        # Login button handling - need to handle both states dynamically
        def handle_login_button_click():
            """Handle login button click - check user state dynamically"""
            current_user = get_current_user()
            if current_user is None:
                # Not logged in - redirect to login
                return handle_login_redirect(), *navigator.show_page(0), ""
            else:
                # Logged in - show user page and update welcome message
                if current_user:
                    username = current_user.get(
                        "firstName", current_user.get("userName", "User")
                    )
                else:
                    username = "Guest"

                welcome_html = f"""
                    <div style="text-align: center; padding: 40px;">
                        <h2>Welcome, {username}!</h2>
                    </div>
                """

                return "", *navigator.show_page(3), welcome_html

        login_btn.click(
            fn=handle_login_button_click,
            outputs=[oauth_status] + all_pages + [user_welcome],
        )

        def handle_user_logout_and_navigate(navigator):
            """Handle logout and navigate to home page"""
            handle_logout()
            clear_user_session()
            # Return home page visibility and updated login button
            return navigator.show_page(0) + [gr.Button("Login", variant="primary")]

        # Logout button handling
        logout_btn.click(
            fn=lambda: handle_user_logout_and_navigate(navigator),
            outputs=all_pages + [login_btn],
        )

        # Check for OAuth callback on page load and update button
        demo.load(fn=check_oauth_callback, outputs=[oauth_status, login_btn])

    return demo


if __name__ == "__main__":
    args = parse_args()

    app = build_app(
        register_api_endpoint_file=args.register_api_endpoint_file,
        moderate=args.moderate,
    )

    app.queue(
        default_concurrency_limit=args.concurrency_count,
        status_update_rate=10,
        api_open=False,
    ).launch(
        server_name=args.host,
        server_port=args.port,
        share=args.share,
        max_threads=200,
        root_path=args.gradio_root_path,
    )
