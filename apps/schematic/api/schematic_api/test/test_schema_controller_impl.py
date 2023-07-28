"""Tests for schema endpoint functions"""

from unittest.mock import patch

from schematic_api.models.basic_error import BasicError
from schematic_api.models.attributes_page import AttributesPage
import schematic_api.controllers.schema_controller_impl
from schematic_api.controllers.schema_controller_impl import (
    list_component_attributes,
)


class TestComponentAttributes:
    """Test case for list_component_attributes"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_component_attributes",
            return_value=["attribute1", "attribute2"],
        ):
            result, status = list_component_attributes(
                schema_url="xxx", component_label="label"
            )
            assert status == 200
            assert isinstance(result, AttributesPage)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_component_attributes",
            side_effect=TypeError,
        ):
            result, status = list_component_attributes(
                schema_url="xxx", component_label="label"
            )
            assert status == 500
            assert isinstance(result, BasicError)
