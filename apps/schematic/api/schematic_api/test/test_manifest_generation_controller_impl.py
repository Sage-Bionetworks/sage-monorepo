"""Tests for manifest generation endpoint functions"""
# pylint: disable=no-member

from unittest.mock import patch
from typing import BinaryIO

from schematic_api.models.basic_error import BasicError
from schematic_api.models.google_sheet_links import GoogleSheetLinks
from schematic_api.controllers.manifest_generation_controller_impl import (
    generate_excel_manifest,
    generate_google_sheet_manifests,
)
from .conftest import (
    GET_ACCESS_TOKEN_MOCK,
    CREATE_MANIFESTS_MOCK,
    CREATE_SINGLE_MANIFEST_MOCK,
)


class GenerateExcelManifests:
    """Tests generate_excel_manifest"""

    def success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        with patch(GET_ACCESS_TOKEN_MOCK):
            with patch(CREATE_SINGLE_MANIFEST_MOCK, return_value=BinaryIO()):  # type: ignore
                result, status = generate_excel_manifest(
                    schema_url=test_schema_url,
                    dataset_id="syn2",
                    asset_view_id="syn1",
                    node_label="syn3",
                    add_annotations=False,
                    manifest_title="title",
                )
                assert status == 200
                assert isinstance(result, BinaryIO)

    def error_statuses(self) -> None:
        """Test for error statuses"""
        with patch(GET_ACCESS_TOKEN_MOCK):
            with patch(CREATE_SINGLE_MANIFEST_MOCK):
                result, status = generate_excel_manifest(
                    schema_url="not_a_url",
                    dataset_id="syn2",
                    asset_view_id="syn1",
                    node_label="syn3",
                    add_annotations=False,
                    manifest_title="title",
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
                    node_label_array=["syn4", "syn5"],
                    add_annotations=False,
                    manifest_title="title",
                    use_strict_validation=True,
                    generate_all_manifests=False,
                )
                assert status == 200
                assert isinstance(result, GoogleSheetLinks)
                assert result.links == ["link1", "link2"]

    def test_error_statuses(self, test_schema_url: str) -> None:
        """Test for error statuses"""
        with patch(GET_ACCESS_TOKEN_MOCK):
            with patch(CREATE_MANIFESTS_MOCK):
                result, status = generate_google_sheet_manifests(
                    schema_url="not_a_url",
                    dataset_id_array=["syn2", "syn3"],
                    asset_view_id="syn1",
                    node_label_array=["syn4", "syn5"],
                    add_annotations=False,
                    manifest_title="title",
                    use_strict_validation=True,
                    generate_all_manifests=False,
                )
                assert status == 404
                assert isinstance(result, BasicError)

                result, status = generate_google_sheet_manifests(
                    schema_url=test_schema_url,
                    dataset_id_array=["syn2", "syn3"],
                    asset_view_id="syn1",
                    node_label_array=["syn4", "syn5"],
                    add_annotations=False,
                    manifest_title="title",
                    use_strict_validation=True,
                    generate_all_manifests=True,
                )
                assert status == 422
                assert isinstance(result, BasicError)
                assert result.detail == (
                    "When generate_all_manifests is True dataset_id_array must be None: "
                    "{'dataset_id_array': ['syn2', 'syn3']}"
                )

                result, status = generate_google_sheet_manifests(
                    schema_url=test_schema_url,
                    dataset_id_array=None,
                    asset_view_id="syn1",
                    node_label_array=["syn4", "syn5"],
                    add_annotations=False,
                    manifest_title="title",
                    use_strict_validation=True,
                    generate_all_manifests=True,
                )
                assert status == 422
                assert isinstance(result, BasicError)
                assert result.detail == (
                    "When generate_all_manifests is True node_label_array must be None: "
                    "{'node_label_array': ['syn4', 'syn5']}"
                )

                result, status = generate_google_sheet_manifests(
                    schema_url=test_schema_url,
                    dataset_id_array=None,
                    asset_view_id="syn1",
                    node_label_array=None,
                    add_annotations=False,
                    manifest_title="title",
                    use_strict_validation=True,
                    generate_all_manifests=False,
                )
                assert status == 422
                assert isinstance(result, BasicError)
                assert result.detail == (
                    "When generate_all_manifests is False node_label_array must be a list with "
                    "atleast one item: {'node_label_array': None}"
                )

                result, status = generate_google_sheet_manifests(
                    schema_url=test_schema_url,
                    dataset_id_array=None,
                    asset_view_id="syn1",
                    node_label_array=[],
                    add_annotations=False,
                    manifest_title="title",
                    use_strict_validation=True,
                    generate_all_manifests=False,
                )
                assert status == 422
                assert isinstance(result, BasicError)
                assert result.detail == (
                    "When generate_all_manifests is False node_label_array must be a list with "
                    "atleast one item: {'node_label_array': []}"
                )

                result, status = generate_google_sheet_manifests(
                    schema_url=test_schema_url,
                    dataset_id_array=["syn4"],
                    asset_view_id="syn1",
                    node_label_array=["syn2", "syn3"],
                    add_annotations=False,
                    manifest_title="title",
                    use_strict_validation=True,
                    generate_all_manifests=False,
                )
                assert status == 422
                assert isinstance(result, BasicError)
                assert result.detail == (
                    "When generate_all_manifests is False node_label_array and dataset_id_array "
                    "must both lists with the same length: "
                    "{'node_label_array': ['syn2', 'syn3'], 'dataset_id_array': ['syn4']}"
                )
