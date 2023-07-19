"""Tests for endpoint functions"""

from unittest.mock import patch

from schematic_api.models.basic_error import BasicError
from schematic_api.models.manifests_page import ManifestsPage
from schematic_api.models.datasets_page import DatasetsPage

import schematic_api.controllers.storage_controller_impl
from schematic_api.controllers.storage_controller_impl import (
    list_storage_project_datasets,
    list_storage_project_manifests,
)


class TestListStorageProjectDatasets:
    """Test case for list_storage_project_datasets"""

    def test_success(
        self,
        synapse_auth_token: str,
        synapse_project_id: str,
        synapse_asset_view_id: str,
    ):
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_access_token",
            return_value=synapse_auth_token,
        ):
            result, status = list_storage_project_datasets(
                project_id=synapse_project_id,
                asset_view_id=synapse_asset_view_id
            )
            assert status == 200
            assert isinstance(result, DatasetsPage)


class TestListStorageProjectManifests:
    """Test case for list_storage_project_manifests"""

    def test_no_access_token(self):
        """Test with no Synapse access token"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_access_token",
            return_value=None,
        ):
            result, status = list_storage_project_manifests("syn1", "syn2")
            assert status == 401
            assert isinstance(result, BasicError)
            assert (
                result.title == "Missing or invalid Synapse credentials error"
            )

    def test_bad_access_token(self):
        """Test with incorrect Synapse access token"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_access_token",
            return_value="xxx",
        ):
            result, status = list_storage_project_manifests("syn1", "syn2")
            assert status == 403
            assert isinstance(result, BasicError)
            assert result.title == "Forbidden Synapse access error"

    def test_no_access_to_entities(self, synapse_auth_token: str):
        """Test with incorrect Synapse ids"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_access_token",
            return_value=synapse_auth_token,
        ):
            result, status = list_storage_project_manifests("syn1", "syn2")
            assert status == 404
            assert isinstance(result, BasicError)
            assert result.title == "Synapse entity access error"

    def test_switched_ids(
        self,
        synapse_auth_token: str,
        synapse_project_id: str,
        synapse_asset_view_id: str,
    ):
        """Test with Synapse ids switched"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_access_token",
            return_value=synapse_auth_token,
        ):
            result, status = list_storage_project_manifests(
                project_id=synapse_asset_view_id,
                asset_view_id=synapse_project_id
            )
            assert status == 404
            assert isinstance(result, BasicError)
            assert result.title == "Synapse entity access error"

    def test_success(
        self,
        synapse_auth_token: str,
        synapse_project_id: str,
        synapse_asset_view_id: str,
    ):
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_access_token",
            return_value=synapse_auth_token,
        ):
            result, status = list_storage_project_manifests(
                project_id=synapse_project_id,
                asset_view_id=synapse_asset_view_id
            )
            assert status == 200
            assert isinstance(result, ManifestsPage)
