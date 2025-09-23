"""Assertion helpers for CLI output tests."""

from __future__ import annotations

from typing import Any
from collections.abc import Sequence

__all__ = [
    "assert_columns_exact",
    "assert_unknown_column_error",
]


def assert_columns_exact(
    rows: Sequence[dict[str, Any]],
    expected: Sequence[str],
    *,
    enforce_order: bool = True,
) -> None:
    for r in rows:
        if enforce_order:
            assert list(r.keys()) == list(expected), (
                f"Expected order {expected} got {list(r.keys())}"
            )
        else:
            assert set(r.keys()) == set(expected), (
                f"Expected keys {set(expected)} got {set(r.keys())}"
            )


def assert_unknown_column_error(
    output: str, unknown: str, *, expect_wide_hint: bool = False
) -> None:
    assert "Unknown column" in output
    assert unknown in output
    if expect_wide_hint:
        assert "wide" in output.lower()
