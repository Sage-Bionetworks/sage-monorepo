"""Tests for schema endpoint functions"""

from unittest.mock import patch

from schematic_api.models.basic_error import BasicError
from schematic_api.models.node_properties_page import NodePropertiesPage
from schematic_api.models.validation_rules_page import ValidationRulesPage
from schematic_api.models.nodes_page import NodesPage
from schematic_api.models.relationships_page import RelationshipsPage
import schematic_api.controllers.schema_controller_impl
from schematic_api.controllers.schema_controller_impl import (
    get_property_label,
    get_node_properties,
    get_relationships,
    list_node_validation_rules,
    list_node_dependencies,
)


class TestGetPropertyLabel:
    """Test case for get_property_label"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_property_label_from_schematic",
            return_value="label1",
        ):
            result, status = get_property_label(
                node_display="display", schema_url="xxx", use_strict_camel_case=True
            )
            assert status == 200
            assert result == "label1"

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_property_label_from_schematic",
            side_effect=TypeError,
        ):
            result, status = get_property_label(
                node_display="display", schema_url="xxx", use_strict_camel_case=True
            )
            assert status == 500
            assert isinstance(result, BasicError)


class TestGetNodeProperties:
    """Test case for get_node_properties"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_properties_from_schematic",
            return_value=["property1", "property2"],
        ):
            result, status = get_node_properties(
                node_label="label",
                schema_url="xxx",
            )
            assert status == 200
            assert isinstance(result, NodePropertiesPage)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_properties_from_schematic",
            side_effect=TypeError,
        ):
            result, status = get_node_properties(
                node_label="label",
                schema_url="xxx",
            )
            assert status == 500
            assert isinstance(result, BasicError)


class TestGetRelationships:
    """Tests for get_relationships"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_relationships_from_schematic",
            return_value=[["component1", "component2"], ["component2", "component3"]],
        ):
            result, status = get_relationships(
                schema_url="xxx",
                relationship_type="type",
            )
            assert status == 200
            assert isinstance(result, RelationshipsPage)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_relationships_from_schematic",
            side_effect=TypeError,
        ):
            result, status = get_relationships(
                schema_url="xxx",
                relationship_type="type",
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
