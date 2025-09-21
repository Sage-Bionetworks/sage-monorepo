"""Shared pagination & retry utilities for gateways.

These helpers centralize patterns used by challenge & organization gateways so
that future resource gateways (datasets, submissions, etc.) can reuse the same
logic. Intent is to later extract this module into a separate support library.
"""

from __future__ import annotations

import random
import time
from collections.abc import Callable, Iterable, Iterator
from dataclasses import dataclass
from typing import Protocol, TypeVar

from openchallenges_api_client.rest import ApiException

from ..config.loader import ClientConfig
from ..core.metrics import MetricsCollector
from ..core.errors import OpenChallengesError

T = TypeVar("T")  # Raw SDK model


class _ApiCaller(Protocol):  # pragma: no cover - structural typing helper
    def __call__(self) -> object: ...


@dataclass
class PageSpec:
    """Description of a page to fetch."""

    page_number: int
    page_size: int


def exponential_backoff_retry(
    *,
    attempt: int,
    max_sleep: float = 30.0,
) -> None:
    """Sleep using exponential backoff with jitter for a given attempt."""

    sleep_for = (2**attempt) + random.random()
    time.sleep(min(sleep_for, max_sleep))


def iter_paginated(
    *,
    config: ClientConfig,
    limit: int,
    fetch_page: Callable[[PageSpec], object],
    extract_items: Callable[[object], Iterable[object] | None],
    transient_statuses: set[int] | None = None,
    metrics: MetricsCollector | None = None,
) -> Iterator[object]:
    """Generic pagination loop with retry on transient statuses.

    Args:
        config: client configuration (retries attribute consulted).
        limit: overall maximum items to yield.
        fetch_page: callable that accepts a PageSpec and returns raw page object.
        extract_items: callable extracting an iterable of items from the raw
            page, or None.
        transient_statuses: HTTP statuses considered transient for which to retry.
    """

    if limit <= 0:
        return iter(())

    remaining = limit
    page_number = 0
    page_cap = 100
    transient_statuses = transient_statuses or {429, 500, 502, 503, 504}

    attempt = 0
    while remaining > 0:
        requested = min(page_cap, remaining)
        spec = PageSpec(page_number=page_number, page_size=requested)
        try:
            raw_page = fetch_page(spec)
        except ApiException as e:  # pragma: no cover (difficult to unit hit fully)
            status = getattr(e, "status", None)
            if status in transient_statuses and attempt < config.retries:
                exponential_backoff_retry(attempt=attempt)
                if metrics is not None:
                    metrics.inc_retries()
                attempt += 1
                continue
            raise
        except Exception as e:  # pragma: no cover
            raise OpenChallengesError(f"Fetch failure: {e}") from e

        attempt = 0
        items = extract_items(raw_page) if raw_page is not None else None
        if not items:
            break
        emitted_this_page = 0
        for obj in items:
            if remaining <= 0:
                break
            yield obj
            remaining -= 1
            emitted_this_page += 1
        if metrics is not None and emitted_this_page:
            metrics.inc_emitted(emitted_this_page)
        if emitted_this_page == 0:
            break
        if remaining <= 0:
            break
        if emitted_this_page < requested:
            # Page shorter than requested -> likely end of collection.
            break
        page_number += 1
