"""Unit tests for generate_snapshot() input validation."""

import math
from contextlib import contextmanager
from unittest.mock import patch

import pytest

from bixarena_leaderboard.snapshot_generator import (
    _BT_SCORE_MAX,
    _BT_SCORE_MIN_EXCLUSIVE,
    _CI_CLAMP,
    SnapshotRunError,
    _clamp_ci_bounds,
    _entry_has_valid_score,
    generate_all_snapshots,
    generate_snapshot,
)


class TestNumBootstrapValidation:
    def test_too_low(self):
        with pytest.raises(
            ValueError, match="num_bootstrap must be between 1 and 5000"
        ):
            generate_snapshot(num_bootstrap=0)

    def test_too_high(self):
        with pytest.raises(
            ValueError, match="num_bootstrap must be between 1 and 5000"
        ):
            generate_snapshot(num_bootstrap=5001)

    def test_upper_boundary_valid(self):
        # 5000 is valid — no ValueError from validation (DB error expected instead)
        try:
            generate_snapshot(num_bootstrap=5000)
        except ValueError as exc:
            assert "num_bootstrap" not in str(exc)
        except Exception:
            pass  # DB connection error expected in unit test environment


class TestMinEvalsValidation:
    def test_negative(self):
        with pytest.raises(ValueError, match="min_evals must be >= 0"):
            generate_snapshot(num_bootstrap=100, min_evals=-1)

    def test_zero_is_valid(self):
        # 0 means no filter — should not raise from validation
        try:
            generate_snapshot(num_bootstrap=100, min_evals=0)
        except ValueError as exc:
            assert "min_evals" not in str(exc)
        except Exception:
            pass  # DB connection error expected in unit test environment

    def test_positive_is_valid(self):
        try:
            generate_snapshot(num_bootstrap=100, min_evals=10)
        except ValueError as exc:
            assert "min_evals" not in str(exc)
        except Exception:
            pass  # DB connection error expected in unit test environment


class TestMinTotalModelsValidation:
    def test_negative(self):
        with pytest.raises(ValueError, match="min_total_models must be >= 0"):
            generate_snapshot(num_bootstrap=100, min_total_models=-1)


class TestGenerateAllSnapshotsValidation:
    def test_negative_min_total_battles(self):
        with pytest.raises(ValueError, match="min_total_battles must be >= 0"):
            generate_all_snapshots(min_total_battles=-1)

    def test_negative_min_total_models(self):
        with pytest.raises(ValueError, match="min_total_models must be >= 0"):
            generate_all_snapshots(min_total_models=-1)


class TestEntryHasValidScore:
    def test_typical_score_is_valid(self):
        assert _entry_has_valid_score({"btScore": 500.0}) is True

    def test_score_at_max_is_valid(self):
        assert _entry_has_valid_score({"btScore": _BT_SCORE_MAX}) is True

    def test_score_above_max_is_invalid(self):
        assert _entry_has_valid_score({"btScore": _BT_SCORE_MAX + 1}) is False

    def test_score_at_lower_threshold_is_invalid(self):
        # Strict > _BT_SCORE_MIN_EXCLUSIVE; equality is rejected.
        assert _entry_has_valid_score({"btScore": _BT_SCORE_MIN_EXCLUSIVE}) is False

    def test_negative_score_is_invalid(self):
        assert _entry_has_valid_score({"btScore": -100.0}) is False

    def test_positive_inf_is_invalid(self):
        assert _entry_has_valid_score({"btScore": math.inf}) is False

    def test_negative_inf_is_invalid(self):
        assert _entry_has_valid_score({"btScore": -math.inf}) is False

    def test_nan_is_invalid(self):
        assert _entry_has_valid_score({"btScore": math.nan}) is False


class TestClampCiBounds:
    @staticmethod
    def _entry(lo, hi):
        return {"bootstrapQ025": lo, "bootstrapQ975": hi}

    def test_in_range_unchanged(self):
        e = self._entry(-100.0, 200.0)
        _clamp_ci_bounds(e)
        assert e["bootstrapQ025"] == -100.0
        assert e["bootstrapQ975"] == 200.0

    def test_above_upper_bound_clamped(self):
        e = self._entry(-100.0, _CI_CLAMP + 5000)
        _clamp_ci_bounds(e)
        assert e["bootstrapQ025"] == -100.0
        assert e["bootstrapQ975"] == _CI_CLAMP

    def test_below_lower_bound_clamped(self):
        e = self._entry(-_CI_CLAMP - 5000, 200.0)
        _clamp_ci_bounds(e)
        assert e["bootstrapQ025"] == -_CI_CLAMP
        assert e["bootstrapQ975"] == 200.0

    def test_non_finite_clamped_to_lower_bound(self):
        # Current behavior: any non-finite value (inf, -inf, NaN) collapses to
        # -_CI_CLAMP. Asymmetric for +inf, but acceptable since divergent CIs
        # are uninterpretable anyway.
        for bad in (math.inf, -math.inf, math.nan):
            e = self._entry(bad, bad)
            _clamp_ci_bounds(e)
            assert e["bootstrapQ025"] == -_CI_CLAMP
            assert e["bootstrapQ975"] == -_CI_CLAMP


@contextmanager
def _fake_db_connection(_conn=None):
    """Stand-in for get_db_connection used by orchestrator phase 1."""
    yield _conn


def _patch_orchestrator(
    leaderboards: list[dict],
    stats_by_slug: dict[str | None, dict[str, int]],
    generate_outcomes: dict[str, dict | Exception],
):
    """Build a stack of patches that simulates DB + per-slug generation.

    leaderboards: rows fetch_leaderboards would return (dicts with 'slug', 'name').
    stats_by_slug: keyed by category_slug (None for 'overall') -> stats dict.
    generate_outcomes: keyed by leaderboard slug -> either a result dict (success)
        or an exception instance (raised when generate_snapshot is called).
    """
    sentinel_conn = object()

    def fake_stats(_conn, category_slug=None):
        return stats_by_slug[category_slug]

    def fake_generate(leaderboard_slug, **kwargs):
        outcome = generate_outcomes[leaderboard_slug]
        if isinstance(outcome, Exception):
            raise outcome
        return outcome

    return [
        patch(
            "bixarena_leaderboard.snapshot_generator.get_db_connection",
            lambda: _fake_db_connection(sentinel_conn),
        ),
        patch(
            "bixarena_leaderboard.snapshot_generator.fetch_leaderboards",
            lambda _conn: leaderboards,
        ),
        patch(
            "bixarena_leaderboard.snapshot_generator.fetch_battle_evaluation_stats",
            fake_stats,
        ),
        patch(
            "bixarena_leaderboard.snapshot_generator.generate_snapshot",
            fake_generate,
        ),
    ]


def _apply_patches(patches):
    for p in patches:
        p.start()


def _stop_patches(patches):
    for p in patches:
        p.stop()


class TestGenerateAllSnapshotsBehavior:
    def test_all_leaderboards_pass_thresholds(self):
        """Every slug above thresholds gets generated."""
        leaderboards = [
            {"slug": "overall", "name": "Overall"},
            {"slug": "cancer-biology", "name": "Cancer Biology"},
        ]
        stats = {
            None: {"battle_count": 200, "model_count": 10},
            "cancer-biology": {"battle_count": 50, "model_count": 5},
        }
        outcomes = {
            "overall": {
                "snapshot_id": "uuid-overall",
                "snapshot_identifier": "snap-1",
                "entry_count": 10,
                "evaluation_count": 200,
                "leaderboard_name": "Overall",
            },
            "cancer-biology": {
                "snapshot_id": "uuid-cancer",
                "snapshot_identifier": "snap-2",
                "entry_count": 5,
                "evaluation_count": 50,
                "leaderboard_name": "Cancer Biology",
            },
        }
        patches = _patch_orchestrator(leaderboards, stats, outcomes)
        _apply_patches(patches)
        try:
            summary = generate_all_snapshots(
                min_total_battles=10,
                min_total_models=2,
            )
        finally:
            _stop_patches(patches)

        assert summary["total"] == 2
        generated_slugs = {e["slug"] for e in summary["generated"]}
        assert generated_slugs == {"overall", "cancer-biology"}
        assert summary["skipped"] == []
        assert summary["failed"] == []

    def test_skips_leaderboard_below_battle_threshold(self):
        leaderboards = [
            {"slug": "overall", "name": "Overall"},
            {"slug": "neuroscience", "name": "Neuroscience"},
        ]
        stats = {
            None: {"battle_count": 200, "model_count": 10},
            "neuroscience": {"battle_count": 5, "model_count": 4},
        }
        outcomes = {
            "overall": {
                "snapshot_id": "uuid-overall",
                "snapshot_identifier": "snap-1",
                "entry_count": 10,
                "evaluation_count": 200,
                "leaderboard_name": "Overall",
            },
        }
        patches = _patch_orchestrator(leaderboards, stats, outcomes)
        _apply_patches(patches)
        try:
            summary = generate_all_snapshots(
                min_total_battles=10,
                min_total_models=2,
            )
        finally:
            _stop_patches(patches)

        assert {e["slug"] for e in summary["generated"]} == {"overall"}
        assert len(summary["skipped"]) == 1
        skip = summary["skipped"][0]
        assert skip["slug"] == "neuroscience"
        assert skip["reason"] == "insufficient_battles"
        assert skip["battle_count"] == 5

    def test_skips_leaderboard_below_model_threshold(self):
        leaderboards = [{"slug": "biochemistry", "name": "Biochemistry"}]
        stats = {"biochemistry": {"battle_count": 100, "model_count": 2}}
        outcomes: dict = {}
        patches = _patch_orchestrator(leaderboards, stats, outcomes)
        _apply_patches(patches)
        try:
            # 2 < 3 → trips the model gate.
            summary = generate_all_snapshots(
                min_total_battles=10,
                min_total_models=3,
            )
        finally:
            _stop_patches(patches)

        assert summary["generated"] == []
        assert len(summary["skipped"]) == 1
        skip = summary["skipped"][0]
        assert skip["reason"] == "insufficient_models"
        assert skip["model_count"] == 2

    def test_one_failure_does_not_block_others(self):
        """When generate_snapshot raises for one slug, others still run."""
        leaderboards = [
            {"slug": "overall", "name": "Overall"},
            {"slug": "cancer-biology", "name": "Cancer Biology"},
        ]
        stats = {
            None: {"battle_count": 200, "model_count": 10},
            "cancer-biology": {"battle_count": 50, "model_count": 5},
        }
        outcomes = {
            "overall": {
                "snapshot_id": "uuid-overall",
                "snapshot_identifier": "snap-1",
                "entry_count": 10,
                "evaluation_count": 200,
                "leaderboard_name": "Overall",
            },
            "cancer-biology": RuntimeError("boom"),
        }
        patches = _patch_orchestrator(leaderboards, stats, outcomes)
        _apply_patches(patches)
        try:
            with pytest.raises(SnapshotRunError) as excinfo:
                generate_all_snapshots(
                    min_total_battles=10,
                    min_total_models=2,
                )
        finally:
            _stop_patches(patches)

        summary = excinfo.value.summary
        assert {e["slug"] for e in summary["generated"]} == {"overall"}
        assert summary["failed"] == [{"slug": "cancer-biology", "error": "boom"}]
        assert summary["total"] == 2

    def test_no_qualifying_entries_routes_to_skipped(self):
        leaderboards = [{"slug": "cancer-biology", "name": "Cancer Biology"}]
        stats: dict = {"cancer-biology": {"battle_count": 50, "model_count": 5}}
        outcomes: dict = {
            "cancer-biology": {
                "snapshot_id": None,
                "snapshot_identifier": None,
                "entry_count": 0,
                "evaluation_count": 50,
                "leaderboard_name": "Cancer Biology",
                "skipped_reason": "no_qualifying_entries",
            },
        }
        patches = _patch_orchestrator(leaderboards, stats, outcomes)
        _apply_patches(patches)
        try:
            summary = generate_all_snapshots(
                min_total_battles=10,
                min_total_models=2,
            )
        finally:
            _stop_patches(patches)

        assert summary["generated"] == []
        assert summary["failed"] == []
        assert len(summary["skipped"]) == 1
        skip = summary["skipped"][0]
        assert skip["slug"] == "cancer-biology"
        assert skip["reason"] == "no_qualifying_entries"
        assert skip["battle_count"] == 50
        assert skip["model_count"] == 5

    def test_insufficient_qualifying_entries_routes_to_skipped(self):
        leaderboards = [{"slug": "cancer-biology", "name": "Cancer Biology"}]
        stats: dict = {"cancer-biology": {"battle_count": 50, "model_count": 5}}
        outcomes: dict = {
            "cancer-biology": {
                "snapshot_id": None,
                "snapshot_identifier": None,
                "entry_count": 2,
                "evaluation_count": 50,
                "leaderboard_name": "Cancer Biology",
                "skipped_reason": "insufficient_qualifying_entries",
            },
        }
        patches = _patch_orchestrator(leaderboards, stats, outcomes)
        _apply_patches(patches)
        try:
            summary = generate_all_snapshots(
                min_total_battles=10,
                min_total_models=2,
            )
        finally:
            _stop_patches(patches)

        assert summary["generated"] == []
        assert len(summary["skipped"]) == 1
        skip = summary["skipped"][0]
        assert skip["reason"] == "insufficient_qualifying_entries"
