from __future__ import annotations

from dataclasses import dataclass

from typer.testing import CliRunner

from openchallenges_client.cli.main import app
from openchallenges_client.domain.models import ChallengeSummary, OrganizationSummary

runner = CliRunner()


@dataclass
class _StubClient:
    items: list[ChallengeSummary]

    def list_challenges(self, *, limit=None, status=None):  # mimic facade signature
        # Respect limit if provided
        if limit is not None:
            return self.items[:limit]
        return self.items


def _sample_items():
    return [
        ChallengeSummary(
            id=1,
            slug="challenge-one",
            name="First Challenge",
            status="ACTIVE",
            platform_id=10,
            platform_name="CodaBench",
            start_date=None,
            end_date=None,
            duration_days=None,
        ),
        ChallengeSummary(
            id=2,
            slug="challenge-two",
            name="Second Challenge",
            status="COMPLETED",
            platform_id=10,
            platform_name="CodaBench",
            start_date=None,
            end_date=None,
            duration_days=None,
        ),
    ]


def test_cli_challenges_list_table(monkeypatch):
    # Monkeypatch the internal _client factory used by Typer callback
    from openchallenges_client.cli import main as cli_main

    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda api_url, api_key, limit: _StubClient(_sample_items()),
    )
    result = runner.invoke(app, ["challenges", "list", "--limit", "2"])
    assert result.exit_code == 0, result.output
    out = result.output
    # Headers present
    assert "id" in out and "name" in out and "status" in out and "platform" in out
    # Removed slug column should not appear
    assert "slug" not in out
    # Status should be plain (no enum prefix)
    assert "ChallengeStatus." not in out
    assert "ACTIVE" in out and "COMPLETED" in out


def test_cli_challenges_list_json(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda api_url, api_key, limit: _StubClient(_sample_items()),
    )
    result = runner.invoke(
        app,
        [
            "challenges",
            "list",
            "--limit",
            "1",
            "--output",
            "json",
        ],
    )
    assert result.exit_code == 0, result.output
    import json as _json

    data = _json.loads(result.output)
    assert isinstance(data, list) and len(data) == 1
    first = data[0]
    # Expected keys (subset) and absence of slug
    assert {"id", "name", "status", "platform"}.issubset(first.keys())
    assert "slug" not in first
    # Status plain
    assert first["status"] == "ACTIVE"


def test_cli_orgs_blank_short_name(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _OrgStub:
        def list_organizations(self, *, limit=None, search=None):
            return [
                OrganizationSummary(
                    id=1,
                    name="Org One",
                    short_name=None,  # should render as blank
                    website_url="https://example.org",
                ),
                OrganizationSummary(
                    id=2,
                    name="Org Two",
                    short_name="O2",
                    website_url=None,
                ),
            ]

    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda api_url, api_key, limit: _OrgStub(),
    )
    result = runner.invoke(app, ["orgs", "list", "--limit", "5", "--output", "json"])
    assert result.exit_code == 0, result.output
    import json as _json

    data = _json.loads(result.output)
    org_one = next(o for o in data if o["name"] == "Org One")
    assert org_one["short_name"] == ""  # blanked out
    # Second organization keeps its short name
    org_two = next(o for o in data if o["name"] == "Org Two")
    assert org_two["short_name"] == "O2"


def test_cli_challenges_blank_platform(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _ChallengeStub:
        def list_challenges(self, *, limit=None, status=None):
            return [
                ChallengeSummary(
                    id=1,
                    slug="c1",
                    name="C1",
                    status="ACTIVE",
                    platform_id=None,
                    platform_name=None,  # should blank
                    start_date=None,
                    end_date=None,
                    duration_days=None,
                ),
                ChallengeSummary(
                    id=2,
                    slug="c2",
                    name="C2",
                    status="ACTIVE",
                    platform_id=5,
                    platform_name="SomePlatform",
                    start_date=None,
                    end_date=None,
                    duration_days=None,
                ),
            ]

    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda api_url, api_key, limit: _ChallengeStub(),
    )
    result = runner.invoke(
        app, ["challenges", "list", "--limit", "10", "--output", "json"]
    )
    assert result.exit_code == 0, result.output
    import json as _json

    data = _json.loads(result.output)
    c1 = next(c for c in data if c["id"] == 1)
    c2 = next(c for c in data if c["id"] == 2)
    assert c1["platform"] == ""
    assert c2["platform"] == "SomePlatform"
