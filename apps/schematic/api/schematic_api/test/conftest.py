"""Globals and pytest fixtures for testing"""
from typing import Generator
import pytest

EXAMPLE_MANIFEST_METADATA = [
    (("dataset_id1", "dataset_name1"), ("syn1", "name1"), ("component1", "component1")),
    (("dataset_id2", "dataset_name2"), ("syn2", "name2"), ("component2", "component2")),
]


@pytest.fixture(scope="session", name="example_manifest_metadata")
def fixture_example_manifest_metadata() -> Generator:
    """
    Yields an example of a list of manifest metadata
    """
    yield EXAMPLE_MANIFEST_METADATA
