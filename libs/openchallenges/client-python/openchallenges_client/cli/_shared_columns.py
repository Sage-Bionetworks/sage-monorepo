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
    if not wide:
        return list(_PLATFORM_BASE_COLS)
    # Derive wide ordering from base + extras without a third constant:
    # place 'slug' (if present) immediately after 'id', then keep remaining
    # base columns, then remaining extras.
    base = list(_PLATFORM_BASE_COLS)
    extras = list(_PLATFORM_WIDE_EXTRA)
    wide_order: list[str] = []
    if base:
        # Always start with first base column (expected 'id')
        wide_order.append(base[0])
    if "slug" in extras:
        wide_order.append("slug")
        extras.remove("slug")
    # Append remaining base (excluding first already added)
    wide_order.extend(base[1:])
    # Finally append any remaining extras (e.g., avatar_key)
    wide_order.extend(extras)
    return wide_order


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


def apply_column_selection(
    rows: list[dict[str, Any]], columns: str | None
) -> list[dict[str, Any]]:
    """Return rows limited to user-specified columns while preserving order.

    Design goals:
    - Only two parameters (rows + columns spec) for reuse across future CLI
      clients without knowledge of resource kinds or wide/base semantics.
    - If ``columns`` is None we trust the input row ordering (callers ensure
      they construct rows with the desired default ordering / width).
    - If ``columns`` is provided we keep the user's order and drop unknown
      columns silently (robust across version drift). This is intentionally
      more forgiving than ``filter_columns`` which validates & errors; callers
      wanting strict validation can still invoke ``filter_columns`` first.
    """
    if not columns:
        return rows
    desired: list[str] = []
    seen: set[str] = set()
    for part in columns.split(","):
        name = part.strip()
        if not name or name in seen:
            continue
        seen.add(name)
        desired.append(name)
    trimmed: list[dict[str, Any]] = []
    for r in rows:
        trimmed.append({k: r.get(k, "") for k in desired if k in r})
    return trimmed
