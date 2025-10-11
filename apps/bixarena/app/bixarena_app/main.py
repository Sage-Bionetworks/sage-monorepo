import argparse
import os

import gradio as gr
import requests

from bixarena_app.auth.auth_service import get_auth_service
from bixarena_app.page.bixarena_battle import build_battle_page
from bixarena_app.page.bixarena_header import (
    build_header,
    handle_login_click,
    update_login_button,
)
from bixarena_app.page.bixarena_home import build_home_page
from bixarena_app.page.bixarena_leaderboard import build_leaderboard_page
from bixarena_app.page.bixarena_user import (
    build_user_page,
    handle_logout_click,
    update_user_page,
)


class PageNavigator:
    """Page navigation"""

    def __init__(self, pages):
        self.pages = pages

    def show_page(self, index):
        return [gr.Column(visible=(i == index)) for i in range(len(self.pages))]


def _extract_session_cookie(request: gr.Request) -> str | None:
    """Extract session cookie from request"""
    if not request or not hasattr(request, "headers"):
        return None

    cookie_header = request.headers.get("cookie", "")
    for cookie in cookie_header.split(";"):
        if "bixarena_session=" in cookie:
            return cookie.split("bixarena_session=")[1].strip()
    return None


def check_oauth_callback(request: gr.Request):
    """Process OAuth callback and (if necessary) sync backend session once.

    We removed continuous polling, but we still need a one-time sync so that
    when the backend (Java) completes the OIDC flow and sets JSESSIONID, the
    Gradio app can reflect authenticated state (button -> Logout, user page).
    """
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
            document.cookie = "bixarena_session=; path=/;" +\
            " expires=Thu, 01 Jan 1970 00:00:00 GMT; samesite=strict;";
            </script>
            """
            return (
                update_login_button(),
                *update_user_page(),
                gr.HTML(clear_cookie_script),
            )

    # (Direct OAuth code handling removed; rely on backend sync below.)

    # One-time backend session sync if still unauthenticated and JSESSIONID present
    if not auth_service.is_authenticated() and request and hasattr(request, "headers"):
        cookie_header = request.headers.get("cookie", "")
        jsessionid = None
        for ck in cookie_header.split(";"):
            ck = ck.strip()
            if ck.startswith("JSESSIONID="):
                jsessionid = ck.split("=", 1)[1]
                break
        if jsessionid:
            backend_base = os.environ.get(
                "BACKEND_BASE_URL", "http://127.0.0.1:8112/v1"
            )
            try:
                print(
                    "[auth-sync] Attempting one-time backend session sync JSESSIONID "
                    f"present len={len(jsessionid)}"
                )
                resp = requests.get(
                    f"{backend_base}/echo",
                    cookies={"JSESSIONID": jsessionid},
                    timeout=2,
                )
                if resp.status_code == 200:
                    data = resp.json()
                    sub = data.get("sub")
                    if sub:
                        auth_service.session.set_current_user(
                            {"firstName": sub, "userName": sub, "source": "backend"}
                        )
                        print(f"[auth-sync] Backend sync success sub={sub}")
                        return update_login_button(), *update_user_page(), gr.HTML("")
                    else:
                        print("[auth-sync] /echo returned 200 but no sub field")
                else:
                    snippet = resp.text[:160] if resp.text else ""
                    print(
                        f"[auth-sync] /echo non-200 status={resp.status_code} "
                        f"bodySnippet={snippet}"
                    )
            except Exception as e:
                print(f"[auth-sync] backend sync failed: {e}")
        else:
            if cookie_header:
                print(
                    "[auth-sync] No JSESSIONID found in cookie header during "
                    "callback sync"
                )
            else:
                print("[auth-sync] Empty cookie header on callback load; cannot sync")

    return update_login_button(), *update_user_page(), gr.HTML("")


def parse_args():
    """Parse command line arguments"""
    parser = argparse.ArgumentParser()
    parser.add_argument("--host", type=str, default="127.0.0.1")
    parser.add_argument("--port", type=int)
    parser.add_argument("--share", action="store_true")
    parser.add_argument("--concurrency-count", type=int, default=10)
    parser.add_argument("--moderate", action="store_true")
    parser.add_argument("--gradio-root-path", type=str)
    return parser.parse_args()


def build_app(moderate=False):
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
            build_battle_page(moderate)

        with gr.Column(visible=False) as leaderboard_page:
            build_leaderboard_page()

        with gr.Column(visible=False) as user_page:
            _, welcome_display, logout_btn = build_user_page()

        # Hidden HTML component(s) for cookie scripts / future use
        cookie_html = gr.HTML("", visible=False, elem_id="cookie-html")

        # Expose start endpoint to login button JS for immediate redirect
        import os as _os

        backend_base = _os.environ.get("BACKEND_BASE_URL", "http://127.0.0.1:8112/v1")
        start_endpoint = f"{backend_base}/auth/oidc/start"
        gr.HTML(
            "<span id='login-start-endpoint' style='display:none'>"
            + start_endpoint
            + "</span><span id='backend-base' style='display:none'>"
            + backend_base
            + "</span>"
        )

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
            outputs=pages + [login_btn, welcome_display, logout_btn, cookie_html],
            js="""
() => {
  const btn = document.querySelector('#login-btn button,#login-btn');
  if(!btn) return;
  const label = btn.innerText.trim();
  if(label === 'Login') {
      const el = document.getElementById('login-start-endpoint');
      if(el){ window.location.href = el.textContent.trim(); }
      return;
  }
  if(label === 'Logout') {
      const baseEl = document.getElementById('backend-base');
      const base = baseEl ? baseEl.textContent.trim() : '';
      if(base){
          fetch(base + '/auth/logout', {method:'POST', credentials:'include'})
             .finally(()=> {
                 try { sessionStorage.setItem('justLoggedOut','1'); } catch(e) {}
                 window.location.href = '/';
             });
      }
  }
}
                """,
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

        # (Removed MutationObserver; direct JS click handles login redirect.)

    return demo


if __name__ == "__main__":
    args = parse_args()
    app = build_app(args.moderate)
    app.queue(default_concurrency_limit=args.concurrency_count).launch(
        server_name=args.host,
        server_port=args.port,
        share=args.share,
        max_threads=200,
        root_path=args.gradio_root_path,
    )
