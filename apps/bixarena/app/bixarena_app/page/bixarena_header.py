import gradio as gr

from bixarena_app.auth.user_state import get_user_state


def build_header():
    """Build header with navigation and login button.

    JS/state logic lives in Gradio callbacks (Option A), so this stays minimal.
    """
    with gr.Row(elem_id="header-row") as header:
        with gr.Column(scale=4):
            gr.HTML(
                """
<div style='display:flex;align-items:center;height:40px;'>
  <h1 style='margin:0;padding:0;font-size:1.5rem;'>
    <a href='/' style='text-decoration:none;color:inherit;'>🧬 BixArena</a>
  </h1>
</div>
                """
            )
        with gr.Column(scale=1):
            battle_btn = gr.Button("Battle", variant="secondary", visible=False)
        with gr.Column(scale=1):
            leaderboard_btn = gr.Button("Leaderboard", variant="secondary")
        with gr.Column(scale=1):
            # Start as Login; value updated by load / callback events
            login_btn = gr.Button("Login", variant="primary", elem_id="login-btn")
    # Minimal CSS styling injection (no JS state toggling):
    gr.HTML(
        """
<style>
#header-row {
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #e0e0e0;
  margin-bottom: 20px;
}
</style>
        """
    )
    return header, battle_btn, leaderboard_btn, login_btn


def update_login_button():
    """Return gr.update for login button based on Python-side auth state."""
    state = get_user_state()
    if state.is_authenticated():
        return gr.update(value="Logout", variant="primary")
    return gr.update(value="Login", variant="primary")


def update_battle_button():
    """Return gr.update for battle button based on Python-side auth state.

    The Battle button should only be visible when the user is authenticated.
    """
    state = get_user_state()
    if state.is_authenticated():
        return gr.update(visible=True)
    return gr.update(visible=False)


def handle_login_click(navigator, update_login_button, update_user_page):
    """Unified login/logout handler (Option A implementation).

    Returns a fixed tuple shape matching outputs:
    (*pages, login_button_update, user_html, logout_button, cookie_html_script)
    """
    state = get_user_state()
    # backend_base retrieval not needed here (handled in main.py login JS)

    # If currently authenticated -> perform logout (server + python) and stay on home
    if state.is_authenticated():
        # Clear python state immediately (browser JS will trigger backend logout)
        username = state.get_display_name()
        state.clear_session()
        print(f"👋 User logged out: {username}")
        logout_script = ""  # client JS handles real logout & reload
        pages = navigator.show_page(0)
        updated_login_btn = update_login_button()
        user_html, logout_btn = update_user_page()  # will hide logout button
        return (*pages, updated_login_btn, user_html, logout_btn, logout_script)

    # Not authenticated -> JS (attached in main.py) will perform redirect
    redirect_script = ""  # no marker needed now
    pages = navigator.show_page(0)
    interim_btn = gr.update(value="Redirecting…", interactive=False)
    user_html, logout_btn = update_user_page()  # still unauthenticated
    return (*pages, interim_btn, user_html, logout_btn, redirect_script)
