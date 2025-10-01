"""Console script entry point wrapper.

This defers to logic defined in the existing flat-layout `main.py` module.
"""

from __future__ import annotations

try:  # Import from the packaged layout after rename
    from bixarena_app.main import build_app, parse_args  # type: ignore
except ImportError:
    # Fallback: attempt legacy flat import name (should not normally occur now)
    try:  # pragma: no cover - legacy safeguard
        from main import build_app, parse_args  # type: ignore
    except ImportError as exc:  # pragma: no cover - defensive
        raise SystemExit(
            f"Failed to import application modules (packaged + legacy fallback): {exc}"
        ) from exc


def main() -> None:  # noqa: D401
    """Entry point used by the `bixarena-app` console script."""
    args = parse_args()
    app = build_app(args.register_api_endpoint_file, args.moderate)
    app.queue(default_concurrency_limit=args.concurrency_count).launch(
        server_name=args.host,
        server_port=args.port,
        share=args.share,
        max_threads=200,
        root_path=args.gradio_root_path,
    )


if __name__ == "__main__":  # pragma: no cover
    main()
