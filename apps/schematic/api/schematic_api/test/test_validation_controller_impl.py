"""Tests for validation endpoint functions"""

from schematic_api.models.manifest_validation_result import ManifestValidationResult
from schematic_api.controllers.validation_controller_impl import (
    validate_manifest_csv,
    validate_manifest_json,
)


class TestValidateManifestCsv:  # pylint: disable=too-few-public-methods
    """Tests validate_manifest_csv"""

    def test_success(self) -> None:
        """Test for successful result"""
        result, status = validate_manifest_csv(
            schema_url="https://raw.githubusercontent.com/Sage-Bionetworks/schematic/develop/tests/data/example.model.jsonld",  # pylint: disable=line-too-long
            component_label="Patient",
            body={"path": "schematic_api/test/data/manifests/patient.csv"},
            restrict_rules=False,
        )
        assert status == 200
        assert isinstance(result, ManifestValidationResult)


class TestValidateManifestJson:  # pylint: disable=too-few-public-methods
    """Tests validate_manifest_json"""

    def test_success(self) -> None:
        """Test for successful result"""

        result, status = validate_manifest_json(
            schema_url="https://raw.githubusercontent.com/Sage-Bionetworks/schematic/develop/tests/data/example.model.jsonld",  # pylint: disable=line-too-long
            component_label="Patient",
            body="",
            restrict_rules=False,
        )
        assert status == 200
        assert isinstance(result, ManifestValidationResult)
