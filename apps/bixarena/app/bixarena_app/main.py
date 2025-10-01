import argparse
import urllib.parse
import gradio as gr
from typing import Optional, Tuple, Any
from bixarena_app.page.bixarena_header import (
    build_header,
    update_login_button,
    handle_login_click,
)
from bixarena_app.page.bixarena_battle import build_battle_page
from bixarena_app.page.bixarena_leaderboard import build_leaderboard_page
from bixarena_app.page.bixarena_home import build_home_page
from bixarena_app.page.bixarena_user import (
    build_user_page,
    update_user_page,
    handle_logout_click,
)
from bixarena_app.auth.auth_service import get_auth_service


class PageNavigator:
    """Page navigation"""

    def __init__(self, pages):
        self.pages = pages

    def show_page(self, index):
        return [gr.Column(visible=(i == index)) for i in range(len(self.pages))]


def _extract_session_cookie(request: gr.Request) -> Optional[str]:
    """Extract session cookie from request"""
    if not request or not hasattr(request, "headers"):
        return None

    cookie_header = request.headers.get("cookie", "")
    for cookie in cookie_header.split(";"):
        if "bixarena_session=" in cookie:
            return cookie.split("bixarena_session=")[1].strip()
    return None


def _validate_oauth_params(code: str, state: str) -> bool:
    """Validate OAuth parameters"""
    if not code or len(code) > 500:
        print("❌ Invalid OAuth code parameter")
        return False
    if state and len(state) > 100:
        print("❌ Invalid OAuth state parameter")
        return False
    return True


def _process_oauth_callback(
    request: gr.Request, auth_service
) -> Optional[Tuple[Any, ...]]:
    """Process OAuth callback parameters"""
    try:
        parsed_url = urllib.parse.urlparse(str(request.url))
        params = urllib.parse.parse_qs(parsed_url.query)

        if "code" not in params:
            return None

        code = params["code"][0]
        state = params.get("state", [None])[0]

        if not _validate_oauth_params(code, state):
            return update_login_button(), *update_user_page(), gr.HTML("")

        success, session_id = auth_service.handle_oauth_callback(code, state)
        if success and session_id:
            # Set cookie with appropriate security for environment (2 hours)
            secure_flag = "secure" if str(request.url).startswith("https") else ""
            cookie_script = f"""
            <script>
            document.cookie = "bixarena_session={session_id}; path=/; max-age=7200; samesite=strict; {secure_flag}";
            </script>
            """
            return update_login_button(), *update_user_page(), gr.HTML(cookie_script)

    except Exception as e:
        print(f"❌ OAuth callback failed: {e}")

    return None


def check_oauth_callback(request: gr.Request):
    """Process OAuth callback with input validation and cookie management"""
    auth_service = get_auth_service()

    # Load session from cookie (for both callback and normal page loads)
    session_cookie = _extract_session_cookie(request)
    if session_cookie:
        success = auth_service.load_session_from_cookie(session_cookie)
        if not success:
            # Invalid session, clear cookie
            # Clear cookie (no need for secure flag when clearing)
            clear_cookie_script = """
            <script>
            document.cookie = "bixarena_session=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT; samesite=strict;";
            </script>
            """
            return (
                update_login_button(),
                *update_user_page(),
                gr.HTML(clear_cookie_script),
            )

    # Process OAuth callback if present
    if request and hasattr(request, "url"):
        result = _process_oauth_callback(request, auth_service)
        if result:
            return result

    return update_login_button(), *update_user_page(), gr.HTML("")


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

    with gr.Blocks(
        title="BixArena - Biomedical LLM Evaluation",
        css="""
        .content-wrapper {
            padding: 0 40px;
            max-width: 1200px;
            margin: 0 auto;
        }
        """,
    ) as demo:
        _, battle_btn, leaderboard_btn, login_btn = build_header()

        with gr.Column(visible=True) as home_page:
            _, cta_btn = build_home_page()

        with gr.Column(visible=False) as battle_page:
            build_battle_page(register_api_endpoint_file, moderate)

        with gr.Column(visible=False) as leaderboard_page:
            build_leaderboard_page()

        with gr.Column(visible=False) as user_page:
            _, welcome_display, logout_btn = build_user_page()

        # Hidden HTML component for cookie scripts
        cookie_html = gr.HTML("", visible=False)

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
            outputs=pages + [login_btn, welcome_display, logout_btn, cookie_html],
        )

        # OAuth callback
        demo.load(
            check_oauth_callback,
            outputs=[login_btn, welcome_display, logout_btn, cookie_html],
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
