"""Tests for endpoints that use Synapse without mocking the Synapse client"""

import json
import os
from unittest import mock
import shutil
from typing import Generator

import pytest
import yaml
import pandas as pd

from schematic.store import SynapseStorage  # type: ignore

from schematic_api.controllers.utils import (
    purge_synapse_cache,
    check_synapse_cache_size,
)
from schematic_api.test import BaseTestCase
from .conftest import (
    MANIFEST_METADATA_KEYS,
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


@pytest.fixture(scope="session", name="synapse_store")
def fixture_synapse_store() -> Generator[SynapseStorage, None, None]:
    """
    Yields A synapse storage object, and deletes the cache at the end of the session
    """
    synapse_store = SynapseStorage(
        access_token=SYNAPSE_TOKEN, synapse_cache_path="test_cache_path"
    )
    yield synapse_store
    shutil.rmtree("test_cache_path")


@pytest.mark.synapse
@pytest.mark.secrets
class TestGenerateGoogleSheetManifests(BaseTestCase):
    """Tests google sheet manifest endpoint"""

    # local environment has variable 'SECRETS_MANAGER_SECRETS that causes an error when creating
    # google credentials
    @mock.patch.dict(os.environ, {}, clear=True)
    def test_success1(self) -> None:
        """Test for successful result"""
        url = (
            f"/api/v1/generateGoogleSheetManifests?schemaUrl={TEST_SCHEMA_URL}"
            "&assetViewId=syn28559058"
            "&dataTypeArray=Patient"
            "&dataTypeArray=Biospecimen"
            "&datasetIdArray=syn51730545"
            "&datasetIdArray=syn51730547"
        )
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        result = response.json
        assert isinstance(result, dict)
        assert list(result.keys()) == ["links"]
        links = result["links"]
        assert isinstance(links, list)
        assert len(links) == 2

    # local environment has variable 'SECRETS_MANAGER_SECRETS that causes an error when creating
    # google credentials
    @mock.patch.dict(os.environ, {}, clear=True)
    def test_success2(self) -> None:
        """Test for successful result"""
        url = (
            f"/api/v1/generateGoogleSheetManifests?schemaUrl={TEST_SCHEMA_URL}"
            "&assetViewId=syn28559058"
            "&generateAllManifests=true"
        )
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        result = response.json
        assert isinstance(result, dict)
        assert list(result.keys()) == ["links"]
        links = result["links"]
        assert isinstance(links, list)
        assert len(links) == 3


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

    def test_get_asset_view_csv(self) -> None:
        """Test for successful result"""
        url = "/api/v1/assetTypes/synapse/" f"assetViews/{TEST_ASSET_VIEW}/csv"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        path = json.loads(response.data)
        assert isinstance(path, str)
        assert path.endswith(".asset_view.csv")
        asset_view = pd.read_csv(path)
        assert isinstance(asset_view, pd.DataFrame)

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

    def test_get_dataset_manifest_csv(self) -> None:
        """Test for successful result"""
        url = (
            "/api/v1/assetTypes/synapse/"
            f"datasets/{TEST_DATASET}/manifestCsv"
            f"?assetViewId={TEST_ASSET_VIEW}"
        )
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        path = json.loads(response.data)
        assert isinstance(path, str)
        assert path.endswith(".manifest.csv")
        asset_view = pd.read_csv(path)
        assert isinstance(asset_view, pd.DataFrame)

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

    def test_get_manifest_csv(self) -> None:
        """Test for successful result"""
        url = "/api/v1/assetTypes/synapse/" f"manifests/{TEST_MANIFEST}/csv"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        path = json.loads(response.data)
        assert isinstance(path, str)
        assert path.endswith(".manifest.csv")
        asset_view = pd.read_csv(path)
        assert isinstance(asset_view, pd.DataFrame)

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

    def test_get_project_metadata_array(self) -> None:
        """Test for successful result"""
        url = (
            "/api/v1/assetTypes/synapse/"
            f"assetViews/{TEST_ASSET_VIEW}/projectMetadataArray"
        )
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert isinstance(response.json, dict)
        assert "projects" in response.json
        assert isinstance(response.json["projects"], list)
        for project in response.json["projects"]:
            assert isinstance(project, dict)
            for key in ["id", "name"]:
                assert key in project

    def test_get_project_metadata_page(self) -> None:
        """Test for successful result"""
        url = (
            "/api/v1/assetTypes/synapse/"
            f"assetViews/{TEST_ASSET_VIEW}/projectMetadataPage"
        )
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

    def test_get_project_manifest_metadata_array(self) -> None:
        """Test for successful result"""
        url = (
            "/api/v1/assetTypes/synapse/"
            f"projects/{TEST_PROJECT}/manifestMetadataArray"
            f"?assetViewId={TEST_ASSET_VIEW}"
        )
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert isinstance(response.json, dict)
        assert "manifests" in response.json
        assert isinstance(response.json["manifests"], list)
        for manifest in response.json["manifests"]:
            assert isinstance(manifest, dict)
            assert list(manifest.keys()) == MANIFEST_METADATA_KEYS

    def test_get_project_manifest_metadata_page(self) -> None:
        """Test for successful result"""
        url = (
            "/api/v1/assetTypes/synapse/"
            f"projects/{TEST_PROJECT}/manifestMetadataPage"
            f"?assetViewId={TEST_ASSET_VIEW}"
        )
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert isinstance(response.json, dict)
        assert "manifests" in response.json
        assert isinstance(response.json["manifests"], list)
        for manifest in response.json["manifests"]:
            assert isinstance(manifest, dict)
            assert list(manifest.keys()) == MANIFEST_METADATA_KEYS


@pytest.mark.synapse
@pytest.mark.secrets
class TestPurgeSynapseCache:  # pylint: disable=too-few-public-methods
    """Tests purge_synapse_cache"""

    def test_success(self, synapse_store: SynapseStorage) -> None:
        """Tests for a successful purge"""
        size_before_purge = check_synapse_cache_size(synapse_store.root_synapse_cache)
        purge_synapse_cache(
            synapse_store, maximum_storage_allowed_cache_gb=0.000001, minute_buffer=0
        )
        size_after_purge = check_synapse_cache_size(synapse_store.root_synapse_cache)
        assert size_before_purge > size_after_purge
