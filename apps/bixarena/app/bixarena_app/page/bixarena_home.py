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
    from pathlib import Path

    # Get absolute path to logo for Gradio's static file serving
    logo_path = Path(__file__).parent.parent / "assets" / "bioarena-logo.svg"
    logo_path_str = str(logo_path.resolve())

    with gr.Row():
        with gr.Column():
            gr.HTML(f"""
            <div style="display: flex; align-items: center; justify-content: center; gap: 3rem; padding: 2.5rem 1.5rem; flex-wrap: wrap;">
                <!-- Text Content -->
                <div style="flex: 1; min-width: 300px; max-width: 600px;">
                    <p style="font-size: var(--text-xl); margin-bottom: 1.5rem; color: var(--body-text-color-subdued);">
                        Welcome to BioArena
                    </p>
                    <h1 style="font-size: var(--text-hero-title); margin-bottom: 2rem; color: var(--body-text-color); line-height: 1.2;">
                        Drive the next wave of biomedical breakthroughs
                    </h1>

                    <p style="font-size: var(--text-xl); line-height: 1.75; color: var(--body-text-color-subdued);">
                        BioArena crowdsources the benchmarking of AI models to unlock the
                        next breakthrough in biomedicine, inviting a global community of
                        digital contributors.
                    </p>
                </div>

                <!-- Fox Logo -->
                <div style="flex: 0 0 auto; display: flex; align-items: center; justify-content: center;">
                    <img src='/gradio_api/file={logo_path_str}' alt='BioArena Fox' style='width: 280px; height: auto; max-width: 100%;'>
                </div>
            </div>
            <style>
            /* Limit metric box row width */
            #public-stats-row, #user-stats-row {{
                max-width: 1000px;
                margin: 0 auto;
            }}
            /* Responsive layout for smaller screens */
            @media (max-width: 768px) {{
                .intro-section {{
                    flex-direction: column-reverse !important;
                }}
            }}
            </style>
            """)


def load_public_stats_on_page_load() -> dict:
    """Load public stats and update the stats bar HTML.

    Returns:
        HTML update for the stats container
    """
    public_stats = fetch_public_stats()

    stats_html = f"""
    <div style="border: 1px solid var(--border-color-primary); border-radius: 8px; background-color: var(--panel-background-fill); padding: 2.5rem 1.5rem; margin: 0 1.5rem;">
        <div id="stats-public-only">
            <div style="display: flex; flex-wrap: wrap; align-items: center; justify-content: center; gap: 3rem;">
                <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                    <div style="font-size: 3.5rem; font-weight: 600; color: var(--accent-teal);">{public_stats["models_evaluated"]:,}</div>
                    <div style="color: var(--body-text-color-subdued);">Models Evaluated</div>
                </div>
                <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                    <div style="font-size: 3.5rem; font-weight: 600; color: var(--accent-teal);">{public_stats["completed_battles"]:,}</div>
                    <div style="color: var(--body-text-color-subdued);">Total Battles</div>
                </div>
                <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                    <div style="font-size: 3.5rem; font-weight: 600; color: var(--accent-teal);">{public_stats["total_users"]:,}</div>
                    <div style="color: var(--body-text-color-subdued);">Total Users</div>
                </div>
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
        <div style="border: 1px solid var(--border-color-primary); border-radius: 8px; background-color: var(--panel-background-fill); padding: 2.5rem 1.5rem; margin: 0 1.5rem;">
            <div id="stats-public-only">
                <div style="display: flex; flex-wrap: wrap; align-items: center; justify-content: center; gap: 3rem;">
                    <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                        <div style="font-size: 3.5rem; font-weight: 600; color: var(--accent-teal);">{public_stats["models_evaluated"]:,}</div>
                        <div style="color: var(--body-text-color-subdued);">Models Evaluated</div>
                    </div>
                    <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                        <div style="font-size: 3.5rem; font-weight: 600; color: var(--accent-teal);">{public_stats["completed_battles"]:,}</div>
                        <div style="color: var(--body-text-color-subdued);">Total Battles</div>
                    </div>
                    <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                        <div style="font-size: 3.5rem; font-weight: 600; color: var(--accent-teal);">{public_stats["total_users"]:,}</div>
                        <div style="color: var(--body-text-color-subdued);">Total Users</div>
                    </div>
                </div>
            </div>
        </div>
        """
        return gr.update(value=stats_html)

    # Return stats with user data included
    stats_html = f"""
    <div style="border: 1px solid var(--border-color-primary); border-radius: 8px; background-color: var(--panel-background-fill); padding: 2.5rem 1.5rem; margin: 0 1.5rem;">
        <div id="stats-with-user">
            <div style="display: flex; flex-wrap: wrap; align-items: center; justify-content: center; gap: 3rem;">
                <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                    <div style="font-size: 3.5rem; font-weight: 600; color: var(--accent-teal);">{public_stats["models_evaluated"]:,}</div>
                    <div style="color: var(--body-text-color-subdued);">Models Evaluated</div>
                </div>
                <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                    <div style="font-size: 3.5rem; font-weight: 600; color: var(--accent-teal);">{public_stats["completed_battles"]:,}</div>
                    <div style="color: var(--body-text-color-subdued);">Total Battles</div>
                </div>
                <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                    <div style="font-size: 3.5rem; font-weight: 600; color: var(--accent-teal);">{public_stats["total_users"]:,}</div>
                    <div style="color: var(--body-text-color-subdued);">Total Users</div>
                </div>
                <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                    <div style="font-size: 3.5rem; font-weight: 600; color: var(--color-accent);">{user_stats.completed_battles:,}</div>
                    <div style="color: var(--body-text-color-subdued);">Battles Completed</div>
                </div>
                <div style="display: flex; flex-direction: column; align-items: center; gap: 0.25rem;">
                    <div style="font-size: 3.5rem; font-weight: 600; color: var(--color-accent);">#{user_stats.rank:,}</div>
                    <div style="color: var(--body-text-color-subdued);">Your Rank</div>
                </div>
            </div>
        </div>
    </div>
    """

    return gr.update(value=stats_html)


def update_cta_buttons_on_page_load(
    request: gr.Request,
) -> tuple[dict, dict, dict]:
    """Update CTA button visibility based on authentication state.

    Args:
        request: Gradio request object

    Returns:
        Tuple of (authenticated_btn_update, login_btn_update, cta_helper_msg_update)
    """
    if is_authenticated(request):
        # Show authenticated button, hide login button and CTA helper message
        return (
            gr.update(visible=True),
            gr.update(visible=False),
            gr.update(visible=False),
        )
    else:
        # Hide authenticated button, show login button and CTA helper message
        return (
            gr.update(visible=False),
            gr.update(visible=True),
            gr.update(visible=True),
        )


def build_stats_section():
    """Create the statistics section as a horizontal bar with metrics"""

    # Single horizontal stats bar
    stats_container = gr.HTML(
        "", elem_id="stats-bar-container", elem_classes=["stats-bar"]
    )

    return stats_container


def build_how_it_works_section():
    """Create the How It Works section explaining the battle mode process"""

    gr.HTML("""
    <div style="padding: 2.5rem 1.5rem;">
        <!-- Section Header -->
        <div style="text-align: center; margin-bottom: 3rem;">
            <h1 style="font-size: var(--text-section-title); color: var(--body-text-color); margin-bottom: 0.75rem; font-weight: 600;">
                Arena Rules
            </h1>
            <p style="color: var(--body-text-color-subdued); font-size: var(--text-xl); max-width: 48rem; margin: 0 auto;">
                Simple evaluation, powerful impact
            </p>
        </div>

        <!-- Steps Grid -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(min(100%, 250px), 1fr)); gap: 1.25rem;">
            <!-- Step 01 -->
            <div style="position: relative; height: 100%; padding: 1.5rem; border-radius: 0.5rem; background-color: var(--panel-background-fill); border: 1px solid var(--border-color-primary); transition: all 0.3s ease;">
                <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem;">
                    <div style="display: inline-flex; align-items: center; justify-content: center; width: 2.5rem; height: 2.5rem; flex-shrink: 0; border-radius: 50%; background: color-mix(in srgb, var(--color-accent) 10%, transparent); border: 1px solid color-mix(in srgb, var(--color-accent) 30%, transparent);">
                        <span style="color: var(--color-accent); font-weight: 600;">01</span>
                    </div>
                    <h3 style="color: var(--body-text-color); margin: 0; font-size: var(--text-lg); font-weight: 600;">
                        Start a Battle
                    </h3>
                </div>
                <p style="color: var(--body-text-color-subdued); line-height: 1.5; margin: 0;">
                    Pick a curated biomedical question or submit your own custom prompt. Two AI models are randomly chosen to face off.
                </p>
            </div>

            <!-- Step 02 -->
            <div style="position: relative; height: 100%; padding: 1.5rem; border-radius: 0.5rem; background-color: var(--panel-background-fill); border: 1px solid var(--border-color-primary); transition: all 0.3s ease;">
                <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem;">
                    <div style="display: inline-flex; align-items: center; justify-content: center; width: 2.5rem; height: 2.5rem; flex-shrink: 0; border-radius: 50%; background: color-mix(in srgb, var(--color-accent) 10%, transparent); border: 1px solid color-mix(in srgb, var(--color-accent) 30%, transparent);">
                        <span style="color: var(--color-accent); font-weight: 600;">02</span>
                    </div>
                    <h3 style="color: var(--body-text-color); margin: 0; font-size: var(--text-lg); font-weight: 600;">
                        Compare Responses
                    </h3>
                </div>
                <p style="color: var(--body-text-color-subdued); line-height: 1.5; margin: 0;">
                    Review the two AI-generated answers side by side. The models remain anonymous so you can focus purely on the quality of the content.
                </p>
            </div>

            <!-- Step 03 -->
            <div style="position: relative; height: 100%; padding: 1.5rem; border-radius: 0.5rem; background-color: var(--panel-background-fill); border: 1px solid var(--border-color-primary); transition: all 0.3s ease;">
                <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem;">
                    <div style="display: inline-flex; align-items: center; justify-content: center; width: 2.5rem; height: 2.5rem; flex-shrink: 0; border-radius: 50%; background: color-mix(in srgb, var(--color-accent) 10%, transparent); border: 1px solid color-mix(in srgb, var(--color-accent) 30%, transparent);">
                        <span style="color: var(--color-accent); font-weight: 600;">03</span>
                    </div>
                    <h3 style="color: var(--body-text-color); margin: 0; font-size: var(--text-lg); font-weight: 600;">
                        Select the Better
                    </h3>
                </div>
                <p style="color: var(--body-text-color-subdued); line-height: 1.5; margin: 0;">
                    Decide which model demonstrates clearer reasoning or insight. Your input directly shapes model performance metrics.
                </p>
            </div>

            <!-- Step 04 -->
            <div style="position: relative; height: 100%; padding: 1.5rem; border-radius: 0.5rem; background-color: var(--panel-background-fill); border: 1px solid var(--border-color-primary); transition: all 0.3s ease;">
                <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem;">
                    <div style="display: inline-flex; align-items: center; justify-content: center; width: 2.5rem; height: 2.5rem; flex-shrink: 0; border-radius: 50%; background: color-mix(in srgb, var(--color-accent) 10%, transparent); border: 1px solid color-mix(in srgb, var(--color-accent) 30%, transparent);">
                        <span style="color: var(--color-accent); font-weight: 600;">04</span>
                    </div>
                    <h3 style="color: var(--body-text-color); margin: 0; font-size: var(--text-lg); font-weight: 600;">
                        Reveal & Impact
                    </h3>
                </div>
                <p style="color: var(--body-text-color-subdued); line-height: 1.5; margin: 0;">
                    Once you've made your choice, the models are revealed. Only biomedical battles count toward the daily leaderboard. Ready for another round? Jump into your next battle.
                </p>
            </div>
        </div>
    </div>
    """)


def build_cta_section():
    """Create the call-to-action section with conditional buttons"""

    with gr.Column(elem_id="cta-section-wrapper"):
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

            # Small note below the button - only shown for non-authenticated users
            with gr.Row():
                with gr.Column():
                    cta_helper_msg = gr.HTML(
                        """
                        <div style="text-align: center; padding: 10px 1.5rem 0 1.5rem;">
                            <div style="display: flex; align-items: center; justify-content: center; gap: 8px; font-size: var(--text-sm); opacity: 0.6;">
                                <div style="width: 6px; height: 6px; border-radius: 50%; background-color: var(--color-accent); animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite; opacity: 1;"></div>
                                <span>Sign in with your Synapse account to Start a Battle</span>
                            </div>
                        </div>
                        <style>
                        /* CTA section wrapper with consistent bottom padding */
                        #cta-section-wrapper {
                            padding: 0 1.5rem 2.5rem 1.5rem;
                            flex-grow: unset !important;
                        }
                        /* Remove gap between CTA button and help message */
                        #cta-section-group {
                            gap: 0 !important;
                        }
                        @keyframes pulse {
                            0%, 100% { opacity: 1; }
                            50% { opacity: 0.5; }
                        }
                        </style>
                        """,
                        visible=False,
                    )

    return start_btn_authenticated, start_btn_login, cta_helper_msg


def build_home_page():
    """Create the complete home page layout"""

    with gr.Column() as home_page, gr.Column():
        # Intro Section
        create_intro_section()

        # Call to Action Section
        start_btn_authenticated, start_btn_login, cta_helper_msg = build_cta_section()

        # Stats Section (single horizontal bar)
        stats_container = build_stats_section()

        # How It Works Section
        build_how_it_works_section()

    return (
        home_page,
        start_btn_authenticated,
        start_btn_login,
        cta_helper_msg,
        stats_container,
    )
