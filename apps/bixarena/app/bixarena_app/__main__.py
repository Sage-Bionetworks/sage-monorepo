"""Console script entry points for running the BixArena application."""

from __future__ import annotations

import uvicorn

from bixarena_app.main import parse_args


def main() -> None:  # noqa: D401
    """Production entry point: runs uvicorn without reload.

    Usage: uv run bixarena-app

    See parse_args() in main.py for supported environment variables.
    """
    args = parse_args()

    uvicorn.run(
        "bixarena_app.main:app",
        host=args.host,
        port=args.port,
        log_level=args.log_level,
    )


def dev() -> None:  # noqa: D401
    """Development entry point: runs uvicorn with auto-reload.

    Usage: uv run bixarena-app-dev

    Watches for file changes and automatically reloads the server.

    See parse_args() in main.py for supported environment variables.
    """
    args = parse_args()

    uvicorn.run(
        "bixarena_app.main:app",
        host=args.host,
        port=args.port,
        log_level=args.log_level,
        reload=True,
        reload_dirs=["bixarena_app"],
    )


if __name__ == "__main__":  # pragma: no cover
    main()
