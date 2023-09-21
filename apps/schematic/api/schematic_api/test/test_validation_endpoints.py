"""Tests for validation endpoints"""

from unittest.mock import patch

from schematic_api.test import BaseTestCase
import schematic_api.controllers.validation_controller_impl
from .conftest import (
    TEST_SCHEMA_URL,
    CORRECT_MANIFEST_PATH,
    csv_to_bytes,
    csv_to_json_str,
)

TEMP_MANIFEST_PATH = "/tmp/manifest.csv"
HEADERS = {
    "Accept": "application/json",
    "Authorization": "Bearer xxx",
}

VALIDATE_MANIFEST_CSV_URL = "/api/v1/validateManifestCsv"
VALIDATE_MANIFEST_JSON_URL = "/api/v1/validateManifestJson"


class TestValidateManifestCsv(BaseTestCase):
    """Tests for validate manifest csv endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        body = csv_to_bytes(CORRECT_MANIFEST_PATH)
        url = f"{VALIDATE_MANIFEST_CSV_URL}?schemaUrl={TEST_SCHEMA_URL}&componentLabel=Biospecimen"
        response = self.client.open(url, method="POST", headers=HEADERS, data=body)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert response.json["errors"] == []
        assert response.json["warnings"] == []

    def test_restrict_rules(self) -> None:
        """Test for the restrict rules argument"""
        with patch.object(
            schematic_api.controllers.validation_controller_impl,
            "validate_manifest_with_schematic",
            return_value=([], []),
        ) as mock_function:
            body = csv_to_bytes(CORRECT_MANIFEST_PATH)

            url1 = f"{VALIDATE_MANIFEST_CSV_URL}?schemaUrl=x&componentLabel=x&restrictRules=true"
            self.client.open(url1, method="POST", headers=HEADERS, data=body)
            mock_function.assert_called_with(TEMP_MANIFEST_PATH, "x", "x", True)

            url2 = f"{VALIDATE_MANIFEST_CSV_URL}?schemaUrl=x2&componentLabel=x2&restrictRules=false"
            self.client.open(url2, method="POST", headers=HEADERS, data=body)
            mock_function.assert_called_with(TEMP_MANIFEST_PATH, "x2", "x2", False)

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.validation_controller_impl,
            "save_manifest_csv_string_as_csv",
            side_effect=TypeError,
        ):
            url = f"{VALIDATE_MANIFEST_CSV_URL}?schemaUrl=xxx&componentLabel=xxx"
            response = self.client.open(url, method="POST", headers=HEADERS)
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestValidateManifestJson(BaseTestCase):
    """Tests for validate manifest json endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        body = csv_to_json_str(CORRECT_MANIFEST_PATH)
        url = f"{VALIDATE_MANIFEST_JSON_URL}?schemaUrl={TEST_SCHEMA_URL}&componentLabel=Biospecimen"
        response = self.client.open(url, method="POST", headers=HEADERS, data=body)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert response.json["errors"] == []
        assert response.json["warnings"] == []

    def test_restrict_rules(self) -> None:
        """Test for the restrict rules argument"""
        with patch.object(
            schematic_api.controllers.validation_controller_impl,
            "validate_manifest_with_schematic",
            return_value=([], []),
        ) as mock_function:
            body = csv_to_json_str(CORRECT_MANIFEST_PATH)

            url1 = f"{VALIDATE_MANIFEST_JSON_URL}?schemaUrl=x&componentLabel=x&restrictRules=true"
            self.client.open(url1, method="POST", headers=HEADERS, data=body)
            mock_function.assert_called_with(TEMP_MANIFEST_PATH, "x", "x", True)

            url2 = (
                f"{VALIDATE_MANIFEST_JSON_URL}"
                "?schemaUrl=x2&componentLabel=x2&restrictRules=false"
            )
            self.client.open(url2, method="POST", headers=HEADERS, data=body)
            mock_function.assert_called_with(TEMP_MANIFEST_PATH, "x2", "x2", False)

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.validation_controller_impl,
            "save_manifest_json_string_as_csv",
            side_effect=TypeError,
        ):
            url = f"{VALIDATE_MANIFEST_JSON_URL}?schemaUrl=xxx&componentLabel=xxx"
            response = self.client.open(url, method="POST", headers=HEADERS)
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
