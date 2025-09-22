"""
Handles user session, OAuth state, and error management
"""

import time
from typing import Optional, Dict, Any


class SessionManager:
    """Centralized session management for user authentication and app state"""

    _instance = None

    def __new__(cls):
        """Singleton pattern to ensure single session manager instance"""
        if cls._instance is None:
            cls._instance = super(SessionManager, cls).__new__(cls)
            cls._instance._initialized = False
        return cls._instance

    def __init__(self):
        """Initialize session manager with clean state"""
        if self._initialized:
            return

        # User authentication state
        self._current_user = None
        self._session_timestamp = None

        # OAuth flow state
        self._oauth_state = None
        self._login_url = None
        self._processed_codes = set()  # Track processed OAuth codes

        # Error handling
        self._error_message = None

        # Mark as initialized
        self._initialized = True

    # User session management
    def get_current_user(self) -> Optional[Dict[str, Any]]:
        """Get currently logged in user data"""
        # Check if session is still valid (basic timeout check)
        if self._current_user and self._session_timestamp:
            # Session expires after 24 hours
            if time.time() - self._session_timestamp > 86400:
                self.clear_current_user()
                return None
        return self._current_user

    def set_current_user(self, user_data: Dict[str, Any]) -> None:
        """Set current user data after successful login"""
        self._current_user = user_data
        self._session_timestamp = time.time()
        self.clear_error()  # Clear any previous errors

    def clear_current_user(self) -> None:
        """Clear user session (logout)"""
        self._current_user = None
        self._session_timestamp = None
        self._oauth_state = None
        self._login_url = None
        self.clear_error()

    def is_authenticated(self) -> bool:
        """Check if user is currently authenticated"""
        return self.get_current_user() is not None

    def get_user_display_name(self) -> str:
        """Get display name for current user"""
        user = self.get_current_user()
        if not user:
            return "Guest"
        return user.get("firstName", user.get("userName", "User"))

    # OAuth state management with code tracking
    def set_oauth_state(self, state: str, login_url: str) -> None:
        """Set OAuth state and login URL for verification"""
        self._oauth_state = state
        self._login_url = login_url

    def get_oauth_state(self) -> Optional[str]:
        """Get stored OAuth state for verification"""
        return self._oauth_state

    def get_login_url(self) -> Optional[str]:
        """Get stored login URL"""
        return self._login_url

    def verify_oauth_state(self, received_state: str) -> bool:
        """Verify OAuth state parameter matches stored state"""
        if not self._oauth_state or not received_state:
            return False
        return self._oauth_state == received_state

    def is_code_processed(self, code: str) -> bool:
        """Check if OAuth code has already been processed"""
        return code in self._processed_codes

    def mark_code_processed(self, code: str) -> None:
        """Mark OAuth code as processed to prevent reuse"""
        self._processed_codes.add(code)
        # Keep only recent codes to prevent memory bloat
        if len(self._processed_codes) > 100:
            # Remove oldest half
            codes_list = list(self._processed_codes)
            self._processed_codes = set(codes_list[-50:])

    # Error management
    def set_error(self, message: str) -> None:
        """Set error message for display to user"""
        self._error_message = message
        print(f"❌ Error set: {message}")

    def get_error(self) -> Optional[str]:
        """Get current error message"""
        return self._error_message

    def clear_error(self) -> None:
        """Clear current error message"""
        self._error_message = None

    def has_error(self) -> bool:
        """Check if there's a current error"""
        return self._error_message is not None

    def reset_session(self) -> None:
        """Reset entire session state (for testing/debugging)"""
        self._current_user = None
        self._session_timestamp = None
        self._oauth_state = None
        self._login_url = None
        self._processed_codes.clear()
        self._error_message = None

    def get_session_info(self) -> Dict[str, Any]:
        """Get session information for debugging"""
        return {
            "authenticated": self.is_authenticated(),
            "user": self.get_user_display_name() if self.is_authenticated() else None,
            "session_age": time.time() - self._session_timestamp
            if self._session_timestamp
            else None,
            "has_error": self.has_error(),
            "error": self.get_error(),
        }


# Global instance accessor
def get_session() -> SessionManager:
    """Get the global session manager instance"""
    return SessionManager()
