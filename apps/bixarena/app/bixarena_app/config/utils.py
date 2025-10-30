"""
Utility functions for bixarena_app.
"""

import os


def _get_api_base_url() -> str | None:
    """Resolve the BixArena API base URL from environment.

    Uses API_BASE_URL. If unset, prints an error and returns None.
    """
    api = os.environ.get("API_BASE_URL")
    if api:
        return api.rstrip("/")
    print(
        "[config] API_BASE_URL not set.\n"
        "[config] Login and identity sync will be disabled until configured."
    )
    return None
