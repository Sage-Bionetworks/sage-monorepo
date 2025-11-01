"""Unit tests for naming utilities."""

import pytest

from bixarena_infra_cdk.shared.naming import (
    generate_resource_name,
    validate_aws_name,
)


class TestGenerateResourceName:
    """Tests for generate_resource_name function."""

    def test_generate_resource_name_dev(self):
        """Test resource naming for dev environment."""
        result = generate_resource_name("img", "dev", "jsmith")
        assert result == "bixarena-dev-jsmith-img"

    def test_generate_resource_name_stage(self):
        """Test resource naming for stage environment."""
        result = generate_resource_name("img", "stage")
        assert result == "bixarena-stage-img"

    def test_generate_resource_name_prod(self):
        """Test resource naming for prod environment."""
        result = generate_resource_name("img", "prod")
        assert result == "bixarena-prod-img"

    def test_generate_resource_name_dev_without_developer(self):
        """Test resource naming for dev without developer name."""
        result = generate_resource_name("vpc", "dev", None)
        assert result == "bixarena-dev-vpc"

    def test_generate_resource_name_empty_resource_type(self):
        """Test error for empty resource type."""
        with pytest.raises(ValueError, match="resource_type cannot be empty"):
            generate_resource_name("", "dev", "jsmith")

    def test_generate_resource_name_empty_environment(self):
        """Test error for empty environment."""
        with pytest.raises(ValueError, match="environment cannot be empty"):
            generate_resource_name("img", "", "jsmith")

    def test_generate_resource_name_too_long(self):
        """Test error for name that's too long."""
        long_developer_name = "a" * 50
        with pytest.raises(ValueError, match="too long"):
            generate_resource_name("img", "dev", long_developer_name)


class TestValidateAwsName:
    """Tests for validate_aws_name function."""

    def test_validate_aws_name_valid(self):
        """Test validation passes for valid name."""
        validate_aws_name("bixarena-dev-jsmith-img")
        # No exception means success

    def test_validate_aws_name_empty(self):
        """Test error for empty name."""
        with pytest.raises(ValueError, match="cannot be empty"):
            validate_aws_name("")

    def test_validate_aws_name_too_long(self):
        """Test error for name exceeding max length."""
        long_name = "a" * 64
        with pytest.raises(ValueError, match="exceeds maximum length"):
            validate_aws_name(long_name, max_length=63)

    def test_validate_aws_name_invalid_characters(self):
        """Test error for invalid characters."""
        with pytest.raises(ValueError, match="invalid characters"):
            validate_aws_name("bucket@name")

    def test_validate_aws_name_must_start_alphanumeric(self):
        """Test error for name not starting with alphanumeric."""
        with pytest.raises(ValueError, match="must start with alphanumeric"):
            validate_aws_name("-bucket-name")

    def test_validate_aws_name_must_end_alphanumeric(self):
        """Test error for name not ending with alphanumeric."""
        with pytest.raises(ValueError, match="must end with alphanumeric"):
            validate_aws_name("bucket-name-")

    def test_validate_aws_name_with_dots(self):
        """Test validation allows dots."""
        validate_aws_name("bucket.name.123")
        # No exception means success
