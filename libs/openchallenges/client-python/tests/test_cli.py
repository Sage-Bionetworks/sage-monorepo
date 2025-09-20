from __future__ import annotations

from dataclasses import dataclass

from typer.testing import CliRunner

from openchallenges_client.cli.main import app
from openchallenges_client.domain.models import ChallengeSummary

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
            is_active=True,
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
            is_active=False,
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
