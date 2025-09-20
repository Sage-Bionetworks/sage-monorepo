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


def to_table(rows: Iterable[dict[str, Any]], *, title: str | None = None) -> None:
    first = None
    rows = list(rows)
    if not rows:
        print("(no results)")
        return
    first = rows[0]
    if Table and Console:
        table = Table(title=title)
        for col in first:
            # Allow long slug values to wrap across lines instead of being truncated
            # Rich defaults to an ellipsis overflow for some wide values; explicit
            # overflow='fold' ensures multi-line display similar to the challenge name.
            if col == "slug":
                table.add_column(col, overflow="fold")
            else:
                table.add_column(col)
        for r in rows:
            table.add_row(*[str(r.get(k, "")) for k in first])
        Console().print(table)
    else:  # fallback
        headers = list(first.keys())
        print(" | ".join(headers))
        print("-+-".join("-" * len(h) for h in headers))
        for r in rows:
            print(" | ".join(str(r.get(h, "")) for h in headers))


def to_json(rows: Sequence[dict[str, Any]]) -> None:
    print(json.dumps(list(rows), indent=2, default=str))


def to_yaml(rows: Sequence[dict[str, Any]]) -> None:
    if not yaml:  # pragma: no cover
        print("YAML support not installed. Run: pip install pyyaml")
        return
    print(yaml.safe_dump(list(rows), sort_keys=False))
