"""Tests for manifest generation endpoint functions"""

# pylint: disable=no-member

from unittest.mock import patch

from schematic_api.models.basic_error import BasicError
from schematic_api.models.google_sheet_links import GoogleSheetLinks
from schematic_api.controllers.manifest_generation_controller_impl import (
    generate_excel_manifest_file,
    generate_excel_manifest,
    generate_google_sheet_manifests,
)
from .conftest import GET_ACCESS_TOKEN_MOCK, CREATE_MANIFESTS_MOCK


class TestGenerateExcelManifestFile:
    """Tests generate_excel_manifest_file"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        with patch(GET_ACCESS_TOKEN_MOCK):
            with patch(CREATE_MANIFESTS_MOCK, return_value=["path1"]):
                result, status = generate_excel_manifest_file(
                    schema_url=test_schema_url,
                    dataset_id="syn2",
                    asset_view_id="syn1",
                    data_type="syn4",
                    add_annotations=False,
                    manifest_title="title",
                    display_label_type="class_label",
                )
                assert status == 200
                assert result == "path1"

    def test_error(self, test_schema_url: str) -> None:
        """Test for successful result"""
        with patch(GET_ACCESS_TOKEN_MOCK):
            with patch(CREATE_MANIFESTS_MOCK, side_effect=TypeError):
                output = generate_excel_manifest_file(
                    schema_url=test_schema_url,
                    dataset_id="syn2",
                    asset_view_id="syn1",
                    data_type="syn4",
                    add_annotations=False,
                    manifest_title="title",
                    use_strict_validation=True,
                    display_label_type="class_label",
                )
                assert isinstance(output, tuple)
                result, status = output
                assert status == 500
                assert isinstance(result, BasicError)


class TestGenerateExcelManifest:  # pylint: disable=too-few-public-methods
    """Tests generate_excel_manifest"""

    def test_error(self, test_schema_url: str) -> None:
        """Test for successful result"""
        with patch(GET_ACCESS_TOKEN_MOCK):
            with patch(CREATE_MANIFESTS_MOCK, side_effect=TypeError):
                output = generate_excel_manifest(
                    schema_url=test_schema_url,
                    dataset_id="syn2",
                    asset_view_id="syn1",
                    data_type="syn4",
                    add_annotations=False,
                    manifest_title="title",
                    display_label_type="class_label",
                )
                assert isinstance(output, tuple)
                result, status = output
                assert status == 500
                assert isinstance(result, BasicError)


class TestGenerateGoogleSheetManifests:
    """Tests generate_google_sheet_manifests"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        with patch(GET_ACCESS_TOKEN_MOCK):
            with patch(CREATE_MANIFESTS_MOCK, return_value=["link1", "link2"]):
                result, status = generate_google_sheet_manifests(
                    schema_url=test_schema_url,
                    dataset_id_array=["syn2", "syn3"],
                    asset_view_id="syn1",
                    data_type_array=["syn4", "syn5"],
                    add_annotations=False,
                    manifest_title="title",
                    use_strict_validation=True,
                    generate_all_manifests=False,
                    display_label_type="class_label",
                )
                assert status == 200
                assert isinstance(result, GoogleSheetLinks)
                assert result.links == ["link1", "link2"]

    def test_error_statuses(self) -> None:
        """Test for error statuses"""
        with patch(GET_ACCESS_TOKEN_MOCK):
            with patch(CREATE_MANIFESTS_MOCK):
                result, status = generate_google_sheet_manifests(
                    schema_url="not_a_url",
                    dataset_id_array=["syn2", "syn3"],
                    asset_view_id="syn1",
                    data_type_array=["syn4", "syn5"],
                    add_annotations=False,
                    manifest_title="title",
                    use_strict_validation=True,
                    generate_all_manifests=False,
                    display_label_type="class_label",
                )
                assert status == 404
                assert isinstance(result, BasicError)
