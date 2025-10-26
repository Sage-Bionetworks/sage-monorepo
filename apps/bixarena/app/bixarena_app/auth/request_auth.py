"""Request-based authentication utilities for Gradio components.

This module provides helpers to check authentication state from Gradio request objects,
enabling components to adapt based on whether the user is logged in.
"""

import gradio as gr

from bixarena_app.auth.user_state import get_user_state


def is_authenticated(request: gr.Request | None) -> bool:
    """Check if the request is from an authenticated user.

    This checks both:
    1. If the request has a valid JSESSIONID cookie
    2. If the global UserState indicates an authenticated session

    Args:
        request: Gradio request object (can be None)

    Returns:
        True if the user is authenticated, False otherwise

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
    jsessionid = request.cookies.get("JSESSIONID")
    if not jsessionid:
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


def get_session_cookie(request: gr.Request | None) -> str | None:
    """Extract the JSESSIONID cookie from the request.

    Args:
        request: Gradio request object (can be None)

    Returns:
        JSESSIONID value if present, None otherwise

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

    return request.cookies.get("JSESSIONID")
