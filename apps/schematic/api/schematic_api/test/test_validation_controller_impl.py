"""Tests for validation endpoint functions"""

from schematic_api.models.manifest_validation_result import ManifestValidationResult
from schematic_api.controllers.validation_controller_impl import (
    validate_manifest_csv,
    validate_manifest_json,
)
from .conftest import csv_to_bytes, csv_to_json_str


class TestValidateManifestCsv:
    """Tests validate_manifest_csv"""

    def test_success_no_errors(
        self, correct_manifest_path: str, test_schema_url: str
    ) -> None:
        """Test for successful result with no validation errors"""
        body = csv_to_bytes(correct_manifest_path)
        result, status = validate_manifest_csv(
            schema_url=test_schema_url,
            component_label="Biospecimen",
            body=body,
            restrict_rules=False,
        )
        assert status == 200
        assert isinstance(result, ManifestValidationResult)
        assert result.errors == []
        assert result.warnings == []

    def test_success_with_one_error(
        self,
        incorrect_manifest_path: str,
        test_schema_url: str,
        incorrect_manifest_errors: list,
    ) -> None:
        """Test for successful result with one validation error"""
        body = csv_to_bytes(incorrect_manifest_path)
        result, status = validate_manifest_csv(
            schema_url=test_schema_url,
            component_label="Biospecimen",
            body=body,
            restrict_rules=False,
        )
        assert status == 200
        assert isinstance(result, ManifestValidationResult)
        assert result.errors == incorrect_manifest_errors
        assert result.warnings == []


class TestValidateManifestJson:
    """Tests validate_manifest_json"""

    def test_success_no_errors(
        self, correct_manifest_path: str, test_schema_url: str
    ) -> None:
        """Test for successful result with no validation errors"""
        body = csv_to_json_str(correct_manifest_path)
        result, status = validate_manifest_json(
            schema_url=test_schema_url,
            component_label="Biospecimen",
            body=body,
            restrict_rules=False,
        )
        assert status == 200
        assert isinstance(result, ManifestValidationResult)
        assert result.errors == []
        assert result.warnings == []

    def test_success_one_error(
        self,
        incorrect_manifest_path: str,
        test_schema_url: str,
        incorrect_manifest_errors: list,
    ) -> None:
        """Test for successful result with one validation error"""
        body = csv_to_json_str(incorrect_manifest_path)
        result, status = validate_manifest_json(
            schema_url=test_schema_url,
            component_label="Biospecimen",
            body=body,
            restrict_rules=False,
        )
        assert status == 200
        assert isinstance(result, ManifestValidationResult)
        assert result.errors == incorrect_manifest_errors
        assert result.warnings == []
