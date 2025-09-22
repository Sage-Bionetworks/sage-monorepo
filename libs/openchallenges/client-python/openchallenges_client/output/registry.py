"""Pluggable output format registry.

Allows registering simple format functions that accept a sequence of
``dict[str, Any]`` and emit to stdout.

Built-in formats:

* table  – human friendly columnar view (Rich if available, fallback ascii)
* json   – buffered JSON array
* yaml   – buffered YAML list (optional dependency: PyYAML)
* ndjson – line-delimited JSON objects (incremental friendly)
"""

from __future__ import annotations

from collections.abc import Callable, Sequence
from typing import Any

_FORMATTERS: dict[str, Callable[[Sequence[dict[str, Any]]], None]] = {}


def register_format(
    name: str, func: Callable[[Sequence[dict[str, Any]]], None]
) -> None:
    _FORMATTERS[name.lower()] = func


def get_format(name: str) -> Callable[[Sequence[dict[str, Any]]], None] | None:
    return _FORMATTERS.get(name.lower())


def list_formats() -> list[str]:  # pragma: no cover (trivial)
    return sorted(_FORMATTERS.keys())


def register_default_formatters() -> None:
    # Late import to avoid circulars.
    from .formatters import to_json, to_ndjson, to_table, to_yaml  # type: ignore

    # Order here only affects list_formats() sorting indirectly.
    register_format("table", to_table)
    register_format("json", to_json)
    register_format("yaml", to_yaml)
    register_format("ndjson", to_ndjson)
