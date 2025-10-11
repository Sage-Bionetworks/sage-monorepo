import os

import gradio as gr

from bixarena_app.auth.auth_service import get_auth_service


def build_header():
    """Build header with navigation and login button"""
    with gr.Row(elem_id="header-row") as header:
        with gr.Column(scale=4):
            gr.HTML("""
                <div style="display: flex; align-items: center; height: 40px;">
                    <h1 style="margin: 0; padding: 0; font-size: 1.5rem;">
                        <a href="/" style="text-decoration: none; color: inherit;">
                            🧬 BixArena
                        </a>
                    </h1>
                </div>
            """)

        with gr.Column(scale=1):
            battle_btn = gr.Button("Battle", variant="secondary")

        with gr.Column(scale=1):
            leaderboard_btn = gr.Button("Leaderboard", variant="secondary")

        with gr.Column(scale=1):
            login_btn = gr.Button(
                "Login",
                variant="primary",
                link="",
                elem_id="login-btn",
            )

        # Inject script: swap Login->Logout if session active
        backend_base = os.environ.get("BACKEND_BASE_URL", "http://127.0.0.1:8112/v1")
        gr.HTML(f"""
                <style>
                #header-row {{
                        align-items: center;
                        padding: 10px 0;
                        border-bottom: 1px solid #e0e0e0;
                        margin-bottom: 20px;
                }}
                </style>
                <script>
                (function() {{
                    const backendBase = '{backend_base}';
                    const startUrl = backendBase + '/auth/oidc/start';
                    const btnWrapper = document.getElementById('login-btn');
                    if(!btnWrapper) return;
                    // Initialize login link
                    const setLogin = () => {{
                        const btn = btnWrapper.querySelector('button') || btnWrapper;
                        if(!btn) return;
                        btn.textContent = 'Login';
                        btn.onclick = null;
                        btn.removeAttribute('data-logout');
                           btn.addEventListener(
                               'click',
                               () => window.location.href = startUrl,
                               {{ once: true }}
                           );
                    }};
                    const setLogout = () => {{
                        const btn = btnWrapper.querySelector('button') || btnWrapper;
                        if(!btn) return;
                        btn.textContent = 'Logout';
                        btn.onclick = null;
                        btn.setAttribute('data-logout', 'true');
                        btn.addEventListener('click', () => {{
                               fetch(backendBase + '/auth/logout', {{
                                   method: 'POST',
                                   credentials: 'include'
                               }}).finally(() => window.location.reload());
                        }}, {{ once: true }});
                    }};
                    // Default to login while we check
                    setLogin();
                    fetch(backendBase + '/echo', {{ credentials: 'include' }})
                        .then(r => r.ok ? r.json() : null)
                        .then(data => {{ if(data && data.sub) setLogout(); }});
                }})();
                </script>
        """)

    return header, battle_btn, leaderboard_btn, login_btn


def update_login_button():
    """Return header button state: Login (with link) or Logout."""
    auth_service = get_auth_service()
    backend_base = os.environ.get("BACKEND_BASE_URL", "http://127.0.0.1:8112/v1")
    start_endpoint = f"{backend_base}/auth/oidc/start"
    if auth_service.is_authenticated():
        return gr.update(value="Logout", link=None, variant="primary")
    else:
        return gr.update(value="Login", link=start_endpoint, variant="primary")


def handle_login_click(navigator, update_login_button, update_user_page):
    """Handle header button: logout if authenticated else go to user page."""
    auth_service = get_auth_service()

    if auth_service.is_authenticated():
        auth_service.logout()
        updated_login_btn = update_login_button()
        user_info = update_user_page()
        return *navigator.show_page(0), updated_login_btn, *user_info

    # Not authenticated: navigate to user page (user page will still show login prompt)
    updated_login_btn = update_login_button()
    user_info = update_user_page()
    return *navigator.show_page(3), updated_login_btn, *user_info
