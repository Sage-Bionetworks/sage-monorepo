from __future__ import annotations

from dataclasses import dataclass
from datetime import date
from typing import Any, cast

from openchallenges_client.config.loader import ClientConfig
from openchallenges_client.services.list_challenges import ListChallengesService
from openchallenges_client.services.list_organizations import ListOrganizationsService


@dataclass
class _FakeChallenge:
    id: int
    slug: str
    name: str
    status: str | None = None
    platform: object | None = None
    start_date: date | None = None
    end_date: date | None = None
    # platform may be an object with id/name or None


def test_list_challenges_enrichment_duration_and_active():
    cfg = ClientConfig(api_url="x", api_key=None, limit=5)
    # ACTIVE challenge with valid date range
    active = _FakeChallenge(
        1,
        "s1",
        "Active Challenge",
        status="ACTIVE",
    )
    active.start_date = date(2024, 1, 1)
    active.end_date = date(2024, 1, 11)  # 10 days
    # COMPLETED challenge with invalid (reversed) date range -> duration None
    completed = _FakeChallenge(
        2,
        "s2",
        "Completed Challenge",
        status="COMPLETED",
    )
    completed.start_date = date(2024, 2, 10)
    completed.end_date = date(2024, 2, 1)
    gw = _ChallengeGatewayStub([active, completed])
    svc = ListChallengesService(cast(Any, gw), cfg)  # type: ignore[arg-type]
    results = list(svc.execute(limit=10, status=None))
    assert results[0].duration_days == 10
    # is_active removed; ACTIVE status still present
    assert results[1].duration_days is None  # reversed dates guard
    # is_active removed; COMPLETED status still present


@dataclass
class _FakePlatform:
    id: int | None
    name: str | None


@dataclass
class _FakeOrg:
    id: int
    name: str
    short_name: str | None = None
    website_url: str | None = None


class _ChallengeGatewayStub:
    def __init__(self, items):
        self._items = items

    def list_challenges(
        self,
        limit: int,
        status: list[str] | None = None,
        search_terms: str | None = None,
        metrics=None,
    ):  # noqa: D401
        return self._items[:limit]


class _OrgGatewayStub:
    def __init__(self, items):
        self._items = items

    def list_organizations(
        self,
        limit: int,
        search_terms: str | None = None,
        metrics=None,
    ):  # noqa: D401
        return self._items[:limit]


def test_list_challenges_service_basic():
    cfg = ClientConfig(api_url="x", api_key=None, limit=5)
    gw = _ChallengeGatewayStub(
        [
            _FakeChallenge(1, "s1", "Challenge 1"),
            _FakeChallenge(2, "s2", "Challenge 2"),
        ]
    )
    svc = ListChallengesService(cast(Any, gw), cfg)  # type: ignore[arg-type]
    results = list(svc.execute(limit=1, status=None))
    assert len(results) == 1
    assert results[0].slug == "s1"


def test_list_challenges_platform_null_and_present():
    cfg = ClientConfig(api_url="x", api_key=None, limit=5)
    gw = _ChallengeGatewayStub(
        [
            _FakeChallenge(1, "s1", "No Platform", platform=None),
            _FakeChallenge(
                2,
                "s2",
                "With Platform",
                platform=_FakePlatform(10, "CodaBench"),
            ),
        ]
    )
    svc = ListChallengesService(cast(Any, gw), cfg)  # type: ignore[arg-type]
    results = list(svc.execute(limit=5, status=None))
    # first challenge: platform fields None
    assert results[0].platform_id is None
    assert results[0].platform_name is None
    # second challenge: platform fields extracted
    assert results[1].platform_id == 10
    assert results[1].platform_name == "CodaBench"


def test_list_orgs_service_basic():
    cfg = ClientConfig(api_url="x", api_key=None, limit=5)
    gw = _OrgGatewayStub(
        [
            _FakeOrg(1, "Org A"),
            _FakeOrg(2, "Org B"),
        ]
    )
    svc = ListOrganizationsService(cast(Any, gw), cfg)  # type: ignore[arg-type]
    results = list(svc.execute(limit=2, search=None))
    assert len(results) == 2
    assert results[1].name == "Org B"
