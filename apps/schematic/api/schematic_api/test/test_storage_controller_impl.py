"""Tests for storage endpoint functions"""
# pylint: disable=duplicate-code

from unittest.mock import patch

import pandas as pd
from synapseclient.core.exceptions import (  # type: ignore
    SynapseNoCredentialsError,
    SynapseAuthenticationError,
)
from schematic.exceptions import AccessCredentialsError  # type: ignore

from schematic_api.models.basic_error import BasicError
from schematic_api.models.manifests_page import ManifestsPage
from schematic_api.models.dataset_metadata import DatasetMetadata
from schematic_api.models.dataset_metadata_array import DatasetMetadataArray
from schematic_api.models.dataset_metadata_page import DatasetMetadataPage
from schematic_api.models.projects_page import ProjectsPage
from schematic_api.models.file_metadata import FileMetadata
from schematic_api.models.file_metadata_page import FileMetadataPage
from schematic_api.models.file_metadata_array import FileMetadataArray
import schematic_api.controllers.storage_controller_impl
from schematic_api.controllers.storage_controller_impl import (
    get_dataset_manifest_json,
    get_manifest_json,
    get_asset_view_json,
    get_dataset_file_metadata_array,
    get_dataset_file_metadata_page,
    get_projects,
    get_project_dataset_metadata_array,
    get_project_dataset_metadata_page,
    get_project_manifests,
)


class TestGetAssetViewJson:
    """Test case for get_asset_view_json"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_asset_view_from_schematic",
            return_value=pd.DataFrame({"col1": [1, 2], "col2": [3, 4]}),
        ):
            result, status = get_asset_view_json(
                asset_type="synapse", asset_view_id="syn1"
            )
            assert status == 200
            assert result == '{"col1":{"0":1,"1":2},"col2":{"0":3,"1":4}}'

    def test_no_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_asset_view_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            result, status = get_asset_view_json(
                asset_type="synapse", asset_view_id="syn1"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_bad_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_asset_view_from_schematic",
            side_effect=SynapseAuthenticationError,
        ):
            result, status = get_asset_view_json(
                asset_type="synapse", asset_view_id="syn1"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_no_access_error(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_asset_view_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            result, status = get_asset_view_json(
                asset_type="synapse", asset_view_id="syn1"
            )
            assert status == 403
            assert isinstance(result, BasicError)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_asset_view_from_schematic",
            side_effect=TypeError,
        ):
            result, status = get_asset_view_json(
                asset_type="synapse", asset_view_id="syn1"
            )
            assert status == 500
            assert isinstance(result, BasicError)


class TestGetDatasetFileMetadataArray:
    """Test case for get_dataset_file_metadata_array"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            return_value=[FileMetadata("syn1", "name1"), FileMetadata("syn2", "name2")],
        ):
            result, status = get_dataset_file_metadata_array(
                dataset_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 200
            assert isinstance(result, FileMetadataArray)
            assert isinstance(result.files, list)
            for item in result.files:
                assert isinstance(item, FileMetadata)
                assert isinstance(item.id, str)
                assert isinstance(item.name, str)

    def test_no_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            result, status = get_dataset_file_metadata_array(
                dataset_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_bad_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            side_effect=SynapseAuthenticationError,
        ):
            result, status = get_dataset_file_metadata_array(
                dataset_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_no_access_error(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            result, status = get_dataset_file_metadata_array(
                dataset_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 403
            assert isinstance(result, BasicError)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            side_effect=TypeError,
        ):
            result, status = get_dataset_file_metadata_array(
                dataset_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 500
            assert isinstance(result, BasicError)


class TestGetDatasetFileMetadataPage:
    """Test case for get_dataset_file_metadata_page"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            return_value=[FileMetadata("syn1", "name1"), FileMetadata("syn2", "name2")],
        ):
            result, status = get_dataset_file_metadata_page(
                dataset_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 200
            assert isinstance(result, FileMetadataPage)
            assert result.number == 1
            assert result.size == 100000
            assert isinstance(result.total_elements, int)
            assert isinstance(result.total_pages, int)
            assert isinstance(result.has_next, bool)
            assert isinstance(result.has_previous, bool)
            assert isinstance(result.files, list)
            for item in result.files:
                assert isinstance(item, FileMetadata)
                assert isinstance(item.id, str)
                assert isinstance(item.name, str)

    def test_no_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            result, status = get_dataset_file_metadata_page(
                dataset_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_bad_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            side_effect=SynapseAuthenticationError,
        ):
            result, status = get_dataset_file_metadata_page(
                dataset_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_no_access_error(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            result, status = get_dataset_file_metadata_page(
                dataset_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 403
            assert isinstance(result, BasicError)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_file_metadata_from_schematic",
            side_effect=TypeError,
        ):
            result, status = get_dataset_file_metadata_page(
                dataset_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 500
            assert isinstance(result, BasicError)


class TestGetDatasetManifestJson:
    """Test case for get_dataset_manifest_json"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_manifest_from_schematic",
            return_value=pd.DataFrame({"col1": [1, 2], "col2": [3, 4]}),
        ):
            result, status = get_dataset_manifest_json(
                asset_type="synapse", dataset_id="syn1", asset_view_id="syn2"
            )
            assert status == 200
            assert result == '{"col1":{"0":1,"1":2},"col2":{"0":3,"1":4}}'

    def test_no_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_manifest_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            result, status = get_dataset_manifest_json(
                asset_type="synapse", dataset_id="syn1", asset_view_id="syn2"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_bad_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_manifest_from_schematic",
            side_effect=SynapseAuthenticationError,
        ):
            result, status = get_dataset_manifest_json(
                asset_type="synapse", dataset_id="syn1", asset_view_id="syn2"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_no_access_error(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_manifest_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            result, status = get_dataset_manifest_json(
                asset_type="synapse", dataset_id="syn1", asset_view_id="syn2"
            )
            assert status == 403
            assert isinstance(result, BasicError)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_dataset_manifest_from_schematic",
            side_effect=TypeError,
        ):
            result, status = get_dataset_manifest_json(
                asset_type="synapse", dataset_id="syn1", asset_view_id="syn2"
            )
            assert status == 500
            assert isinstance(result, BasicError)


class TestGetManifestJson:
    """Test case for get_manifest_json"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_manifest_from_schematic",
            return_value=pd.DataFrame({"col1": [1, 2], "col2": [3, 4]}),
        ):
            result, status = get_manifest_json(asset_type="synapse", manifest_id="syn1")
            assert status == 200
            assert result == '{"col1":{"0":1,"1":2},"col2":{"0":3,"1":4}}'

    def test_no_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_manifest_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            result, status = get_manifest_json(asset_type="synapse", manifest_id="syn1")
            assert status == 401
            assert isinstance(result, BasicError)

    def test_bad_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_manifest_from_schematic",
            side_effect=SynapseAuthenticationError,
        ):
            result, status = get_manifest_json(asset_type="synapse", manifest_id="syn1")
            assert status == 401
            assert isinstance(result, BasicError)

    def test_no_access_error(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_manifest_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            result, status = get_manifest_json(asset_type="synapse", manifest_id="syn1")
            assert status == 403
            assert isinstance(result, BasicError)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_manifest_from_schematic",
            side_effect=TypeError,
        ):
            result, status = get_manifest_json(asset_type="synapse", manifest_id="syn1")
            assert status == 500
            assert isinstance(result, BasicError)


class TestGetProjects:
    """Test case for list_projects"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_projects_from_schematic",
            return_value=[("syn1", "name1"), ("syn2", "name2")],
        ):
            result, status = get_projects(asset_view_id="syn1", asset_type="synapse")
            assert status == 200
            assert isinstance(result, ProjectsPage)

    def test_no_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_projects_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            result, status = get_projects(asset_view_id="syn1", asset_type="synapse")
            assert status == 401
            assert isinstance(result, BasicError)

    def test_bad_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_projects_from_schematic",
            side_effect=SynapseAuthenticationError,
        ):
            result, status = get_projects(asset_view_id="syn1", asset_type="synapse")
            assert status == 401
            assert isinstance(result, BasicError)

    def test_no_access_error(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_projects_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            result, status = get_projects(asset_view_id="syn1", asset_type="synapse")
            assert status == 403
            assert isinstance(result, BasicError)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_projects_from_schematic",
            side_effect=TypeError,
        ):
            result, status = get_projects(asset_view_id="syn1", asset_type="synapse")
            assert status == 500
            assert isinstance(result, BasicError)


class TestGetProjectDatasetMetadataArray:
    """Test case for get_project_dataset_metadata_array"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            return_value=[
                DatasetMetadata("syn1", "name1"),
                DatasetMetadata("syn2", "name2"),
            ],
        ):
            result, status = get_project_dataset_metadata_array(
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 200
            assert isinstance(result, DatasetMetadataArray)
            assert isinstance(result.datasets, list)
            for item in result.datasets:
                assert isinstance(item, DatasetMetadata)
                assert isinstance(item.id, str)
                assert isinstance(item.name, str)

    def test_no_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            result, status = get_project_dataset_metadata_array(
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_bad_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            side_effect=SynapseAuthenticationError,
        ):
            result, status = get_project_dataset_metadata_array(
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_no_access_error(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            result, status = get_project_dataset_metadata_array(
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 403
            assert isinstance(result, BasicError)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            side_effect=TypeError,
        ):
            result, status = get_project_dataset_metadata_array(
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 500
            assert isinstance(result, BasicError)


class TestGetProjectDatasetMetadataPage:
    """Test case for get_project_dataset_metadata_page"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            return_value=[
                DatasetMetadata("syn1", "name1"),
                DatasetMetadata("syn2", "name2"),
            ],
        ):
            result, status = get_project_dataset_metadata_page(
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 200
            assert isinstance(result, DatasetMetadataPage)
            assert result.number == 1
            assert result.size == 100000
            assert isinstance(result.total_elements, int)
            assert isinstance(result.total_pages, int)
            assert isinstance(result.has_next, bool)
            assert isinstance(result.has_previous, bool)
            assert isinstance(result.datasets, list)
            for item in result.datasets:
                assert isinstance(item, DatasetMetadata)
                assert isinstance(item.id, str)
                assert isinstance(item.name, str)

    def test_no_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            result, status = get_project_dataset_metadata_page(
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_bad_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            side_effect=SynapseAuthenticationError,
        ):
            result, status = get_project_dataset_metadata_page(
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_no_access_error(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            result, status = get_project_dataset_metadata_page(
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 403
            assert isinstance(result, BasicError)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_dataset_metadata_from_schematic",
            side_effect=TypeError,
        ):
            result, status = get_project_dataset_metadata_page(
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 500
            assert isinstance(result, BasicError)


class TestGetProjectManifests:
    """Test case for list_storage_project_manifests"""

    def test_success(self, example_manifest_metadata: list) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifests_from_schematic",
            return_value=example_manifest_metadata,
        ):
            result, status = get_project_manifests(
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 200
            assert isinstance(result, ManifestsPage)

    def test_no_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifests_from_schematic",
            side_effect=SynapseNoCredentialsError,
        ):
            result, status = get_project_manifests(
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_bad_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifests_from_schematic",
            side_effect=SynapseAuthenticationError,
        ):
            result, status = get_project_manifests(
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 401
            assert isinstance(result, BasicError)

    def test_no_access_error(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifests_from_schematic",
            side_effect=AccessCredentialsError("project"),
        ):
            result, status = get_project_manifests(
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 403
            assert isinstance(result, BasicError)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_manifests_from_schematic",
            side_effect=TypeError,
        ):
            result, status = get_project_manifests(
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 500
            assert isinstance(result, BasicError)
