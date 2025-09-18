"""
Standalone OAuth handler for BixArena
Handles Synapse authentication flow
"""

import os
import base64
import secrets
import urllib.parse
import requests
from typing import Optional, Dict, Any
from dotenv import load_dotenv

load_dotenv()


class BixArenaOAuth:
    """Handles OAuth authentication with Synapse for BixArena"""

    def __init__(self):
        self.client_id = os.getenv("SYNAPSE_CLIENT_ID")
        self.client_secret = os.getenv("SYNAPSE_CLIENT_SECRET")
        self.redirect_uri = f"http://127.0.0.1:{os.getenv('APP_PORT', '8100')}"

        # Synapse endpoints
        self.auth_url = "https://signin.synapse.org"
        self.token_url = "https://repo-prod.prod.sagebase.org/auth/v1/oauth2/token"
        self.user_profile_url = (
            "https://repo-prod.prod.sagebase.org/repo/v1/userProfile"
        )

        if not all([self.client_id, self.client_secret]):
            raise ValueError(
                "Missing SYNAPSE_CLIENT_ID or SYNAPSE_CLIENT_SECRET in .env file"
            )

    def get_login_url(self) -> tuple[str, str]:
        """Generate the Synapse login URL"""
        state = secrets.token_urlsafe(32)

        params = {
            "client_id": self.client_id,
            "redirect_uri": self.redirect_uri,
            "response_type": "code",
            "scope": "openid view",
            "state": state,
        }

        login_url = f"{self.auth_url}?{urllib.parse.urlencode(params)}"
        return login_url, state

    def exchange_code_for_token(self, code: str) -> Optional[str]:
        """
        Exchange authorization code for access token

        Args:
            code: Authorization code from Synapse callback

        Returns:
            Access token if successful, None otherwise
        """
        auth_header = base64.b64encode(
            f"{self.client_id}:{self.client_secret}".encode()
        ).decode()

        headers = {
            "Authorization": f"Basic {auth_header}",
            "Content-Type": "application/x-www-form-urlencoded",
        }

        data = {
            "grant_type": "authorization_code",
            "redirect_uri": self.redirect_uri,
            "code": code,
        }

        try:
            response = requests.post(self.token_url, headers=headers, data=data)

            if response.status_code == 200:
                tokens = response.json()
                return tokens.get("access_token")
            else:
                print(
                    f"Token exchange failed: {response.status_code} - {response.text}"
                )
                return None

        except Exception as e:
            print(f"Error during token exchange: {e}")
            return None

    def get_user_profile(self, access_token: str) -> Optional[Dict[str, Any]]:
        """
        Get user profile information

        Args:
            access_token: Valid access token

        Returns:
            User profile dict if successful, None otherwise
        """
        headers = {"Authorization": f"Bearer {access_token}"}

        try:
            response = requests.get(self.user_profile_url, headers=headers)

            if response.status_code == 200:
                return response.json()
            else:
                print(
                    f"Failed to get user profile: {response.status_code} - {response.text}"
                )
                return None

        except Exception as e:
            print(f"Error getting user profile: {e}")
            return None

    def complete_oauth_flow(self, code: str) -> Optional[Dict[str, Any]]:
        """
        Complete the full OAuth flow: exchange code for token and get user info

        Args:
            code: Authorization code from callback

        Returns:
            User profile dict if successful, None otherwise
        """
        # Exchange code for access token
        access_token = self.exchange_code_for_token(code)
        if not access_token:
            return None

        # Get user profile
        user_profile = self.get_user_profile(access_token)
        if not user_profile:
            return None

        # Add access token to user profile for future use
        user_profile["access_token"] = access_token

        return user_profile


# Convenience functions for easy integration
def get_synapse_login_url() -> tuple[str, str]:
    """Get Synapse login URL and state"""
    oauth = BixArenaOAuth()
    return oauth.get_login_url()


def handle_synapse_callback(code: str) -> Optional[Dict[str, Any]]:
    """Handle Synapse OAuth callback and update global state"""
    oauth = BixArenaOAuth()
    user_profile = oauth.complete_oauth_flow(code)

    if user_profile:
        # Update global state in header module
        import page.bixarena_header as header_module

        header_module.current_user = user_profile
        return user_profile
    else:
        return None


def validate_synapse_token(access_token: str) -> Optional[Dict[str, Any]]:
    """Validate an existing access token and return user profile"""
    oauth = BixArenaOAuth()
    return oauth.get_user_profile(access_token)


def get_current_user():
    """Get the currently logged in user"""
    import page.bixarena_header as header_module

    return header_module.current_user


def logout_user():
    """Logout the current user"""
    import page.bixarena_header as header_module

    header_module.current_user = None
