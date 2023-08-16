"""Tests for schema endpoint functions"""

from unittest.mock import patch

from schematic_api.models.basic_error import BasicError
from schematic_api.models.attributes_page import AttributesPage
from schematic_api.models.validation_rules_page import ValidationRulesPage
from schematic_api.models.nodes_page import NodesPage
import schematic_api.controllers.schema_controller_impl
from schematic_api.controllers.schema_controller_impl import (
    node_is_required,
    get_node_label,
    list_node_attributes,
    list_node_validation_rules,
    list_node_dependencies,
)


class TestNodeIsRequired:
    """Test case for node_is_required"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_is_required",
            return_value=True,
        ):
            result, status = node_is_required(
                node_display="name",
                schema_url="xxx",
            )
            assert status == 200
            assert result

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_is_required",
            side_effect=TypeError,
        ):
            result, status = node_is_required(
                node_display="name",
                schema_url="xxx",
            )
            assert status == 500
            assert isinstance(result, BasicError)


class TestNodeLabel:
    """Test case for get_node_label"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_label_from_schematic",
            return_value="label1",
        ):
            result, status = get_node_label(
                node_display="display", schema_url="xxx", use_strict_camel_case=True
            )
            assert status == 200
            assert result == "label1"

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_label_from_schematic",
            side_effect=TypeError,
        ):
            result, status = get_node_label(
                node_display="display", schema_url="xxx", use_strict_camel_case=True
            )
            assert status == 500
            assert isinstance(result, BasicError)


class TestNodeAttributes:
    """Test case for list_node_attributes"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_attributes",
            return_value=["attribute1", "attribute2"],
        ):
            result, status = list_node_attributes(
                node_label="label",
                schema_url="xxx",
            )
            assert status == 200
            assert isinstance(result, AttributesPage)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_attributes",
            side_effect=TypeError,
        ):
            result, status = list_node_attributes(
                node_label="label",
                schema_url="xxx",
            )
            assert status == 500
            assert isinstance(result, BasicError)


class TestNodeValidationRules:
    """Test case for list_node_validation_rules"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_validation_rules",
            return_value=["rule1", "rule2"],
        ):
            result, status = list_node_validation_rules(
                node_display="name",
                schema_url="xxx",
            )
            assert status == 200
            assert isinstance(result, ValidationRulesPage)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_validation_rules",
            side_effect=TypeError,
        ):
            result, status = list_node_validation_rules(
                node_display="name",
                schema_url="xxx",
            )
            assert status == 500
            assert isinstance(result, BasicError)


class TestListNodeDependencies:
    """Test case for list_node_dependencies"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_dependencies",
            return_value=["attribute1", "attribute2"],
        ):
            result, status = list_node_dependencies(
                schema_url="xxx",
                node_label="label",
            )
            assert status == 200
            assert isinstance(result, NodesPage)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_dependencies",
            side_effect=TypeError,
        ):
            result, status = list_node_dependencies(
                schema_url="xxx",
                node_label="label",
            )
            assert status == 500
            assert isinstance(result, BasicError)
