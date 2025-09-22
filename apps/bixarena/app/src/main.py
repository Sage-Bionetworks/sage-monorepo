import argparse
import urllib.parse
import gradio as gr
from page.bixarena_header import build_header, update_login_button
from page.bixarena_battle import build_battle_page
from page.bixarena_leaderboard import build_leaderboard_page
from page.bixarena_home import build_home_page
from page.bixarena_user import build_user_page, update_user_page
from config.auth_service import get_auth_service


class PageNavigator:
    """Clean navigation class for managing page visibility"""

    def __init__(self, sections):
        self.sections = sections

    def show_page(self, page_index):
        """Show only the specified page, hide others"""
        return [gr.Column(visible=(i == page_index)) for i in range(len(self.sections))]


def check_oauth_callback(request: gr.Request):
    """Process OAuth callback and return updated states"""
    auth_service = get_auth_service()

    if request and hasattr(request, "url"):
        try:
            parsed_url = urllib.parse.urlparse(str(request.url))
            url_params = urllib.parse.parse_qs(parsed_url.query)

            if "code" in url_params:
                oauth_code = url_params["code"][0]
                oauth_state = url_params.get("state", [None])[0]

                success = auth_service.handle_oauth_callback(oauth_code, oauth_state)

                if success:
                    print(
                        f"✅ Login successful: {auth_service.session.get_user_display_name()}"
                    )
                else:
                    print("❌ Login failed")

        except Exception as e:
            auth_service.session.set_error(f"Callback processing error: {str(e)}")
            print(f"❌ Login failed: {str(e)}")

    return (update_login_button(), *update_user_page())


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
    """Create the main application with clean header positioning"""

    # JavaScript code to clean OAuth parameters from URL
    cleanup_js = """
    function() {
        setTimeout(function() {
            if (window.location.search.includes('code=') || 
                window.location.search.includes('state=')) {
                
                const url = new URL(window.location);
                url.searchParams.delete('code');
                url.searchParams.delete('state'); 
                
                window.history.replaceState({}, document.title, url.pathname + url.hash);
            }
        }, 100);
    }
    """

    with gr.Blocks(title="BixArena - Biomedical LLM Evaluation") as demo:
        header, battle_btn, leaderboard_btn, login_btn = build_header()

        with gr.Column(visible=True) as home_page:
            home_content, cta_btn = build_home_page()

        with gr.Column(visible=False) as battle_page:
            battle_content = build_battle_page(register_api_endpoint_file, moderate)

        with gr.Column(visible=False) as leaderboard_page:
            leaderboard_content = build_leaderboard_page()

        with gr.Column(visible=False) as user_page:
            # User page now returns the update function along with components
            user_container, welcome_display, logout_btn, update_user_display = (
                build_user_page()
            )

        # Initialize navigator
        all_pages = [home_page, battle_page, leaderboard_page, user_page]
        navigator = PageNavigator(all_pages)

        # Navigation event handlers
        battle_btn.click(lambda: navigator.show_page(1), outputs=all_pages)
        leaderboard_btn.click(lambda: navigator.show_page(2), outputs=all_pages)
        cta_btn.click(lambda: navigator.show_page(1), outputs=all_pages)

        # Login button handler - simplified
        def handle_login_click():
            auth_service = get_auth_service()
            user_info = update_user_page()
            if auth_service.is_user_authenticated():
                # User is logged in - show user page
                return *navigator.show_page(3), *user_info
            else:
                # User not logged in - stay on current page (OAuth redirect via button link)
                return *navigator.show_page(0), *user_info

        login_btn.click(
            fn=handle_login_click,
            outputs=all_pages + [welcome_display, logout_btn],
        )

        # Handle OAuth callback and clean URL - simplified
        demo.load(
            fn=check_oauth_callback,
            outputs=[login_btn, welcome_display, logout_btn],
            js=cleanup_js,
        )

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
