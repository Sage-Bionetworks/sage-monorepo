import time
from typing import Any


class SessionManager:
    """Minimal in-memory user session (server-side store removed)"""

    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._initialized = False
        return cls._instance

    def __init__(self):
        if self._initialized:
            return

        self._current_user = None
        self._session_timestamp = None
        # Direct OAuth / access token tracking removed; backend handles OIDC/JWT.
        self._error_message = None
        self._initialized = True

    def get_current_user(self) -> dict[str, Any] | None:
        """Get currently logged in user data"""
        return self._current_user

    def set_current_user(self, user_data: dict[str, Any]) -> None:
        """Set current user data after successful login"""
        self._current_user = user_data
        self._session_timestamp = time.time()
        self._error_message = None

    def clear_session(self) -> None:
        """Clear all session data"""
        self._current_user = None
        self._session_timestamp = None
        self._error_message = None

    # Removed: session ID & access token helpers (no server-side session store now).

    def is_authenticated(self) -> bool:
        """Check if user is currently authenticated"""
        return self.get_current_user() is not None

    def get_display_name(self) -> str:
        """Get display name for current user"""
        user = self.get_current_user()
        if not user:
            return "Guest"
        return user.get("firstName", user.get("userName", "User"))

    # Deprecated OAuth-specific helpers removed (state & processed codes).

    def set_error(self, message: str) -> None:
        """Set error message"""
        self._error_message = message

    def get_error(self) -> str | None:
        """Get current error message"""
        return self._error_message


def get_session() -> SessionManager:
    """Get the global session manager instance"""
    return SessionManager()
