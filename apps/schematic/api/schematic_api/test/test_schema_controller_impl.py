"""Tests for schema endpoint functions"""

from unittest.mock import patch

from schematic_api.models.basic_error import BasicError
from schematic_api.models.node_properties_page import NodePropertiesPage
from schematic_api.models.validation_rules_page import ValidationRulesPage
from schematic_api.models.nodes_page import NodesPage
from schematic_api.models.connected_nodes_page import ConnectedNodesPage
import schematic_api.controllers.schema_controller_impl
from schematic_api.controllers.schema_controller_impl import (
    get_component,
    get_node_is_required,
    get_property_label,
    get_schema_attributes,
    get_node_properties,
    get_connected_nodes,
    list_node_validation_rules,
    list_node_dependencies,
)


class TestGetComponent:
    """Tests get_component"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        result, status = get_component(
            component_label="Patient", schema_url=test_schema_url
        )
        assert status == 200
        assert isinstance(result, str)

    def test_internal_error(self, test_schema_url: str) -> None:
        """Test for 500 result"""
        result, status = get_component(
            component_label="not_a_component", schema_url=test_schema_url
        )
        assert status == 500
        assert isinstance(result, BasicError)


class TestGetConnectedNodes:
    """Tests forget_connected_nodes"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_connected_nodes_from_schematic",
            return_value=[["node1", "node2"], ["node2", "node3"]],
        ):
            result, status = get_connected_nodes(
                schema_url="xxx",
                relationship_type="type",
            )
            assert status == 200
            assert isinstance(result, ConnectedNodesPage)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_connected_nodes_from_schematic",
            side_effect=TypeError,
        ):
            result, status = get_connected_nodes(
                schema_url="xxx",
                relationship_type="type",
            )
            assert status == 500
            assert isinstance(result, BasicError)


class TestGetNodeIsRequired:
    """Test case for get_node_is_required"""

    def test_success(self) -> None:
        """Test for successful result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_is_required_from_schematic",
            return_value=True,
        ):
            result, status = get_node_is_required(
                node_display="name",
                schema_url="xxx",
            )
            assert status == 200
            assert result

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_is_required_from_schematic",
            side_effect=TypeError,
        ):
            result, status = get_node_is_required(
                node_display="name",
                schema_url="xxx",
            )
            assert status == 500
            assert isinstance(result, BasicError)


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


class TestGetSchemaAttributes:
    """Tests geget_schema_attributes"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        result, status = get_schema_attributes(schema_url=test_schema_url)
        assert status == 200
        assert isinstance(result, str)

    def test_internal_error(
        self,
    ) -> None:
        """Test for 500 result"""
        result, status = get_schema_attributes(schema_url="not_a_url")
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
