"""Deprecated placeholder for legacy direct Synapse OAuth logic.

The Gradio app now delegates OAuth to backend REST endpoints. This module
retains a minimal shim class so existing imports do not break during the
transition. It will be removed once all references are cleaned up.
"""

import os


class SynapseOAuthClient:
    """Deprecated direct Synapse OAuth client.

    The Gradio app now delegates OAuth to the backend REST API.
    This class remains only to support the development SKIP_AUTH
    shortcut and to avoid breaking imports during the transition.
    """

    def __init__(self):
        # Development bypass flag
        self.skip_auth = os.environ.get("SKIP_AUTH", "").lower() == "true"

    # Backwards-compatible no-op methods retained so other code
    # paths do not break if still referenced. They now raise.
    def generate_login_url(self):  # pragma: no cover - should not be used anymore
        raise RuntimeError(
            "generate_login_url removed; UI must call backend /auth/oidc/start"
        )

    def exchange_code_for_token(self, code: str):  # pragma: no cover
        raise RuntimeError("exchange_code_for_token removed; backend does exchange")

    def get_user_profile(self, access_token: str) -> dict | None:  # pragma: no cover
        raise RuntimeError("get_user_profile removed; backend provides user context")
