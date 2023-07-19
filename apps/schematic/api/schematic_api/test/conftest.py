"""Pytest fixtures for whole API"""

import os
from typing import Generator, Any
import pytest
from yaml import safe_load

TESTS_DIR = os.path.dirname(os.path.abspath(__file__))
DATA_DIR = os.path.join(TESTS_DIR, "data")
SECRETS_PATH = os.path.join(DATA_DIR, "secrets.yaml")


@pytest.fixture(scope="session", name="secrets_dict")
def fixture_secrets_dict() -> Generator:
    """
    Yields a dict with various secrets
    """
    with open(SECRETS_PATH, mode="rt", encoding="utf-8") as file:
        dct = safe_load(file)
    yield dct


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
