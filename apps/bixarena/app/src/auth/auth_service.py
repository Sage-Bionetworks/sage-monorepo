from typing import Optional, Dict, Any
from auth.oauth_client import SynapseOAuthClient
from auth.session_manager import get_session


class AuthService:
    """Authentication service"""

    def __init__(self):
        self.oauth_client = SynapseOAuthClient()
        self.session = get_session()

    def generate_login_url(self) -> str:
        """Generate OAuth login URL"""
        # Development bypass
        if self.oauth_client.skip_auth:
            # Set mock user immediately
            mock_user = {
                "firstName": "Developer",
                "userName": "developer",
                "authenticated": True,
            }
            self.session.set_current_user(mock_user)
            return ""

        try:
            login_url, state = self.oauth_client.generate_login_url()
            self.session.set_oauth_state(state)
            return login_url
        except Exception as e:
            self.session.set_error(f"Failed to generate login URL: {e}")
            return ""

    def handle_oauth_callback(self, code: str, state: str = None) -> bool:
        """Handle OAuth callback from Synapse"""
        try:
            if self.session.is_code_processed(code):
                return self.session.is_authenticated()

            self.session.mark_code_processed(code)

            if state and not self.session.verify_oauth_state(state):
                self.session.set_error("Invalid OAuth state")
                return False

            access_token = self.oauth_client.exchange_code_for_token(code)
            if not access_token:
                self.session.set_error("Failed to obtain access token")
                return False

            user_profile = self.oauth_client.get_user_profile(access_token)
            if not user_profile:
                self.session.set_error("Failed to get user profile")
                return False

            user_profile["access_token"] = access_token
            self.session.set_current_user(user_profile)

            print(f"✅ Login successful: {self.session.get_display_name()}")
            return True

        except Exception as e:
            self.session.set_error(f"OAuth callback failed: {e}")
            print(f"Error during OAuth callback: {e}")
            return False

    def logout(self) -> None:
        """Logout current user"""
        username = self.session.get_display_name()
        self.session.clear_session()
        print(f"👋 User logged out: {username}")

    def is_authenticated(self) -> bool:
        """Check if user is authenticated"""
        return self.session.is_authenticated()

    def get_current_user(self) -> Optional[Dict[str, Any]]:
        """Get current user data"""
        return self.session.get_current_user()

    def get_display_name(self) -> str:
        """Get current user display name"""
        return self.session.get_display_name()


_auth_service = None


def get_auth_service() -> AuthService:
    """Get the global authentication service instance"""
    global _auth_service
    if _auth_service is None:
        _auth_service = AuthService()
    return _auth_service
