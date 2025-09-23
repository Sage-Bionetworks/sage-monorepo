"""Stub factory utilities for constructing test clients.

These factories simulate list_* and iter_all_* behaviors for different entity
collections, including optional pagination and transient failure injection.
They remain internal until multiple Python clients need them.
"""

from __future__ import annotations

from dataclasses import dataclass
from typing import Any

try:  # pragma: no cover - optional import for type alignment
    from openchallenges_api_client.rest import ApiException  # type: ignore
except Exception:  # pragma: no cover

    class ApiException(Exception):  # fallback minimal
        def __init__(self, status: int | None = None):
            super().__init__(f"ApiException status={status}")
            self.status = status


@dataclass
class _CollectionSpec:
    items: list[Any]
    page_size: int
    transient_fail_on_pages: set[int]
    transient_status: int


class StubClient:
    """Unified stub client that mimics facade surface for tests.

    Supported methods (conditionally based on provided collections):
      list_challenges(limit=None, status=None)
      iter_all_challenges(status=None)
      list_organizations(limit=None, search=None)
      iter_all_organizations(search=None)
    """

    def __init__(
        self,
        *,
        challenges: list[Any] | None = None,
        organizations: list[Any] | None = None,
        simulate_pagination: bool = False,
        page_size: int = 50,
        transient_fail_on_pages: set[int] | None = None,
        transient_status: int = 502,
    ) -> None:
        self._simulate_pagination = simulate_pagination
        self._page_size = page_size
        tf = transient_fail_on_pages or set()
        self._challenge_spec = (
            _CollectionSpec(challenges, page_size, tf, transient_status)
            if challenges is not None
            else None
        )
        self._org_spec = (
            _CollectionSpec(organizations, page_size, tf, transient_status)
            if organizations is not None
            else None
        )

    # -------------- Generic helpers --------------
    def _paged_iter(self, spec: _CollectionSpec):
        items = spec.items
        if not self._simulate_pagination:
            for obj in items:
                yield obj
            return
        page_number = 0
        idx = 0
        while idx < len(items):
            if page_number in spec.transient_fail_on_pages:
                # simulate transient error; caller under test should retry
                raise ApiException(status=spec.transient_status)
            batch = items[idx : idx + spec.page_size]
            for obj in batch:
                yield obj
            if len(batch) < spec.page_size:
                break
            idx += spec.page_size
            page_number += 1

    # -------------- Challenges --------------
    def list_challenges(self, *, limit=None, status=None):  # mimic facade
        if not self._challenge_spec:
            raise AttributeError("Challenges not configured in this stub")
        data = self._challenge_spec.items
        if limit is not None:
            data = data[:limit]
        return data

    def iter_all_challenges(self, *, status=None):
        if not self._challenge_spec:
            raise AttributeError("Challenges not configured in this stub")
        yield from self._paged_iter(self._challenge_spec)

    # -------------- Organizations --------------
    def list_organizations(self, *, limit=None, search=None):
        if not self._org_spec:
            raise AttributeError("Organizations not configured in this stub")
        data = self._org_spec.items
        if limit is not None:
            data = data[:limit]
        return data

    def iter_all_organizations(self, *, search=None):
        if not self._org_spec:
            raise AttributeError("Organizations not configured in this stub")
        yield from self._paged_iter(self._org_spec)


def make_stub_client(**kwargs) -> StubClient:
    """Public factory wrapper for readability in tests."""
    return StubClient(**kwargs)
