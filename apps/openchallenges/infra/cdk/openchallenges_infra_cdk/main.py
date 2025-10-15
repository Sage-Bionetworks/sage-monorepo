"""CLI entrypoint wrapping build_app()."""

from .app import build_app


def main() -> None:  # pragma: no cover - simple wrapper
    build_app().synth()


if __name__ == "__main__":  # pragma: no cover
    main()
