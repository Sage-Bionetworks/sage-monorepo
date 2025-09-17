"""Configuration loading (env vars + overrides)."""

from __future__ import annotations

import os
from dataclasses import dataclass
from pathlib import Path
from typing import Any

import tomllib

DEFAULT_API_URL = "http://localhost:8082/api/v1"
DEFAULT_LIMIT = 5


@dataclass(frozen=True)
class ClientConfig:
    api_url: str
    api_key: str | None
    default_limit: int = DEFAULT_LIMIT
    retries: int = 0


def _load_file_config() -> dict[str, Any]:  # pragma: no cover (IO)
    """Load optional TOML config.

    Search order (first hit wins):
    1. Closest `.openchallenges.toml` walking upward from CWD
      2. `$HOME/.openchallenges.toml`

    Supports either a top-level table or `[openchallenges]` section.
    Returns empty dict if not found or an error occurs.
    """

    seen: set[Path] = set()
    paths: list[Path] = []

    cwd = Path.cwd().resolve()
    for parent in [cwd, *cwd.parents]:
        candidate = parent / ".openchallenges.toml"
        if candidate not in seen:
            paths.append(candidate)
            seen.add(candidate)
    home_candidate = Path.home() / ".openchallenges.toml"
    if home_candidate not in seen:
        paths.append(home_candidate)

    for path in paths:
        if path.is_file():
            try:
                with path.open("rb") as f:
                    data = tomllib.load(f)
                section = data.get("openchallenges", data)
                if isinstance(section, dict):
                    return {k.lower(): v for k, v in section.items()}
            except Exception:
                return {}
    return {}


def load_config(
    *,
    override_api_key: str | None,
    override_api_url: str | None,
    default_limit: int,
) -> ClientConfig:
    file_cfg = _load_file_config()
    # Precedence: explicit overrides > env vars > file > defaults
    api_url = (
        override_api_url
        or os.getenv("OC_API_URL")
        or file_cfg.get("api_url")
        or DEFAULT_API_URL
    )
    api_key = override_api_key or os.getenv("OC_API_KEY") or file_cfg.get("api_key")
    limit_val = default_limit or file_cfg.get("default_limit") or DEFAULT_LIMIT
    retries_val = os.getenv("OC_RETRIES") or file_cfg.get("retries") or 0
    return ClientConfig(
        api_url=api_url,
        api_key=api_key,
        default_limit=int(limit_val),
        retries=int(retries_val),
    )
