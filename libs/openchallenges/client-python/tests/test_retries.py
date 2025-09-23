from __future__ import annotations

import pytest

from openchallenges_client.config.loader import ClientConfig
from openchallenges_client.core.metrics import MetricsCollector
from openchallenges_client.gateways import _shared_paging as paging

"""Retry behavior tests.

These tests simulate transient failures in pagination to ensure the shared
`iter_paginated` logic respects the configured retry count. We monkeypatch the
sleep/backoff to avoid slow test execution.
"""


class _Transient(Exception):
    pass


def test_iter_paginated_retries_transient(monkeypatch):
    # Arrange: simulate two transient failures then success, with retries=3
    attempts = {"count": 0}

    def fetch_page(spec):  # spec unused beyond sequencing
        if attempts["count"] < 2:
            attempts["count"] += 1
            raise paging.ApiException(status=502)  # transient
        # success page after failures
        return {"items": [1, 2, 3]}

    def extract_items(raw):
        return raw.get("items")

    # Monkeypatch backoff to skip real sleeping
    monkeypatch.setattr(paging, "exponential_backoff_retry", lambda **_: None)

    cfg = ClientConfig(api_url="x", api_key=None, limit=5, retries=3)
    out = list(
        paging.iter_paginated(
            config=cfg,
            limit=3,
            fetch_page=fetch_page,
            extract_items=extract_items,
        )
    )
    assert out == [1, 2, 3]
    # Exactly two transient failures retried
    assert attempts["count"] == 2


def test_iter_paginated_exceeds_retries(monkeypatch):
    # Arrange: always fail with transient status, retries set to 1
    attempts = {"count": 0}

    def fetch_page(spec):
        attempts["count"] += 1
        raise paging.ApiException(status=503)

    def extract_items(raw):  # pragma: no cover - never reached
        return []

    monkeypatch.setattr(paging, "exponential_backoff_retry", lambda **_: None)

    cfg = ClientConfig(api_url="x", api_key=None, limit=5, retries=1)

    with pytest.raises(paging.ApiException):
        list(
            paging.iter_paginated(
                config=cfg,
                limit=3,
                fetch_page=fetch_page,
                extract_items=extract_items,
            )
        )
    # attempts should be initial try + 1 retry = 2
    assert attempts["count"] == 2


def test_iter_paginated_retries_metrics(monkeypatch):
    """MetricsCollector should record retries & emitted counts."""

    attempts = {"count": 0}

    def fetch_page(spec):
        # First two attempts transient fail, then success returning two pages of data
        if attempts["count"] < 2:
            attempts["count"] += 1
            raise paging.ApiException(status=502)
        # After failures, emit a short page (will terminate pagination)
        return {"items": ["a", "b", "c"]}

    def extract_items(raw):
        return raw.get("items")

    monkeypatch.setattr(paging, "exponential_backoff_retry", lambda **_: None)

    cfg = ClientConfig(api_url="x", api_key=None, limit=10, retries=5)
    metrics = MetricsCollector()
    out = list(
        paging.iter_paginated(
            config=cfg,
            limit=3,
            fetch_page=fetch_page,
            extract_items=extract_items,
            metrics=metrics,
        )
    )
    assert out == ["a", "b", "c"]
    # Two transient retries recorded
    assert metrics.retries == 2
    # Emitted count equals number of yielded items
    assert metrics.emitted == 3
    # No skips in this scenario
    assert metrics.skipped == 0
