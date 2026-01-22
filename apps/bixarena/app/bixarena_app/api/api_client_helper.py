"""Utilities for configuring BixArena API clients with authentication."""

import logging
from datetime import datetime, timedelta

from bixarena_api_client import ApiClient, Configuration, StatsApi

from bixarena_app.config.utils import _get_api_base_url

logger = logging.getLogger(__name__)


def get_api_configuration(cookies: dict[str, str] | None = None) -> Configuration:
    """
    Create an API configuration for the BixArena API.

    Args:
        cookies: Optional cookies dict for authenticated requests (e.g., {"JSESSIONID": "..."})

    Returns:
        Configured Configuration object
    """
    api_base = _get_api_base_url()
    if not api_base:
        raise RuntimeError("API_BASE_URL not configured")
    configuration = Configuration(host=api_base)

    # Store cookies in configuration for later use
    if cookies:
        configuration.api_key = cookies  # Temporarily store in api_key dict

    return configuration


def create_authenticated_api_client(
    cookies: dict[str, str] | None = None,
) -> ApiClient:
    """
    Create an authenticated API client that uses session cookies.

    The client sends the JSESSIONID cookie to the API gateway, which
    validates the session and mints a JWT for backend service requests.

    Args:
        cookies: Optional cookies dict for authenticated requests (e.g., {"JSESSIONID": "..."})

    Returns:
        Configured ApiClient instance (use in 'with' statement)

    Example:
        >>> from bixarena_app.api.api_client_helper import (
        ...     create_authenticated_api_client
        ... )
        >>> from bixarena_api_client import LeaderboardApi
        >>>
        >>> def my_gradio_function(request: gr.Request):
        ...     cookies = {"JSESSIONID": request.cookies.get("JSESSIONID")}
        ...     with create_authenticated_api_client(cookies) as client:
        ...         api = LeaderboardApi(client)
        ...         data = api.get_leaderboard("open-source")
        ...     return data
    """
    configuration = get_api_configuration(cookies)
    client = ApiClient(configuration)

    # Configure the REST client to send cookies as a Cookie header
    if cookies:
        cookie_header = "; ".join([f"{k}={v}" for k, v in cookies.items()])
        client.set_default_header("Cookie", cookie_header)

    return client


def fetch_public_stats() -> dict:
    """Fetch public platform statistics from the API.

    Returns:
        Dictionary with models_evaluated, completed_battles, and total_users.
        Returns default values if API call fails.
    """
    try:
        configuration = get_api_configuration()
        with ApiClient(configuration) as client:
            api = StatsApi(client)
            stats = api.get_public_stats()
            logger.info(
                f"Fetched public stats: {stats.models_evaluated} models, "
                f"{stats.completed_battles} battles, {stats.total_users} users"
            )
            return {
                "models_evaluated": stats.models_evaluated,
                "completed_battles": stats.completed_battles,
                "total_users": stats.total_users,
            }
    except Exception as e:
        logger.error(f"Error fetching public stats: {e}")
        # Return fallback values if API call fails
        return {
            "models_evaluated": 0,
            "completed_battles": 0,
            "total_users": 0,
        }


def calculate_quest_progress() -> dict:
    """Calculate Community Quest progress using existing public stats.

    Uses the existing fetch_public_stats() to get total battles completed,
    which represents blocks placed in the Minecraft arena (1 battle = 1 block).

    Quest configuration (hardcoded for Season 1):
    - Start date: February 1, 2026
    - Duration: 90 days
    - Goal: 2,850 blocks

    Returns:
        Dictionary with:
            - current_blocks: int (total battles completed)
            - goal_blocks: int (hardcoded 2850)
            - percentage: float (0-100+, can exceed 100%)
            - days_remaining: int (calculated from start date + 90 days)
    """
    # Hardcoded quest configuration
    QUEST_START_DATE = datetime(2026, 2, 1)
    QUEST_DURATION_DAYS = 90
    QUEST_GOAL_BLOCKS = 2850

    # Fetch current battle count (represents blocks placed)
    stats = fetch_public_stats()
    current_blocks = stats["completed_battles"]

    # Calculate percentage (allow >100% if goal exceeded)
    percentage = (
        (current_blocks / QUEST_GOAL_BLOCKS * 100) if QUEST_GOAL_BLOCKS > 0 else 0.0
    )

    # Calculate days remaining
    quest_end_date = QUEST_START_DATE + timedelta(days=QUEST_DURATION_DAYS)
    now = datetime.now()
    days_remaining = max(0, (quest_end_date - now).days)

    logger.info(
        f"Quest progress: {current_blocks}/{QUEST_GOAL_BLOCKS} blocks "
        f"({percentage:.1f}%), {days_remaining} days remaining"
    )

    return {
        "current_blocks": current_blocks,
        "goal_blocks": QUEST_GOAL_BLOCKS,
        "percentage": percentage,
        "days_remaining": days_remaining,
    }
