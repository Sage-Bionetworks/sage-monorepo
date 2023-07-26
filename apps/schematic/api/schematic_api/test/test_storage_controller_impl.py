"""Tests for endpoint functions"""

from unittest.mock import patch

from synapseclient.core.exceptions import (  # type: ignore
    SynapseNoCredentialsError,
    SynapseAuthenticationError,
)
from schematic.exceptions import AccessCredentialsError  # type: ignore

from schematic_api.models.basic_error import BasicError
from schematic_api.models.manifests_page import ManifestsPage
from schematic_api.models.datasets_page import DatasetsPage
from schematic_api.models.files_page import FilesPage
import schematic_api.controllers.storage_controller_impl
from schematic_api.controllers.storage_controller_impl import (
    list_dataset_files,
    list_storage_project_datasets,
    list_storage_project_manifests,
)


class TestListDatasetFiles:
    """Test case for list_dataset_files"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_files",
            return_value=[("id1", "name1"), ("id2", "name2")],
        ):
            result, status = list_dataset_files(
                dataset_id="id1", asset_view_id="id2", asset_type="synapse"
            )
            assert status == 200
            assert isinstance(result, FilesPage)

    def test_no_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_files",
            side_effect=SynapseNoCredentialsError,
        ):
            result, status = list_dataset_files(
                dataset_id="id1", asset_view_id="id2", asset_type="synapse"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_bad_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_files",
            side_effect=SynapseAuthenticationError,
        ):
            result, status = list_dataset_files(
                dataset_id="id1", asset_view_id="id2", asset_type="synapse"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_no_access_error(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_files",
            side_effect=AccessCredentialsError("project"),
        ):
            result, status = list_dataset_files(
                dataset_id="id1", asset_view_id="id2", asset_type="synapse"
            )
            assert status == 403
            assert isinstance(result, BasicError)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_datasets",
            side_effect=TypeError,
        ):
            result, status = list_dataset_files(
                dataset_id="id1", asset_view_id="id2", asset_type="synapse"
            )
            assert status == 500
            assert isinstance(result, BasicError)


class TestListStorageProjectDatasets:
    """Test case for list_storage_project_datasets"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_datasets",
            return_value=[("id1", "name1"), ("id2", "name2")],
        ):
            result, status = list_storage_project_datasets(
                project_id="id1", asset_view_id="id2", asset_type="synapse"
            )
            assert status == 200
            assert isinstance(result, DatasetsPage)

    def test_no_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_datasets",
            side_effect=SynapseNoCredentialsError,
        ):
            result, status = list_storage_project_datasets(
                project_id="id1", asset_view_id="id2", asset_type="synapse"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_bad_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_datasets",
            side_effect=SynapseAuthenticationError,
        ):
            result, status = list_storage_project_datasets(
                project_id="id1", asset_view_id="id2", asset_type="synapse"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_no_access_error(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_datasets",
            side_effect=AccessCredentialsError("project"),
        ):
            result, status = list_storage_project_datasets(
                project_id="id1", asset_view_id="id2", asset_type="synapse"
            )
            assert status == 403
            assert isinstance(result, BasicError)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_datasets",
            side_effect=TypeError,
        ):
            result, status = list_storage_project_datasets(
                project_id="id1", asset_view_id="id2", asset_type="synapse"
            )
            assert status == 500
            assert isinstance(result, BasicError)


class TestListStorageProjectManifests:
    """Test case for list_storage_project_manifests"""

    def test_success(self, example_manifest_metadata: list) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifests",
            return_value=example_manifest_metadata,
        ):
            result, status = list_storage_project_manifests(
                project_id="id1", asset_view_id="id2", asset_type="synapse"
            )
            assert status == 200
            assert isinstance(result, ManifestsPage)

    def test_no_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifests",
            side_effect=SynapseNoCredentialsError,
        ):
            result, status = list_storage_project_manifests(
                project_id="id1", asset_view_id="id2", asset_type="synapse"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_bad_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifests",
            side_effect=SynapseAuthenticationError,
        ):
            result, status = list_storage_project_manifests(
                project_id="id1", asset_view_id="id2", asset_type="synapse"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_no_access_error(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifests",
            side_effect=AccessCredentialsError("project"),
        ):
            result, status = list_storage_project_manifests(
                project_id="id1", asset_view_id="id2", asset_type="synapse"
            )
            assert status == 403
            assert isinstance(result, BasicError)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifests",
            side_effect=TypeError,
        ):
            result, status = list_storage_project_manifests(
                project_id="id1", asset_view_id="id2", asset_type="synapse"
            )
            assert status == 500
            assert isinstance(result, BasicError)
