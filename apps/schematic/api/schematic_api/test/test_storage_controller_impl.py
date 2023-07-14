"""Tests for endpoint functions"""

import os
from typing import Generator, Any
from unittest.mock import patch
from yaml import safe_load
import pytest

from schematic_api.models.basic_error import BasicError
from schematic_api.models.manifests_page import ManifestsPage
import schematic_api.controllers.storage_controller_impl
from schematic_api.controllers.storage_controller_impl import (
    list_storage_project_manifests,
)


TESTS_DIR = os.path.dirname(os.path.abspath(__file__))
DATA_DIR = os.path.join(TESTS_DIR, "data")
SECRETS_PATH = os.path.join(DATA_DIR, "secrets.yaml")


@pytest.fixture(scope="session", name="secrets_dict")
def fixture_secrets_dict() -> Generator:
    """
    Yields a dict with various secrets
    """
    with open(SECRETS_PATH, mode="rt", encoding="utf-8") as file:
        config = safe_load(file)
    yield config


@pytest.fixture(scope="session", name="synapse_auth_token")
def fixture_synapse_auth_token(secrets_dict: dict[str, Any]) -> Generator:
    """
    Yields a Synapse authorization token
    """
    yield secrets_dict["synapse_auth_token"]


@pytest.fixture(scope="session", name="synapse_project_id")
def fixture_synapse_project_id() -> Generator:
    """
    Yields a Synapse project ID
    """
    yield "syn30988314"


@pytest.fixture(scope="session", name="synapse_asset_view_id")
def fixture_synapse_asset_view_id() -> Generator:
    """
    Yields a Synapse asset view ID
    """
    yield "syn23643253"


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
            assert result.title == "Missing or invalid Synapse credentials error"

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
                project_id=synapse_asset_view_id, asset_view=synapse_project_id
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
                project_id=synapse_project_id, asset_view=synapse_asset_view_id
            )
            assert status == 200
            assert isinstance(result, ManifestsPage)
