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
STORAGE_PROJECTS_DATASETS_URL = "/api/v1/storages/asset-views/id1/projects/id2/datasets"


class TestStorageProjectDatasets(BaseTestCase):
    """Test case for storages/projects/datasets"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_datasets",
            return_value=[("id1", "name1"), ("id2", "name2")],
        ):

            response = self.client.open(
                STORAGE_PROJECTS_DATASETS_URL, method="GET", headers=HEADERS
            )
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
            for dataset in datasets:
                assert list(dataset.keys()) == ["id", "name"]

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_datasets",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(
                STORAGE_PROJECTS_DATASETS_URL, method="GET", headers=HEADERS
            )
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
            response = self.client.open(
                STORAGE_PROJECTS_DATASETS_URL, method="GET", headers=HEADERS
            )
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
            response = self.client.open(
                STORAGE_PROJECTS_DATASETS_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestStorageController(BaseTestCase):
    """StorageController integration test stubs"""

    def test_list_storage_project_datasets(self) -> None:
        """Test case for list_storage_project_datasets

        Gets all datasets in folder under a given storage project that the
         current user has access to.
        """
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_datasets",
            return_value=[("id1", "name1"), ("id2", "name2")],
        ):

            endpoint_url = "/api/v1/storages/asset-views/id1/projects/id2/datasets"

            response = self.client.open(endpoint_url, method="GET", headers=HEADERS)
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
            for dataset in datasets:
                assert list(dataset.keys()) == ["id", "name"]

    def test_list_storage_project_manifests(self) -> None:
        """Test case for list_storage_project_manifests

        Gets all manifests in a project folder that users have access to
        """
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifests",
            return_value=EXAMPLE_MANIFEST_METADATA,
        ):
            endpoint_url = "/api/v1/storages/asset-views/id1/projects/id2/manifests"
            response = self.client.open(endpoint_url, method="GET", headers=HEADERS)
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
            for manifest in manifests:
                assert list(manifest.keys()) == [
                    "componentName",
                    "datasetId",
                    "datasetName",
                    "id",
                    "name",
                ]


if __name__ == "__main__":
    unittest.main()
