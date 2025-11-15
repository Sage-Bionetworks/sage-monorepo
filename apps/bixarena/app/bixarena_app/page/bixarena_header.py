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
    <a href='/' style='text-decoration:none;color:inherit;'>🧬 BioArena</a>
  </h1>
</div>
                """
            )
        with gr.Column(scale=1, min_width=180):
            battle_btn = gr.Button("Battle", variant="primary", visible=False)
        with gr.Column(scale=1, min_width=180):
            leaderboard_btn = gr.Button("Leaderboard", variant="secondary")
        with gr.Column(scale=1, min_width=180):
            # Start as Login; value updated by load / callback events
            login_btn = gr.Button("Login", variant="primary", elem_id="login-btn")
    # Minimal CSS styling injection (no JS state toggling):
    gr.HTML(
        """
<style>
#header-row {
  align-items: center;
  padding: 10px 0;
  border-bottom: 2px solid var(--border-color);
  margin-bottom: 20px;
}
#header-row button {
  white-space: nowrap;
}
@media (min-width: 1024px) {
  #header-row {
    flex-wrap: nowrap;
  }
}
</style>
        """
    )
    return header, battle_btn, leaderboard_btn, login_btn


def update_login_button(request: gr.Request | None = None):
    """Return gr.update for login button based on Python-side auth state."""
    state = get_user_state(request)
    if state.is_authenticated():
        return gr.update(value="Logout", variant="secondary")
    return gr.update(value="Login", variant="primary")


def update_battle_button(request: gr.Request | None = None):
    """Return gr.update for battle button based on Python-side auth state.

    The Battle button should only be visible when the user is authenticated.
    """
    state = get_user_state(request)
    if state.is_authenticated():
        return gr.update(visible=True)
    return gr.update(visible=False)


def handle_start_evaluation_click(
    navigator, refresh_prompts_fn, request: gr.Request | None = None
):
    """Handle start evaluation button click with authentication check.

    If authenticated: navigate to battle page
    If not authenticated: stay on home page and return marker for JS redirect

    Returns: list of (*pages, *prompt_outputs, auth_marker)
    """
    state = get_user_state(request)

    if state.is_authenticated():
        # User is authenticated, navigate to battle page (index 1)
        return (
            navigator.show_page(1) + refresh_prompts_fn() + [gr.HTML("AUTHENTICATED")]
        )

    # Not authenticated, stay on home page and return marker for JS to redirect
    return navigator.show_page(0) + refresh_prompts_fn() + [gr.HTML("UNAUTHORIZED")]


def handle_login_click(
    navigator,
    update_login_button,
    update_user_page,
    request: gr.Request | None = None,
):
    """Unified login/logout handler (Option A implementation).

    Returns a fixed tuple shape matching outputs:
    (*pages, login_button_update, user_html, logout_button, cookie_html_script)
    """
    state = get_user_state(request)
    # backend_base retrieval not needed here (handled in main.py login JS)

    # If currently authenticated -> perform logout (server + python) and stay on home
    if state.is_authenticated():
        # Clear python state immediately (browser JS will trigger backend logout)
        username = state.get_display_name()
        state.clear_session()
        print(f"👋 User logged out: {username}")
        logout_script = ""  # client JS handles real logout & reload
        pages = navigator.show_page(0)
        updated_login_btn = update_login_button(request)
        user_html, logout_btn = update_user_page(request)  # will hide logout button
        return (*pages, updated_login_btn, user_html, logout_btn, logout_script)

    # Not authenticated -> JS (attached in main.py) will perform redirect
    redirect_script = ""  # no marker needed now
    pages = navigator.show_page(0)
    interim_btn = gr.update(value="Redirecting…", interactive=False)
    user_html, logout_btn = update_user_page(request)  # still unauthenticated
    return (*pages, interim_btn, user_html, logout_btn, redirect_script)
