"""Utilities for configuring BixArena API clients with authentication."""

import os

from bixarena_api_client import ApiClient, Configuration


def get_api_configuration(cookies: dict[str, str] | None = None) -> Configuration:
    """
    Create an API configuration for the BixArena API.

    Args:
        cookies: Optional cookies dict for authenticated requests (e.g., {"JSESSIONID": "..."})

    Returns:
        Configured Configuration object
    """
    api_base = os.environ.get("API_BASE_URL", "http://bixarena-api:8112/v1")
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
