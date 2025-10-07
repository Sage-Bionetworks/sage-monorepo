"""Console script entry point for the BixArena AI Service.

This wrapper intentionally avoids a custom argument parser. It provides:
  * A default ASGI app target: ``bixarena_ai_service.main:app`` if the user
    doesn't specify one as the first argument.
  * Optional defaults for ``--host`` and ``--port`` (env overridable via
    ``APP_HOST`` / ``APP_PORT``) only when the user hasn't already supplied
    those flags.

Usage examples:
  bixarena-ai-service --reload
  bixarena-ai-service --reload --port 9000
  bixarena-ai-service bixarena_ai_service.main:app --host 0.0.0.0 --port 8114

Environment variables:
  APP_HOST  (default: 0.0.0.0)
  APP_PORT  (default: 8000)

Any additional flags are passed straight through to uvicorn.
"""

from __future__ import annotations

import os
import sys

import uvicorn

DEFAULT_APP = "bixarena_ai_service.main:app"


def _inject_defaults(argv: list[str]) -> list[str]:
    """Return a modified argv (excluding program name) with defaults injected.

    Rules:
      1. If the first arg is missing or starts with '--', insert DEFAULT_APP.
      2. If '--host' not present (and no UNIX socket specified via '--uds'), add
         a default host (APP_HOST env or 0.0.0.0) immediately after app target.
    3. If '--port' not present and no '--uds', add default port
      (APP_PORT env or 8000).
    """

    if not argv or argv[0].startswith("--"):
        argv = [DEFAULT_APP, *argv]

    # Detect if user provided host/port/uds already
    has_host = "--host" in argv or "--uds" in argv
    has_port = "--port" in argv or "--uds" in argv

    # Insert defaults only if missing
    parts: list[str] = [argv[0]]  # start with app target

    if not has_host:
        parts += ["--host", os.getenv("APP_HOST", "0.0.0.0")]
    if not has_port:
        parts += ["--port", os.getenv("APP_PORT", "8000")]

    parts.extend(argv[1:])
    return parts


def main() -> None:  # noqa: D401
    """Entry point that adapts arguments then delegates to ``uvicorn.main``."""
    args = _inject_defaults(sys.argv[1:])
    # Rebuild sys.argv for uvicorn's own CLI parsing
    sys.argv = ["uvicorn", *args]
    uvicorn.main()


if __name__ == "__main__":  # pragma: no cover
    main()
