import logging

import gradio as gr

from bixarena_api_client import StatsApi, UserApi
from bixarena_app.api.api_client_helper import (
    create_authenticated_api_client,
    get_api_configuration,
)
from bixarena_app.auth.request_auth import is_authenticated, get_session_cookie
from bixarena_api_client.api_client import ApiClient

logger = logging.getLogger(__name__)


def fetch_public_stats() -> dict:
    """Fetch public platform statistics from the API.

    Returns:
        Dictionary with models_evaluated, total_battles, and total_users.
        Returns default values if API call fails.
    """
    try:
        # Create an unauthenticated API client for public endpoint
        # Use the same configuration helper to get the correct API base URL
        configuration = get_api_configuration()
        with ApiClient(configuration) as client:
            api = StatsApi(client)
            stats = api.get_public_stats()
            logger.info(
                f"Fetched public stats: {stats.models_evaluated} models, "
                f"{stats.total_battles} battles, {stats.total_users} users"
            )
            return {
                "models_evaluated": stats.models_evaluated,
                "total_battles": stats.total_battles,
                "total_users": stats.total_users,
            }
    except Exception as e:
        logger.error(f"Error fetching public stats: {e}")
        # Return fallback values if API call fails
        return {
            "models_evaluated": 0,
            "total_battles": 0,
            "total_users": 0,
        }


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
                    Welcome to BixArena
                </h1>
                <p style="font-size: 1.2rem; line-height: 1.6; max-width: 800px; margin: 0 auto; color: #e5e7eb;">
                    Anyone with a computer and an internet connection can evaluate and benchmark Large 
                    Language Models (LLMs) to solve major biomedical problems through a community-driven 
                    platform known as "BixArena." This web-based evaluation arena helps biomedical researchers 
                    compare LLMs on domain-specific tasks, fostering collaboration and establishing best 
                    practices. The top-performing models might just accelerate the next breakthrough in clinical 
                    technology or drug discovery.
                </p>
            </div>
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
        <h2 style="color: #2dd4bf; font-size: 3rem; margin: 0;">{public_stats["total_battles"]:,}</h2>
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


def build_stats_section():
    """Create the statistics section with metrics"""

    # First row: Public stats
    with gr.Row():
        with gr.Column(visible=False) as models_evaluated_column:
            with gr.Group():
                models_evaluated_box = gr.HTML("")

        with gr.Column(visible=False) as total_battles_column:
            with gr.Group():
                total_battles_box = gr.HTML("")

        with gr.Column(visible=False) as total_users_column:
            with gr.Group():
                total_users_box = gr.HTML("")

    # Second row: User-specific stats (only shown when logged in)
    with gr.Row():
        with gr.Column(visible=False) as user_battles_column:
            with gr.Group():
                user_battles_box = gr.HTML("")

        # New: User rank box
        with gr.Column(visible=False) as user_rank_column:
            with gr.Group():
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
    """Create the call-to-action section"""

    with gr.Row():
        with gr.Column():
            gr.HTML("""
            <div style="text-align: center; padding: 40px 20px;">
                <h2 style="font-size: 2rem; margin-bottom: 20px; color: white;">
                    Ready to Shape the Future of Biomedical AI?
                </h2>
                <p style="font-size: 1.1rem; color: #e5e7eb; margin-bottom: 30px;">
                    Join our community of researchers and help evaluate the next generation of AI models for healthcare breakthroughs.
                </p>
            </div>
            """)

    # Button will take to the battle page
    with gr.Row():
        with gr.Column(scale=2):
            pass
        with gr.Column(scale=1):
            start_btn = gr.Button(
                "Start Evaluating Models", variant="primary", size="lg"
            )
        with gr.Column(scale=2):
            pass

    return start_btn


def build_home_page():
    """Create the complete home page layout"""

    with gr.Column() as home_page:
        with gr.Column(elem_classes=["content-wrapper"]):
            # Intro Section
            create_intro_section()

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

            # Call to Action Section
            start_btn = build_cta_section()

    return (
        home_page,
        start_btn,
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
