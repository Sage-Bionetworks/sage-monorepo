import logging

import gradio as gr

from bixarena_api_client import UserApi
from bixarena_app.api.api_client_helper import create_authenticated_api_client

logger = logging.getLogger(__name__)


def fetch_user_stats(request: gr.Request) -> int | None:
    """Fetch user's battle statistics from the API.

    Args:
        request: Gradio request object

    Returns:
        Total number of battles arbitrated, or None if not authenticated or error
    """
    try:
        # Extract session cookie from request
        jsessionid = request.cookies.get("JSESSIONID") if request else None
        if not jsessionid:
            logger.debug("No JSESSIONID cookie, user not authenticated")
            return None

        # Call API gateway with session cookie
        # The gateway will validate the session and mint a JWT for the API service
        cookies = {"JSESSIONID": jsessionid}
        with create_authenticated_api_client(cookies) as client:
            api = UserApi(client)
            user_stats = api.get_user_stats()
            logger.info(f"Fetched user stats: {user_stats.total_battles} battles")
            return user_stats.total_battles

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


def render_user_battles_box(total_battles: int | None) -> str:
    """Render the user's battles arbitrated box.

    Args:
        total_battles: Number of battles arbitrated, or None if not authenticated

    Returns:
        HTML string for the box, or empty string if not authenticated
    """
    if total_battles is None:
        return ""

    return f"""
    <div style="text-align: center; padding: 20px;">
        <p style="color: #9ca3af; text-transform: uppercase; font-size: 0.9rem; margin-bottom: 10px;">
            BATTLES ARBITRATED
        </p>
        <h2 style="color: #f59e0b; font-size: 3rem; margin: 0;">{total_battles}</h2>
    </div>
    """


def build_stats_section():
    """Create the statistics section with metrics"""

    with gr.Row():
        with gr.Column():
            with gr.Group():
                gr.HTML("""
                <div style="text-align: center; padding: 20px;">
                    <p style="color: #9ca3af; text-transform: uppercase; font-size: 0.9rem; margin-bottom: 10px;">
                        MODELS SUPPORTED
                    </p>
                    <h2 style="color: #2dd4bf; font-size: 3rem; margin: 0;">15</h2>
                </div>
                """)

        with gr.Column():
            with gr.Group():
                gr.HTML("""
                <div style="text-align: center; padding: 20px;">
                    <p style="color: #9ca3af; text-transform: uppercase; font-size: 0.9rem; margin-bottom: 10px;">
                        VOTES COLLECTED
                    </p>
                    <h2 style="color: #2dd4bf; font-size: 3rem; margin: 0;">12,500</h2>
                </div>
                """)

        with gr.Column():
            with gr.Group():
                gr.HTML("""
                <div style="text-align: center; padding: 20px;">
                    <p style="color: #9ca3af; text-transform: uppercase; font-size: 0.9rem; margin-bottom: 10px;">
                        PARTICIPANTS
                    </p>
                    <h2 style="color: #2dd4bf; font-size: 3rem; margin: 0;">450</h2>
                </div>
                """)

        # Fourth box: User's battles arbitrated (only shown when logged in)
        with gr.Column():
            with gr.Group():
                user_battles_box = gr.HTML("", visible=True)

    return user_battles_box


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
            user_battles_box = build_stats_section()

            # Call to Action Section
            start_btn = build_cta_section()

    return home_page, start_btn, user_battles_box
