import secrets
import time
from typing import Any

from .session_store import create_session_store


class SessionManager:
    """Session management with server-side session storage"""

    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._initialized = False
        return cls._instance

    def __init__(self):
        if self._initialized:
            return

        self._store = create_session_store()
        self._current_user = None
        self._session_timestamp = None
        # Removed direct OAuth state/code tracking; backend handles OIDC.
        self._error_message = None
        self._access_token = None
        self._session_id = None
        self._initialized = True

    def create_session(self, user_data: dict[str, Any], access_token: str) -> str:
        """Create a new server-side session and return session ID"""
        session_id = secrets.token_urlsafe(32)

        session_data = {
            "user": user_data,
            "access_token": access_token,
            "first_login_at": time.time(),
            "last_login_at": time.time(),
        }

        self._store.set(session_id, session_data)
        self._session_id = session_id
        self._current_user = user_data
        self._access_token = access_token
        self._session_timestamp = time.time()
        self._error_message = None

        return session_id

    def load_session(self, session_id: str) -> bool:
        """Load session from server-side storage"""
        if not session_id:
            return False

        session_data = self._store.get(session_id)
        if not session_data:
            return False

        # Update last accessed time
        session_data["last_login_at"] = time.time()
        self._store.set(session_id, session_data)

        # Load session data
        self._session_id = session_id
        self._current_user = session_data["user"]
        self._access_token = session_data["access_token"]
        self._session_timestamp = session_data["last_login_at"]

        return True

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
        if self._session_id:
            self._store.delete(self._session_id)

        self._current_user = None
        self._session_timestamp = None
        self._oauth_state = None
        self._error_message = None
        self._access_token = None
        self._session_id = None

    def get_session_id(self) -> str | None:
        """Get current session ID for cookie storage"""
        return self._session_id

    def get_access_token(self) -> str | None:
        """Get access token"""
        return self._access_token

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
