"""Tests for schema endpoint functions"""

from schematic_api.models.basic_error import BasicError
from schematic_api.controllers.tangled_tree_controller_impl import (
    get_tangled_tree_layers,
    get_tangled_tree_text,
)


class TestGetTangledTreeLayers:
    """Tests get_tangled_tree_layers"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        result, status = get_tangled_tree_layers(
            schema_url=test_schema_url,
            figure_type="component",
        )
        assert status == 200
        assert isinstance(result, str)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        result, status = get_tangled_tree_layers(
            schema_url="not_a_url", figure_type="component"
        )
        assert status == 500
        assert isinstance(result, BasicError)


class TestGetTangledTreeText:
    """Tests get_tangled_tree_text"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        result, status = get_tangled_tree_text(
            schema_url=test_schema_url, figure_type="component", text_format="plain"
        )
        assert status == 200
        assert isinstance(result, str)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        result, status = get_tangled_tree_text(
            schema_url="not_a_url", figure_type="component", text_format="plain"
        )
        assert status == 500
        assert isinstance(result, BasicError)
