"""Tests for endpoint functions"""

from unittest.mock import patch

import pandas as pd
from synapseclient.core.exceptions import (  # type: ignore
    SynapseNoCredentialsError,
    SynapseAuthenticationError,
)
from schematic.exceptions import AccessCredentialsError  # type: ignore

from schematic_api.models.basic_error import BasicError
from schematic_api.models.manifests_page import ManifestsPage
from schematic_api.models.datasets_page import DatasetsPage
from schematic_api.models.projects_page import ProjectsPage
import schematic_api.controllers.storage_controller_impl
from schematic_api.controllers.storage_controller_impl import (
    get_manifest_json,
    list_projects,
    list_storage_project_datasets,
    list_storage_project_manifests,
)


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


class TestListProjects:
    """Test case for list_projects"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_projects",
            return_value=[("syn1", "name1"), ("syn2", "name2")],
        ):
            result, status = list_projects(asset_view_id="syn1", asset_type="synapse")
            assert status == 200
            assert isinstance(result, ProjectsPage)

    def test_no_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_projects",
            side_effect=SynapseNoCredentialsError,
        ):
            result, status = list_projects(asset_view_id="syn1", asset_type="synapse")
            assert status == 401
            assert isinstance(result, BasicError)

    def test_bad_credentials_error(self) -> None:
        """Test for 401 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_projects",
            side_effect=SynapseAuthenticationError,
        ):
            result, status = list_projects(asset_view_id="syn1", asset_type="synapse")
            assert status == 401
            assert isinstance(result, BasicError)

    def test_no_access_error(self) -> None:
        """Test for 403 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_projects",
            side_effect=AccessCredentialsError("project"),
        ):
            result, status = list_projects(asset_view_id="syn1", asset_type="synapse")
            assert status == 403
            assert isinstance(result, BasicError)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_projects",
            side_effect=TypeError,
        ):
            result, status = list_projects(asset_view_id="syn1", asset_type="synapse")
            assert status == 500
            assert isinstance(result, BasicError)


class TestListStorageProjectDatasets:
    """Test case for list_storage_project_datasets"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.storage_controller_impl,
            "get_project_datasets",
            return_value=[("syn1", "name1"), ("syn2", "name2")],
        ):
            result, status = list_storage_project_datasets(
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
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
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
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
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
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
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
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
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
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
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
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
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
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
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
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
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
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
                project_id="syn1", asset_view_id="syn2", asset_type="synapse"
            )
            assert status == 500
            assert isinstance(result, BasicError)
