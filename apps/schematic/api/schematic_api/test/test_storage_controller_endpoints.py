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
FILES_URL = "/api/v1/assetTypes/synapse/assetViews/id1/datasets/id2/files"
DATASETS_URL = "/api/v1/assetTypes/synapse/assetViews/id1/projects/id2/datasets"
MANIFESTS_URL = "/api/v1/assetTypes/synapse/assetViews/id1/projects/id2/manifests"


class TestListDatasetFiles(BaseTestCase):
    """Test case for files endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_files",
            return_value=[("id1", "name1"), ("id2", "name2")],
        ):
            response = self.client.open(FILES_URL, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

            assert not response.json["hasNext"]
            assert not response.json["hasPrevious"]
            assert response.json["number"] == 0
            assert response.json["size"] == 100
            assert response.json["totalElements"] == 2
            assert response.json["totalPages"] == 1
            files = response.json["files"]
            assert len(files) == 2
            file = files[0]
            assert list(file.keys()) == ["id", "name"]
            assert file["name"] == "name1"
            assert file["id"] == "id1"

    def test_file_names(self) -> None:
        """Test with file_names parameter"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_files",
            return_value=[("id1", "name1"), ("id2", "name2")],
        ):
            url = f"{FILES_URL}?fileNames=file.text"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_files",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(FILES_URL, method="GET", headers=HEADERS)
            self.assert401(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_403(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_files",
            side_effect=AccessCredentialsError("project"),
        ):
            response = self.client.open(FILES_URL, method="GET", headers=HEADERS)
            self.assert403(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_files",
            side_effect=TypeError,
        ):
            response = self.client.open(FILES_URL, method="GET", headers=HEADERS)
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestDatasets(BaseTestCase):
    """Test case for datasets endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_datasets",
            return_value=[("id1", "name1"), ("id2", "name2")],
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
            assert dataset["id"] == "id1"

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
            assert manifest["id"] == "id1"
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
