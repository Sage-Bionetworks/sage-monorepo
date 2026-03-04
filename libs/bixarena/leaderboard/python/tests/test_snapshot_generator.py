"""Unit tests for generate_snapshot() input validation."""

import pytest

from bixarena_leaderboard.snapshot_generator import generate_snapshot


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
