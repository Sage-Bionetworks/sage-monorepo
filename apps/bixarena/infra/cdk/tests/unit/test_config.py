"""Unit tests for configuration management."""

import os
from unittest.mock import patch

import pytest

from bixarena_infra_cdk.shared.config import (
    get_developer_name,
    get_environment,
    get_stack_prefix,
)


class TestGetEnvironment:
    """Tests for get_environment function."""

    def test_get_environment_dev(self):
        """Test environment detection for dev."""
        with patch.dict(os.environ, {"ENV": "dev"}):
            assert get_environment() == "dev"

    def test_get_environment_stage(self):
        """Test environment detection for stage."""
        with patch.dict(os.environ, {"ENV": "stage"}):
            assert get_environment() == "stage"

    def test_get_environment_prod(self):
        """Test environment detection for prod."""
        with patch.dict(os.environ, {"ENV": "prod"}):
            assert get_environment() == "prod"

    def test_get_environment_missing(self):
        """Test error when ENV is not set."""
        with (
            patch.dict(os.environ, {}, clear=True),
            pytest.raises(ValueError, match="ENV environment variable is required"),
        ):
            get_environment()

    def test_get_environment_invalid(self):
        """Test error when ENV is invalid."""
        with (
            patch.dict(os.environ, {"ENV": "invalid"}),
            pytest.raises(ValueError, match="Invalid environment"),
        ):
            get_environment()


class TestGetDeveloperName:
    """Tests for get_developer_name function."""

    def test_get_developer_name_dev_with_name(self):
        """Test developer name extraction for dev with name set."""
        with patch.dict(os.environ, {"ENV": "dev", "DEVELOPER_NAME": "jsmith"}):
            assert get_developer_name() == "jsmith"

    def test_get_developer_name_dev_missing(self):
        """Test error when DEVELOPER_NAME is missing for dev."""
        with (
            patch.dict(os.environ, {"ENV": "dev"}, clear=True),
            pytest.raises(
                ValueError,
                match="DEVELOPER_NAME environment variable is required for dev",
            ),
        ):
            get_developer_name()

    def test_get_developer_name_stage(self):
        """Test developer name is None for stage."""
        with patch.dict(os.environ, {"ENV": "stage", "DEVELOPER_NAME": "ignored"}):
            assert get_developer_name() is None

    def test_get_developer_name_prod(self):
        """Test developer name is None for prod."""
        with patch.dict(os.environ, {"ENV": "prod", "DEVELOPER_NAME": "ignored"}):
            assert get_developer_name() is None

    def test_get_developer_name_invalid_characters(self):
        """Test error for invalid developer name characters."""
        with (
            patch.dict(os.environ, {"ENV": "dev", "DEVELOPER_NAME": "j@smith"}),
            pytest.raises(ValueError, match="Invalid DEVELOPER_NAME"),
        ):
            get_developer_name()

    def test_get_developer_name_valid_with_hyphens(self):
        """Test valid developer name with hyphens."""
        with patch.dict(os.environ, {"ENV": "dev", "DEVELOPER_NAME": "john-smith"}):
            assert get_developer_name() == "john-smith"

    def test_get_developer_name_valid_with_underscores(self):
        """Test valid developer name with underscores."""
        with patch.dict(os.environ, {"ENV": "dev", "DEVELOPER_NAME": "john_smith"}):
            assert get_developer_name() == "john_smith"


class TestGetStackPrefix:
    """Tests for get_stack_prefix function."""

    def test_get_stack_prefix_dev(self):
        """Test stack prefix for dev with developer name."""
        with patch.dict(os.environ, {"ENV": "dev", "DEVELOPER_NAME": "jsmith"}):
            assert get_stack_prefix() == "bixarena-dev-jsmith"

    def test_get_stack_prefix_stage(self):
        """Test stack prefix for stage."""
        with patch.dict(os.environ, {"ENV": "stage"}):
            assert get_stack_prefix() == "bixarena-stage"

    def test_get_stack_prefix_prod(self):
        """Test stack prefix for prod."""
        with patch.dict(os.environ, {"ENV": "prod"}):
            assert get_stack_prefix() == "bixarena-prod"
