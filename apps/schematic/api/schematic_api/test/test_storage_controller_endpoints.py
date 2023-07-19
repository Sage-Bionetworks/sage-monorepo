# coding: utf-8

from __future__ import absolute_import
import unittest

from yaml import safe_load

from schematic_api.test import BaseTestCase

from .conftest import SECRETS_PATH

with open(SECRETS_PATH, mode="rt", encoding="utf-8") as file:
    DICT = safe_load(file)
PROJECT_ID = "syn26251192"
ASSET_VIEW_ID = "syn23643253"
HEADERS = {
    'Accept': 'application/json',
    'Authorization': f'Bearer {DICT["synapse_auth_token"]}',
}


class TestStorageController(BaseTestCase):
    """StorageController integration test stubs"""

    def test_list_storage_project_datasets(self) -> None:
        """Test case for list_storage_project_datasets

        Gets all datasets in folder under a given storage project that the
         current user has access to.
        """
        endpoint_url = (
            "/api/v1/storages/asset-views/"
            f"{ASSET_VIEW_ID}/projects/{PROJECT_ID}/datasets"
        )
        response = self.client.open(
            endpoint_url,
            method='GET',
            headers=HEADERS
        )
        self.assert200(
            response,
            f"Response body is : {response.data.decode('utf-8')}"
        )

        assert not response.json['hasNext']
        assert not response.json['hasPrevious']
        assert response.json['number'] == 0
        assert response.json['size'] == 100
        assert response.json['totalElements'] == 1
        assert response.json['totalPages'] == 1
        datasets = response.json['datasets']
        assert len(datasets) == 1
        for dataset in datasets:
            assert list(dataset.keys()) == [
                "id",
                "name"
            ]

    def test_list_storage_project_manifests(self) -> None:
        """Test case for list_storage_project_manifests

        Gets all manifests in a project folder that users have access to
        """
        endpoint_url = (
            "/api/v1/storages/asset-views/"
            f"{ASSET_VIEW_ID}/projects/{PROJECT_ID}/manifests"
        )
        response = self.client.open(
            endpoint_url,
            method='GET',
            headers=HEADERS
        )
        self.assert200(
            response,
            f"Response body is : {response.data.decode('utf-8')}"
        )

        assert not response.json['hasNext']
        assert not response.json['hasPrevious']
        assert response.json['number'] == 0
        assert response.json['size'] == 100
        assert response.json['totalElements'] == 1
        assert response.json['totalPages'] == 1
        manifests = response.json['manifests']
        assert len(manifests) == 1
        for manifest in manifests:
            assert list(manifest.keys()) == [
                "componentName",
                "datasetId",
                "datasetName",
                "id",
                "name"
            ]


if __name__ == '__main__':
    unittest.main()
