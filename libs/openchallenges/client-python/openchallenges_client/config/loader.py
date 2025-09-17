"""Configuration loading (env vars + overrides)."""

from __future__ import annotations

import os
from dataclasses import dataclass

DEFAULT_API_URL = "http://localhost:8082/api/v1"
DEFAULT_LIMIT = 5


@dataclass(frozen=True)
class ClientConfig:
    api_url: str
    api_key: str | None
    default_limit: int = DEFAULT_LIMIT


def load_config(
    *,
    override_api_key: str | None,
    override_api_url: str | None,
    default_limit: int,
) -> ClientConfig:
    api_url = override_api_url or os.getenv("OC_API_URL", DEFAULT_API_URL)
    api_key = override_api_key or os.getenv("OC_API_KEY")
    return ClientConfig(
        api_url=api_url,
        api_key=api_key,
        default_limit=default_limit or DEFAULT_LIMIT,
    )
