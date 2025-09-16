"""Smoke test ensuring test infrastructure is operational.

This test intentionally performs a trivial assertion so that the Nx test
pipeline (pytest) succeeds even when no functional tests are present.
"""


def test_smoke():
    assert True
