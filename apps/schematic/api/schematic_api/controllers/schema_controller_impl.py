"""Implementation of all endpoints"""
from typing import Union

from schematic.schemas.generator import SchemaGenerator, SchemaExplorer  # type: ignore
from schematic.visualization.attributes_explorer import AttributesExplorer  # type: ignore

from schematic_api.models.basic_error import BasicError
from schematic_api.models.node_property_array import NodePropertyArray
from schematic_api.models.validation_rule_array import ValidationRuleArray
from schematic_api.models.node import Node
from schematic_api.models.node_array import NodeArray
from schematic_api.models.node_page import NodePage
from schematic_api.models.connected_node_pair_array import ConnectedNodePairArray
from schematic_api.models.connected_node_pair_page import ConnectedNodePairPage
from schematic_api.models.connected_node_pair import ConnectedNodePair
from schematic_api.controllers.utils import (
    handle_exceptions,
    download_schema_file_as_jsonld,
)
from schematic_api.controllers.paging import Page


@handle_exceptions
def get_component(
    component_label: str, schema_url: str, include_index: bool = False
) -> tuple[Union[str, BasicError], int]:
    """
    Get all the attributes associated with a specific data model component formatted as a
    dataframe (stored as a JSON String).

    Args:
        component_label (str): The label of the component
        schema_url (str): The URL of the schema in json form
        include_index (bool): Whether to include the indexes of the dataframe

    Returns:
        tuple[Union[str, BasicError], int]: A tuple
          The first item is either the component or an error object
          The second item is the response status
    """
    schema_path = download_schema_file_as_jsonld(schema_url)
    explorer = AttributesExplorer(schema_path)
    result: Union[str, BasicError] = explorer.parse_component_attributes(
        component=component_label, save_file=False, include_index=include_index
    )
    status = 200
    return result, status


def get_connected_node_pairs_from_schematic(
    relationship_type: str,
    schema_url: str,
) -> list[ConnectedNodePair]:
    """Gets a list of connected node pairs via the provided relationship

    Args:
        relationship_type (str): the type of relationship in the schema to get
        schema_url (str): The URL of the schema in jsonld form

    Returns:
        list[ConnectedNodePair]: A list of connected node pairs
    """
    schema_explorer = SchemaExplorer()
    schema_explorer.load_schema(schema_url)
    schema_graph = schema_explorer.get_nx_schema()

    schema_generator = SchemaGenerator(path_to_json_ld=schema_url)
    relationship_subgraph = schema_generator.get_subgraph_by_edge_type(
        schema_graph, relationship_type
    )

    lst = [list(edge) for edge in relationship_subgraph.edges]

    return [
        ConnectedNodePair(connected_nodes[0], connected_nodes[1])
        for connected_nodes in lst
    ]


@handle_exceptions
def get_connected_node_pair_array(
    schema_url: str, relationship_type: str
) -> tuple[Union[ConnectedNodePairArray, BasicError], int]:
    """Gets a list of connected node pairs via the provided relationship

    Args:
        relationship_type (str): the type of relationship in the schema to get
        schema_url (str): The URL of the schema in jsonld form

    Returns:
        tuple[Union[ConnectedNodePairArray, BasicError], int]: A list of connected node pairs
    """
    nodes = get_connected_node_pairs_from_schematic(relationship_type, schema_url)
    result: Union[ConnectedNodePairArray, BasicError] = ConnectedNodePairArray(nodes)
    status = 200
    return result, status


@handle_exceptions
def get_connected_node_pair_page(
    schema_url: str,
    relationship_type: str,
    page_number: int = 1,
    page_max_items: int = 100000,
) -> tuple[Union[ConnectedNodePairPage, BasicError], int]:
    """Gets a page of connected node pairs via the provided relationship

    Args:
        relationship_type (str): the type of relationship in the schema to get
        schema_url (str): The URL of the schema in json form
        page_number (int): The page number the current request is for
        page_max_items (int): The maximum number of items per page

    Returns:
        tuple[Union[ConnectedNodePairPage, BasicError], int: A tuple
          The first item is either the connected nodes or an error object
          The second item is the response status
    """
    # pylint: disable=duplicate-code

    connected_nodes = get_connected_node_pairs_from_schematic(
        relationship_type, schema_url
    )
    page = Page(connected_nodes, page_number, page_max_items)

    cn_page = ConnectedNodePairPage(
        number=page.page_number,
        size=page.page_max_items,
        total_elements=page.total_items,
        total_pages=page.total_pages,
        has_next=page.has_next,
        has_previous=page.has_previous,
        connected_nodes=page.items,
    )
    result: Union[ConnectedNodePairPage, BasicError] = cn_page
    status = 200
    return result, status


def get_node_is_required_from_schematic(
    node_display: str,
    schema_url: str,
) -> bool:
    """Gets whether or not the node is required by the schema

    Args:
        node_display(str): The display name of the node
        schema_url (str): The URL of the schema in jsonld form

    Returns:
       bool: Whether or no the node is required
    """
    schema_generator = SchemaGenerator(path_to_json_ld=schema_url)
    return schema_generator.is_node_required(node_display)


@handle_exceptions
def get_node_is_required(
    node_display: str,
    schema_url: str,
) -> tuple[Union[bool, BasicError], int]:
    """Gets whether or not the node is required by the schema

    Args:
        node_display(str): The display name of the node
        schema_url (str): The URL of the schema in jsonld form

    Returns:
        tuple[Union[bool, BasicError], int]: A tuple
          The first item is either whether or not the node is required or an error object
          The second item is the response status
    """
    result: Union[bool, BasicError] = get_node_is_required_from_schematic(
        node_display, schema_url
    )
    status = 200
    return result, status


def get_property_label_from_schematic(
    node_display: str, schema_url: str, use_strict_camel_case: bool
) -> str:
    """Gets the property label of the node

    Args:
        node_display(str): The display name of the node
        schema_url (str): The URL of the schema in jsonld form
        use_strict_camel_case (bool): whether or not to use strict camel case when doing the
          conversion

    Returns:
        str: The node label
    """
    schema_explorer = SchemaExplorer()
    schema_explorer.load_schema(schema_url)
    return schema_explorer.get_property_label_from_display_name(
        node_display, use_strict_camel_case
    )


@handle_exceptions
def get_property_label(
    node_display: str, schema_url: str, use_strict_camel_case: bool
) -> tuple[Union[str, BasicError], int]:
    """Gets the property label of the node

    Args:
        node_display(str): The display name of the node
        schema_url (str): The URL of the schema in jsonld form
        use_strict_camel_case (bool): whether or not to use strict camel case when doing the
          conversion

    Returns:
        tuple[Union[str, BasicError], int]: A tuple
          The first item is either the label or an error object
          The second item is the response status
    """
    result: Union[str, BasicError] = get_property_label_from_schematic(
        node_display, schema_url, use_strict_camel_case
    )
    status = 200
    return result, status


@handle_exceptions
def get_schema_attributes(schema_url: str) -> tuple[Union[str, BasicError], int]:
    """
    Get all the attributes associated with a data model formatted as a dataframe
    (stored as a JSON String).

    Args:
        schema_url (str): The URL of the schema in json form

    Returns:
        tuple[Union[str, BasicError], int]: A tuple
          The first item is either the sttraibutes or an error object
          The second item is the response status
    """
    schema_path = download_schema_file_as_jsonld(schema_url)
    explorer = AttributesExplorer(schema_path)
    result: Union[str, BasicError] = explorer.parse_attributes(save_file=False)
    status = 200
    return result, status


def get_node_properties_from_schematic(
    node_label: str,
    schema_url: str,
) -> list[str]:
    """Gets the properties associated with the node

    Args:
        schema_url (str): The URL of the schema in jsonld form
        node_label (str): The label of the node

    Returns:
        list[str]: A list of properties of the node
    """
    schema_explorer = SchemaExplorer()
    schema_explorer.load_schema(schema_url)
    return schema_explorer.find_class_specific_properties(node_label)


@handle_exceptions
def get_node_property_array(
    node_label: str,
    schema_url: str,
) -> tuple[Union[NodePropertyArray, BasicError], int]:
    """Gets the properties associated with the node

    Args:
        schema_url (str): The URL of the schema in jsonld form
        node_label (str): The label of the node

    Returns:
        tuple[Union[NodePropertyArray, BasicError], int]: A tuple
          The first item is either the node properties or an error object
          The second item is the response status
    """

    properties = get_node_properties_from_schematic(node_label, schema_url)
    result: Union[NodePropertyArray, BasicError] = NodePropertyArray(properties)
    status = 200
    return result, status


def get_node_validation_rules_from_schematic(
    node_display: str,
    schema_url: str,
) -> list[str]:
    """Gets the validation_rules associated with the node

    Args:
        schema_url (str): The URL of the schema in jsonld form
        node_display (str): The display name of the node

    Returns:
        list[str]: A list of validation_rules of the node
    """
    schema_generator = SchemaGenerator(path_to_json_ld=schema_url)
    return schema_generator.get_node_validation_rules(node_display)  # type: ignore


@handle_exceptions
def get_node_validation_rule_array(
    node_display: str,
    schema_url: str,
) -> tuple[Union[ValidationRuleArray, BasicError], int]:
    """Gets the validation rules associated with the node

    Args:
        schema_url (str): The URL of the schema in jsonld form
        node_display(str): The display name of the node

    Returns:
        tuple[Union[ValidationRuleArray, BasicError], int]: A tuple
          The first item is either the validation rules or an error object
          The second item is the response status
    """
    validation_rules = get_node_validation_rules_from_schematic(
        node_display, schema_url
    )
    result: Union[ValidationRuleArray, BasicError] = ValidationRuleArray(
        validation_rules
    )
    status = 200
    return result, status


def get_node_dependencies_from_schematic(
    node_label: str,
    schema_url: str,
    return_display_names: bool = True,
    return_ordered_by_schema: bool = True,
) -> list[Node]:
    """Gets the nodes that the input node is dependent on

    Args:
        node_label (str): The label of the node to get dependencies for
        schema_url (str): The URL of the schema in json form
        return_display_names (bool): Whether or not to return the display names of the node,
          otherwise the label
        return_ordered_by_schema (bool):Whether or not to order the nodes by their order in
          the schema, otherwise random

    Returns:
        list[Node]: A list of nodes

    """
    schema_generator = SchemaGenerator(path_to_json_ld=schema_url)
    nodes = schema_generator.get_node_dependencies(
        source_node=node_label,
        display_names=return_display_names,
        schema_ordered=return_ordered_by_schema,
    )
    return [Node(node) for node in nodes]


@handle_exceptions
def get_node_dependency_array(
    node_label: str,
    schema_url: str,
    return_display_names: bool = True,
    return_ordered_by_schema: bool = True,
) -> tuple[Union[NodeArray, BasicError], int]:
    """Gets the nodes that the input node is dependent on

    Args:
        node_label (str): The label of the node to get dependencies for
        schema_url (str): The URL of the schema in json form
        return_display_names (bool): Whether or not to return the display names of the dependencies,
          otherwise the label
        return_ordered_by_schema (bool):Whether or not to order the dependencies by their order in
          the schema, otherwise random

    Returns:
        tuple[Union[NodeArray, BasicError], int]: A tuple
          The first item is either the nodes or an error object
          The second item is the response status
    """

    nodes = get_node_dependencies_from_schematic(
        node_label, schema_url, return_display_names, return_ordered_by_schema
    )
    result: Union[NodeArray, BasicError] = NodeArray(nodes)
    status = 200
    return result, status


@handle_exceptions
def get_node_dependency_page(  # pylint: disable=too-many-arguments
    node_label: str,
    schema_url: str,
    return_display_names: bool = True,
    return_ordered_by_schema: bool = True,
    page_number: int = 1,
    page_max_items: int = 100000,
) -> tuple[Union[NodePage, BasicError], int]:
    """Gets the nodes that the input node is dependent on

    Args:
        node_label (str): The label of the node to get dependencies for
        schema_url (str): The URL of the schema in json form
        return_display_names (bool): Whether or not to return the display names of the dependencies,
          otherwise the label
        return_ordered_by_schema (bool):Whether or not to order the dependencies by their order in
          the schema, otherwise random
        page_number (int): The page number the current request is for
        page_max_items (int): The maximum number of items per page

    Returns:
        tuple[Union[NodePage, BasicError], int]: A tuple
          The first item is either the nodes or an error object
          The second item is the response status
    """
    # pylint: disable=duplicate-code
    nodes = get_node_dependencies_from_schematic(
        node_label, schema_url, return_display_names, return_ordered_by_schema
    )
    page = Page(nodes, page_number, page_max_items)

    node_page = NodePage(
        number=page.page_number,
        size=page.page_max_items,
        total_elements=page.total_items,
        total_pages=page.total_pages,
        has_next=page.has_next,
        has_previous=page.has_previous,
        nodes=page.items,
    )
    result: Union[NodePage, BasicError] = node_page
    status = 200

    return result, status
