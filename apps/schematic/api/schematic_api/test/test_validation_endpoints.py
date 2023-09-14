"""Tests for validation endpoints"""

from schematic_api.test import BaseTestCase


HEADERS = {
    "Accept": "application/json",
    "Authorization": "Bearer xxx",
}
SCHEMA_URL = "https://raw.githubusercontent.com/Sage-Bionetworks/schematic/develop/tests/data/example.model.jsonld"  # pylint: disable=line-too-long
MANIFEST_JSON = '[{"Patient ID": 123,"Sex": "Female","Year of Birth": "","Diagnosis": "Healthy","Component": "Patient","Cancer Type": "Breast","Family History": "Breast, Lung"}]'  # pylint: disable=line-too-long
"""
MANIFEST_JSON = [
    {
        "Patient ID": 123,
        "Sex": "Female",
        "Year of Birth": "",
        "Diagnosis": "Healthy",
        "Component": "Patient",
        "Cancer Type": "Breast",
        "Family History": "Breast, Lung",
    }
]
"""
VALIDATE_MANIFEST_CSV_URL = "/api/v1/validateManifestCsv"
VALIDATE_MANIFEST_JSON_URL = "/api/v1/validateManifestJson"


class TestValidateManifestCsv(BaseTestCase):
    """Tests for validate manifest csv endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        url = (
            f"{VALIDATE_MANIFEST_CSV_URL}?schemaUrl={SCHEMA_URL}&componentLabel=Patient"
        )
        response = self.client.open(url, method="POST", headers=HEADERS, data={})
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")


class TestValidateManifestJson(BaseTestCase):
    """Tests for validate manifest json endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        response = self.client.open(
            VALIDATE_MANIFEST_JSON_URL, method="POST", headers=HEADERS
        )
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
