"""Tests for endpoints"""

# pylint: disable=duplicate-code

import unittest
from unittest.mock import patch
import pandas as pd

from synapseclient.core.exceptions import SynapseNoCredentialsError  # type: ignore
from schematic.exceptions import AccessCredentialsError  # type: ignore

import schematic_api.controllers.storage_controller_impl
from schematic_api.test import BaseTestCase
from schematic_api.models.file_metadata import FileMetadata

HEADERS = {
    "Accept": "application/json",
    "Authorization": "Bearer xxx",
}

ASSET_VIEW_CSV_URL = "/api/v1/assetTypes/synapse/assetViews/syn1/csv"
ASSET_VIEW_JSON_URL = "/api/v1/assetTypes/synapse/assetViews/syn1/json"
DATASET_FILE_METADATA_ARRAY_URL = (
    "/api/v1/assetTypes/synapse/datasets/syn2/fileMetadataArray?assetViewId=syn1"
)
DATASET_FILE_METADATA_PAGE_URL = (
    "/api/v1/assetTypes/synapse/datasets/syn2/fileMetadataPage?assetViewId=syn1"
)
DATASET_MANIFEST_CSV_URL = (
    "/api/v1/assetTypes/synapse/datasets/syn2/manifestCsv?assetViewId=syn1"
)
DATASET_MANIFEST_JSON_URL = (
    "/api/v1/assetTypes/synapse/datasets/syn2/manifestJson?assetViewId=syn1"
)
MANIFEST_CSV_URL = "/api/v1/assetTypes/synapse/manifests/syn1/csv"
MANIFEST_JSON_URL = "/api/v1/assetTypes/synapse/manifests/syn1/json"


class TestGetAssetViewCsv(BaseTestCase):
    """Test case for asset view json endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_asset_view_from_schematic",
            return_value=pd.DataFrame({"col1": [1, 2], "col2": [3, 4]}),
        ):
            response = self.client.open(
                ASSET_VIEW_CSV_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            result = response.json
            assert isinstance(result, str)
            assert result.endswith("asset_view.csv")

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_asset_view_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(
                ASSET_VIEW_CSV_URL, method="GET", headers=HEADERS
            )
            self.assert401(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_403(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_asset_view_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            response = self.client.open(
                ASSET_VIEW_CSV_URL, method="GET", headers=HEADERS
            )
            self.assert403(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_asset_view_from_schematic",
            side_effect=TypeError,
        ):
            response = self.client.open(
                ASSET_VIEW_CSV_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestGetAssetViewJson(BaseTestCase):
    """Test case for asset view json endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_asset_view_from_schematic",
            return_value=pd.DataFrame({"col1": [1, 2], "col2": [3, 4]}),
        ):
            response = self.client.open(
                ASSET_VIEW_JSON_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            assert response.json == '{"col1":{"0":1,"1":2},"col2":{"0":3,"1":4}}'

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_asset_view_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(
                ASSET_VIEW_JSON_URL, method="GET", headers=HEADERS
            )
            self.assert401(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_403(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_asset_view_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            response = self.client.open(
                ASSET_VIEW_JSON_URL, method="GET", headers=HEADERS
            )
            self.assert403(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_asset_view_from_schematic",
            side_effect=TypeError,
        ):
            response = self.client.open(
                ASSET_VIEW_JSON_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestGetDatasetFileMetadataArray(BaseTestCase):
    """Test case for files endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            return_value=[FileMetadata("syn1", "name1"), FileMetadata("syn2", "name2")],
        ):
            response = self.client.open(
                DATASET_FILE_METADATA_ARRAY_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            result = response.json
            assert isinstance(result, dict)
            assert isinstance(result["files"], list)
            for item in result["files"]:
                assert isinstance(item, dict)
                assert isinstance(item["id"], str)
                assert isinstance(item["name"], str)

    def test_file_names(self) -> None:
        """Test with file_names parameter"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            return_value=[FileMetadata("syn1", "name1"), FileMetadata("syn2", "name2")],
        ) as mock_function:
            response = self.client.open(
                DATASET_FILE_METADATA_ARRAY_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with("syn2", "synapse", None, False)

            url = f"{DATASET_FILE_METADATA_ARRAY_URL}&fileNames=file.text"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with("syn2", "synapse", ["file.text"], False)

            url = f"{DATASET_FILE_METADATA_ARRAY_URL}&fileNames=file.text&fileNames=file2.text"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with(
                "syn2", "synapse", ["file.text", "file2.text"], False
            )

    def test_use_full_file_path(self) -> None:
        """Test with use_full_file_path parameter"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            return_value=[FileMetadata("syn1", "name1"), FileMetadata("syn2", "name2")],
        ) as mock_function:
            url = f"{DATASET_FILE_METADATA_ARRAY_URL}&useFullFilePath=true"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with("syn2", "synapse", None, True)

            url = f"{DATASET_FILE_METADATA_ARRAY_URL}&useFullFilePath=false"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with("syn2", "synapse", None, False)

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(
                DATASET_FILE_METADATA_ARRAY_URL, method="GET", headers=HEADERS
            )
            self.assert401(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_403(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            response = self.client.open(
                DATASET_FILE_METADATA_ARRAY_URL, method="GET", headers=HEADERS
            )
            self.assert403(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            side_effect=TypeError,
        ):
            response = self.client.open(
                DATASET_FILE_METADATA_ARRAY_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestGetDatasetFileMetadataPage(BaseTestCase):
    """Test case for files endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            return_value=[FileMetadata("syn1", "name1"), FileMetadata("syn2", "name2")],
        ):
            response = self.client.open(
                DATASET_FILE_METADATA_PAGE_URL, method="GET", headers=HEADERS
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
            assert isinstance(result["files"], list)
            for item in result["files"]:
                assert isinstance(item, dict)
                assert list(item.keys()) == ["id", "name"]
                assert isinstance(item["id"], str)
                assert isinstance(item["name"], str)

    def test_file_names(self) -> None:
        """Test with file_names parameter"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            return_value=[FileMetadata("syn1", "name1"), FileMetadata("syn2", "name2")],
        ) as mock_function:
            response = self.client.open(
                DATASET_FILE_METADATA_PAGE_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with("syn2", "synapse", None, False)

            url = f"{DATASET_FILE_METADATA_PAGE_URL}&fileNames=file.text"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with("syn2", "synapse", ["file.text"], False)

            url = f"{DATASET_FILE_METADATA_PAGE_URL}&fileNames=file.text&fileNames=file2.text"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with(
                "syn2", "synapse", ["file.text", "file2.text"], False
            )

    def test_use_full_file_path(self) -> None:
        """Test with use_full_file_path parameter"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            return_value=[FileMetadata("syn1", "name1"), FileMetadata("syn2", "name2")],
        ) as mock_function:
            url = f"{DATASET_FILE_METADATA_PAGE_URL}&useFullFilePath=true"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with("syn2", "synapse", None, True)

            url = f"{DATASET_FILE_METADATA_PAGE_URL}&useFullFilePath=false"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with("syn2", "synapse", None, False)

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(
                DATASET_FILE_METADATA_PAGE_URL, method="GET", headers=HEADERS
            )
            self.assert401(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_403(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            response = self.client.open(
                DATASET_FILE_METADATA_PAGE_URL, method="GET", headers=HEADERS
            )
            self.assert403(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            side_effect=TypeError,
        ):
            response = self.client.open(
                DATASET_FILE_METADATA_PAGE_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestGetDatasetManifestCSV(BaseTestCase):
    """Test case for manifest json endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_manifest_from_schematic",
            return_value=pd.DataFrame({"col1": [1, 2], "col2": [3, 4]}),
        ):
            response = self.client.open(
                DATASET_MANIFEST_CSV_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            result = response.json
            assert isinstance(result, str)
            assert result.endswith("manifest.csv")

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_manifest_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(
                DATASET_MANIFEST_CSV_URL, method="GET", headers=HEADERS
            )
            self.assert401(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_403(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_manifest_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            response = self.client.open(
                DATASET_MANIFEST_CSV_URL, method="GET", headers=HEADERS
            )
            self.assert403(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_manifest_from_schematic",
            side_effect=TypeError,
        ):
            response = self.client.open(
                DATASET_MANIFEST_CSV_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestGetDatasetManifestJson(BaseTestCase):
    """Test case for manifest json endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_manifest_from_schematic",
            return_value=pd.DataFrame({"col1": [1, 2], "col2": [3, 4]}),
        ):
            response = self.client.open(
                DATASET_MANIFEST_JSON_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            assert response.json == '{"col1":{"0":1,"1":2},"col2":{"0":3,"1":4}}'

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_manifest_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(
                DATASET_MANIFEST_JSON_URL, method="GET", headers=HEADERS
            )
            self.assert401(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_403(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_manifest_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            response = self.client.open(
                DATASET_MANIFEST_JSON_URL, method="GET", headers=HEADERS
            )
            self.assert403(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_manifest_from_schematic",
            side_effect=TypeError,
        ):
            response = self.client.open(
                DATASET_MANIFEST_JSON_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestGetManifestCSV(BaseTestCase):
    """Test case for manifest json endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_manifest_from_schematic",
            return_value=pd.DataFrame({"col1": [1, 2], "col2": [3, 4]}),
        ):
            response = self.client.open(MANIFEST_CSV_URL, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            result = response.json
            assert isinstance(result, str)
            assert result.endswith("manifest.csv")

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_manifest_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(MANIFEST_CSV_URL, method="GET", headers=HEADERS)
            self.assert401(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_403(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_manifest_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            response = self.client.open(MANIFEST_CSV_URL, method="GET", headers=HEADERS)
            self.assert403(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_manifest_from_schematic",
            side_effect=TypeError,
        ):
            response = self.client.open(MANIFEST_CSV_URL, method="GET", headers=HEADERS)
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestGetManifestJson(BaseTestCase):
    """Test case for manifest json endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_manifest_from_schematic",
            return_value=pd.DataFrame({"col1": [1, 2], "col2": [3, 4]}),
        ):
            response = self.client.open(
                MANIFEST_JSON_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            assert response.json == '{"col1":{"0":1,"1":2},"col2":{"0":3,"1":4}}'

    def test_401(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_manifest_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            response = self.client.open(
                MANIFEST_JSON_URL, method="GET", headers=HEADERS
            )
            self.assert401(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_403(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_manifest_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            response = self.client.open(
                MANIFEST_JSON_URL, method="GET", headers=HEADERS
            )
            self.assert403(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_manifest_from_schematic",
            side_effect=TypeError,
        ):
            response = self.client.open(
                MANIFEST_JSON_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


if __name__ == "__main__":
    unittest.main()
