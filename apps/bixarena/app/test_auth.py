#!/usr/bin/env python3
"""
Test script for session-based authentication
"""

import os
import sys
import time
import requests
from urllib.parse import urlencode, parse_qs, urlparse

# Add src to path
sys.path.insert(0, os.path.join(os.path.dirname(__file__), "src"))

from auth.session_manager import SessionManager
from auth.oauth_client import SynapseOAuthClient
from auth.auth_service import AuthService


def test_session_management():
    """Test session creation and loading"""
    print("🧪 Testing session management...")

    # Create session manager
    session = SessionManager()

    # Test user data
    user_data = {"firstName": "Test", "userName": "testuser", "authenticated": True}

    # Test access token
    access_token = "test_access_token"

    # Create session
    session_id = session.create_session(user_data, access_token)
    print(f"✅ Created session: {session_id}")

    # Clear current session state
    session._current_user = None
    session._access_token = None

    # Load session
    success = session.load_session(session_id)
    print(f"✅ Loaded session: {success}")
    print(f"✅ User: {session.get_display_name()}")

    # Test authentication check
    print(f"✅ Authenticated: {session.is_authenticated()}")

    # Clean up
    session.clear_session()
    print("✅ Session management test completed")


def test_oauth_client():
    """Test OAuth client functionality"""
    print("\n🧪 Testing OAuth client...")

    try:
        client = SynapseOAuthClient()

        # Test login URL generation
        login_url, state = client.generate_login_url()
        print(f"✅ Generated login URL: {login_url[:100]}...")
        print(f"✅ OAuth state: {state}")

        # Check that required scopes are included
        parsed_url = urlparse(login_url)
        params = parse_qs(parsed_url.query)
        scope = params.get("scope", [""])[0]

        if "openid" in scope and "view" in scope:
            print("✅ Required scopes (openid, view) included")
        else:
            print("❌ Required scopes missing")

        print("✅ OAuth client test completed")

    except Exception as e:
        print(f"❌ OAuth client test failed: {e}")


def test_auth_service():
    """Test authentication service"""
    print("\n🧪 Testing authentication service...")

    try:
        auth_service = AuthService()

        # Test initial state
        print(f"✅ Initial auth state: {auth_service.is_authenticated()}")

        # Test login URL generation
        login_url = auth_service.generate_login_url()

        if auth_service.oauth_client.skip_auth:
            print("✅ Development bypass mode active")
            print(f"✅ Mock user authenticated: {auth_service.is_authenticated()}")
        else:
            print(f"✅ Login URL generated: {bool(login_url)}")

        print("✅ Authentication service test completed")

    except Exception as e:
        print(f"❌ Authentication service test failed: {e}")


def main():
    """Run all tests"""
    print("🚀 Starting BixArena authentication tests...\n")

    # Test individual components
    test_session_management()
    test_oauth_client()
    test_auth_service()

    print("\n🎉 All tests completed!")


if __name__ == "__main__":
    main()
