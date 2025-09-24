import time
import secrets
from typing import Optional, Dict, Any
from .session_store import create_session_store


class SessionManager:
    """Session management with server-side session storage"""

    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super(SessionManager, cls).__new__(cls)
            cls._instance._initialized = False
        return cls._instance

    def __init__(self):
        if self._initialized:
            return

        self._store = create_session_store()
        self._current_user = None
        self._session_timestamp = None
        self._oauth_state = None
        self._processed_codes = set()
        self._error_message = None
        self._access_token = None
        self._session_id = None
        self._initialized = True

    def create_session(self, user_data: Dict[str, Any], access_token: str) -> str:
        """Create a new server-side session and return session ID"""
        session_id = secrets.token_urlsafe(32)

        session_data = {
            "user": user_data,
            "access_token": access_token,
            "created_at": time.time(),
            "last_accessed": time.time(),
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
        session_data["last_accessed"] = time.time()
        self._store.set(session_id, session_data)

        # Load session data
        self._session_id = session_id
        self._current_user = session_data["user"]
        self._access_token = session_data["access_token"]
        self._session_timestamp = session_data["last_accessed"]

        return True

    def get_current_user(self) -> Optional[Dict[str, Any]]:
        """Get currently logged in user data"""
        return self._current_user

    def set_current_user(self, user_data: Dict[str, Any]) -> None:
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

    def get_session_id(self) -> Optional[str]:
        """Get current session ID for cookie storage"""
        return self._session_id

    def get_access_token(self) -> Optional[str]:
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

    def set_oauth_state(self, state: str) -> None:
        """Set OAuth state for verification"""
        self._oauth_state = state

    def verify_oauth_state(self, received_state: str) -> bool:
        """Verify OAuth state parameter"""
        return (
            self._oauth_state == received_state
            if self._oauth_state and received_state
            else False
        )

    def is_code_processed(self, code: str) -> bool:
        """Check if OAuth code has been processed"""
        return code in self._processed_codes

    def mark_code_processed(self, code: str) -> None:
        """Mark OAuth code as processed"""
        self._processed_codes.add(code)
        if len(self._processed_codes) > 50:  # Keep memory usage low
            self._processed_codes = set(list(self._processed_codes)[-25:])

    def set_error(self, message: str) -> None:
        """Set error message"""
        self._error_message = message

    def get_error(self) -> Optional[str]:
        """Get current error message"""
        return self._error_message


def get_session() -> SessionManager:
    """Get the global session manager instance"""
    return SessionManager()
