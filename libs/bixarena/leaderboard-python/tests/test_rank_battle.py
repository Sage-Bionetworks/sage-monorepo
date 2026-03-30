"""Unit tests for the Bradley-Terry ranking module."""

import pytest

from bixarena_leaderboard.rank_battle import (
    compute_bt,
    compute_leaderboard_bt,
    preprocess_evaluations_for_bt,
)


def _make_evals(pairs: list[tuple[str, str, str]]) -> list[dict]:
    """Helper to build evaluation dicts from (model1, model2, outcome) tuples."""
    return [
        {"model1_name": m1, "model2_name": m2, "outcome": outcome}
        for m1, m2, outcome in pairs
    ]


def _make_models(*names: str) -> dict:
    """Helper to build minimal model info dicts."""
    return {
        name: {"id": f"id-{name}", "license": "open-source", "slug": name}
        for name in names
    }


class TestPreprocessEvaluations:
    def test_empty_input_returns_empty_arrays(self):
        matchups, outcomes, models, weights = preprocess_evaluations_for_bt([])
        assert len(matchups) == 0
        assert len(models) == 0

    def test_basic_matchup(self):
        evals = _make_evals([("A", "B", "model1")])
        matchups, outcomes, models, weights = preprocess_evaluations_for_bt(evals)
        assert sorted(models) == ["A", "B"]
        assert len(matchups) == 1
        assert outcomes[0] == 1.0  # model1 win → 1.0

    def test_tie_outcome(self):
        evals = _make_evals([("A", "B", "tie")])
        _, outcomes, _, _ = preprocess_evaluations_for_bt(evals)
        assert outcomes[0] == 0.5

    def test_model2_win(self):
        evals = _make_evals([("A", "B", "model2")])
        _, outcomes, _, _ = preprocess_evaluations_for_bt(evals)
        assert outcomes[0] == 0.0

    def test_duplicate_matchups_aggregated(self):
        evals = _make_evals([("A", "B", "model1"), ("A", "B", "model1")])
        matchups, outcomes, models, weights = preprocess_evaluations_for_bt(evals)
        assert len(matchups) == 1
        assert weights[0] == 2.0

    def test_skips_invalid_outcomes(self):
        evals = _make_evals([("A", "B", "invalid"), ("A", "B", "model1")])
        matchups, _, _, _ = preprocess_evaluations_for_bt(evals)
        assert len(matchups) == 1


class TestComputeBt:
    def test_returns_dict_sorted_by_rating(self):
        evals = _make_evals(
            [("A", "B", "model1"), ("A", "B", "model1"), ("A", "C", "model1")]
        )
        ratings = compute_bt(evals)
        assert isinstance(ratings, dict)
        values = list(ratings.values())
        assert values == sorted(values, reverse=True)

    def test_empty_returns_empty_dict(self):
        assert compute_bt([]) == {}

    def test_winner_has_higher_rating(self):
        evals = _make_evals([("A", "B", "model1")] * 10)
        ratings = compute_bt(evals)
        assert ratings["A"] > ratings["B"]


class TestComputeLeaderboardBt:
    def _default_evals(self):
        return _make_evals(
            [
                ("A", "B", "model1"),
                ("A", "C", "model1"),
                ("B", "C", "model2"),
                ("A", "B", "model1"),
                ("A", "C", "tie"),
            ]
        )

    def test_returns_list_of_entries(self):
        evals = self._default_evals()
        models = _make_models("A", "B", "C")
        entries = compute_leaderboard_bt(evals, models, num_bootstrap=10)
        assert isinstance(entries, list)
        assert len(entries) == 3

    def test_entry_has_required_keys(self):
        evals = self._default_evals()
        models = _make_models("A", "B", "C")
        entries = compute_leaderboard_bt(evals, models, num_bootstrap=10)
        required = {
            "id",
            "modelId",
            "btScore",
            "voteCount",
            "rank",
            "bootstrapQ025",
            "bootstrapQ975",
        }
        for entry in entries:
            assert required.issubset(entry.keys())

    def test_ranks_are_positive_integers(self):
        evals = self._default_evals()
        models = _make_models("A", "B", "C")
        entries = compute_leaderboard_bt(evals, models, num_bootstrap=10)
        for entry in entries:
            assert isinstance(entry["rank"], int)
            assert entry["rank"] >= 1

    def test_raises_if_model_missing(self):
        evals = _make_evals([("A", "B", "model1")])
        models = _make_models("A")  # B is missing
        with pytest.raises(ValueError, match="'B' not found"):
            compute_leaderboard_bt(evals, models, num_bootstrap=10)

    def test_empty_evaluations_returns_empty(self):
        entries = compute_leaderboard_bt([], {}, num_bootstrap=10)
        assert entries == []
