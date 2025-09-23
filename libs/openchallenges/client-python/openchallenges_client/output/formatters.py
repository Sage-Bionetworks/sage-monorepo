"""Output formatting helpers (table/json/yaml)."""

from __future__ import annotations

import json
from collections.abc import Iterable, Sequence
from typing import Any

try:  # optional dependency
    import yaml  # type: ignore
except Exception:  # pragma: no cover
    yaml = None  # type: ignore

try:
    from rich.console import Console
    from rich.table import Table
except ImportError:  # pragma: no cover
    Table = None  # type: ignore
    Console = None  # type: ignore


def _enum_to_name(value: Any) -> Any:
    """Return enum name (ACTIVE) instead of repr (ChallengeStatus.ACTIVE)."""
    try:
        from enum import Enum  # local import to avoid unconditional dependency

        if isinstance(value, Enum):
            return value.name
    except Exception:  # pragma: no cover
        pass
    return value


def _normalize_row(row: dict[str, Any]) -> dict[str, Any]:
    """Return a display-friendly row (simplify enums).

    Previously we dropped the 'slug' field globally. This prevented showing the
    platform slug in wide mode for the `platforms list` command. We now retain
    all keys so callers can explicitly control which columns are emitted.
    """
    return {k: _enum_to_name(v) for k, v in row.items()}


def to_table(rows: Iterable[dict[str, Any]], *, title: str | None = None) -> None:
    rows = list(rows)
    if not rows:
        print("(no results)")
        return
    norm_rows = [_normalize_row(r) for r in rows]
    first = norm_rows[0]
    if Table and Console:
        table = Table(title=title)
        for col in first:
            if col == "name":
                table.add_column(col, overflow="fold")
            else:
                table.add_column(col)
        for r in norm_rows:
            table.add_row(*[str(r.get(k, "")) for k in first])
        Console().print(table)
    else:  # fallback
        headers = list(first.keys())
        print(" | ".join(headers))
        print("-+-".join("-" * len(h) for h in headers))
        for r in norm_rows:
            print(" | ".join(str(r.get(h, "")) for h in headers))


def to_json(rows: Sequence[dict[str, Any]]) -> None:
    norm = [_normalize_row(r) for r in rows]
    print(json.dumps(norm, indent=2, default=str))


def to_yaml(rows: Sequence[dict[str, Any]]) -> None:
    if not yaml:  # pragma: no cover
        print("YAML support not installed. Run: pip install pyyaml")
        return
    norm = [_normalize_row(r) for r in rows]
    print(yaml.safe_dump(norm, sort_keys=False))


def to_ndjson(rows: Iterable[dict[str, Any]]) -> None:
    """Emit one normalized JSON object per line (incremental-friendly)."""
    for r in rows:
        norm = _normalize_row(r)
        # Compact form; flush for progressive processing
        print(
            json.dumps(norm, separators=(",", ":"), default=str),
            flush=True,
        )
