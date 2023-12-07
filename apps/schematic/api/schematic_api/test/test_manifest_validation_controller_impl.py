"""Tests for validation endpoint functions"""

from unittest.mock import patch
import pytest

from schematic_api.models.basic_error import BasicError
import schematic_api.controllers.manifest_validation_controller_impl
from schematic_api.models.manifest_validation_result import ManifestValidationResult
from schematic_api.controllers.manifest_validation_controller_impl import (
    submit_manifest_json,
    submit_manifest_csv,
    validate_manifest_csv,
    validate_manifest_json,
)
from .conftest import csv_to_bytes, csv_to_json_str


class TestSubmitManifestCsv:
    """Tests submit_manifest_csv"""

    def test_success(self, correct_manifest_path: str, test_schema_url: str) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.manifest_validation_controller_impl,
            "submit_manifest_with_schematic",
            return_value="syn1",
        ):
            body = csv_to_bytes(correct_manifest_path)
            result, status = submit_manifest_csv(
                schema_url=test_schema_url,
                component="Biospecimen",
                dataset_id="syn2",
                asset_view_id="syn3",
                body=body,
            )

            assert status == 200
            assert result == "syn1"

    def test_500(self, correct_manifest_path: str, test_schema_url: str) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.manifest_validation_controller_impl,
            "submit_manifest_with_schematic",
            side_effect=TypeError,
        ):
            body = csv_to_bytes(correct_manifest_path)
            result, status = submit_manifest_csv(
                schema_url=test_schema_url,
                component="Biospecimen",
                dataset_id="syn2",
                asset_view_id="syn3",
                body=body,
            )

            assert status == 500
            assert isinstance(result, BasicError)


@pytest.mark.unit
class TestSubmitManifestJson:
    """Tests submit_manifest_"""

    def test_success(self, correct_manifest_path: str, test_schema_url: str) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.manifest_validation_controller_impl,
            "submit_manifest_with_schematic",
            return_value="syn1",
        ):
            body = csv_to_json_str(correct_manifest_path)
            result, status = submit_manifest_json(
                schema_url=test_schema_url,
                component="Biospecimen",
                dataset_id="syn2",
                asset_view_id="syn3",
                body=body,
            )

            assert status == 200
            assert result == "syn1"

    def test_500(self, correct_manifest_path: str, test_schema_url: str) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.manifest_validation_controller_impl,
            "submit_manifest_with_schematic",
            side_effect=TypeError,
        ):
            body = csv_to_json_str(correct_manifest_path)
            result, status = submit_manifest_json(
                schema_url=test_schema_url,
                component="Biospecimen",
                dataset_id="syn2",
                asset_view_id="syn3",
                body=body,
            )

            assert status == 500
            assert isinstance(result, BasicError)


@pytest.mark.unit
class TestValidateManifestCsv:
    """Tests validate_manifest_csv"""

    def test_success_no_errors(
        self, correct_manifest_path: str, test_schema_url: str
    ) -> None:
        """Test for successful result with no validation errors"""
        with patch.object(
            schematic_api.controllers.manifest_validation_controller_impl,
            "get_access_token",
            return_value=None,
        ):
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
        with patch.object(
            schematic_api.controllers.manifest_validation_controller_impl,
            "get_access_token",
            return_value=None,
        ):
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

    def test_500(self, correct_manifest_path: str, test_schema_url: str) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.manifest_validation_controller_impl,
            "save_manifest_csv_string_as_csv",
            side_effect=TypeError,
        ):
            body = csv_to_bytes(correct_manifest_path)
            result, status = validate_manifest_csv(
                schema_url=test_schema_url,
                component_label="Biospecimen",
                body=body,
                restrict_rules=False,
            )

            assert status == 500
            assert isinstance(result, BasicError)


@pytest.mark.unit
class TestValidateManifestJson:
    """Tests validate_manifest_json"""

    def test_success_no_errors(
        self, correct_manifest_path: str, test_schema_url: str
    ) -> None:
        """Test for successful result with no validation errors"""
        with patch.object(
            schematic_api.controllers.manifest_validation_controller_impl,
            "get_access_token",
            return_value=None,
        ):
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
        with patch.object(
            schematic_api.controllers.manifest_validation_controller_impl,
            "get_access_token",
            return_value=None,
        ):
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

    def test_500(self, correct_manifest_path: str, test_schema_url: str) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.manifest_validation_controller_impl,
            "save_manifest_json_string_as_csv",
            side_effect=TypeError,
        ):
            body = csv_to_json_str(correct_manifest_path)
            result, status = validate_manifest_json(
                schema_url=test_schema_url,
                component_label="Biospecimen",
                body=body,
                restrict_rules=False,
            )

            assert status == 500
            assert isinstance(result, BasicError)
