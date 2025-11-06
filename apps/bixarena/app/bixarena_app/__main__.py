"""Console script entry points for running the BixArena application."""

from __future__ import annotations

import os

import uvicorn

from bixarena_app.main import parse_args


def main() -> None:  # noqa: D401
    """Production entry point: runs uvicorn without reload.

    Usage: uv run bixarena-app

    Environment variables:
    - LOG_LEVEL: Logging level (default: info)
    """
    args = parse_args()
    log_level = os.environ.get("LOG_LEVEL", "info").lower()

    uvicorn.run(
        "bixarena_app.main:app",
        host=args.host,
        port=args.port,
        log_level=log_level,
    )


def dev() -> None:  # noqa: D401
    """Development entry point: runs uvicorn with auto-reload.

    Usage: uv run bixarena-app-dev

    Watches for file changes and automatically reloads the server.

    Environment variables:
    - LOG_LEVEL: Logging level (default: info)
    """
    args = parse_args()
    log_level = os.environ.get("LOG_LEVEL", "info").lower()

    uvicorn.run(
        "bixarena_app.main:app",
        host=args.host,
        port=args.port,
        log_level=log_level,
        reload=True,
        reload_dirs=["bixarena_app"],
    )


if __name__ == "__main__":  # pragma: no cover
    main()
