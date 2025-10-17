"""Unit tests for shared constants."""

from platform_infra_cdk_common.constants import VALID_ENVIRONMENTS


def test_valid_environments_contains_expected_values():
    """Ensure the shared env list contains expected values."""
    assert VALID_ENVIRONMENTS == ["dev", "stage", "prod"]
