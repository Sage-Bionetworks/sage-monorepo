import os

import requests

from .token_store import TOKEN_STORE

BACKEND_BASE = os.environ.get("BACKEND_BASE_URL", "http://127.0.0.1:8112/v1")


def fetch_token_if_needed(session_cookies=None):
    """Fetch internal JWT if not cached; rely on session cookie for auth."""
    if TOKEN_STORE.get():
        return TOKEN_STORE.get()
    url = f"{BACKEND_BASE}/token"
    resp = requests.post(url, cookies=session_cookies or {})
    if resp.status_code == 200:
        data = resp.json()
        TOKEN_STORE.set(data["accessToken"], data.get("expiresIn", 600))
        return TOKEN_STORE.get()
    return None
