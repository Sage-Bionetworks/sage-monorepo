"""NDJSON and structured output parsing helpers for tests.

These utilities are intentionally lightweight and internal to this client. They
can be migrated to a shared test support package when additional Python clients
are introduced.
"""

from __future__ import annotations

from typing import Any
import json

__all__ = [
    "parse_ndjson",
]


def parse_ndjson(
    text: str,
    *,
    expect_count: int | None = None,
    required_keys: set[str] | None = None,
    dict_only: bool = True,
) -> list[dict[str, Any]]:
    """Parse line-delimited JSON (NDJSON) into a list of objects.

    Args:
        text: Raw NDJSON text (possibly with blank lines).
        expect_count: If provided, assert parsed row count equals this value.
        required_keys: If provided, assert each row contains this key subset.
        dict_only: When True, enforce each line decodes to a JSON object (dict).

    Returns:
        List of decoded JSON objects.

    Raises:
        AssertionError: On malformed JSON, type mismatch, missing keys, or
            count mismatch.
    """
    rows: list[dict[str, Any]] = []
    for idx, line in enumerate(text.splitlines(), start=1):
        if not line.strip():
            continue
        try:
            obj = json.loads(line)
        except ValueError as e:  # pragma: no cover (rare malformed injection)
            raise AssertionError(
                f"Malformed JSON on line {idx}: {e}\nLine: {line}"
            ) from e
        if dict_only and not isinstance(obj, dict):
            raise AssertionError(f"Line {idx} is not a JSON object: {obj!r}")
        if required_keys and not required_keys.issubset(obj.keys()):
            missing = required_keys - set(obj.keys())
            raise AssertionError(f"Line {idx} missing keys: {missing}")
        rows.append(obj)
    if expect_count is not None and len(rows) != expect_count:
        raise AssertionError(
            f"Expected {expect_count} rows, got {len(rows)}"  # pragma: no cover
        )
    return rows
