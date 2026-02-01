"""Utilities for configuring BixArena API clients with authentication."""

import logging
from datetime import datetime

from bixarena_api_client import ApiClient, Configuration, QuestApi, StatsApi
from bixarena_api_client.exceptions import ApiException, NotFoundException

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

    Quest configuration is imported from QUEST_CONFIG in bixarena_quest_section.

    Returns:
        Dictionary with:
            - current_blocks: int (total battles completed)
            - goal_blocks: int (from QUEST_CONFIG)
            - percentage: float (0-100+, can exceed 100%)
            - days_remaining: int (calculated from end date)
    """
    # Import quest configuration
    from bixarena_app.page.bixarena_quest_section import QUEST_CONFIG

    QUEST_END_DATE = datetime.strptime(QUEST_CONFIG["end_date"], "%Y-%m-%d")
    QUEST_GOAL_BLOCKS = QUEST_CONFIG["goal"]

    # Fetch current battle count (represents blocks placed)
    stats = fetch_public_stats()
    current_blocks = stats["completed_battles"]

    # Calculate percentage (allow >100% if goal exceeded)
    percentage = (
        (current_blocks / QUEST_GOAL_BLOCKS * 100) if QUEST_GOAL_BLOCKS > 0 else 0.0
    )

    # Calculate days remaining (add 1 to show "1 day left" on the final day)
    now = datetime.now()
    days_remaining = (
        max(1, (QUEST_END_DATE - now).days + 1) if now < QUEST_END_DATE else 0
    )

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


def fetch_quest_contributors(
    quest_id: str, min_battles: int = 1, limit: int = 100
) -> dict:
    """Fetch contributors for a specific quest from the API.

    Args:
        quest_id: The quest identifier (e.g., 'build-bioarena-together')
        min_battles: Minimum number of battles required to be listed (default: 1)
        limit: Maximum number of contributors to return (default: 100)

    Returns:
        Dictionary with:
            - contributors_by_rank: dict with 'champion', 'knight', 'apprentice' lists
            - total_contributors: int
            - error: bool indicating if there was an error (quest not found, etc.)
            Returns empty structure with error=True if API call fails.
    """
    try:
        configuration = get_api_configuration()
        with ApiClient(configuration) as client:
            api = QuestApi(client)
            result = api.get_quest_contributors(
                quest_id, min_battles=min_battles, limit=limit
            )

            # Group contributors by rank
            contributors_by_rank = {"champion": [], "knight": [], "apprentice": []}

            for contributor in result.contributors:
                rank = contributor.rank.lower()
                if rank in contributors_by_rank:
                    contributors_by_rank[rank].append(
                        {
                            "username": contributor.username,
                            "battle_count": contributor.battle_count,
                            "battles_per_week": contributor.battles_per_week,
                        }
                    )

            logger.info(
                f"Fetched {result.total_contributors} quest contributors: "
                f"{len(contributors_by_rank['champion'])} champions, "
                f"{len(contributors_by_rank['knight'])} knights, "
                f"{len(contributors_by_rank['apprentice'])} apprentices"
            )

            return {
                "contributors_by_rank": contributors_by_rank,
                "total_contributors": result.total_contributors,
                "error": False,
            }
    except NotFoundException:
        # Quest not found - this is expected for invalid quest IDs
        logger.warning(f"Quest not found: {quest_id}")
        return {
            "contributors_by_rank": {"champion": [], "knight": [], "apprentice": []},
            "total_contributors": 0,
            "error": True,
        }
    except ApiException as e:
        # Other API errors (400, 500, etc.)
        logger.error(
            f"API error fetching quest contributors (status {e.status}): {e.reason}"
        )
        return {
            "contributors_by_rank": {"champion": [], "knight": [], "apprentice": []},
            "total_contributors": 0,
            "error": True,
        }
    except Exception as e:
        # Unexpected errors (network, parsing, etc.)
        logger.error(f"Unexpected error fetching quest contributors: {e}")
        return {
            "contributors_by_rank": {"champion": [], "knight": [], "apprentice": []},
            "total_contributors": 0,
            "error": True,
        }
