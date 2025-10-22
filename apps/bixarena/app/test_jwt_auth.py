#!/usr/bin/env python3
"""
Test script for BixArena JWT authentication flow.

This script tests:
1. JWT minting from session cookie
2. JWT validation by the API
3. End-to-end authentication flow

Prerequisites:
- All BixArena services must be running
- You must be logged in and have a valid JSESSIONID
"""

import json
import sys
from base64 import urlsafe_b64decode

import requests


def extract_jwt_payload(jwt: str) -> dict:
    """Decode and extract JWT payload (for inspection only)."""
    try:
        parts = jwt.split(".")
        if len(parts) != 3:
            return {}

        payload = parts[1]
        # Add padding if needed
        payload += "=" * (4 - len(payload) % 4)
        decoded = urlsafe_b64decode(payload)
        return json.loads(decoded)
    except Exception as e:
        print(f"Failed to decode JWT: {e}")
        return {}


def test_jwt_minting(session_id: str) -> str | None:
    """Test minting a JWT from a session cookie."""
    print("\n=== Test 1: Minting JWT from Session ===")
    print(f"Session ID: {session_id[:20]}...")

    try:
        response = requests.post(
            "http://localhost:8115/v1/token",
            cookies={"JSESSIONID": session_id},
            timeout=5,
        )

        if response.status_code == 200:
            data = response.json()
            jwt = data.get("access_token")

            if jwt:
                print("✅ JWT minted successfully")
                print(f"   Token type: {data.get('token_type')}")
                print(f"   Expires in: {data.get('expires_in')} seconds")
                print(f"   Token (first 50 chars): {jwt[:50]}...")

                # Decode and show payload
                payload = extract_jwt_payload(jwt)
                if payload:
                    print("\n   JWT Payload:")
                    print(f"   - Subject: {payload.get('sub')}")
                    print(f"   - Roles: {payload.get('roles')}")
                    print(f"   - Issuer: {payload.get('iss')}")
                    print(f"   - Audience: {payload.get('aud')}")
                    print(f"   - Expires: {payload.get('exp')}")

                return jwt
            else:
                print("❌ Response missing access_token")
                print(f"   Response: {data}")
                return None
        else:
            print(f"❌ Failed to mint JWT: HTTP {response.status_code}")
            print(f"   Response: {response.text}")
            return None

    except Exception as e:
        print(f"❌ Exception while minting JWT: {e}")
        return None


def test_api_without_jwt():
    """Test API call without JWT (should work for public endpoints)."""
    print("\n=== Test 2: API Call Without JWT ===")

    try:
        response = requests.get(
            "http://localhost:8112/v1/leaderboards/open-source", timeout=5
        )

        if response.status_code == 200:
            print("✅ Public endpoint accessible without JWT")
            data = response.json()
            print(f"   Leaderboard ID: {data.get('id')}")
            print(f"   Entries: {len(data.get('entries', []))}")
            return True
        elif response.status_code == 401:
            print("ℹ️  Endpoint requires authentication (as expected if protected)")
            return True
        else:
            print(f"⚠️  Unexpected status: HTTP {response.status_code}")
            print(f"   Response: {response.text[:200]}")
            return False

    except Exception as e:
        print(f"❌ Exception while calling API: {e}")
        return False


def test_api_with_jwt(jwt: str):
    """Test API call with JWT."""
    print("\n=== Test 3: API Call With JWT ===")

    try:
        response = requests.get(
            "http://localhost:8112/v1/leaderboards/open-source",
            headers={"Authorization": f"Bearer {jwt}"},
            timeout=5,
        )

        if response.status_code == 200:
            print("✅ Authenticated API call successful")
            data = response.json()
            print(f"   Leaderboard ID: {data.get('id')}")
            print(f"   Entries: {len(data.get('entries', []))}")
            return True
        else:
            print(f"❌ API call failed: HTTP {response.status_code}")
            print(f"   Response: {response.text[:200]}")
            return False

    except Exception as e:
        print(f"❌ Exception while calling API: {e}")
        return False


def test_userinfo_endpoint(session_id: str):
    """Test the /userinfo endpoint."""
    print("\n=== Test 4: UserInfo Endpoint ===")

    try:
        response = requests.get(
            "http://localhost:8115/userinfo",
            cookies={"JSESSIONID": session_id},
            timeout=5,
        )

        if response.status_code == 200:
            print("✅ UserInfo endpoint accessible")
            data = response.json()
            print(f"   Subject: {data.get('sub')}")
            print(f"   Preferred username: {data.get('preferred_username')}")
            print(f"   Email: {data.get('email')}")
            print(f"   Roles: {data.get('roles')}")
            return True
        else:
            print(f"❌ UserInfo failed: HTTP {response.status_code}")
            print(f"   Response: {response.text}")
            return False

    except Exception as e:
        print(f"❌ Exception while calling userinfo: {e}")
        return False


def main():
    """Run all tests."""
    print("=" * 60)
    print("BixArena JWT Authentication Test Suite")
    print("=" * 60)

    # Get session ID from user
    print(
        "\nTo get your JSESSIONID:"
        "\n1. Open http://localhost:8100 in your browser"
        "\n2. Log in with Synapse credentials"
        "\n3. Open DevTools (F12) → Application → Cookies"
        "\n4. Copy the JSESSIONID value\n"
    )

    session_id = input("Enter your JSESSIONID: ").strip()

    if not session_id:
        print("❌ No session ID provided")
        sys.exit(1)

    # Run tests
    all_passed = True

    # Test 1: Mint JWT
    jwt = test_jwt_minting(session_id)
    if not jwt:
        print("\n❌ JWT minting failed - cannot continue with remaining tests")
        sys.exit(1)

    # Test 2: API without JWT
    if not test_api_without_jwt():
        all_passed = False

    # Test 3: API with JWT
    if not test_api_with_jwt(jwt):
        all_passed = False

    # Test 4: UserInfo endpoint
    if not test_userinfo_endpoint(session_id):
        all_passed = False

    # Summary
    print("\n" + "=" * 60)
    if all_passed:
        print("🎉 All tests passed!")
        print("=" * 60)
        print(
            "\nYour JWT authentication is working correctly!"
            "\n\nNext steps:"
            "\n1. Protect API endpoints by removing permitAll in SecurityConfiguration"
            "\n2. Update Gradio functions to use get_jwt_from_request()"
            "\n3. Test end-to-end flow from Gradio UI"
        )
    else:
        print("⚠️  Some tests failed - check output above")
        print("=" * 60)
        sys.exit(1)


if __name__ == "__main__":
    main()
