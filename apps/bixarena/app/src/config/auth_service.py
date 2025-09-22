"""
Authentication service - handles OAuth business logic and integrates with session management
"""

from typing import Optional, Dict, Any, Tuple
from .oauth_client import SynapseOAuthClient
from .session_manager import get_session


class AuthService:
    """Authentication service handling OAuth business logic"""

    def __init__(self):
        self.oauth_client = SynapseOAuthClient()
        self.session = get_session()

    def initiate_login(self) -> str:
        """
        Start OAuth login flow

        Returns:
            OAuth login URL for redirection
        """
        try:
            login_url, state = self.oauth_client.generate_login_url()

            # Store OAuth state for later verification
            self.session.set_oauth_state(state, login_url)

            return login_url

        except Exception as e:
            error_msg = f"Failed to generate login URL: {e}"
            self.session.set_error(error_msg)
            print(f"❌ {error_msg}")
            return ""

    def handle_oauth_callback(self, code: str, state: str = None) -> bool:
        """
        Handle OAuth callback from Synapse

        Args:
            code: Authorization code from callback
            state: State parameter from callback (optional for backward compatibility)

        Returns:
            True if login successful, False otherwise
        """
        try:
            # Check if this code has already been processed
            if self.session.is_code_processed(code):
                print("⚠️ OAuth code already processed, skipping")
                return self.session.is_authenticated()  # Return current auth state

            # Mark code as processed immediately to prevent reuse
            self.session.mark_code_processed(code)

            # Verify OAuth state if provided
            if state and not self.session.verify_oauth_state(state):
                self.session.set_error("Invalid OAuth state - possible security issue")
                print("❌ OAuth state verification failed")
                return False

            # Exchange code for access token
            access_token = self.oauth_client.exchange_code_for_token(code)
            if not access_token:
                self.session.set_error("Failed to obtain access token")
                print("❌ Token exchange failed")
                return False

            # Get user profile
            user_profile = self.oauth_client.get_user_profile(access_token)
            if not user_profile:
                self.session.set_error("Failed to get user profile")
                print("❌ User profile fetch failed")
                return False

            # Add access token to user profile for future API calls
            user_profile["access_token"] = access_token

            # Update session with user data
            self.session.set_current_user(user_profile)

            username = self.session.get_user_display_name()
            print(f"✅ Login successful: {username}")
            return True

        except Exception as e:
            error_msg = f"OAuth callback processing failed: {e}"
            self.session.set_error(error_msg)
            print(f"❌ {error_msg}")
            return False

    def logout_user(self) -> None:
        """
        Logout current user and clear session
        """
        username = self.session.get_user_display_name()
        self.session.clear_current_user()
        print(f"👋 User logged out: {username}")

    def is_user_authenticated(self) -> bool:
        """
        Check if user is currently authenticated

        Returns:
            True if user is logged in, False otherwise
        """
        return self.session.is_authenticated()

    def get_current_user(self) -> Optional[Dict[str, Any]]:
        """
        Get current user data

        Returns:
            User profile dict if authenticated, None otherwise
        """
        return self.session.get_current_user()

    def validate_current_session(self) -> bool:
        """
        Validate current user session and access token

        Returns:
            True if session is valid, False if user needs to re-authenticate
        """
        user = self.session.get_current_user()
        if not user:
            return False

        access_token = user.get("access_token")
        if not access_token:
            # No access token stored
            self.session.clear_current_user()
            return False

        # Validate token with Synapse
        if not self.oauth_client.validate_access_token(access_token):
            # Token expired or invalid
            self.session.set_error("Session expired - please log in again")
            self.session.clear_current_user()
            return False

        return True

    def get_login_button_config(self) -> Dict[str, Any]:
        """Get configuration for login button based on authentication state

        Returns:
            Dictionary with button value and variant
        """
        if self.is_user_authenticated():
            return {"value": self.session.get_user_display_name(), "variant": "primary"}
        else:
            return {"value": "Login", "variant": "primary"}

    def get_current_error(self) -> Optional[str]:
        """
        Get current authentication error message

        Returns:
            Error message string or None
        """
        return self.session.get_error()

    def clear_error(self) -> None:
        """Clear current error message"""
        self.session.clear_error()


# Global instance accessor
_auth_service_instance = None


def get_auth_service() -> AuthService:
    """Get the global authentication service instance"""
    global _auth_service_instance
    if _auth_service_instance is None:
        _auth_service_instance = AuthService()
    return _auth_service_instance
