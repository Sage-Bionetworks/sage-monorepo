import logging

import gradio as gr
from bixarena_api_client import UserApi

from bixarena_app.api.api_client_helper import (
    create_authenticated_api_client,
    fetch_public_stats,
)
from bixarena_app.auth.request_auth import get_session_cookie, is_authenticated

logger = logging.getLogger(__name__)


def fetch_user_stats(request: gr.Request):
    """Fetch user's battle statistics from the API.

    Args:
        request: Gradio request object

    Returns:
        UserStats object, or None if not authenticated or error
    """
    try:
        # Check if user is authenticated
        if not is_authenticated(request):
            logger.debug("User not authenticated")
            return None

        # Get session cookie
        cookies = get_session_cookie(request)
        if not cookies:
            logger.debug("No JSESSIONID cookie")
            return None

        # Call API gateway with session cookie
        # The gateway will validate the session and mint a JWT for the API service
        with create_authenticated_api_client(cookies) as client:
            api = UserApi(client)
            user_stats = api.get_user_stats()
            logger.info(
                f"Fetched user stats: {user_stats.completed_battles} completed battles, rank #{user_stats.rank}"
            )
            return user_stats

    except Exception as e:
        logger.error(f"Error fetching user stats: {e}")
        return None


def create_intro_section():
    """Create the intro section of the homepage"""

    with gr.Row():
        with gr.Column():
            gr.HTML("""
            <div style="text-align: center; padding: 45px 20px;">
                <h1 style="font-size: 3rem; margin-bottom: 30px; color: white;">
                    Welcome to BioArena
                </h1>
                <br/>
                <p style="font-size: 1.2rem; line-height: 1.6; max-width: 800px; margin: 0 auto; color: #e5e7eb;">
                    BioArena crowdsources the benchmarking of AI models to unlock the
                    next breakthrough in biomedicine, inviting a global community of
                    digital contributors.
                </p>
            </div>
            <style>
            /* Limit metric box row width */
            #public-stats-row, #user-stats-row {
                max-width: 1000px;
                margin: 0 auto;
            }
            </style>
            """)


def load_public_stats_on_page_load() -> tuple[dict, dict, dict, dict, dict, dict]:
    """Load public stats and update the HTML boxes.

    Returns:
        Tuple of (models_column, models_html, battles_column, battles_html, users_column, users_html) updates
    """
    public_stats = fetch_public_stats()

    models_html = f"""
    <div style="text-align: center; padding: 20px;">
        <p style="color: #9ca3af; text-transform: uppercase; font-size: 0.9rem; margin-bottom: 10px;">
            MODELS EVALUATED
        </p>
        <h2 style="color: #2dd4bf; font-size: 3rem; margin: 0;">{public_stats["models_evaluated"]:,}</h2>
    </div>
    """

    battles_html = f"""
    <div style="text-align: center; padding: 20px;">
        <p style="color: #9ca3af; text-transform: uppercase; font-size: 0.9rem; margin-bottom: 10px;">
            TOTAL BATTLES
        </p>
        <h2 style="color: #2dd4bf; font-size: 3rem; margin: 0;">{public_stats["completed_battles"]:,}</h2>
    </div>
    """

    users_html = f"""
    <div style="text-align: center; padding: 20px;">
        <p style="color: #9ca3af; text-transform: uppercase; font-size: 0.9rem; margin-bottom: 10px;">
            TOTAL USERS
        </p>
        <h2 style="color: #2dd4bf; font-size: 3rem; margin: 0;">{public_stats["total_users"]:,}</h2>
    </div>
    """

    return (
        gr.update(visible=True),  # models_evaluated_column
        gr.update(value=models_html),  # models_evaluated_box
        gr.update(visible=True),  # total_battles_column
        gr.update(value=battles_html),  # total_battles_box
        gr.update(visible=True),  # total_users_column
        gr.update(value=users_html),  # total_users_box
    )


def load_user_battles_on_page_load(
    request: gr.Request,
) -> tuple[dict, dict, dict, dict]:
    """Load user battles and rank data and control column visibility.

    Args:
        request: Gradio request object

    Returns:
        Tuple of (battles_column_update, battles_html_update, rank_column_update, rank_html_update)
    """
    user_stats = fetch_user_stats(request)

    if user_stats is None:
        # Hide both columns when user is not authenticated
        return (
            gr.update(visible=False),
            gr.update(value=""),
            gr.update(visible=False),
            gr.update(value=""),
        )

    # Battles Completed box
    battles_html = f"""
    <div style="text-align: center; padding: 20px;">
        <p style="color: #9ca3af; text-transform: uppercase; font-size: 0.9rem; margin-bottom: 10px;">
            BATTLES COMPLETED
        </p>
        <h2 style="color: #f59e0b; font-size: 3rem; margin: 0;">{user_stats.completed_battles}</h2>
    </div>
    """

    # User Rank box - always show for authenticated users
    rank_html = f"""
    <div style="text-align: center; padding: 20px;">
        <p style="color: #9ca3af; text-transform: uppercase; font-size: 0.9rem; margin-bottom: 10px;">
            YOUR RANK
        </p>
        <h2 style="color: #f59e0b; font-size: 3rem; margin: 0;">#{user_stats.rank}</h2>
    </div>
    """

    return (
        gr.update(visible=True),
        gr.update(value=battles_html),
        gr.update(visible=True),  # Always show rank for authenticated users
        gr.update(value=rank_html),
    )


def update_cta_buttons_on_page_load(request: gr.Request) -> tuple[dict, dict]:
    """Update CTA button visibility based on authentication state.

    Args:
        request: Gradio request object

    Returns:
        Tuple of (authenticated_btn_update, login_btn_update)
    """
    if is_authenticated(request):
        # Show authenticated button, hide login button
        return (gr.update(visible=True), gr.update(visible=False))
    else:
        # Hide authenticated button, show login button
        return (gr.update(visible=False), gr.update(visible=True))


def build_stats_section():
    """Create the statistics section with metrics"""

    # First row: Public stats
    with gr.Row(elem_id="public-stats-row"):
        with gr.Column(visible=False) as models_evaluated_column, gr.Group():
            models_evaluated_box = gr.HTML("")

        with gr.Column(visible=False) as total_battles_column, gr.Group():
            total_battles_box = gr.HTML("")

        with gr.Column(visible=False) as total_users_column, gr.Group():
            total_users_box = gr.HTML("")

    # Second row: User-specific stats (only shown when logged in)
    with gr.Row(elem_id="user-stats-row"):
        with gr.Column(visible=False) as user_battles_column, gr.Group():
            user_battles_box = gr.HTML("")

        # New: User rank box
        with gr.Column(visible=False) as user_rank_column, gr.Group():
            user_rank_box = gr.HTML("")

    return (
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
    )


def build_cta_section():
    """Create the call-to-action section with conditional buttons"""

    # Group button and help message together
    with gr.Column(elem_id="cta-section-group"):
        # Two buttons - one for authenticated, one for unauthenticated users
        # Visibility will be controlled based on authentication state
        with gr.Row(elem_id="cta-button-row"):
            with gr.Column(scale=1, min_width=200, elem_id="cta-button-container"):
                # Button for authenticated users - navigates to battle page
                start_btn_authenticated = gr.Button(
                    "Start Evaluating Models",
                    variant="primary",
                    size="lg",
                    visible=False,
                    elem_id="cta-btn-authenticated",
                )
                # Button for unauthenticated users - redirects to login
                start_btn_login = gr.Button(
                    "Start Evaluating Models",
                    variant="primary",
                    size="lg",
                    visible=False,
                    elem_id="cta-btn-login",
                )

        # Small note below the button
        with gr.Row():
            with gr.Column():
                gr.HTML("""
                <div style="text-align: center; padding-top: 10px;">
                    <div style="display: flex; align-items: center; justify-content: center; gap: 8px; font-size: 0.875rem; color: rgba(229, 231, 235, 0.6);">
                        <div style="width: 6px; height: 6px; border-radius: 50%; background-color: #f97316; animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;"></div>
                        <span>Sign in with your Synapse account to Start a Battle</span>
                    </div>
                </div>
                <style>
                /* Remove gap between CTA button and help message */
                #cta-section-group {
                    gap: 0 !important;
                }
                @keyframes pulse {
                    0%, 100% { opacity: 1; }
                    50% { opacity: 0.5; }
                }
                </style>
                """)

    return start_btn_authenticated, start_btn_login


def build_home_page():
    """Create the complete home page layout"""

    with gr.Column() as home_page, gr.Column():
        # Intro Section
        create_intro_section()

        # Call to Action Section
        start_btn_authenticated, start_btn_login = build_cta_section()

        # Stats Section
        (
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
        ) = build_stats_section()

    return (
        home_page,
        start_btn_authenticated,
        start_btn_login,
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
    )
