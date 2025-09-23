"""Interactive prompt utilities.

This module centralizes interactive UI logic so that higher-level commands
(`platforms create|update`, future `challenges create`, etc.) do not import
`questionary` directly. This keeps imports lazy and makes the dependency
optional.

Design goals:
    * Zero runtime penalty if questionary isn't installed.
    * Fallback to prompt_toolkit (already a dependency) or Typer's prompt.
    * Small surface area (text, select, confirm) that can be expanded later.

Usage pattern:
    from .interactive import prompt_text, prompt_select, prompt_confirm

    value = prompt_text("Name", default="foo")

Testing strategy:
    In unit tests we avoid interactive mode OR monkeypatch the functions here.
"""

from __future__ import annotations

from collections.abc import Iterable

import typer

try:  # pragma: no cover - only executed when questionary is installed
    import questionary  # type: ignore

    _HAS_Q = True
except Exception:  # pragma: no cover - fallback path
    questionary = None  # type: ignore
    _HAS_Q = False

try:  # pragma: no cover - optional richer editing experience
    from prompt_toolkit import prompt as pt_prompt  # type: ignore
except Exception:  # pragma: no cover
    pt_prompt = None  # type: ignore


def has_questionary() -> bool:
    return _HAS_Q


def prompt_text(label: str, *, default: str | None = None) -> str:
    """Prompt for free text.

    Preference order: questionary > prompt_toolkit > typer.prompt.
    """

    if _HAS_Q:  # pragma: no branch - tiny logic
        return questionary.text(label, default=default or "").unsafe_ask()  # type: ignore[attr-defined]
    if pt_prompt is not None:
        return pt_prompt(f"{label}: ", default=default or "")
    return typer.prompt(label, default=default or "")


def prompt_select(
    label: str, choices: Iterable[str], *, default: str | None = None
) -> str:
    """Prompt user to select one value from choices.

    If questionary is unavailable we fallback to a numbered list + prompt.
    """

    choices_list: list[str] = list(choices)
    if not choices_list:
        raise ValueError("prompt_select requires at least one choice")
    if _HAS_Q:  # pragma: no branch
        return questionary.select(  # type: ignore[attr-defined]
            label, choices=choices_list, default=default
        ).unsafe_ask()
    # Fallback simple textual menu
    typer.echo(label)
    for idx, val in enumerate(choices_list, start=1):
        marker = "*" if default and default == val else " "
        typer.echo(f" {idx}. {val}{' (default)' if marker == '*' else ''}")
    while True:  # pragma: no cover - minimal manual path
        default_index = (
            choices_list.index(default) + 1
            if default and default in choices_list
            else 1
        )
        raw = typer.prompt("Enter number", default=str(default_index))
        if raw.isdigit():
            i = int(raw) - 1
            if 0 <= i < len(choices_list):
                return choices_list[i]
        typer.echo("Invalid selection, try again.", err=True)


def prompt_confirm(label: str, *, default: bool = True) -> bool:
    import sys

    # In non-interactive (no TTY) contexts auto-return default to avoid aborts.
    if not sys.stdin.isatty() or not sys.stdout.isatty():  # pragma: no cover
        return default
    if _HAS_Q:  # pragma: no branch
        return questionary.confirm(  # type: ignore[attr-defined]
            label, default=default
        ).unsafe_ask()
    return typer.confirm(label, default=default)


__all__ = [
    "has_questionary",
    "prompt_text",
    "prompt_select",
    "prompt_confirm",
]
