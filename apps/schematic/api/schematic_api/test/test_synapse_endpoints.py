"""Tests for endpoints that use Synapse without mocking the Synapse client"""

import json
import os

import pytest
import yaml
import pandas as pd

from schematic_api.test import BaseTestCase
from .conftest import (
    TEST_SCHEMA_URL,
    CORRECT_MANIFEST_PATH,
    csv_to_bytes,
    csv_to_json_str,
)

SECRETS_FILE = "schematic_api/test/data/synapse_config.yaml"
EXAMPLE_SECRETS_FILE = "schematic_api/test/data/synapse_config_example.yaml"

if os.path.exists(SECRETS_FILE):
    with open(SECRETS_FILE, "r", encoding="utf-8") as file:
        secrets = yaml.safe_load(file)
else:
    with open(EXAMPLE_SECRETS_FILE, "r", encoding="utf-8") as file:
        secrets = yaml.safe_load(file)

SYNAPSE_TOKEN = secrets["synapse_token"]
TEST_DATASET = secrets["test_dataset"]
TEST_MANIFEST = secrets["test_manifest"]
TEST_ASSET_VIEW = secrets["test_asset_view"]
TEST_PROJECT = secrets["test_project"]

HEADERS = {
    "Accept": "application/json",
    "Authorization": f"Bearer {SYNAPSE_TOKEN}",
}


@pytest.mark.synapse
@pytest.mark.secrets
class TestValidationEndpoints(BaseTestCase):
    """Integration tests"""

    def test_submit_manifest_csv(self) -> None:
        """Test for successful result"""
        url = (
            f"/api/v1/submitManifestCsv?schemaUrl={TEST_SCHEMA_URL}"
            "&component=Biospecimen"
            f"&datasetId={TEST_DATASET}"
            f"&assetViewId={TEST_ASSET_VIEW}"
            "&storageMethod=file_only"
        )
        body = csv_to_bytes(CORRECT_MANIFEST_PATH)
        response = self.client.open(url, method="POST", headers=HEADERS, data=body)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert isinstance(response.json, str)

    def test_submit_manifest_json(self) -> None:
        """Test for successful result"""
        url = (
            f"/api/v1/submitManifestJson?schemaUrl={TEST_SCHEMA_URL}"
            "&component=Biospecimen"
            f"&datasetId={TEST_DATASET}"
            f"&assetViewId={TEST_ASSET_VIEW}"
            "&storageMethod=file_only"
        )
        body = csv_to_json_str(CORRECT_MANIFEST_PATH)
        response = self.client.open(url, method="POST", headers=HEADERS, data=body)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert isinstance(response.json, str)


@pytest.mark.synapse
@pytest.mark.secrets
class TestStorageEndpoints(BaseTestCase):
    """Integration tests"""

    def test_get_asset_view_json(self) -> None:
        """Test for successful result"""
        url = "/api/v1/assetTypes/synapse/" f"assetViews/{TEST_ASSET_VIEW}/json"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert isinstance(response.json, str)
        response_dict = json.loads(response.json)
        assert isinstance(response_dict, dict)
        dataframe = pd.DataFrame.from_dict(response_dict)
        assert isinstance(dataframe, pd.DataFrame)

    def test_get_dataset_file_metadata_array(self) -> None:
        """Test for successful result"""
        url = (
            "/api/v1/assetTypes/synapse/"
            f"datasets/{TEST_DATASET}/fileMetadataArray"
            f"?assetViewId={TEST_ASSET_VIEW}"
        )
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert isinstance(response.json, dict)
        assert "files" in response.json
        assert isinstance(response.json["files"], list)
        for item in response.json["files"]:
            assert isinstance(item, dict)
            for key in ["id", "name"]:
                assert key in item

    def test_get_dataset_file_metadata_page(self) -> None:
        """Test for successful result"""
        url = (
            "/api/v1/assetTypes/synapse/"
            f"datasets/{TEST_DATASET}/fileMetadataPage"
            f"?assetViewId={TEST_ASSET_VIEW}"
        )
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert isinstance(response.json, dict)
        assert "files" in response.json
        assert isinstance(response.json["files"], list)
        for item in response.json["files"]:
            assert isinstance(item, dict)
            for key in ["id", "name"]:
                assert key in item

    def test_get_dataset_manifest_json(self) -> None:
        """Test for successful result"""
        url = (
            "/api/v1/assetTypes/synapse/"
            f"datasets/{TEST_DATASET}/manifestJson"
            f"?assetViewId={TEST_ASSET_VIEW}"
        )
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert isinstance(response.json, str)
        response_dict = json.loads(response.json)
        assert isinstance(response_dict, dict)
        dataframe = pd.DataFrame.from_dict(response_dict)
        assert isinstance(dataframe, pd.DataFrame)

    def test_get_manifest_json(self) -> None:
        """Test for successful result"""
        url = "/api/v1/assetTypes/synapse/" f"manifests/{TEST_MANIFEST}/json"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert isinstance(response.json, str)
        response_dict = json.loads(response.json)
        assert isinstance(response_dict, dict)
        dataframe = pd.DataFrame.from_dict(response_dict)
        assert isinstance(dataframe, pd.DataFrame)

    def test_get_projects(self) -> None:
        """Test for successful result"""
        url = "/api/v1/assetTypes/synapse/" f"assetViews/{TEST_ASSET_VIEW}/projects"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert isinstance(response.json, dict)
        assert "projects" in response.json
        assert isinstance(response.json["projects"], list)
        for project in response.json["projects"]:
            assert isinstance(project, dict)
            for key in ["id", "name"]:
                assert key in project

    def test_get_project_dataset_metadata_array(self) -> None:
        """Test for successful result"""
        url = (
            "/api/v1/assetTypes/synapse/"
            f"projects/{TEST_PROJECT}/datasetMetadataArray"
            f"?assetViewId={TEST_ASSET_VIEW}"
        )
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert isinstance(response.json, dict)
        assert "datasets" in response.json
        assert isinstance(response.json["datasets"], list)
        for dataset in response.json["datasets"]:
            assert isinstance(dataset, dict)
            for key in ["id", "name"]:
                assert key in dataset

    def test_get_project_dataset_metadata_page(self) -> None:
        """Test for successful result"""
        url = (
            "/api/v1/assetTypes/synapse/"
            f"projects/{TEST_PROJECT}/datasetMetadataPage"
            f"?assetViewId={TEST_ASSET_VIEW}"
        )
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert isinstance(response.json, dict)
        assert "datasets" in response.json
        assert isinstance(response.json["datasets"], list)
        for dataset in response.json["datasets"]:
            assert isinstance(dataset, dict)
            for key in ["id", "name"]:
                assert key in dataset

    def test_get_project_manifests(self) -> None:
        """Test for successful result"""
        url = (
            "/api/v1/assetTypes/synapse/"
            f"projects/{TEST_PROJECT}/manifests"
            f"?assetViewId={TEST_ASSET_VIEW}"
        )
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert isinstance(response.json, dict)
        assert "manifests" in response.json
        assert isinstance(response.json["manifests"], list)
        for manifest in response.json["manifests"]:
            assert isinstance(manifest, dict)
            for key in ["componentName", "datasetId", "datasetName", "id", "name"]:
                assert key in manifest
