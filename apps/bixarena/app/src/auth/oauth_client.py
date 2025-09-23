"""
Simplified OAuth client for Synapse API calls
"""

import os
import base64
import secrets
import urllib.parse
import requests
from typing import Optional, Dict, Any, Tuple


class SynapseOAuthClient:
    """Simplified OAuth client for Synapse"""

    def __init__(self):
        self.client_id = os.environ.get("SYNAPSE_CLIENT_ID")
        self.client_secret = os.environ.get("SYNAPSE_CLIENT_SECRET")
        self.redirect_uri = f"http://127.0.0.1:{os.environ.get('APP_PORT', '8100')}"

        # Development bypass flag
        self.skip_auth = os.environ.get("SKIP_AUTH", "").lower() == "true"

        self.auth_url = "https://signin.synapse.org"
        self.token_url = "https://repo-prod.prod.sagebase.org/auth/v1/oauth2/token"
        self.user_profile_url = (
            "https://repo-prod.prod.sagebase.org/repo/v1/userProfile"
        )

        if not all([self.client_id, self.client_secret]):
            raise ValueError(
                "Missing SYNAPSE_CLIENT_ID or SYNAPSE_CLIENT_SECRET environment variables"
            )

    def generate_login_url(self) -> Tuple[str, str]:
        """Generate Synapse OAuth login URL and state token"""
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
        """Exchange authorization code for access token"""
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
                return response.json().get("access_token")
            return None
        except Exception:
            return None

    def get_user_profile(self, access_token: str) -> Optional[Dict[str, Any]]:
        """Get user profile information using access token"""
        headers = {"Authorization": f"Bearer {access_token}"}
        try:
            response = requests.get(self.user_profile_url, headers=headers)
            return response.json() if response.status_code == 200 else None
        except Exception:
            return None
