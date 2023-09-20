"""Tests for validation endpoint functions"""

import pandas as pd

from schematic_api.models.manifest_validation_result import ManifestValidationResult
from schematic_api.controllers.validation_controller_impl import (
    validate_manifest_csv,
    validate_manifest_json,
)

TEST_SCHEMA_URL = "https://raw.githubusercontent.com/Sage-Bionetworks/schematic/develop/tests/data/example.model.jsonld"  # pylint: disable=line-too-long


class TestValidateManifestCsv:  # pylint: disable=too-few-public-methods
    """Tests validate_manifest_csv"""

    def test_success(self) -> None:
        """Test for successful result"""
        dataframe = pd.read_csv("schematic_api/test/data/manifests/patient.csv")
        body = bytes(
            dataframe.to_csv(line_terminator="\r\n", index=False), encoding="utf-8"  # type: ignore
        )
        result, status = validate_manifest_csv(
            schema_url=TEST_SCHEMA_URL,
            component_label="Patient",
            body=body,
            restrict_rules=False,
        )
        assert status == 200
        assert isinstance(result, ManifestValidationResult)


class TestValidateManifestJson:  # pylint: disable=too-few-public-methods
    """Tests validate_manifest_json"""

    def test_success(self) -> None:
        """Test for successful result"""

        result, status = validate_manifest_json(
            schema_url=TEST_SCHEMA_URL,
            component_label="Patient",
            body="",
            restrict_rules=False,
        )
        assert status == 200
        assert isinstance(result, ManifestValidationResult)
