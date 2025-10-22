"""Utilities for configuring BixArena API clients with authentication."""

import os

from bixarena_api_client import ApiClient, Configuration


def get_api_configuration(jwt_token: str | None = None) -> Configuration:
    """
    Create an API configuration for the BixArena API.

    Args:
        jwt_token: Optional JWT token for authenticated requests

    Returns:
        Configured Configuration object
    """
    api_base = os.environ.get("API_BASE_URL", "http://bixarena-api:8112/v1")
    configuration = Configuration(host=api_base)

    if jwt_token:
        configuration.access_token = jwt_token

    return configuration


def create_authenticated_api_client(
    jwt_token: str | None = None,
) -> ApiClient:
    """
    Create an authenticated API client.

    Args:
        jwt_token: Optional JWT token for authenticated requests

    Returns:
        Configured ApiClient instance (use in 'with' statement)

    Example:
        >>> from bixarena_app.api.api_client_helper import (
        ...     create_authenticated_api_client
        ... )
        >>> from bixarena_app.auth.jwt_helper import get_jwt_from_request
        >>> from bixarena_api_client import LeaderboardApi
        >>>
        >>> def my_gradio_function(request: gr.Request):
        ...     jwt = get_jwt_from_request(request)
        ...     with create_authenticated_api_client(jwt) as client:
        ...         api = LeaderboardApi(client)
        ...         data = api.get_leaderboard("open-source")
        ...     return data
    """
    configuration = get_api_configuration(jwt_token)
    return ApiClient(configuration)
