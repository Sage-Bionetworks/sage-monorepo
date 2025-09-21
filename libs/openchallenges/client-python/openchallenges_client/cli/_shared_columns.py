"""Shared column selection helpers.

Centralizes logic for computing available columns and filtering row dictionaries
so other future resource CLIs (e.g., datasets, submissions) can reuse the
pattern. Currently internal to this client; intended for future extraction into
an external support library.
"""

from __future__ import annotations

from typing import Any

import typer

_CHALLENGE_BASE_COLS = ["id", "name", "status", "platform"]
_CHALLENGE_WIDE_EXTRA = ["start_date", "end_date", "duration_days"]
_ORG_COLS = ["id", "name", "short_name", "website_url"]
_PLATFORM_BASE_COLS = ["id", "name", "website_url"]
_PLATFORM_WIDE_EXTRA = ["slug", "avatar_key"]


def available_challenge_columns(wide: bool) -> list[str]:
    return _CHALLENGE_BASE_COLS + (_CHALLENGE_WIDE_EXTRA if wide else [])


def available_org_columns() -> list[str]:
    return list(_ORG_COLS)


def print_challenge_columns(wide: bool) -> None:
    typer.echo("Available challenge columns:")
    for c in available_challenge_columns(wide):
        typer.echo(f"  - {c}")
    if not wide:
        typer.echo("(Use --wide to access: start_date, end_date, duration_days)")


def print_org_columns() -> None:
    typer.echo("Available organization columns:")
    for c in _ORG_COLS:
        typer.echo(f"  - {c}")


def available_platform_columns(wide: bool) -> list[str]:
    return _PLATFORM_BASE_COLS + (_PLATFORM_WIDE_EXTRA if wide else [])


def print_platform_columns(wide: bool) -> None:
    typer.echo("Available platform columns:")
    for c in available_platform_columns(True):  # show full set for discoverability
        typer.echo(f"  - {c}")
    if not wide:
        typer.echo("(Use --wide to access: slug, avatar_key)")


def filter_columns(
    rows: list[dict[str, Any]],
    columns: str | None,
    *,
    kind: str,
    wide: bool,
) -> list[dict[str, Any]]:
    """Filter row dicts to a specified ordered subset of columns.

    Args:
        rows: list of homogeneous row dictionaries.
        columns: user-specified comma list or None.
    kind: "challenge" | "org" | "platform" (controls available sets & hints).
        wide: whether wide mode is active for challenge rows.
    """
    if not columns:
        return rows
    raw = [c.strip() for c in columns.split(",") if c.strip()]
    seen: set[str] = set()
    ordered: list[str] = []
    for c in raw:
        if c not in seen:
            seen.add(c)
            ordered.append(c)

    if kind == "challenge":
        avail = set(available_challenge_columns(wide))
        wide_only = set(_CHALLENGE_WIDE_EXTRA)
    elif kind == "org":
        avail = set(_ORG_COLS)
        wide_only = set()
    elif kind == "platform":
        avail = set(available_platform_columns(wide))
        wide_only = set(_PLATFORM_WIDE_EXTRA)
    else:  # pragma: no cover (defensive)
        raise ValueError(f"Unknown kind: {kind}")

    unknown = [c for c in ordered if c not in avail]
    if unknown:
        hint = (
            "Use --wide or remove wide-only fields."
            if kind == "challenge" and any(c in wide_only for c in unknown)
            else ""
        )
        typer.echo(
            "Unknown column(s): "
            + ", ".join(unknown)
            + "\nValid: "
            + ", ".join(sorted(avail))
            + (f"\n{hint}" if hint else ""),
            err=True,
        )
        raise typer.Exit(1)

    filtered: list[dict[str, Any]] = []
    for r in rows:
        filtered.append({k: r.get(k, "") for k in ordered})
    return filtered
