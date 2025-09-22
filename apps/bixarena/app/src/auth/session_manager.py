import time
from typing import Optional, Dict, Any


class SessionManager:
    """Session management"""

    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super(SessionManager, cls).__new__(cls)
            cls._instance._initialized = False
        return cls._instance

    def __init__(self):
        if self._initialized:
            return

        self._current_user = None
        self._session_timestamp = None
        self._oauth_state = None
        self._processed_codes = set()
        self._error_message = None
        self._initialized = True

    def get_current_user(self) -> Optional[Dict[str, Any]]:
        """Get currently logged in user data with timeout check"""
        if self._current_user and self._session_timestamp:
            if time.time() - self._session_timestamp > 86400:  # 24 hours
                self.clear_session()
                return None
        return self._current_user

    def set_current_user(self, user_data: Dict[str, Any]) -> None:
        """Set current user data after successful login"""
        self._current_user = user_data
        self._session_timestamp = time.time()
        self._error_message = None

    def clear_session(self) -> None:
        """Clear all session data"""
        self._current_user = None
        self._session_timestamp = None
        self._oauth_state = None
        self._error_message = None

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
