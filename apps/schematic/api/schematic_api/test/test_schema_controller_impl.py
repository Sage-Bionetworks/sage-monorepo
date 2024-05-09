"""Tests for schema endpoint functions"""
# pylint: disable=duplicate-code


from schematic_api.models.basic_error import BasicError
from schematic_api.models.component_requirement_subgraph import (
    ComponentRequirementSubgraph,
)
from schematic_api.models.node_property_array import NodePropertyArray
from schematic_api.models.validation_rule import ValidationRule
from schematic_api.models.validation_rule_array import ValidationRuleArray
from schematic_api.models.node import Node
from schematic_api.models.node_array import NodeArray
from schematic_api.models.node_page import NodePage
from schematic_api.models.connected_node_pair_page import ConnectedNodePairPage
from schematic_api.models.connected_node_pair_array import ConnectedNodePairArray
from schematic_api.models.connected_node_pair import ConnectedNodePair
from schematic_api.controllers.schema_controller_impl import (
    get_component,
    get_component_requirements_array,
    get_component_requirements_graph,
    get_connected_node_pair_page,
    get_connected_node_pair_array,
    get_node_is_required,
    get_property_label,
    get_schema_attributes,
    get_node_properties,
    get_node_validation_rules,
    get_node_dependency_array,
    get_node_dependency_page,
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


class TestGetComponentRequirementsArray:
    """Tests get_component_requirements_array"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        result, status = get_component_requirements_array(
            component_label="Biospecimen",
            schema_url=test_schema_url,
            display_label_type="class_label",
        )
        assert status == 200
        assert isinstance(result, list)
        for req in result:
            assert isinstance(req, str)

    def test_internal_error(self, test_schema_url: str) -> None:
        """Test for 500 result"""
        result, status = get_component_requirements_array(
            component_label="not_a_component",
            schema_url=test_schema_url,
            display_label_type="class_label",
        )
        assert status == 500
        assert isinstance(result, BasicError)


class TestGetComponentRequirementsGraph:
    """Tests get_component_requirements_graph"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        result, status = get_component_requirements_graph(
            component_label="Biospecimen",
            schema_url=test_schema_url,
            display_label_type="class_label",
        )
        assert status == 200
        assert isinstance(result, list)
        for subgraph in result:
            assert isinstance(subgraph, ComponentRequirementSubgraph)
            assert isinstance(subgraph.component1, str)
            assert isinstance(subgraph.component2, str)

    def test_internal_error(self, test_schema_url: str) -> None:
        """Test for 500 result"""
        result, status = get_component_requirements_graph(
            component_label="not_a_component",
            schema_url=test_schema_url,
            display_label_type="class_label",
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


def test_get_property_label() -> None:
    """Test for get_property_label"""
    result, status = get_property_label(
        node_display="display_name",
        use_strict_camel_case=True,
    )
    assert status == 200
    assert result == "displayName"


class TestGetSchemaAttributes:
    """Tests get_schema_attributes"""

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
            node_label="Patient",
            schema_url=test_schema_url,
        )
        assert status == 200
        assert isinstance(result, NodePropertyArray)
        assert isinstance(result.node_properties, list)
        for item in result.node_properties:
            assert isinstance(item, str)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        result, status = get_node_properties(
            node_label="Patient",
            schema_url="not_a_url",
        )
        assert status == 500
        assert isinstance(result, BasicError)


class TestGetNodeValidationRuleArray:
    """Test case for get_node_validation_rules"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        result, status = get_node_validation_rules(
            node_display="CheckRegexList",
            schema_url=test_schema_url,
        )
        assert status == 200
        assert isinstance(result, ValidationRuleArray)
        assert isinstance(result.validation_rules, list)
        for item in result.validation_rules:
            assert isinstance(item, ValidationRule)
            assert isinstance(item.name, str)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        result, status = get_node_validation_rules(
            node_display="CheckRegexList",
            schema_url="not_a_url",
        )
        assert status == 500
        assert isinstance(result, BasicError)


class TestGetNodeDependencyArray:
    """Test case for get_node_dependency_array"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        result, status = get_node_dependency_array(
            schema_url=test_schema_url,
            node_label="Patient",
        )
        assert status == 200
        assert isinstance(result, NodeArray)
        assert isinstance(result.nodes, list)
        for item in result.nodes:
            assert isinstance(item, Node)
            assert isinstance(item.name, str)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        result, status = get_node_dependency_page(
            schema_url="not_a_url",
            node_label="Patient",
        )
        assert status == 500
        assert isinstance(result, BasicError)


class TestGetNodeDependencyPage:
    """Test case for get_node_dependency_page"""

    def test_success(self, test_schema_url: str) -> None:
        """Test for successful result"""
        result, status = get_node_dependency_page(
            schema_url=test_schema_url,
            node_label="Patient",
        )
        assert status == 200
        assert isinstance(result, NodePage)
        assert result.number == 1
        assert result.size == 100000
        assert isinstance(result.total_elements, int)
        assert isinstance(result.total_pages, int)
        assert isinstance(result.has_next, bool)
        assert isinstance(result.has_previous, bool)
        assert isinstance(result.nodes, list)
        for item in result.nodes:
            assert isinstance(item, Node)
            assert isinstance(item.name, str)

    def test_internal_error(self) -> None:
        """Test for 500 result"""
        result, status = get_node_dependency_page(
            schema_url="not_a_url",
            node_label="Patient",
        )
        assert status == 500
        assert isinstance(result, BasicError)
