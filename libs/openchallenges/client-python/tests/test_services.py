from __future__ import annotations

from dataclasses import dataclass

from openchallenges_client.services.list_challenges import ListChallengesService
from openchallenges_client.services.list_organizations import ListOrganizationsService
from openchallenges_client.config.loader import ClientConfig


@dataclass
class _FakeChallenge:
    id: int
    slug: str
    name: str
    status: str | None = None
    platform: object | None = None
    start_date = None
    end_date = None


@dataclass
class _FakeOrg:
    id: int
    name: str
    acronym: str | None = None
    website_url: str | None = None


class _ChallengeGatewayStub:
    def __init__(self, items):
        self._items = items

    # updated to accept status for compatibility with service call
    def list_challenges(self, limit: int, status=None):
        return self._items[:limit]


class _OrgGatewayStub:
    def __init__(self, items):
        self._items = items

    def list_organizations(self, limit: int, search_terms=None):
        return self._items[:limit]


def test_list_challenges_service_basic():
    cfg = ClientConfig(api_url="x", api_key=None, default_limit=5)
    gw = _ChallengeGatewayStub(
        [
            _FakeChallenge(1, "s1", "Challenge 1"),
            _FakeChallenge(2, "s2", "Challenge 2"),
        ]
    )
    svc = ListChallengesService(gw, cfg)
    results = list(svc.execute(limit=1, status=None))
    assert len(results) == 1
    assert results[0].slug == "s1"


def test_list_orgs_service_basic():
    cfg = ClientConfig(api_url="x", api_key=None, default_limit=5)
    gw = _OrgGatewayStub(
        [
            _FakeOrg(1, "Org A"),
            _FakeOrg(2, "Org B"),
        ]
    )
    svc = ListOrganizationsService(gw, cfg)
    results = list(svc.execute(limit=2, search=None))
    assert len(results) == 2
    assert results[1].name == "Org B"
