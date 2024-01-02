"""Tests for schema endpoint functions"""

from schematic_api.models.basic_error import BasicError
from schematic_api.models.node_properties_page import NodePropertiesPage
from schematic_api.models.validation_rules_page import ValidationRulesPage
from schematic_api.models.nodes_page import NodesPage
from schematic_api.models.connected_node_pair_page import ConnectedNodePairPage
from schematic_api.models.connected_node_pair_array import ConnectedNodePairArray
from schematic_api.models.connected_node_pair import ConnectedNodePair
from schematic_api.controllers.schema_controller_impl import (
    get_component,
    get_connected_node_pair_page,
    get_connected_node_pair_array,
    get_node_is_required,
    get_property_label,
    get_schema_attributes,
    get_node_properties,
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


class TestGetConnectedNodePairArray:
    """Tests get_connected_node_pair_array"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        result, status = get_connected_node_pair_array(
            schema_url=test_schema_url,
            relationship_type="requiresDependency",
        )
        assert status == 200
        assert isinstance(result, ConnectedNodePairArray)
        assert isinstance(result.connected_nodes, list)
        for item in result.connected_nodes:
            assert isinstance(item, ConnectedNodePair)
            assert isinstance(item.node1, str)
            assert isinstance(item.node2, str)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        result, status = get_connected_node_pair_array(
            schema_url="not_a_url",
            relationship_type="requiresDependency",
        )
        assert status == 500
        assert isinstance(result, BasicError)


class TestGetConnectedNodePairPage:
    """Tests get_connected_node_pair_page"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        result, status = get_connected_node_pair_page(
            schema_url=test_schema_url,
            relationship_type="requiresDependency",
        )
        assert status == 200
        assert isinstance(result, ConnectedNodePairPage)
        assert result.number == 1
        assert result.size == 100000
        assert isinstance(result.total_elements, int)
        assert isinstance(result.total_pages, int)
        assert isinstance(result.has_next, bool)
        assert isinstance(result.has_previous, bool)
        assert isinstance(result.connected_nodes, list)
        for item in result.connected_nodes:
            assert isinstance(item, ConnectedNodePair)
            assert isinstance(item.node1, str)
            assert isinstance(item.node2, str)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        result, status = get_connected_node_pair_page(
            schema_url="not_a_url",
            relationship_type="requiresDependency",
        )
        assert status == 500
        assert isinstance(result, BasicError)


class TestGetNodeIsRequired:
    """Test case for get_node_is_required"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        result, status = get_node_is_required(
            node_display="FamilyHistory",
            schema_url=test_schema_url,
        )
        assert status == 200
        assert result

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        result, status = get_node_is_required(
            node_display="name",
            schema_url="not_a_url",
        )
        assert status == 500
        assert isinstance(result, BasicError)


class TestGetPropertyLabel:
    """Test case for get_property_label"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        result, status = get_property_label(
            node_display="display_name",
            schema_url=test_schema_url,
            use_strict_camel_case=True,
        )
        assert status == 200
        assert result == "displayName"

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        result, status = get_property_label(
            node_display="display", schema_url="not_a_url", use_strict_camel_case=True
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

    def test_404_error(
        self,
    ) -> None:
        """Test for 404 result"""
        result, status = get_schema_attributes(schema_url="not_a_url")
        assert status == 404
        assert isinstance(result, BasicError)


class TestGetNodeProperties:
    """Test case for get_node_properties"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        result, status = get_node_properties(
            node_label="MolecularEntity",
            schema_url=test_schema_url,
        )
        assert status == 200
        assert isinstance(result, NodePropertiesPage)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        result, status = get_node_properties(
            node_label="MolecularEntity",
            schema_url="not_a_url",
        )
        assert status == 500
        assert isinstance(result, BasicError)


class TestNodeValidationRules:
    """Test case for list_node_validation_rules"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        result, status = list_node_validation_rules(
            node_display="CheckRegexList",
            schema_url=test_schema_url,
        )
        assert status == 200
        assert isinstance(result, ValidationRulesPage)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        result, status = list_node_validation_rules(
            node_display="CheckRegexList",
            schema_url="not_a_url",
        )
        assert status == 500
        assert isinstance(result, BasicError)


class TestListNodeDependencies:
    """Test case for list_node_dependencies"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        result, status = list_node_dependencies(
            schema_url=test_schema_url,
            node_label="Patient",
        )
        assert status == 200
        assert isinstance(result, NodesPage)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        result, status = list_node_dependencies(
            schema_url="not_a_url",
            node_label="Patient",
        )
        assert status == 500
        assert isinstance(result, BasicError)
