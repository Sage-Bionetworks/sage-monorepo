"""Tests for manifest generation endpoint functions"""
# pylint: disable=no-member

from unittest.mock import patch

from schematic_api.test import BaseTestCase

from .conftest import (
    CREATE_MANIFESTS_MOCK,
    TEST_SCHEMA_URL,
)

GENERATE_EXCEL_MANIFESTS_URL = (
    f"/api/v1/generateExcelManifest?schemaUrl={TEST_SCHEMA_URL}"
)
GENERATE_GOOGLE_SHEET_MANIFESTS_URL = (
    f"/api/v1/generateGoogleSheetManifests?schemaUrl={TEST_SCHEMA_URL}"
)

HEADERS = {
    "Accept": "application/json",
    "Authorization": "Bearer xxx",
}


class TestGenerateExcelManifest(BaseTestCase):
    """Tests excel manifest endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch(CREATE_MANIFESTS_MOCK, return_value=["path"]) as mock_method:  # type: ignore
            url = (
                f"{GENERATE_EXCEL_MANIFESTS_URL}"
                "&datasetId=syn2"
                "&dataType=data_type"
                "&assetViewId=syn1"
            )
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            result = response.json
            assert isinstance(result, str)
            assert result == "path"
            call_args = mock_method.call_args.kwargs
            assert call_args["output_format"] == "excel"
            assert call_args["data_types"] == ["data_type"]
            assert not call_args["title"]
            assert call_args["dataset_ids"] == ["syn2"]
            assert call_args["strict"]
            assert not call_args["use_annotations"]
            assert call_args["data_model_labels"] == "class_label"

    def test_arguments(self) -> None:
        """Test for arguments"""
        with patch(CREATE_MANIFESTS_MOCK, return_value=["path"]) as mock_method:  # type: ignore
            url = (
                f"{GENERATE_EXCEL_MANIFESTS_URL}"
                "&datasetId=syn2"
                "&dataType=data_type1"
                "&manifestTitle=title"
                "&useStrictValidation=false"
                "&addAnnotations=true"
                "&displayLabelType=display_label"
            )
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            result = response.json
            assert isinstance(result, str)
            assert result == "path"
            call_args = mock_method.call_args.kwargs
            assert call_args["output_format"] == "excel"
            assert call_args["data_types"] == ["data_type1"]
            assert call_args["dataset_ids"] == ["syn2"]
            assert call_args["title"] == "title"
            assert not call_args["strict"]
            assert call_args["use_annotations"]
            assert call_args["data_model_labels"] == "display_label"


class TestGenerateGoogleSheetManifests(BaseTestCase):
    """Tests google sheet manifest endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch(CREATE_MANIFESTS_MOCK, return_value=["l1"]) as mock_method:  # type: ignore
            url = (
                f"{GENERATE_GOOGLE_SHEET_MANIFESTS_URL}"
                "&datasetIdArray=syn2"
                "&dataTypeArray=node_label"
                "&assetViewId=syn1"
            )
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            result = response.json
            assert isinstance(result, dict)
            assert list(result.keys()) == ["links"]
            assert result["links"] == ["l1"]
            call_args = mock_method.call_args.kwargs
            assert call_args["output_format"] == "google_sheet"
            assert call_args["data_types"] == ["node_label"]
            assert not call_args["title"]
            assert call_args["dataset_ids"] == ["syn2"]
            assert call_args["strict"]
            assert not call_args["use_annotations"]
            assert call_args["data_model_labels"] == "class_label"

    def test_arguments(self) -> None:
        """Test for correct arguments"""
        with patch(CREATE_MANIFESTS_MOCK, return_value=["l1"]) as mock_method:  # type: ignore
            url = (
                f"{GENERATE_GOOGLE_SHEET_MANIFESTS_URL}"
                "&dataTypeArray=data_type1"
                "&dataTypeArray=data_type2"
                "&datasetIdArray=syn2"
                "&datasetIdArray=syn3"
                "&manifestTitle=title"
                "&useStrictValidation=false"
                "&addAnnotations=true"
                "&displayLabelType=display_label"
            )
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            result = response.json
            assert isinstance(result, dict)
            assert list(result.keys()) == ["links"]
            assert result["links"] == ["l1"]
            call_args = mock_method.call_args.kwargs
            assert call_args["output_format"] == "google_sheet"
            assert call_args["data_types"] == ["data_type1", "data_type2"]
            assert call_args["dataset_ids"] == ["syn2", "syn3"]
            assert call_args["title"] == "title"
            assert not call_args["strict"]
            assert call_args["use_annotations"]
            assert call_args["data_model_labels"] == "display_label"

            url = f"{GENERATE_GOOGLE_SHEET_MANIFESTS_URL}&generateAllManifests=true"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            result = response.json
            assert isinstance(result, dict)
            assert list(result.keys()) == ["links"]
            assert result["links"] == ["l1"]
            call_args = mock_method.call_args.kwargs
            assert call_args["output_format"] == "google_sheet"
            assert call_args["data_types"] == ["all manifests"]
            assert not call_args["title"]
            assert not call_args["dataset_ids"]
            assert call_args["strict"]
            assert not call_args["use_annotations"]
            assert call_args["data_model_labels"] == "class_label"
