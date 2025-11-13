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


def load_public_stats_on_page_load() -> dict:
    """Load public stats and update the stats bar HTML.

    Returns:
        HTML update for the stats container
    """
    public_stats = fetch_public_stats()

    stats_html = f"""
    <div id="stats-public-only">
        <div style="display: flex; flex-wrap: wrap; align-items: center; justify-content: center; gap: 3rem;">
            <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                <div style="font-size: 1.5rem; color: #2dd4bf;">{public_stats["models_evaluated"]:,}</div>
                <div style="font-size: 0.875rem; color: rgba(229, 231, 235, 0.5);">Models Evaluated</div>
            </div>
            <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                <div style="font-size: 1.5rem; color: #2dd4bf;">{public_stats["completed_battles"]:,}</div>
                <div style="font-size: 0.875rem; color: rgba(229, 231, 235, 0.5);">Total Battles</div>
            </div>
            <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                <div style="font-size: 1.5rem; color: #2dd4bf;">{public_stats["total_users"]:,}</div>
                <div style="font-size: 0.875rem; color: rgba(229, 231, 235, 0.5);">Total Users</div>
            </div>
        </div>
    </div>
    """

    return gr.update(value=stats_html)


def load_user_battles_on_page_load(
    request: gr.Request,
) -> dict:
    """Load user battles and rank data and update stats bar to include user stats.

    Args:
        request: Gradio request object

    Returns:
        HTML update for the stats container with user stats included
    """
    user_stats = fetch_user_stats(request)
    public_stats = fetch_public_stats()

    if user_stats is None:
        # Return only public stats when user is not authenticated
        stats_html = f"""
        <div id="stats-public-only">
            <div style="display: flex; flex-wrap: wrap; align-items: center; justify-content: center; gap: 3rem;">
                <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                    <div style="font-size: 1.5rem; color: #2dd4bf;">{public_stats["models_evaluated"]:,}</div>
                    <div style="font-size: 0.875rem; color: rgba(229, 231, 235, 0.5);">Models Evaluated</div>
                </div>
                <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                    <div style="font-size: 1.5rem; color: #2dd4bf;">{public_stats["completed_battles"]:,}</div>
                    <div style="font-size: 0.875rem; color: rgba(229, 231, 235, 0.5);">Total Battles</div>
                </div>
                <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                    <div style="font-size: 1.5rem; color: #2dd4bf;">{public_stats["total_users"]:,}</div>
                    <div style="font-size: 0.875rem; color: rgba(229, 231, 235, 0.5);">Total Users</div>
                </div>
            </div>
        </div>
        """
        return gr.update(value=stats_html)

    # Return stats with user data included
    stats_html = f"""
    <div id="stats-with-user">
        <div style="display: flex; flex-wrap: wrap; align-items: center; justify-content: center; gap: 3rem;">
            <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                <div style="font-size: 1.5rem; color: #2dd4bf;">{public_stats["models_evaluated"]:,}</div>
                <div style="font-size: 0.875rem; color: rgba(229, 231, 235, 0.5);">Models Evaluated</div>
            </div>
            <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                <div style="font-size: 1.5rem; color: #2dd4bf;">{public_stats["completed_battles"]:,}</div>
                <div style="font-size: 0.875rem; color: rgba(229, 231, 235, 0.5);">Total Battles</div>
            </div>
            <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                <div style="font-size: 1.5rem; color: #2dd4bf;">{public_stats["total_users"]:,}</div>
                <div style="font-size: 0.875rem; color: rgba(229, 231, 235, 0.5);">Total Users</div>
            </div>
            <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                <div style="font-size: 1.5rem; color: #f97316;">{user_stats.completed_battles:,}</div>
                <div style="font-size: 0.875rem; color: rgba(229, 231, 235, 0.5);">Battles Completed</div>
            </div>
            <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                <div style="font-size: 1.5rem; color: #f97316;">#{user_stats.rank:,}</div>
                <div style="font-size: 0.875rem; color: rgba(229, 231, 235, 0.5);">Your Rank</div>
            </div>
        </div>
    </div>
    """

    return gr.update(value=stats_html)


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
    """Create the statistics section as a horizontal bar with metrics"""

    # Single horizontal stats bar
    stats_container = gr.HTML(
        "", elem_id="stats-bar-container", elem_classes=["stats-bar"]
    )

    # Add custom CSS for the stats bar
    gr.HTML("""
    <style>
    #stats-bar-container {
        border-top: 1px solid rgba(255, 255, 255, 0.1);
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        background-color: rgba(255, 255, 255, 0.02);
        padding: 1.5rem 1rem;
    }
    </style>
    """)

    return stats_container


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

        # Stats Section (single horizontal bar)
        stats_container = build_stats_section()

    return (
        home_page,
        start_btn_authenticated,
        start_btn_login,
        stats_container,
    )
