"""JWT Helper Module for BixArena Gradio App.

This module provides utilities for minting JWTs from session cookies,
enabling the Gradio backend to make authenticated API calls on behalf
of logged-in users.
"""

import logging
import os

import requests

logger = logging.getLogger(__name__)

# Base URL for auth service (server-side rendering)
AUTH_BASE_URL_SSR = os.getenv("AUTH_BASE_URL_SSR", "http://localhost:8115")


def mint_jwt_from_session(jsessionid: str) -> str | None:
    """Mint a JWT token from a session cookie.

    Args:
        jsessionid: The JSESSIONID cookie value from the user's session

    Returns:
        The JWT access token string, or None if minting failed

    Raises:
        None - logs errors but returns None on failure
    """
    if not jsessionid:
        logger.warning("Cannot mint JWT: JSESSIONID is empty")
        return None

    try:
        token_url = f"{AUTH_BASE_URL_SSR}/oauth2/token"
        cookies = {"JSESSIONID": jsessionid}

        logger.debug(f"Minting JWT from session at {token_url}")
        response = requests.post(token_url, cookies=cookies, timeout=5)

        if response.status_code == 200:
            token_data = response.json()
            access_token = token_data.get("access_token")
            logger.info("Successfully minted JWT from session")
            return access_token
        else:
            logger.error(
                f"Failed to mint JWT: HTTP {response.status_code} - {response.text}"
            )
            return None

    except requests.exceptions.RequestException as e:
        logger.error(f"Error minting JWT from session: {e}")
        return None


def get_jwt_from_request(request) -> str | None:
    """Extract JSESSIONID from a Gradio request and mint a JWT.

    This is a convenience function for Gradio event handlers that
    automatically extracts the session cookie and mints a JWT.

    Args:
        request: The Gradio request object (gr.Request)

    Returns:
        The JWT access token string, or None if extraction/minting failed

    Example:
        ```python
        import gradio as gr
        from bixarena_app.auth.jwt_helper import get_jwt_from_request

        def my_gradio_function(input_data, request: gr.Request):
            jwt_token = get_jwt_from_request(request)
            if jwt_token:
                # Use JWT to call authenticated API
                api_client = create_authenticated_api_client(jwt_token)
                # ... make API calls
            else:
                # Handle unauthenticated case
                pass
        ```
    """
    if not request:
        logger.warning("Cannot extract JWT: request is None")
        return None

    # Extract JSESSIONID from cookies
    cookies = getattr(request, "cookies", {})
    jsessionid = cookies.get("JSESSIONID")

    if not jsessionid:
        logger.warning("No JSESSIONID found in request cookies")
        return None

    return mint_jwt_from_session(jsessionid)
