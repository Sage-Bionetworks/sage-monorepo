from __future__ import annotations

import threading
import time
from typing import Any

import gradio as gr


class UserState:
    """Minimal in-memory user state tied to a Gradio session."""

    def __init__(self):
        self._current_user = None
        self._session_timestamp = None
        self._error_message = None

    def get_current_user(self) -> dict[str, Any] | None:
        return self._current_user

    def set_current_user(self, user_data: dict[str, Any]) -> None:
        self._current_user = user_data
        self._session_timestamp = time.time()
        self._error_message = None

    def clear_session(self) -> None:
        self._current_user = None
        self._session_timestamp = None
        self._error_message = None

    def is_authenticated(self) -> bool:
        return self.get_current_user() is not None

    def get_display_name(self) -> str:
        user = self.get_current_user()
        if not user:
            return "Guest"
        return user.get("firstName", user.get("userName", "User"))

    def set_error(self, message: str) -> None:
        self._error_message = message

    def get_error(self) -> str | None:
        return self._error_message


_SESSION_STATES: dict[str, UserState] = {}
_SESSION_LOCK = threading.Lock()


def get_user_state(request: gr.Request | None = None) -> UserState:
    """Return the UserState associated with the current Gradio session.

    Uses JSESSIONID cookie as the session key for multi-user isolation.
    If no JSESSIONID is present, returns a fresh user state.
    """
    if not request:
        return UserState()

    jsessionid = request.cookies.get("JSESSIONID")
    if not jsessionid:
        return UserState()

    with _SESSION_LOCK:
        state = _SESSION_STATES.get(jsessionid)
        if state is None:
            state = UserState()
            _SESSION_STATES[jsessionid] = state
        return state
