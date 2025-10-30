"""
Utility functions for bixarena_app.
"""

import logging
import os


def setup_logging():
    """Configure logging format for the entire application."""
    log_level_str = os.environ.get("LOG_LEVEL", "INFO").upper()
    log_level_map = {
        "DEBUG": logging.DEBUG,
        "INFO": logging.INFO,
        "WARNING": logging.WARNING,
        "ERROR": logging.ERROR,
        "CRITICAL": logging.CRITICAL,
    }
    log_level = log_level_map.get(log_level_str, logging.INFO)

    # Configure root logger with consistent format
    logging.basicConfig(
        level=log_level,
        format="%(asctime)s | %(levelname)s | %(name)s | %(message)s",
        datefmt="%Y-%m-%d %H:%M:%S",
        force=True,  # Override any existing configuration
    )

    # Reduce noise from httpx
    logging.getLogger("httpx").setLevel(logging.WARNING)


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
