"""Utilities for configuring BixArena API clients with authentication."""

import logging
from datetime import datetime, timezone

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


def fetch_quest(quest_id: str, cookies: dict[str, str] | None = None) -> dict | None:
    """Fetch quest data including posts from the API.

    Args:
        quest_id: The quest identifier (e.g., 'build-bioarena-together')
        cookies: Optional cookies dict for authenticated requests

    Returns:
        Dictionary with quest data or None if quest not found / error.
    """
    try:
        with create_authenticated_api_client(cookies) as client:
            api = QuestApi(client)
            quest = api.get_quest(quest_id)

            # Convert end_date to naive datetime for days_remaining calculation
            end_date = quest.end_date.replace(tzinfo=None)
            now = datetime.now(timezone.utc).replace(tzinfo=None)
            days_remaining = max(1, (end_date - now).days + 1) if now < end_date else 0

            # Calculate progress percentage
            percentage = (
                (quest.total_blocks / quest.goal * 100) if quest.goal > 0 else 0.0
            )

            # Convert posts to dicts
            posts = []
            for post in quest.posts:
                date_str = ""
                if post.var_date is not None:
                    date_str = post.var_date.strftime("%Y-%m-%d")
                posts.append(
                    {
                        "post_index": post.post_index,
                        "date": date_str,
                        "title": post.title,
                        "description": post.description or "",
                        "images": post.images or [],
                        "locked": post.description is None,
                        "required_progress": post.required_progress,
                        "required_tier": post.required_tier,
                    }
                )

            logger.info(
                f"Fetched quest '{quest_id}': {quest.total_blocks}/{quest.goal} blocks, "
                f"{len(posts)} posts"
            )

            return {
                "quest_id": quest.quest_id,
                "title": quest.title,
                "description": quest.description,
                "goal": quest.goal,
                "start_date": quest.start_date.strftime("%Y-%m-%d"),
                "end_date": quest.end_date.strftime("%Y-%m-%d"),
                "active_post_index": quest.active_post_index,
                "total_blocks": quest.total_blocks,
                "posts": posts,
                "progress": {
                    "current_blocks": quest.total_blocks,
                    "goal_blocks": quest.goal,
                    "percentage": percentage,
                    "days_remaining": days_remaining,
                },
                "error": False,
            }
    except NotFoundException:
        logger.warning(f"Quest not found: {quest_id}")
        return None
    except ApiException as e:
        logger.error(f"API error fetching quest (status {e.status}): {e.reason}")
        return None
    except Exception as e:
        logger.error(f"Unexpected error fetching quest: {e}")
        return None


def calculate_quest_progress(quest_data: dict) -> dict:
    """Extract quest progress from quest API data.

    Args:
        quest_data: Dict from fetch_quest() containing quest data with progress.

    Returns:
        Dictionary with:
            - current_blocks: int
            - goal_blocks: int
            - percentage: float (0-100+, can exceed 100%)
            - days_remaining: int
    """
    return quest_data.get(
        "progress",
        {
            "current_blocks": 0,
            "goal_blocks": 0,
            "percentage": 0.0,
            "days_remaining": 0,
        },
    )


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
            result = api.list_quest_contributors(
                quest_id, min_battles=min_battles, limit=limit
            )

            # Group contributors by tier
            contributors_by_tier = {"champion": [], "knight": [], "apprentice": []}

            for contributor in result.contributors:
                tier = contributor.tier.lower()
                if tier in contributors_by_tier:
                    contributors_by_tier[tier].append(
                        {
                            "username": contributor.username,
                            "battle_count": contributor.battle_count,
                            "battles_per_week": contributor.battles_per_week,
                        }
                    )

            logger.info(
                f"Fetched {result.total_contributors} quest contributors: "
                f"{len(contributors_by_tier['champion'])} champions, "
                f"{len(contributors_by_tier['knight'])} knights, "
                f"{len(contributors_by_tier['apprentice'])} apprentices"
            )

            return {
                "contributors_by_tier": contributors_by_tier,
                "total_contributors": result.total_contributors,
                "error": False,
            }
    except NotFoundException:
        # Quest not found - this is expected for invalid quest IDs
        logger.warning(f"Quest not found: {quest_id}")
        return {
            "contributors_by_tier": {"champion": [], "knight": [], "apprentice": []},
            "total_contributors": 0,
            "error": True,
        }
    except ApiException as e:
        # Other API errors (400, 500, etc.)
        logger.error(
            f"API error fetching quest contributors (status {e.status}): {e.reason}"
        )
        return {
            "contributors_by_tier": {"champion": [], "knight": [], "apprentice": []},
            "total_contributors": 0,
            "error": True,
        }
    except Exception as e:
        # Unexpected errors (network, parsing, etc.)
        logger.error(f"Unexpected error fetching quest contributors: {e}")
        return {
            "contributors_by_tier": {"champion": [], "knight": [], "apprentice": []},
            "total_contributors": 0,
            "error": True,
        }
