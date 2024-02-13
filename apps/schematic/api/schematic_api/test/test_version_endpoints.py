"""Tests for version endpoints"""
# pylint: disable=duplicate-code

import importlib.metadata
from unittest.mock import patch

from schematic_api.test import BaseTestCase


HEADERS = {
    "Accept": "application/json",
    "Authorization": "Bearer xxx",
}
SCHEMATIC_VERSION_URL = "/api/v1/schematicVersion"


class TestGetSchematicVersion(BaseTestCase):
    """Test case for schematic version endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        response = self.client.open(
            SCHEMATIC_VERSION_URL, method="GET", headers=HEADERS
        )
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            importlib.metadata,
            "version",
            side_effect=TypeError,
        ):
            response = self.client.open(
                SCHEMATIC_VERSION_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
