"""Utilities for configuring BixArena API clients with authentication."""

import logging

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
