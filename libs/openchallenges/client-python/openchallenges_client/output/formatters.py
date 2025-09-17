"""Output formatting helpers (table/json)."""

from __future__ import annotations

import json
from collections.abc import Iterable, Sequence
from typing import Any

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
        for col in first.keys():
            table.add_column(col)
        for r in rows:
            table.add_row(*[str(r.get(k, "")) for k in first.keys()])
        Console().print(table)
    else:  # fallback
        headers = list(first.keys())
        print(" | ".join(headers))
        print("-+-".join("-" * len(h) for h in headers))
        for r in rows:
            print(" | ".join(str(r.get(h, "")) for h in headers))


def to_json(rows: Sequence[dict[str, Any]]) -> None:
    print(json.dumps(list(rows), indent=2, default=str))
