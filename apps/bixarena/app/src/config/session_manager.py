"""
Centralized session and state management for BixArena
Handles user session, OAuth state, and error management
"""

import secrets
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

        # OAuth flow state
        self._oauth_state = None
        self._login_url = None

        # Error handling
        self._error_message = None

        # Mark as initialized
        self._initialized = True

    # User session management
    def get_current_user(self) -> Optional[Dict[str, Any]]:
        """Get currently logged in user data"""
        return self._current_user

    def set_current_user(self, user_data: Dict[str, Any]) -> None:
        """Set current user data after successful login"""
        self._current_user = user_data
        self.clear_error()  # Clear any previous errors

    def clear_current_user(self) -> None:
        """Clear user session (logout)"""
        self._current_user = None
        self._oauth_state = None
        self._login_url = None
        self.clear_error()

    def is_authenticated(self) -> bool:
        """Check if user is currently authenticated"""
        return self._current_user is not None

    def get_user_display_name(self) -> str:
        """Get display name for current user"""
        if not self._current_user:
            return "Guest"
        return self._current_user.get(
            "firstName", self._current_user.get("userName", "User")
        )

    # OAuth state management
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

    # Error management
    def set_error(self, message: str) -> None:
        """Set error message for display to user"""
        self._error_message = message

    def get_error(self) -> Optional[str]:
        """Get current error message"""
        return self._error_message

    def clear_error(self) -> None:
        """Clear current error message"""
        self._error_message = None

    def has_error(self) -> bool:
        """Check if there's a current error"""
        return self._error_message is not None

    # Utility methods
    def get_login_button_config(self) -> Dict[str, Any]:
        """Get login button configuration based on current state"""
        if self.is_authenticated():
            return {"value": self.get_user_display_name(), "variant": "primary"}
        else:
            return {"value": "Login", "variant": "primary"}

    def reset_session(self) -> None:
        """Reset entire session state (for testing/debugging)"""
        self._current_user = None
        self._oauth_state = None
        self._login_url = None
        self._error_message = None


# Global instance accessor
def get_session() -> SessionManager:
    """Get the global session manager instance"""
    return SessionManager()
