"""Request-based authentication utilities for Gradio components.

This module provides helpers to check authentication state from Gradio request objects,
enabling components to adapt based on whether the user is logged in.
"""

import gradio as gr

from bixarena_app.auth.user_state import get_user_state


def is_authenticated(request: gr.Request | None) -> bool:
    """Check if the request is from an authenticated user.

    This function only checks if the request has a JSESSIONID cookie,
    making it suitable for use in page load handlers that run before
    UserState is populated.

    For handlers that need verified user info from UserState, use
    is_authenticated_with_state() instead.

    Args:
        request: Gradio request object (can be None)

    Returns:
        True if the request has a JSESSIONID cookie, False otherwise

    Example:
        >>> import gradio as gr
        >>> from bixarena_app.auth.request_auth import is_authenticated
        >>>
        >>> def my_component_handler(request: gr.Request):
        ...     if is_authenticated(request):
        ...         return "Welcome back!"
        ...     else:
        ...         return "Please log in"
    """
    if not request:
        return False

    # Check if request has JSESSIONID cookie
    # This is sufficient to indicate authentication since the cookie
    # will be validated by the API gateway when making API calls
    jsessionid = request.cookies.get("JSESSIONID")
    return jsessionid is not None and jsessionid != ""


def is_authenticated_with_state(request: gr.Request | None) -> bool:
    """Check if the request is from an authenticated user with verified state.

    This function checks both the JSESSIONID cookie AND verifies that
    the global UserState has been populated. Use this when you need
    to access user information from UserState.

    Note: This may return False during initial page load if UserState
    hasn't been populated yet by sync_backend_session_on_load().

    Args:
        request: Gradio request object (can be None)

    Returns:
        True if authenticated and UserState is populated, False otherwise
    """
    if not is_authenticated(request):
        return False

    # Verify against global user state (synced by main.py on page load)
    user_state = get_user_state()
    return user_state.is_authenticated()


def get_username(request: gr.Request | None) -> str | None:
    """Get the username of the authenticated user.

    Args:
        request: Gradio request object (can be None)

    Returns:
        Username if authenticated, None otherwise

    Example:
        >>> from bixarena_app.auth.request_auth import get_username
        >>>
        >>> def greet_user(request: gr.Request):
        ...     username = get_username(request)
        ...     if username:
        ...         return f"Hello, {username}!"
        ...     return "Hello, Guest!"
    """
    if not is_authenticated(request):
        return None

    user_state = get_user_state()
    user = user_state.get_current_user()
    if not user:
        return None

    # Try preferred_username first, then sub (subject)
    return user.get("preferred_username") or user.get("sub")


def get_user_display_name(request: gr.Request | None) -> str:
    """Get the display name of the user (or 'Guest' if not authenticated).

    Args:
        request: Gradio request object (can be None)

    Returns:
        Display name of the user, or 'Guest' if not authenticated

    Example:
        >>> from bixarena_app.auth.request_auth import get_user_display_name
        >>>
        >>> def show_welcome(request: gr.Request):
        ...     name = get_user_display_name(request)
        ...     return f"Welcome, {name}!"
    """
    if not is_authenticated(request):
        return "Guest"

    user_state = get_user_state()
    return user_state.get_display_name()


def get_session_cookie(request: gr.Request | None) -> dict[str, str] | None:
    """Extract the JSESSIONID cookie from the request as a cookie dict.

    Args:
        request: Gradio request object (can be None)

    Returns:
        A cookies dict suitable for API calls, e.g. {"JSESSIONID": "..."}, or None

    Example:
        >>> from bixarena_app.auth.request_auth import get_session_cookie
        >>>
        >>> def make_api_call(request: gr.Request):
        ...     jsessionid = get_session_cookie(request)
        ...     if jsessionid:
        ...         cookies = {"JSESSIONID": jsessionid}
        ...         # Use cookies for API call
    """
    if not request:
        return None

    jsessionid = request.cookies.get("JSESSIONID")
    if jsessionid:
        return {"JSESSIONID": jsessionid}
    return None
