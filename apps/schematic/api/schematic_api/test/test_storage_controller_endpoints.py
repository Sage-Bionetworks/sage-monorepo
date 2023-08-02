"""Tests for endpoints"""


from __future__ import absolute_import
import unittest
from unittest.mock import patch

from synapseclient.core.exceptions import SynapseNoCredentialsError  # type: ignore
from schematic.exceptions import AccessCredentialsError  # type: ignore

import schematic_api.controllers.storage_controller_impl
from schematic_api.test import BaseTestCase
from .conftest import EXAMPLE_MANIFEST_METADATA

HEADERS = {
    "Accept": "application/json",
    "Authorization": "Bearer xxx",
}
DATASETS_URL = "/api/v1/assetTypes/synapse/assetViews/syn1/projects/syn2/datasets"
MANIFESTS_URL = "/api/v1/assetTypes/synapse/assetViews/syn1/projects/syn2/manifests"


class TestDatasets(BaseTestCase):
    """Test case for datasets endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_datasets",
            return_value=[("syn1", "name1"), ("syn2", "name2")],
        ):
            response = self.client.open(DATASETS_URL, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

            assert not response.json["hasNext"]
            assert not response.json["hasPrevious"]
            assert response.json["number"] == 0
            assert response.json["size"] == 100
            assert response.json["totalElements"] == 2
            assert response.json["totalPages"] == 1
            datasets = response.json["datasets"]
            assert len(datasets) == 2
            dataset = datasets[0]
            assert list(dataset.keys()) == ["id", "name"]
            assert dataset["name"] == "name1"
            assert dataset["id"] == "syn1"

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_datasets",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(DATASETS_URL, method="GET", headers=HEADERS)
            self.assert401(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_403(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_datasets",
            side_effect=AccessCredentialsError("project"),
        ):
            response = self.client.open(DATASETS_URL, method="GET", headers=HEADERS)
            self.assert403(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_datasets",
            side_effect=TypeError,
        ):
            response = self.client.open(DATASETS_URL, method="GET", headers=HEADERS)
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestManifests(BaseTestCase):
    """Test case for manifests endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifests",
            return_value=EXAMPLE_MANIFEST_METADATA,
        ):
            response = self.client.open(MANIFESTS_URL, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

            assert not response.json["hasNext"]
            assert not response.json["hasPrevious"]
            assert response.json["number"] == 0
            assert response.json["size"] == 100
            assert response.json["totalElements"] == 2
            assert response.json["totalPages"] == 1
            manifests = response.json["manifests"]
            assert len(manifests) == 2
            manifest = manifests[0]
            assert list(manifest.keys()) == [
                "componentName",
                "datasetId",
                "datasetName",
                "id",
                "name",
            ]
            assert manifest["name"] == "name1"
            assert manifest["id"] == "syn1"
            assert manifest["componentName"] == "component1"
            assert manifest["datasetId"] == "dataset_id1"
            assert manifest["datasetName"] == "dataset_name1"

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifests",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(MANIFESTS_URL, method="GET", headers=HEADERS)
            self.assert401(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_403(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifests",
            side_effect=AccessCredentialsError("project"),
        ):
            response = self.client.open(MANIFESTS_URL, method="GET", headers=HEADERS)
            self.assert403(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifests",
            side_effect=TypeError,
        ):
            response = self.client.open(MANIFESTS_URL, method="GET", headers=HEADERS)
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


if __name__ == "__main__":
    unittest.main()
