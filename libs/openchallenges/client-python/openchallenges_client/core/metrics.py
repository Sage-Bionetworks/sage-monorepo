"""Lightweight metrics collection utilities.

These counters are intentionally minimal and write-only during collection; the
CLI reads them after an operation (list / stream) completes to expose
observability with ``--verbose``.
"""

from __future__ import annotations

from dataclasses import dataclass


@dataclass
class MetricsCollector:
    """Mutable counters aggregated during gateway operations.

    Attributes:
        emitted: Number of successfully validated (and yielded) items.
        skipped: Number of items dropped due to validation errors.
        retries: Number of retry attempts performed for transient failures.
    """

    emitted: int = 0
    skipped: int = 0
    retries: int = 0

    def inc_emitted(self, n: int = 1) -> None:  # pragma: no cover - trivial
        self.emitted += n

    def inc_skipped(self, n: int = 1) -> None:  # pragma: no cover - trivial
        self.skipped += n

    def inc_retries(self, n: int = 1) -> None:  # pragma: no cover - trivial
        self.retries += n
