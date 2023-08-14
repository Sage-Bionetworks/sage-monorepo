"""Tests for schema endpoint functions"""

from unittest.mock import patch

from schematic_api.models.basic_error import BasicError
from schematic_api.models.attributes_page import AttributesPage
from schematic_api.models.validation_rules_page import ValidationRulesPage
import schematic_api.controllers.schema_controller_impl
from schematic_api.controllers.schema_controller_impl import (
    list_component_attributes,
    list_component_validation_rules,
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
                component_label="label",
                schema_url="xxx",
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
                component_label="label",
                schema_url="xxx",
            )
            assert status == 500
            assert isinstance(result, BasicError)


class TestComponentValidationRules:
    """Test case for list_component_validation_rules"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_component_validation_rules",
            return_value=["rule1", "rule2"],
        ):
            result, status = list_component_validation_rules(
                component_display="name",
                schema_url="xxx",
            )
            assert status == 200
            assert isinstance(result, ValidationRulesPage)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_component_validation_rules",
            side_effect=TypeError,
        ):
            result, status = list_component_validation_rules(
                component_display="name",
                schema_url="xxx",
            )
            assert status == 500
            assert isinstance(result, BasicError)
