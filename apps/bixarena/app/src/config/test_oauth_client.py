#!/usr/bin/env python3
"""
Minimal OAuth test - starts tiny server just to catch callback, then exits
"""

import os
import base64
import requests
import webbrowser
import urllib.parse
from http.server import HTTPServer, BaseHTTPRequestHandler
import threading
import time
from dotenv import load_dotenv

load_dotenv()

CLIENT_ID = os.getenv("SYNAPSE_CLIENT_ID")
CLIENT_SECRET = os.getenv("SYNAPSE_CLIENT_SECRET")
REDIRECT_URI = "http://127.0.0.1:8100"  # Back to 8100


class QuickCallbackHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        # Parse URL for code
        parsed = urllib.parse.urlparse(self.path)
        params = urllib.parse.parse_qs(parsed.query)

        code = params.get("code", [None])[0]

        if code:
            # Store code in server
            self.server.auth_code = code

            # Send success page
            self.send_response(200)
            self.send_header("Content-type", "text/html")
            self.end_headers()
            self.wfile.write(b"""
            <html><body>
            <h2>Success! Got authorization code.</h2>
            <p>You can close this window. Test will continue automatically.</p>
            </body></html>
            """)
        else:
            # Send error page
            self.send_response(400)
            self.send_header("Content-type", "text/html")
            self.end_headers()
            self.wfile.write(b"<html><body><h2>No code found</h2></body></html>")

        # Signal we got the callback
        self.server.got_callback = True

    def log_message(self, format, *args):
        pass  # Suppress logs


def test_oauth_minimal():
    print("Minimal OAuth Test - Auto callback")
    print("=" * 35)

    # Start tiny server
    server = HTTPServer(("127.0.0.1", 8100), QuickCallbackHandler)  # Back to 8100
    server.auth_code = None
    server.got_callback = False

    # Run server in background
    server_thread = threading.Thread(target=server.serve_forever)
    server_thread.daemon = True
    server_thread.start()

    print("Started callback server on port 8100")

    # Build auth URL
    auth_url = f"https://signin.synapse.org?client_id={CLIENT_ID}&redirect_uri={REDIRECT_URI}&response_type=code&scope=openid+view"

    print(f"Opening browser to: {auth_url}")
    webbrowser.open(auth_url)

    print("Waiting for login callback...")

    # Wait for callback (max 2 minutes)
    start_time = time.time()
    for i in range(120):
        if server.got_callback:
            break
        time.sleep(1)
        if i % 10 == 0 and i > 0:
            print(f"Still waiting... ({120 - i}s remaining)")

        # Check if timeout
        if time.time() - start_time > 120:
            break

    # Stop server
    server.shutdown()
    server.server_close()

    if not server.got_callback:
        print("Timeout - no callback received")
        print(
            "If you got redirected to http://127.0.0.1:8100/?code=... please enter the code manually:"
        )
        manual_code = input(
            "Enter the code from the URL (or press Enter to exit): "
        ).strip()
        if manual_code:
            print(f"Using manual code: {manual_code[:20]}...")
            server.auth_code = manual_code
        else:
            return False

    if not server.auth_code:
        print("Callback received but no code")
        return False

    print(f"Got authorization code: {server.auth_code[:20]}...")

    # Now test token exchange
    print("Testing token exchange...")

    token_url = "https://repo-prod.prod.sagebase.org/auth/v1/oauth2/token"
    auth_header = base64.b64encode(f"{CLIENT_ID}:{CLIENT_SECRET}".encode()).decode()

    response = requests.post(
        token_url,
        headers={
            "Authorization": f"Basic {auth_header}",
            "Content-Type": "application/x-www-form-urlencoded",
        },
        data={
            "grant_type": "authorization_code",
            "redirect_uri": REDIRECT_URI,
            "code": server.auth_code,
        },
    )

    if response.status_code == 200:
        tokens = response.json()
        access_token = tokens.get("access_token")
        print(f"Got access token: {access_token[:30]}...")

        # Test user info
        user_response = requests.get(
            "https://repo-prod.prod.sagebase.org/repo/v1/userProfile",
            headers={"Authorization": f"Bearer {access_token}"},
        )

        if user_response.status_code == 200:
            user_info = user_response.json()
            print(f"SUCCESS! User ID: {user_info.get('ownerId')}")
            print(f"Username: {user_info.get('userName')}")
            print(f"Name: {user_info.get('firstName')} {user_info.get('lastName')}")
            print(
                f"Email: {user_info.get('emails', [{}])[0] if user_info.get('emails') else 'N/A'}"
            )
            return True
        else:
            print(f"User info failed: {user_response.status_code}")
            print(f"Response: {user_response.text}")
    else:
        print(f"Token exchange failed: {response.status_code}")
        print(f"Response: {response.text}")

    return False


if __name__ == "__main__":
    try:
        success = test_oauth_minimal()
        if success:
            print("\nOAuth client is working perfectly!")
        else:
            print("\nOAuth test failed")
    except KeyboardInterrupt:
        print("\nTest cancelled")
    except Exception as e:
        print(f"\nError: {e}")
