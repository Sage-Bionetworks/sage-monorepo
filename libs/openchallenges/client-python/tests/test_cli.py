from __future__ import annotations

import json as _json
from dataclasses import dataclass

from typer.testing import CliRunner

from openchallenges_client.cli.main import app
from openchallenges_client.domain.models import ChallengeSummary, OrganizationSummary
from tests.utils.assertions import (
    assert_columns_exact,
    assert_unknown_column_error,
)
from tests.utils.parsing import parse_ndjson

runner = CliRunner()


@dataclass
class _StubClient:
    items: list[ChallengeSummary]

    def list_challenges(
        self, *, limit=None, status=None, search=None, metrics=None
    ):  # mimic current facade signature
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
    # Validate required columns present (order not enforced for JSON output)
    assert_columns_exact(
        data,
        ["id", "name", "status", "platform"],
        enforce_order=False,
    )
    first = data[0]
    assert "slug" not in first
    assert first["status"] == "ACTIVE"  # plain status


def test_cli_platforms_list_default_columns(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _PlatformSummary:
        def __init__(self, id, slug, name, website_url, avatar_key):
            self.id = id
            self.slug = slug
            self.name = name
            self.website_url = website_url
            self.avatar_key = avatar_key

    class _PlatformStub:
        def list_platforms(self, *, limit=None, search=None, metrics=None):
            return [
                _PlatformSummary(
                    1,
                    "codabench",
                    "CodaBench",
                    "https://cb.org",
                    "logo/cb.png",
                ),
                _PlatformSummary(2, "synapse", "Synapse", None, None),
            ]

    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda api_url, api_key, limit: _PlatformStub(),
    )
    result = runner.invoke(app, ["platforms", "list", "--limit", "2"])
    assert result.exit_code == 0, result.output
    out = result.output
    # Only id, name, website_url should be present in headers; slug/avatar_key absent
    assert "id" in out and "name" in out and "website_url" in out
    assert "slug" not in out and "avatar_key" not in out


def test_cli_platforms_list_wide(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _PlatformSummary:
        def __init__(self, id, slug, name, website_url, avatar_key):
            self.id = id
            self.slug = slug
            self.name = name
            self.website_url = website_url
            self.avatar_key = avatar_key

    class _PlatformStub:
        def list_platforms(self, *, limit=None, search=None, metrics=None):
            return [
                _PlatformSummary(
                    1,
                    "codabench",
                    "CodaBench",
                    "https://cb.org",
                    "logo/cb.png",
                ),
            ]

    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda api_url, api_key, limit: _PlatformStub(),
    )
    result = runner.invoke(app, ["platforms", "list", "--limit", "1", "--wide"])
    assert result.exit_code == 0, result.output
    out = result.output
    # All wide columns should appear
    assert (
        "id" in out
        and "slug" in out
        and "name" in out
        and "website_url" in out
        and "avatar_key" in out
    )


def test_cli_orgs_blank_short_name(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _OrgStub:
        def list_organizations(self, *, limit=None, search=None, metrics=None):
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
    # All rows have the expected org columns
    assert_columns_exact(
        data,
        ["id", "name", "short_name", "website_url"],
        enforce_order=False,
    )
    org_one = next(o for o in data if o["name"] == "Org One")
    assert org_one["short_name"] == ""  # blanked out
    org_two = next(o for o in data if o["name"] == "Org Two")
    assert org_two["short_name"] == "O2"


def test_cli_challenges_blank_platform(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _ChallengeStub:
        def list_challenges(
            self, *, limit=None, status=None, search=None, metrics=None
        ):
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
    assert_columns_exact(
        data,
        ["id", "name", "status", "platform"],
        enforce_order=False,
    )
    c1 = next(c for c in data if c["id"] == 1)
    c2 = next(c for c in data if c["id"] == 2)
    assert c1["platform"] == ""
    assert c2["platform"] == "SomePlatform"


# ---------------- NDJSON & Column Selection Tests -----------------


def _make_stream_items(n: int, wide: bool = False) -> list[ChallengeSummary]:
    items: list[ChallengeSummary] = []
    for i in range(1, n + 1):
        items.append(
            ChallengeSummary(
                id=i,
                slug=f"c{i}",
                name=f"Challenge {i}",
                status="ACTIVE" if i % 2 else "COMPLETED",
                platform_id=None if i % 3 else 10,
                platform_name=None if i % 3 else "CodaBench",
                start_date=None,
                end_date=None,
                duration_days=42 if wide else None,
            )
        )
    return items


def test_cli_challenges_stream_ndjson_count(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _StreamStub:
        def iter_all_challenges(self, *, status=None, search=None, metrics=None):
            yield from _make_stream_items(5)

    monkeypatch.setattr(cli_main, "_client", lambda *a, **k: _StreamStub())
    result = runner.invoke(
        app,
        [
            "challenges",
            "stream",
            "--limit",
            "5",
            "--output",
            "ndjson",
        ],
    )
    assert result.exit_code == 0, result.output
    rows = parse_ndjson(
        result.output,
        expect_count=5,
        required_keys={"id", "name", "status"},
    )
    assert_columns_exact(
        rows,
        ["id", "name", "status", "platform"],
        enforce_order=False,
    )


def test_cli_challenges_stream_ndjson_wide(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _StreamStub:
        def iter_all_challenges(self, *, status=None, search=None, metrics=None):
            yield from _make_stream_items(3, wide=True)

    monkeypatch.setattr(cli_main, "_client", lambda *a, **k: _StreamStub())
    result = runner.invoke(
        app,
        [
            "challenges",
            "stream",
            "--limit",
            "3",
            "--output",
            "ndjson",
            "--wide",
        ],
    )
    assert result.exit_code == 0, result.output
    rows = parse_ndjson(result.output, expect_count=3)
    for obj in rows:
        assert {"start_date", "end_date", "duration_days"}.issubset(obj.keys())


def test_cli_challenges_list_columns_subset_json(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda *a, **k: _StubClient(_sample_items()),
    )
    result = runner.invoke(
        app,
        [
            "challenges",
            "list",
            "--limit",
            "2",
            "--output",
            "json",
            "--columns",
            "id,name",
        ],
    )
    assert result.exit_code == 0, result.output
    data = _json.loads(result.output)
    assert_columns_exact(data, ["id", "name"], enforce_order=True)


def test_cli_challenges_stream_ndjson_columns_subset(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _StreamStub:
        def iter_all_challenges(self, *, status=None, search=None, metrics=None):
            yield from _make_stream_items(2)

    monkeypatch.setattr(cli_main, "_client", lambda *a, **k: _StreamStub())
    result = runner.invoke(
        app,
        [
            "challenges",
            "stream",
            "--limit",
            "2",
            "--output",
            "ndjson",
            "--columns",
            "id,status",
        ],
    )
    assert result.exit_code == 0, result.output
    rows = parse_ndjson(result.output, expect_count=2, required_keys={"id", "status"})
    assert_columns_exact(rows, ["id", "status"], enforce_order=True)


def test_cli_challenges_list_unknown_column_error(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda *a, **k: _StubClient(_sample_items()),
    )
    result = runner.invoke(
        app,
        [
            "challenges",
            "list",
            "--columns",
            "id,notacolumn",
        ],
    )
    assert result.exit_code != 0
    assert_unknown_column_error(result.output, "notacolumn")


def test_cli_challenges_list_wide_only_column_without_wide(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda *a, **k: _StubClient(_sample_items()),
    )
    result = runner.invoke(
        app,
        [
            "challenges",
            "list",
            "--columns",
            "id,duration_days",
        ],
    )
    assert result.exit_code != 0
    assert_unknown_column_error(result.output, "duration_days", expect_wide_hint=True)


# ---------------- Organization NDJSON & Column Selection Parity Tests -----------------


def _sample_org_items(n: int) -> list[OrganizationSummary]:
    return [
        OrganizationSummary(
            id=i,
            name=f"Org {i}",
            short_name=None if i % 2 else f"O{i}",
            website_url=f"https://org{i}.example.com" if i % 3 else None,
        )
        for i in range(1, n + 1)
    ]


def test_cli_orgs_stream_ndjson_count(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _OrgStreamStub:
        def iter_all_organizations(self, *, search=None, metrics=None):
            yield from _sample_org_items(4)

    monkeypatch.setattr(cli_main, "_client", lambda *a, **k: _OrgStreamStub())
    result = runner.invoke(
        app,
        [
            "orgs",
            "stream",
            "--limit",
            "4",
            "--output",
            "ndjson",
        ],
    )
    assert result.exit_code == 0, result.output
    lines = [ln for ln in result.output.strip().splitlines() if ln.strip()]
    assert len(lines) == 4
    for ln in lines:
        obj = _json.loads(ln)
        assert set(obj.keys()) == {"id", "name", "short_name", "website_url"}


def test_cli_orgs_list_columns_subset_json(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _OrgStub:
        def list_organizations(self, *, limit=None, search=None, metrics=None):
            return _sample_org_items(3)

    monkeypatch.setattr(cli_main, "_client", lambda *a, **k: _OrgStub())
    result = runner.invoke(
        app,
        [
            "orgs",
            "list",
            "--limit",
            "3",
            "--output",
            "json",
            "--columns",
            "id,name",
        ],
    )
    assert result.exit_code == 0, result.output
    data = _json.loads(result.output)
    assert all(set(d.keys()) == {"id", "name"} for d in data)


def test_cli_challenges_stream_reject_non_ndjson(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _StreamStub:
        def iter_all_challenges(self, *, status=None, search=None, metrics=None):
            yield from _make_stream_items(1)

    monkeypatch.setattr(cli_main, "_client", lambda *a, **k: _StreamStub())
    result = runner.invoke(
        app,
        [
            "challenges",
            "stream",
            "--limit",
            "1",
            "--output",
            "table",
        ],
    )
    assert result.exit_code != 0
    assert "stream supports only ndjson" in result.output.lower()


def test_cli_orgs_stream_reject_non_ndjson(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _OrgStreamStub:
        def iter_all_organizations(self, *, search=None, metrics=None):
            yield from _sample_org_items(1)

    monkeypatch.setattr(cli_main, "_client", lambda *a, **k: _OrgStreamStub())
    result = runner.invoke(
        app,
        [
            "orgs",
            "stream",
            "--limit",
            "1",
            "--output",
            "yaml",
        ],
    )
    assert result.exit_code != 0
    assert "stream supports only ndjson" in result.output.lower()


def test_cli_platforms_stream_ndjson_count(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _PlatformSummary:
        def __init__(self, id, slug, name, website_url, avatar_key):
            self.id = id
            self.slug = slug
            self.name = name
            self.website_url = website_url
            self.avatar_key = avatar_key

    class _PlatformStreamStub:
        def iter_all_platforms(self, *, search=None, metrics=None):
            yield _PlatformSummary(
                1,
                "codabench",
                "CodaBench",
                "https://cb.org",
                "logo/cb.png",
            )
            yield _PlatformSummary(2, "synapse", "Synapse", None, None)

    monkeypatch.setattr(cli_main, "_client", lambda *a, **k: _PlatformStreamStub())
    result = runner.invoke(
        app,
        [
            "platforms",
            "stream",
            "--limit",
            "2",
            "--output",
            "ndjson",
        ],
    )
    assert result.exit_code == 0, result.output
    lines = [ln for ln in result.output.strip().splitlines() if ln.strip()]
    assert len(lines) == 2
    # Ensure required keys present
    for ln in lines:
        obj = _json.loads(ln)
        assert {"id", "name", "website_url"}.issubset(obj.keys())


def test_cli_platforms_stream_reject_non_ndjson(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _PlatformStreamStub:
        def iter_all_platforms(self, *, search=None, metrics=None):
            yield from []

    monkeypatch.setattr(cli_main, "_client", lambda *a, **k: _PlatformStreamStub())
    result = runner.invoke(
        app,
        [
            "platforms",
            "stream",
            "--limit",
            "1",
            "--output",
            "json",
        ],
    )
    assert result.exit_code != 0
    assert "stream supports only ndjson" in result.output.lower()


def test_cli_challenges_list_all_overrides_limit(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _ListAllStub:
        def list_challenges(
            self, *, limit=None, status=None, search=None, metrics=None
        ):
            # Should not be used when --all provided (limit path)
            return [
                ChallengeSummary(
                    id=999,
                    slug="ignored",
                    name="Ignored",
                    status="ACTIVE",
                    platform_id=None,
                    platform_name=None,
                    start_date=None,
                    end_date=None,
                    duration_days=None,
                )
            ]

        def iter_all_challenges(self, *, status=None, search=None, metrics=None):
            for i in range(1, 6):  # 5 items > limit we will specify
                yield ChallengeSummary(
                    id=i,
                    slug=f"c{i}",
                    name=f"Challenge {i}",
                    status="ACTIVE",
                    platform_id=None,
                    platform_name=None,
                    start_date=None,
                    end_date=None,
                    duration_days=None,
                )

    monkeypatch.setattr(cli_main, "_client", lambda *a, **k: _ListAllStub())
    result = runner.invoke(
        app,
        [
            "challenges",
            "list",
            "--limit",
            "2",
            "--all",
            "--output",
            "json",
        ],
    )
    assert result.exit_code == 0, result.output
    # First line contains the override notice; strip it if present
    lines = result.output.strip().splitlines()
    if lines and lines[0].startswith("--all overrides --limit"):
        json_text = "\n".join(lines[1:])
    else:  # pragma: no cover - safety
        json_text = result.output
    data = _json.loads(json_text)
    # Expect 5 items from iterator despite limit=2
    assert len(data) == 5


def test_cli_challenges_list_ndjson_output(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda *a, **k: _StubClient(_sample_items()),
    )
    result = runner.invoke(
        app,
        [
            "challenges",
            "list",
            "--limit",
            "2",
            "--output",
            "ndjson",
        ],
    )
    assert result.exit_code == 0, result.output
    lines = [ln for ln in result.output.strip().splitlines() if ln.strip()]
    assert len(lines) == 2
    # Each line should be valid JSON with required keys
    for ln in lines:
        obj = _json.loads(ln)
        assert {"id", "name", "status", "platform"}.issubset(obj.keys())


def test_cli_orgs_stream_ndjson_columns_subset(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _OrgStreamStub:
        def iter_all_organizations(self, *, search=None, metrics=None):
            yield from _sample_org_items(2)

    monkeypatch.setattr(cli_main, "_client", lambda *a, **k: _OrgStreamStub())
    result = runner.invoke(
        app,
        [
            "orgs",
            "stream",
            "--limit",
            "2",
            "--output",
            "ndjson",
            "--columns",
            "name,id",
        ],
    )
    assert result.exit_code == 0, result.output
    lines = result.output.strip().splitlines()
    for ln in lines:
        obj = _json.loads(ln)
        # Order preserved in insertion order of dict (Python 3.7+)
        assert list(obj.keys()) == ["name", "id"]


def test_cli_orgs_list_unknown_column_error(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _OrgStub:
        def list_organizations(self, *, limit=None, search=None, metrics=None):
            return _sample_org_items(1)

    monkeypatch.setattr(cli_main, "_client", lambda *a, **k: _OrgStub())
    result = runner.invoke(
        app,
        [
            "orgs",
            "list",
            "--columns",
            "id,notacolumn",
        ],
    )
    assert result.exit_code != 0
    assert "Unknown column" in result.output


def test_cli_orgs_list_duplicate_columns_dedup(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _OrgStub:
        def list_organizations(self, *, limit=None, search=None, metrics=None):
            return _sample_org_items(1)

    monkeypatch.setattr(cli_main, "_client", lambda *a, **k: _OrgStub())
    result = runner.invoke(
        app,
        [
            "orgs",
            "list",
            "--limit",
            "1",
            "--output",
            "json",
            "--columns",
            "name,id,name",
        ],
    )
    assert result.exit_code == 0, result.output
    data = _json.loads(result.output)
    assert list(data[0].keys()) == ["name", "id"]  # dedup preserved order


# ---------------- Verbose Metrics Tests -----------------


def test_cli_challenges_list_verbose_metrics(monkeypatch):
    """Verbose mode should emit metrics reflecting emitted & skipped."""
    from openchallenges_client.cli import main as cli_main

    # One valid, one invalid challenge (invalid due to missing required 'id')
    class _ChallengeStub:
        def list_challenges(
            self, *, limit=None, status=None, search=None, metrics=None
        ):
            # Simulate gateway per-item validation behavior:
            # increment skipped for invalid
            if metrics is not None:
                metrics.inc_emitted()
                metrics.inc_skipped()
            return [
                ChallengeSummary(
                    id=123,
                    slug="good",
                    name="Good",
                    status="ACTIVE",
                    platform_id=None,
                    platform_name=None,
                    start_date=None,
                    end_date=None,
                    duration_days=None,
                )
            ]

    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda api_url, api_key, limit: _ChallengeStub(),
    )
    result = runner.invoke(
        app,
        [
            "--verbose",
            "challenges",
            "list",
            "--limit",
            "5",
        ],
    )
    assert result.exit_code == 0, result.output
    # Metrics line should show emitted=1 skipped=1
    assert "[stats] challenges" in result.stderr
    assert "emitted=1" in result.stderr
    assert "skipped=1" in result.stderr
    assert "retries=0" in result.stderr


def test_cli_orgs_list_verbose_metrics(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _OrgStub:
        def list_organizations(self, *, limit=None, search=None, metrics=None):
            if metrics is not None:
                metrics.inc_emitted(2)
                metrics.inc_retries(3)
            return [
                OrganizationSummary(
                    id=1,
                    name="Org A",
                    short_name=None,
                    website_url=None,
                ),
                OrganizationSummary(
                    id=2,
                    name="Org B",
                    short_name=None,
                    website_url=None,
                ),
            ]

    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda api_url, api_key, limit: _OrgStub(),
    )
    result = runner.invoke(
        app,
        [
            "--verbose",
            "orgs",
            "list",
            "--limit",
            "2",
        ],
    )
    assert result.exit_code == 0, result.output
    assert "[stats] orgs" in result.stderr
    assert "emitted=2" in result.stderr
    assert "skipped=0" in result.stderr
    assert "retries=3" in result.stderr


# ---------------- Platform Delete Tests -----------------


def test_cli_platforms_delete_confirmation(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _PlatformStub:
        def __init__(self):
            self.deleted: list[int] = []

        # Provide required factory-used attributes/methods
        def delete_platform(self, platform_id: int):  # facade method signature
            self.deleted.append(platform_id)

        # Unused in this test but Typer callback creates facade via _client
        def list_challenges(self, *a, **k):  # pragma: no cover - safety
            return []

    stub = _PlatformStub()
    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda api_url, api_key, limit: stub,
    )
    # Provide 'y' to confirmation prompt
    result = runner.invoke(app, ["platforms", "delete", "42"], input="y\n")
    assert result.exit_code == 0, result.output
    assert "Deleted platform 42" in result.output
    assert stub.deleted == [42]


def test_cli_platforms_delete_skip_confirm(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _PlatformStub:
        def __init__(self):
            self.deleted: list[int] = []

        def delete_platform(self, platform_id: int):
            self.deleted.append(platform_id)

    stub = _PlatformStub()
    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda api_url, api_key, limit: stub,
    )
    result = runner.invoke(app, ["platforms", "delete", "7", "--yes"])
    assert result.exit_code == 0, result.output
    assert "Deleted platform 7" in result.output
    assert stub.deleted == [7]


def test_cli_platforms_create_preview_and_confirm(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _PlatformStub:
        def __init__(self):
            self.created: list[dict] = []

        def create_platform(self, *, slug, name, avatar_key, website_url):
            self.created.append(
                {
                    "slug": slug,
                    "name": name,
                    "avatar_key": avatar_key,
                    "website_url": website_url,
                }
            )

            class _Obj:
                def __init__(self, slug, name, avatar_key, website_url):
                    self.id = 101
                    self.slug = slug
                    self.name = name
                    self.avatar_key = avatar_key
                    self.website_url = website_url

            return _Obj(slug, name, avatar_key, website_url)

    stub = _PlatformStub()
    monkeypatch.setattr(cli_main, "_client", lambda *a, **k: stub)
    # Provide 'y' to confirmation after preview
    result = runner.invoke(
        app,
        [
            "platforms",
            "create",
            "--slug",
            "new-platform",
            "--name",
            "New Platform",
            "--avatar-key",
            "logo/new.png",
            "--website-url",
            "https://new.example",
        ],
        input="\n",  # accept default 'Yes'
    )
    assert result.exit_code == 0, result.output
    assert stub.created and stub.created[0]["slug"] == "new-platform"
    # Preview table title should appear
    assert "New Platform (preview)" in result.output


def test_cli_platforms_create_skip_preview(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _PlatformStub:
        def __init__(self):
            self.created: list[dict] = []

        def create_platform(self, *, slug, name, avatar_key, website_url):
            self.created.append(
                {
                    "slug": slug,
                    "name": name,
                    "avatar_key": avatar_key,
                    "website_url": website_url,
                }
            )

            class _Obj:
                def __init__(self, slug, name, avatar_key, website_url):
                    self.id = 202
                    self.slug = slug
                    self.name = name
                    self.avatar_key = avatar_key
                    self.website_url = website_url

            return _Obj(slug, name, avatar_key, website_url)

    stub = _PlatformStub()
    monkeypatch.setattr(cli_main, "_client", lambda *a, **k: stub)
    result = runner.invoke(
        app,
        [
            "platforms",
            "create",
            "--slug",
            "quick-platform",
            "--name",
            "Quick Platform",
            "--avatar-key",
            "logo/quick.png",
            "--website-url",
            "https://quick.example",
            "--yes",
        ],
    )
    assert result.exit_code == 0, result.output
    assert stub.created and stub.created[0]["slug"] == "quick-platform"
    assert "New Platform (preview)" not in result.output


# ---------------- Platform Update Tests -----------------


def test_cli_platforms_update_change(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _FacadeStub:
        def __init__(self):
            self.updated: list[dict] = []

        def get_platform(self, platform_id: int):
            class _Obj:
                id = platform_id
                slug = "alpha-platform"
                name = "Alpha"
                avatar_key = "logos/alpha.png"
                website_url = "https://alpha.example"

            return _Obj()

        def update_platform(
            self,
            *,
            platform_id: int,
            slug: str,
            name: str,
            avatar_key: str,
            website_url: str | None,
        ):
            self.updated.append(
                {
                    "id": platform_id,
                    "slug": slug,
                    "name": name,
                    "avatar_key": avatar_key,
                    "website_url": website_url,
                }
            )

            class _Obj:
                def __init__(
                    self,
                    platform_id: int,
                    slug: str,
                    name: str,
                    avatar_key: str,
                    website_url: str | None,
                ):
                    self.id = platform_id
                    self.slug = slug
                    self.name = name
                    self.avatar_key = avatar_key
                    self.website_url = website_url

            return _Obj(platform_id, slug, name, avatar_key, website_url)

    stub = _FacadeStub()
    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda api_url, api_key, limit: stub,
    )
    # Update just the name; auto-confirm diff with --yes
    result = runner.invoke(
        app,
        [
            "platforms",
            "update",
            "5",
            "--name",
            "Alpha Renamed",
            "--yes",
            "--output",
            "json",
        ],
    )
    assert result.exit_code == 0, result.output
    assert stub.updated and stub.updated[0]["name"] == "Alpha Renamed"
    assert "Alpha Renamed" in result.output


def test_cli_platforms_update_no_change(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _FacadeStub:
        def get_platform(self, platform_id: int):
            class _Obj:
                id = platform_id
                slug = "beta-platform"
                name = "Beta"
                avatar_key = "logos/beta.png"
                website_url = None

            return _Obj()

    stub = _FacadeStub()
    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda api_url, api_key, limit: stub,
    )
    result = runner.invoke(app, ["platforms", "update", "9", "--yes"])
    assert result.exit_code == 0, result.output
    assert "No changes to apply" in result.output


def test_cli_platforms_update_invalid_slug(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _FacadeStub:
        def get_platform(self, platform_id: int):
            class _Obj:
                id = platform_id
                slug = "gamma-platform"
                name = "Gamma"
                avatar_key = "logos/gamma.png"
                website_url = "https://gamma.example"

            return _Obj()

    stub = _FacadeStub()
    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda api_url, api_key, limit: stub,
    )
    result = runner.invoke(
        app,
        [
            "platforms",
            "update",
            "11",
            "--slug",
            "BadSlug",  # invalid (uppercase)
            "--yes",
        ],
    )
    assert result.exit_code == 1
    assert "Invalid slug format" in result.output


def test_cli_platforms_update_clear_website(monkeypatch):
    from openchallenges_client.cli import main as cli_main

    class _FacadeStub:
        def __init__(self):
            self.updated: list[dict] = []

        def get_platform(self, platform_id: int):
            class _Obj:
                id = platform_id
                slug = "delta-platform"
                name = "Delta"
                avatar_key = "logos/delta.png"
                website_url = "https://delta.example"

            return _Obj()

        def update_platform(
            self,
            *,
            platform_id: int,
            slug: str,
            name: str,
            avatar_key: str,
            website_url: str | None,
        ):
            self.updated.append(
                {
                    "id": platform_id,
                    "slug": slug,
                    "name": name,
                    "avatar_key": avatar_key,
                    "website_url": website_url,
                }
            )

            class _Obj:
                def __init__(
                    self,
                    platform_id: int,
                    slug: str,
                    name: str,
                    avatar_key: str,
                    website_url: str | None,
                ):
                    self.id = platform_id
                    self.slug = slug
                    self.name = name
                    self.avatar_key = avatar_key
                    self.website_url = website_url

            return _Obj(platform_id, slug, name, avatar_key, website_url)

    stub = _FacadeStub()
    monkeypatch.setattr(
        cli_main,
        "_client",
        lambda api_url, api_key, limit: stub,
    )
    result = runner.invoke(
        app,
        [
            "platforms",
            "update",
            "13",
            "--website-url",
            "",
            "--yes",
        ],
    )
    assert result.exit_code == 0, result.output
    assert stub.updated and stub.updated[0]["website_url"] is None
