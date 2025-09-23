import argparse
import urllib.parse
import gradio as gr
from page.bixarena_header import build_header, update_login_button, handle_login_click
from page.bixarena_battle import build_battle_page
from page.bixarena_leaderboard import build_leaderboard_page
from page.bixarena_home import build_home_page
from page.bixarena_user import build_user_page, update_user_page, handle_logout_click
from auth.auth_service import get_auth_service


class PageNavigator:
    """Page navigation"""

    def __init__(self, pages):
        self.pages = pages

    def show_page(self, index):
        return [gr.Column(visible=(i == index)) for i in range(len(self.pages))]


def check_oauth_callback(request: gr.Request):
    """Process OAuth callback"""
    auth_service = get_auth_service()

    if request and hasattr(request, "url"):
        try:
            parsed_url = urllib.parse.urlparse(str(request.url))
            params = urllib.parse.parse_qs(parsed_url.query)

            if "code" in params:
                code = params["code"][0]
                state = params.get("state", [None])[0]
                auth_service.handle_oauth_callback(code, state)

        except Exception as e:
            print(f"❌ OAuth callback failed: {e}")

    return update_login_button(), *update_user_page()


def parse_args():
    """Parse command line arguments"""
    parser = argparse.ArgumentParser()
    parser.add_argument("--host", type=str, default="127.0.0.1")
    parser.add_argument("--port", type=int)
    parser.add_argument("--share", action="store_true")
    parser.add_argument("--concurrency-count", type=int, default=10)
    parser.add_argument("--register-api-endpoint-file", type=str)
    parser.add_argument("--moderate", action="store_true")
    parser.add_argument("--gradio-root-path", type=str)
    return parser.parse_args()


def build_app(register_api_endpoint_file=None, moderate=False):
    """Create the main application"""

    cleanup_js = """
    function() {
        setTimeout(function() {
            if (window.location.search.includes('code=')) {
                const url = new URL(window.location);
                url.searchParams.delete('code');
                url.searchParams.delete('state');
                window.history.replaceState({}, document.title, url.pathname);
            }
        }, 100);
    }
    """

    with gr.Blocks(title="BixArena - Biomedical LLM Evaluation") as demo:
        header, battle_btn, leaderboard_btn, login_btn = build_header()

        with gr.Column(visible=True) as home_page:
            home_content, cta_btn = build_home_page()

        with gr.Column(visible=False) as battle_page:
            build_battle_page(register_api_endpoint_file, moderate)

        with gr.Column(visible=False) as leaderboard_page:
            build_leaderboard_page()

        with gr.Column(visible=False) as user_page:
            user_container, welcome_display, logout_btn = build_user_page()

        pages = [home_page, battle_page, leaderboard_page, user_page]
        navigator = PageNavigator(pages)

        # Navigation
        battle_btn.click(lambda: navigator.show_page(1), outputs=pages)
        leaderboard_btn.click(lambda: navigator.show_page(2), outputs=pages)
        cta_btn.click(lambda: navigator.show_page(1), outputs=pages)

        # Login
        login_btn.click(
            lambda: handle_login_click(
                navigator, update_login_button, update_user_page
            ),
            outputs=pages + [login_btn, welcome_display, logout_btn],
        )

        # Logout
        logout_btn.click(
            lambda: handle_logout_click(
                navigator, update_login_button, update_user_page
            ),
            outputs=pages + [login_btn, welcome_display, logout_btn],
        )

        # OAuth callback
        demo.load(
            check_oauth_callback,
            outputs=[login_btn, welcome_display, logout_btn],
            js=cleanup_js,
        )

    return demo


if __name__ == "__main__":
    args = parse_args()
    app = build_app(args.register_api_endpoint_file, args.moderate)
    app.queue(default_concurrency_limit=args.concurrency_count).launch(
        server_name=args.host,
        server_port=args.port,
        share=args.share,
        max_threads=200,
        root_path=args.gradio_root_path,
    )
