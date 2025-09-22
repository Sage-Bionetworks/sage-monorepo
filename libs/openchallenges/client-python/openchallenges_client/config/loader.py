"""Configuration loading (env vars + overrides)."""

from __future__ import annotations

import os
from dataclasses import dataclass
from pathlib import Path
from typing import Any

import tomllib

DEFAULT_API_URL = "http://localhost:8082/api/v1"
DEFAULT_LIMIT = 10


@dataclass(frozen=True)
class ClientConfig:
    api_url: str
    api_key: str | None
    limit: int = DEFAULT_LIMIT
    retries: int = 0
    # Optional metadata describing where each value came from for diagnostics.
    sources: dict[str, str] | None = None


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
    limit: int,
    with_sources: bool = False,
) -> ClientConfig:
    file_cfg = _load_file_config()
    sources: dict[str, str] = {}

    # api_url resolution
    if override_api_url is not None:
        api_url = override_api_url
        sources["api_url"] = "override"
    elif os.getenv("OC_API_URL"):
        api_url = os.getenv("OC_API_URL")  # type: ignore[assignment]
        sources["api_url"] = "env:OC_API_URL"
    elif file_cfg.get("api_url"):
        api_url = file_cfg.get("api_url")  # type: ignore[assignment]
        sources["api_url"] = "file"
    else:
        api_url = DEFAULT_API_URL
        sources["api_url"] = "default"

    # api_key resolution
    if override_api_key is not None:
        api_key = override_api_key
        sources["api_key"] = "override"
    elif os.getenv("OC_API_KEY"):
        api_key = os.getenv("OC_API_KEY")
        sources["api_key"] = "env:OC_API_KEY"
    elif file_cfg.get("api_key"):
        api_key = file_cfg.get("api_key")
        sources["api_key"] = "file"
    else:
        api_key = None
        sources["api_key"] = "unset"

    # limit resolution
    if limit != DEFAULT_LIMIT:
        limit_val: int | str = limit
        sources["limit"] = "override"
    elif file_cfg.get("limit") is not None:
        limit_val = file_cfg.get("limit")  # type: ignore[assignment]
        sources["limit"] = "file"
    else:
        limit_val = DEFAULT_LIMIT
        sources["limit"] = "default"

    # retries resolution
    if os.getenv("OC_RETRIES") is not None:
        retries_val: int | str = os.getenv("OC_RETRIES")  # type: ignore[assignment]
        sources["retries"] = "env:OC_RETRIES"
    elif file_cfg.get("retries") is not None:
        retries_val = file_cfg.get("retries")  # type: ignore[assignment]
        sources["retries"] = "file"
    else:
        retries_val = 0
        sources["retries"] = "default"

    return ClientConfig(
        api_url=str(api_url),
        api_key=api_key,
        limit=int(limit_val),
        retries=int(retries_val),
        sources=sources if with_sources else None,
    )
