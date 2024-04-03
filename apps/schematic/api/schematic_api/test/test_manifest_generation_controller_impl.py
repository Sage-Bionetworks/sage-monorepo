"""Tests for manifest generation endpoint functions"""
# pylint: disable=no-member

from unittest.mock import patch

from schematic_api.models.basic_error import BasicError
from schematic_api.models.google_sheet_links import GoogleSheetLinks
from schematic_api.controllers.manifest_generation_controller_impl import (
    generate_google_sheet_manifests,
    generate_excel_manifest,
)
from .conftest import GET_ACCESS_TOKEN_MOCK, CREATE_MANIFESTS_MOCK


class TestGenerateExcelManifest:
    """Tests generate_excel_manifest"""

    def success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        with patch(GET_ACCESS_TOKEN_MOCK):
            with patch(CREATE_MANIFESTS_MOCK, return_value="path"):  # type: ignore
                result, status = generate_excel_manifest(
                    schema_url=test_schema_url,
                    dataset_id="syn2",
                    add_annotations=False,
                    manifest_title="title",
                    data_type="syn3",
                    display_label_type="class_label",
                    use_strict_validation=True,
                    asset_view_id="syn1",
                )
                assert status == 200
                assert result == "path"

    def error_statuses(self) -> None:
        """Test for error statuses"""
        with patch(GET_ACCESS_TOKEN_MOCK):
            with patch(CREATE_MANIFESTS_MOCK):
                result, status = generate_excel_manifest(
                    schema_url="not_a_url",
                    dataset_id="syn2",
                    add_annotations=False,
                    manifest_title="title",
                    data_type="syn3",
                    display_label_type="class_label",
                    use_strict_validation=True,
                    asset_view_id="syn1",
                )
                assert status == 404
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
