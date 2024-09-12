"""Tests for endpoints"""

# pylint: disable=duplicate-code

import unittest
from unittest.mock import patch

from synapseclient.core.exceptions import SynapseNoCredentialsError  # type: ignore
from schematic.exceptions import AccessCredentialsError  # type: ignore

import schematic_api.controllers.storage_controller_impl
from schematic_api.test import BaseTestCase
from schematic_api.models.dataset_metadata import DatasetMetadata
from schematic_api.models.project_metadata import ProjectMetadata
from .conftest import EXAMPLE_MANIFEST_METADATA, MANIFEST_METADATA_KEYS, PAGING_KEYS

HEADERS = {
    "Accept": "application/json",
    "Authorization": "Bearer xxx",
}

PROJECT_METADATA_ARRAY_URL = (
    "/api/v1/assetTypes/synapse/assetViews/syn1/projectMetadataArray"
)
PROJECT_METADATA_PAGE_URL = (
    "/api/v1/assetTypes/synapse/assetViews/syn1/projectMetadataPage"
)
PROJECT_DATASET_METATDATA_ARRRAY_URL = (
    "/api/v1/assetTypes/synapse/projects/syn2/datasetMetadataArray?assetViewId=syn1"
)
PROJECT_DATASET_METATDATA_PAGE_URL = (
    "/api/v1/assetTypes/synapse/projects/syn2/datasetMetadataPage?assetViewId=syn1"
)
PROJECT_MANIFEST_METADATA_ARRAY_URL = (
    "/api/v1/assetTypes/synapse/projects/syn2/manifestMetadataArray?assetViewId=syn1"
)
PROJECT_MANIFEST_METADATA_PAGE_URL = (
    "/api/v1/assetTypes/synapse/projects/syn2/manifestMetadataPage?assetViewId=syn1"
)


class TestGetProjectMetadataArray(BaseTestCase):
    """Test case for projects endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_metadata_from_schematic",
            return_value=[
                ProjectMetadata("syn1", "name1"),
                ProjectMetadata("syn2", "name2"),
            ],
        ):
            response = self.client.open(
                PROJECT_METADATA_ARRAY_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            result = response.json
            assert isinstance(result, dict)
            assert list(result.keys()) == ["projects"]
            assert isinstance(result["projects"], list)
            for item in result["projects"]:
                assert isinstance(item, dict)
                assert list(item.keys()) == ["id", "name"]
                assert isinstance(item["name"], str)
                assert isinstance(item["id"], str)

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_metadata_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(
                PROJECT_METADATA_ARRAY_URL, method="GET", headers=HEADERS
            )
            self.assert401(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_403(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_metadata_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            response = self.client.open(
                PROJECT_METADATA_ARRAY_URL, method="GET", headers=HEADERS
            )
            self.assert403(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_metadata_from_schematic",
            side_effect=TypeError,
        ):
            response = self.client.open(
                PROJECT_METADATA_ARRAY_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestGetProjectMetadataPage(BaseTestCase):
    """Test case for projects endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_metadata_from_schematic",
            return_value=[
                ProjectMetadata("syn1", "name1"),
                ProjectMetadata("syn2", "name2"),
            ],
        ):
            response = self.client.open(
                PROJECT_METADATA_PAGE_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            result = response.json
            assert isinstance(result, dict)
            assert list(result.keys()) == sorted(PAGING_KEYS + ["projects"])
            assert result["number"] == 1
            assert result["size"] == 100000
            assert not result["hasNext"]
            assert not result["hasPrevious"]
            assert result["totalPages"] == 1
            assert isinstance(result["totalElements"], int)
            assert isinstance(result["projects"], list)
            for item in result["projects"]:
                assert isinstance(item, dict)
                assert list(item.keys()) == ["id", "name"]
                assert isinstance(item["name"], str)
                assert isinstance(item["id"], str)

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_metadata_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(
                PROJECT_METADATA_PAGE_URL, method="GET", headers=HEADERS
            )
            self.assert401(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_403(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_metadata_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            response = self.client.open(
                PROJECT_METADATA_PAGE_URL, method="GET", headers=HEADERS
            )
            self.assert403(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_metadata_from_schematic",
            side_effect=TypeError,
        ):
            response = self.client.open(
                PROJECT_METADATA_PAGE_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestGetProjectDatasetMetadataArray(BaseTestCase):
    """Test case for dataset metadat endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            return_value=[
                DatasetMetadata("syn1", "name1"),
                DatasetMetadata("syn2", "name2"),
            ],
        ):
            response = self.client.open(
                PROJECT_DATASET_METATDATA_ARRRAY_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            result = response.json
            assert isinstance(result, dict)
            assert isinstance(result["datasets"], list)
            for item in result["datasets"]:
                assert isinstance(item, dict)
                assert list(item.keys()) == ["id", "name"]
                assert isinstance(item["id"], str)
                assert isinstance(item["name"], str)

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(
                PROJECT_DATASET_METATDATA_ARRRAY_URL, method="GET", headers=HEADERS
            )
            self.assert401(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_403(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            response = self.client.open(
                PROJECT_DATASET_METATDATA_ARRRAY_URL, method="GET", headers=HEADERS
            )
            self.assert403(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            side_effect=TypeError,
        ):
            response = self.client.open(
                PROJECT_DATASET_METATDATA_ARRRAY_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestGetProjectDatasetMetadataPage(BaseTestCase):
    """Test case for dataset metadat endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            return_value=[
                DatasetMetadata("syn1", "name1"),
                DatasetMetadata("syn2", "name2"),
            ],
        ):
            response = self.client.open(
                PROJECT_DATASET_METATDATA_PAGE_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            result = response.json
            assert isinstance(result, dict)
            assert result["number"] == 1
            assert result["size"] == 100000
            assert not result["hasNext"]
            assert not result["hasPrevious"]
            assert result["totalPages"] == 1
            assert isinstance(result["totalElements"], int)
            assert isinstance(result["datasets"], list)
            for item in result["datasets"]:
                assert isinstance(item, dict)
                assert list(item.keys()) == ["id", "name"]
                assert isinstance(item["id"], str)
                assert isinstance(item["name"], str)

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(
                PROJECT_DATASET_METATDATA_PAGE_URL, method="GET", headers=HEADERS
            )
            self.assert401(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_403(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            response = self.client.open(
                PROJECT_DATASET_METATDATA_PAGE_URL, method="GET", headers=HEADERS
            )
            self.assert403(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            side_effect=TypeError,
        ):
            response = self.client.open(
                PROJECT_DATASET_METATDATA_PAGE_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestGetProjectManifestMetadataArray(BaseTestCase):
    """Test case for manifests endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifest_metadata_from_schematic",
            return_value=EXAMPLE_MANIFEST_METADATA,
        ):
            response = self.client.open(
                PROJECT_MANIFEST_METADATA_ARRAY_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            result = response.json
            assert isinstance(result, dict)
            assert isinstance(result["manifests"], list)
            for item in result["manifests"]:
                assert isinstance(item, dict)
                assert list(item.keys()) == MANIFEST_METADATA_KEYS
                assert isinstance(item["id"], str)
                assert isinstance(item["name"], str)
                assert isinstance(item["datasetName"], str)
                assert isinstance(item["datasetId"], str)
                assert isinstance(item["componentName"], str)

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifest_metadata_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(
                PROJECT_MANIFEST_METADATA_ARRAY_URL, method="GET", headers=HEADERS
            )
            self.assert401(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_403(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifest_metadata_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            response = self.client.open(
                PROJECT_MANIFEST_METADATA_ARRAY_URL, method="GET", headers=HEADERS
            )
            self.assert403(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifest_metadata_from_schematic",
            side_effect=TypeError,
        ):
            response = self.client.open(
                PROJECT_MANIFEST_METADATA_ARRAY_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestGetProjectManifestMetadataPage(BaseTestCase):
    """Test case for manifests endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifest_metadata_from_schematic",
            return_value=EXAMPLE_MANIFEST_METADATA,
        ):
            response = self.client.open(
                PROJECT_MANIFEST_METADATA_PAGE_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            result = response.json
            assert isinstance(result, dict)
            assert result["number"] == 1
            assert result["size"] == 100000
            assert not result["hasNext"]
            assert not result["hasPrevious"]
            assert result["totalPages"] == 1
            assert isinstance(result["totalElements"], int)
            assert isinstance(result["manifests"], list)
            for item in result["manifests"]:
                assert isinstance(item, dict)
                assert list(item.keys()) == MANIFEST_METADATA_KEYS
                assert isinstance(item["id"], str)
                assert isinstance(item["name"], str)
                assert isinstance(item["datasetName"], str)
                assert isinstance(item["datasetId"], str)
                assert isinstance(item["componentName"], str)

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifest_metadata_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(
                PROJECT_MANIFEST_METADATA_PAGE_URL, method="GET", headers=HEADERS
            )
            self.assert401(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_403(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifest_metadata_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            response = self.client.open(
                PROJECT_MANIFEST_METADATA_PAGE_URL, method="GET", headers=HEADERS
            )
            self.assert403(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifest_metadata_from_schematic",
            side_effect=TypeError,
        ):
            response = self.client.open(
                PROJECT_MANIFEST_METADATA_PAGE_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


if __name__ == "__main__":
    unittest.main()
