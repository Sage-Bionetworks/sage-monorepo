"""Console script entry points for running the BixArena application."""

from __future__ import annotations

import uvicorn

from bixarena_app.main import parse_args


def main() -> None:  # noqa: D401
    """Production entry point: runs uvicorn without reload.

    Usage: uv run bixarena-app
    """
    args = parse_args()
    uvicorn.run(
        "bixarena_app.main:app",
        host=args.host,
        port=args.port,
        log_level="info",
    )


def dev() -> None:  # noqa: D401
    """Development entry point: runs uvicorn with auto-reload.

    Usage: uv run bixarena-app-dev

    Watches for file changes and automatically reloads the server.
    """
    args = parse_args()
    uvicorn.run(
        "bixarena_app.main:app",
        host=args.host,
        port=args.port,
        log_level="info",
        reload=True,
        reload_dirs=["bixarena_app"],
    )


if __name__ == "__main__":  # pragma: no cover
    main()
