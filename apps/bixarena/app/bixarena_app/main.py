import argparse
import functools
import logging
import os

import gradio as gr
import requests
from fastapi import FastAPI
from fastapi.responses import JSONResponse

from bixarena_app.auth.user_state import get_user_state
from bixarena_app.config.utils import setup_logging
from bixarena_app.page.bixarena_battle import build_battle_page

# Configure logging first
setup_logging()
logger = logging.getLogger(__name__)
from bixarena_app.page.bixarena_footer import build_footer
from bixarena_app.page.bixarena_header import (
    build_header,
    handle_login_click,
    update_battle_button,
    update_login_button,
)
from bixarena_app.page.bixarena_home import (
    build_home_page,
    load_public_stats_on_page_load,
    load_user_battles_on_page_load,
    update_cta_buttons_on_page_load,
)
from bixarena_app.page.bixarena_leaderboard import (
    build_leaderboard_page,
    load_leaderboard_stats_on_page_load,
)
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


def _get_auth_base_url_ssr() -> str | None:
    """Resolve the auth service base URL for server-side requests (SSR).

    Uses AUTH_BASE_URL_SSR for server-to-server communication
    (e.g., /userinfo endpoint).
    If unset, prints an error and returns None.
    """
    base = os.environ.get("AUTH_BASE_URL_SSR")
    if base:
        return base.rstrip("/")
    print(
        "[config] AUTH_BASE_URL_SSR not set.\n"
        "[config] Server-side auth requests will be disabled until configured."
    )
    return None


def _get_auth_base_url_csr() -> str | None:
    """Resolve the auth service base URL for client-side browser redirects (CSR).

    Uses AUTH_BASE_URL_CSR for browser-driven auth redirects (login/logout).
    If unset, prints an error and returns None.
    """
    base = os.environ.get("AUTH_BASE_URL_CSR")
    if base:
        return base.rstrip("/")
    print(
        "[config] AUTH_BASE_URL_CSR not set.\n"
        "[config] Login/logout redirects will be disabled until configured."
    )
    return None


def sync_backend_session_on_load(request: gr.Request):
    """Fetch user identity from backend once (if JSESSIONID cookie present).

    No local token storage, refresh logic, or OAuth flow lives here—only a
    best-effort identity pull so UI components can render the logged-in name.
    """
    state = get_user_state(request)

    # Skip if already populated or request has no headers (e.g. internal load)
    if not state.is_authenticated() and request and hasattr(request, "headers"):
        cookie_header = request.headers.get("cookie", "")
        jsessionid = None
        for ck in cookie_header.split(";"):
            ck = ck.strip()
            if ck.startswith("JSESSIONID="):
                jsessionid = ck.split("=", 1)[1]
                break
        if jsessionid:
            backend_base = _get_auth_base_url_ssr()
            try:
                print(
                    "[auth-sync] Starting auth service identity fetch "
                    f"(JSESSIONID present) len={len(jsessionid)}"
                )
                if not backend_base:
                    print(
                        "[auth-sync] Skipping identity fetch: AUTH_BASE_URL_SSR missing"
                    )
                else:
                    resp = requests.get(
                        f"{backend_base}/userinfo",
                        cookies={"JSESSIONID": jsessionid},
                        timeout=2,
                    )
                    if resp.status_code == 200:
                        data = resp.json()
                        sub = data.get("sub")
                        preferred_username = data.get("preferred_username", sub)
                        if sub:
                            state.set_current_user(
                                {
                                    "firstName": preferred_username,
                                    "userName": sub,
                                    "source": "backend",
                                }
                            )
                            print(
                                f"[auth-sync] Identity sync success sub={sub} "
                                f"preferred_username={preferred_username}"
                            )
                            return (
                                update_battle_button(request),
                                update_login_button(request),
                                *update_user_page(request),
                                gr.HTML(""),
                            )
                        else:
                            print(
                                "[auth-sync] /userinfo 200 but no sub field; "
                                "leaving guest state"
                            )
                    else:
                        snippet = resp.text[:160] if resp.text else ""
                        print(
                            f"[auth-sync] /userinfo {resp.status_code} "
                            f"bodySnippet={snippet}"
                        )
            except Exception as e:
                print(f"[auth-sync] identity fetch failed: {e}")
        else:
            if cookie_header:
                print("[auth-sync] Cookie header present but no JSESSIONID token")
            else:
                print("[auth-sync] No cookie header; skipping identity fetch")

    return (
        update_battle_button(request),
        update_login_button(request),
        *update_user_page(request),
        gr.HTML(""),
    )


def parse_args():
    """Parse command line arguments or environment variables.

    Supports two modes:
    1. Direct execution: python main.py --host 0.0.0.0 --port 7860
    2. Gradio CLI (dev mode): APP_PORT=7860 gradio main.py

    When using the `gradio` CLI for auto-reload, command-line arguments
    are not passed through, so environment variables are used instead.

    Environment variables:
    - APP_HOST: Server host (default: 127.0.0.1)
    - APP_PORT: Server port (default: None, uvicorn will auto-select)
    - APP_SHARE: Enable sharing (true/1/yes, default: false)
    - LOG_LEVEL: Logging level for uvicorn (default: info)

    Priority for port selection:
    1. --port command line argument (if provided)
    2. APP_PORT environment variable (if set)
    3. Default value: None (uvicorn will auto-select)

    Priority for log level selection:
    1. --log-level command line argument (if provided)
    2. LOG_LEVEL environment variable (if set)
    3. Default value: info
    """

    # Helper to parse environment variable for port with fallback to None
    def get_port_default():
        port_env = os.environ.get("APP_PORT")
        return int(port_env) if port_env else None

    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--host",
        type=str,
        default=os.environ.get("APP_HOST", "127.0.0.1"),
    )
    parser.add_argument(
        "--port",
        type=int,
        default=get_port_default(),
    )
    parser.add_argument(
        "--log-level",
        type=str,
        default=os.environ.get("LOG_LEVEL", "info"),
        help="Logging level (critical, error, warning, info, debug, trace)",
    )
    parser.add_argument(
        "--share",
        action="store_true",
        default=os.environ.get("APP_SHARE", "").lower() in ("true", "1", "yes"),
    )
    # Use parse_known_args to ignore unknown args (like demo path from gradio CLI)
    args, _ = parser.parse_known_args()

    # Normalize log level to lowercase
    args.log_level = args.log_level.lower()

    return args


def build_app():
    """Create the main application"""

    enable_crisp = os.environ.get("ENABLE_CRISP", "false").lower() == "true"

    cleanup_js = """
    function() {
        setTimeout(function() {
            // Reset Crisp chat session on page load
            if (window.$crisp) {
                window.$crisp.push(["do", "session:reset"]);
            }
        }, 100);
    }
    """

    crisp_script = ""
    if enable_crisp:
        crisp_script = """
    <script type="text/javascript">
        window.$crisp=[];
        window.CRISP_WEBSITE_ID="d58ad402-1217-476c-be6a-c8949671ced4";
        (function(){
            d=document;
            s=d.createElement("script");
            s.src="https://client.crisp.chat/l.js";
            s.async=1;
            d.getElementsByTagName("head")[0].appendChild(s);
        })();

        // Trigger scenario when the chatbox is opened for the first time by the user
        window.$crisp.push(["on", "chat:initiated", function() {
            window.$crisp.push([
                "do",
                "bot:scenario:run",
                ["scenario_6e1d8905-069c-41f6-b28a-33ef05520766"]
            ]);
        }]);
    </script>
    """

    with gr.Blocks(
        title="BioArena - Benchmarking LLMs for Biomedical Breakthroughs",
        head=crisp_script,
        css="""
        /* Hide Gradio's default footer */
        footer {
            display: none !important;
        }
        /* Remove padding from HTML containers */
        .html-no-padding .html-container {
            padding: 0 !important;
        }
        /* Override Gradio's default container max-width */
        .fillable.app {
            max-width: 1600px !important;
        }
        .page-content {
            min-height: calc(100vh - 200px);
            padding: 0 40px;
            max-width: 1400px;
            margin: 0 auto;
        }
        /* Keep CTA buttons centered at all screen sizes */
        #cta-button-row {
            justify-content: center !important;
        }
        #cta-button-container {
            max-width: 300px !important;
        }
        #cta-btn-authenticated, #cta-btn-login {
            width: 100% !important;
        }
        #cta-btn-authenticated *, #cta-btn-login * {
            white-space: nowrap !important;
        }
        """,
    ) as demo:
        _, battle_btn, leaderboard_btn, login_btn = build_header()

        with gr.Column(visible=True, elem_classes=["page-content"]) as home_page:
            (
                _,
                cta_btn_authenticated,
                cta_btn_login,
                models_evaluated_column,
                models_evaluated_box,
                total_battles_column,
                total_battles_box,
                total_users_column,
                total_users_box,
                user_battles_column,
                user_battles_box,
                user_rank_column,  # New
                user_rank_box,  # New
            ) = build_home_page()

        with gr.Column(visible=False, elem_classes=["page-content"]) as battle_page:
            _, example_prompt_ui, prompt_outputs = build_battle_page()

        with gr.Column(
            visible=False, elem_classes=["page-content"]
        ) as leaderboard_page:
            leaderboard_metrics = build_leaderboard_page()

        with gr.Column(visible=False, elem_classes=["page-content"]) as user_page:
            _, welcome_display, logout_btn = build_user_page()

        # Footer
        build_footer()

        # Hidden HTML component(s) for cookie scripts / future use
        cookie_html = gr.HTML("", visible=False, elem_id="cookie-html")

        # Expose start endpoint to login button JS for immediate redirect
        auth_base = _get_auth_base_url_csr()
        if not auth_base:
            print("[config] AUTH_BASE_URL_CSR missing; login button will be disabled.")
            start_endpoint = ""
            base_markup = ""
        else:
            start_endpoint = f"{auth_base}/auth/login"
            base_markup = auth_base
        gr.HTML(
            "<span id='login-start-endpoint' style='display:none'>"
            + start_endpoint
            + "</span><span id='backend-base' style='display:none'>"
            + base_markup
            + "</span>",
            elem_classes="html-no-padding",
        )

        pages = [home_page, battle_page, leaderboard_page, user_page]
        navigator = PageNavigator(pages)

        # Navigation - battle page will refresh prompts via its own load handler
        battle_btn.click(
            lambda: navigator.show_page(1),
            outputs=pages,
        )
        leaderboard_btn.click(
            lambda: navigator.show_page(2) + [load_leaderboard_stats_on_page_load()],
            outputs=pages + [leaderboard_metrics],
        )
        # Authenticated CTA button - navigates to battle page
        cta_btn_authenticated.click(
            lambda: navigator.show_page(1),
            outputs=pages,
        )

        # Bind static args so Gradio can still inject request without warnings.
        login_handler = functools.partial(
            handle_login_click, navigator, update_login_button, update_user_page
        )
        logout_handler = functools.partial(
            handle_logout_click, navigator, update_login_button, update_user_page
        )

        # Login CTA button - redirects to login page
        cta_btn_login.click(
            None,
            js="""
() => {
  const el = document.getElementById('login-start-endpoint');
  const url = el ? el.textContent.trim() : '';
  if (url) window.location.href = url;
}
            """,
        )

        # Login
        login_btn.click(
            login_handler,
            outputs=pages + [login_btn, welcome_display, logout_btn, cookie_html],
            js="""
() => {
  const btn = document.querySelector('#login-btn button,#login-btn');
  if(!btn) return;
  const label = btn.innerText.trim();
  if(label === 'Login') {
      const el = document.getElementById('login-start-endpoint');
      const url = el ? el.textContent.trim() : '';
      if(url){ window.location.href = url; }
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
            logout_handler,
            outputs=pages + [login_btn, welcome_display, logout_btn, cookie_html],
        )

        # Initial identity sync (not an OAuth callback—just a passive identity fetch)
        demo.load(
            sync_backend_session_on_load,
            outputs=[battle_btn, login_btn, welcome_display, logout_btn, cookie_html],
            js=cleanup_js,
        )

        # Load public stats on page load (for the first three stats boxes)
        demo.load(
            fn=load_public_stats_on_page_load,
            inputs=None,
            outputs=[
                models_evaluated_column,
                models_evaluated_box,
                total_battles_column,
                total_battles_box,
                total_users_column,
                total_users_box,
            ],
        )

        # Load user stats on page load (for the fourth and fifth stats boxes)
        demo.load(
            fn=load_user_battles_on_page_load,
            inputs=None,
            outputs=[
                user_battles_column,
                user_battles_box,
                user_rank_column,  # New
                user_rank_box,  # New
            ],
        )

        # Load CTA button visibility based on authentication
        demo.load(
            fn=update_cta_buttons_on_page_load,
            outputs=[cta_btn_authenticated, cta_btn_login],
        )

        # (Removed MutationObserver; direct JS click handles login redirect.)

    return demo


def create_app() -> FastAPI:
    """Create and configure the FastAPI application with Gradio mounted.

    Returns:
        Configured FastAPI application
    """
    # Create FastAPI app with health endpoint
    fastapi_app = FastAPI()

    @fastapi_app.get("/health")
    async def health_check():
        """Health check endpoint for ECS"""
        return JSONResponse(
            content={"status": "healthy", "service": "bixarena-app"},
            status_code=200,
        )

    # Build Gradio app and mount it to FastAPI
    demo = build_app()
    # Use unlimited concurrency
    demo.queue(default_concurrency_limit=None)

    # Mount Gradio to the FastAPI app at root path
    fastapi_app = gr.mount_gradio_app(fastapi_app, demo, path="/")

    return fastapi_app


# Create app instance at module level for uvicorn imports
args = parse_args()
app = create_app()
