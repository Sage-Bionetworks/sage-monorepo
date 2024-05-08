"""Implementation of all endpoints"""
from typing import Union

from schematic.schemas.data_model_parser import DataModelParser  # type: ignore
from schematic.schemas.data_model_graph import (  # type: ignore
    DataModelGraph,
    DataModelGraphExplorer,
)
from schematic.visualization.attributes_explorer import AttributesExplorer  # type: ignore
from schematic.utils.schema_utils import get_property_label_from_display_name  # type: ignore
from schematic.utils.schema_utils import DisplayLabelType  # type: ignore

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
from schematic_api.models.connected_node_pair_array import ConnectedNodePairArray
from schematic_api.models.connected_node_pair_page import ConnectedNodePairPage
from schematic_api.models.connected_node_pair import ConnectedNodePair
from schematic_api.controllers.utils import (
    handle_exceptions,
    download_schema_file_as_jsonld,
)
from schematic_api.controllers.paging import Page


def create_data_model_graph_explorer(
    schema_url: str, display_label_type: DisplayLabelType
) -> DataModelGraphExplorer:
    """Creates a DataModelGraphExplorer for use in variopus endpoints

    Args:
        schema_url (str): The URL of the schema in json form
        display_label_type (DisplayLabelType):
           The type of label to use as display

    Returns:
        DataModelGraphExplorer: _description_
    """
    data_model_parser = DataModelParser(path_to_data_model=schema_url)
    parsed_data_model = data_model_parser.parse_model()
    data_model_grapher = DataModelGraph(
        attribute_relationships_dict=parsed_data_model,
        data_model_labels=display_label_type,
    )
    graph_data_model = data_model_grapher.generate_data_model_graph()
    return DataModelGraphExplorer(graph_data_model)


@handle_exceptions
def get_component(
    component_label: str,
    schema_url: str,
    include_index: bool = False,
    display_label_type: DisplayLabelType = "class_label",
) -> tuple[Union[str, BasicError], int]:
    """
    Get all the attributes associated with a specific data model component formatted as a
    dataframe (stored as a JSON String).

    Args:
        component_label (str): The label of the component
        schema_url (str): The URL of the schema in json form
        include_index (bool): Whether to include the indexes of the dataframe
        display_label_type (DisplayLabelType):
           The type of label to use as display
           Defaults to "class_label"

    Returns:
        tuple[Union[str, BasicError], int]: A tuple
          The first item is either the component or an error object
          The second item is the response status
    """
    schema_path = download_schema_file_as_jsonld(schema_url)
    explorer = AttributesExplorer(
        path_to_jsonld=schema_path, data_model_labels=display_label_type
    )
    attributes = (
        explorer._parse_component_attributes(  # pylint:disable=protected-access
            component=component_label, save_file=False, include_index=include_index
        )
    )
    assert isinstance(attributes, str)
    result: Union[str, BasicError] = attributes
    status = 200
    return result, status


@handle_exceptions
def get_component_requirements_array(
    component_label: str,
    schema_url: str,
    display_label_type: DisplayLabelType,
) -> tuple[Union[list[str], BasicError], int]:
    """Gets the input components required components

    Args:
        component_label (str): The label of the component
        schema_url (str): The URL of the schema in json form
        display_label_type (DisplayLabelType):
           The type of label to use as display
    Returns:
        tuple[Union[ComponentRequirementArray, BasicError], int]: A tuple
          item 1 is either the required coponents or an error
          item 2 is the status
    """
    dmge = create_data_model_graph_explorer(schema_url, display_label_type)
    result = dmge.get_component_requirements(source_component=component_label)
    status = 200
    return result, status


@handle_exceptions
def get_component_requirements_graph(
    component_label: str,
    schema_url: str,
    display_label_type: DisplayLabelType,
) -> tuple[Union[list[ComponentRequirementSubgraph], BasicError], int]:
    """Gets the input components required components

    Args:
        component_label (str): The label of the component
        schema_url (str): The URL of the schema in json form
        display_label_type (DisplayLabelType):
           The type of label to use as display
    Returns:
        tuple[Union[ComponentRequirementGrpah, BasicError], int]: A tuple
          item 1 is either the required coponents or an error
          item 2 is the status
    """
    dmge = create_data_model_graph_explorer(schema_url, display_label_type)
    graph = dmge.get_component_requirements_graph(source_component=component_label)
    edges: list[tuple[str, str]] = graph.edges()
    result = [ComponentRequirementSubgraph(edge[0], edge[1]) for edge in edges]
    status = 200
    return result, status


def get_connected_node_pairs_from_schematic(
    relationship_type: str,
    schema_url: str,
    display_label_type: DisplayLabelType = "class_label",
) -> list[ConnectedNodePair]:
    """Gets a list of connected node pairs via the provided relationship

    Args:
        relationship_type (str): the type of relationship in the schema to get
        schema_url (str): The URL of the schema in jsonld form
        display_label_type (DisplayLabelType):
           The type of label to use as display
           Defaults to "class_label"

    Returns:
        list[ConnectedNodePair]: A list of connected node pairs
    """
    dmge = create_data_model_graph_explorer(schema_url, display_label_type)
    relationship_subgraph = dmge.get_subgraph_by_edge_type(relationship_type)
    lst = [list(edge) for edge in relationship_subgraph.edges]

    return [
        ConnectedNodePair(connected_nodes[0], connected_nodes[1])
        for connected_nodes in lst
    ]


@handle_exceptions
def get_connected_node_pair_array(
    schema_url: str,
    relationship_type: str,
    display_label_type: DisplayLabelType = "class_label",
) -> tuple[Union[ConnectedNodePairArray, BasicError], int]:
    """Gets a list of connected node pairs via the provided relationship

    Args:
        relationship_type (str): the type of relationship in the schema to get
        schema_url (str): The URL of the schema in jsonld form
        display_label_type (DisplayLabelType):
           The type of label to use as display
           Defaults to "class_label"

    Returns:
        tuple[Union[ConnectedNodePairArray, BasicError], int]: A list of connected node pairs
    """
    nodes = get_connected_node_pairs_from_schematic(
        relationship_type, schema_url, display_label_type
    )
    result: Union[ConnectedNodePairArray, BasicError] = ConnectedNodePairArray(nodes)
    status = 200
    return result, status


@handle_exceptions
def get_connected_node_pair_page(
    schema_url: str,
    relationship_type: str,
    page_number: int = 1,
    page_max_items: int = 100000,
    display_label_type: DisplayLabelType = "class_label",
) -> tuple[Union[ConnectedNodePairPage, BasicError], int]:
    """Gets a page of connected node pairs via the provided relationship

    Args:
        relationship_type (str): the type of relationship in the schema to get
        schema_url (str): The URL of the schema in json form
        page_number (int): The page number the current request is for
        page_max_items (int): The maximum number of items per page
        display_label_type (DisplayLabelType):
           The type of label to use as display
           Defaults to "class_label"

    Returns:
        tuple[Union[ConnectedNodePairPage, BasicError], int: A tuple
          The first item is either the connected nodes or an error object
          The second item is the response status
    """
    # pylint: disable=duplicate-code

    connected_nodes = get_connected_node_pairs_from_schematic(
        relationship_type, schema_url, display_label_type
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
    display_label_type: DisplayLabelType = "class_label",
) -> bool:
    """Gets whether or not the node is required by the schema

    Args:
        node_display(str): The display name of the node
        schema_url (str): The URL of the schema in jsonld form
        display_label_type (DisplayLabelType):
           The type of label to use as display
           Defaults to "class_label"

    Returns:
       bool: Whether or no the node is required
    """
    dmge = create_data_model_graph_explorer(schema_url, display_label_type)
    return dmge.get_node_required(node_display)


@handle_exceptions
def get_node_is_required(
    node_display: str,
    schema_url: str,
    display_label_type: DisplayLabelType = "class_label",
) -> tuple[Union[bool, BasicError], int]:
    """Gets whether or not the node is required by the schema

    Args:
        node_display(str): The display name of the node
        schema_url (str): The URL of the schema in jsonld form
        display_label_type (DisplayLabelType):
           The type of label to use as display
           Defaults to "class_label"

    Returns:
        tuple[Union[bool, BasicError], int]: A tuple
          The first item is either whether or not the node is required or an error object
          The second item is the response status
    """
    result: Union[bool, BasicError] = get_node_is_required_from_schematic(
        node_display, schema_url, display_label_type
    )
    status = 200
    return result, status


@handle_exceptions
def get_property_label(
    node_display: str, use_strict_camel_case: bool
) -> tuple[Union[str, BasicError], int]:
    """Gets the property label of the node

    Args:
        node_display(str): The display name of the node
        use_strict_camel_case (bool): whether or not to use strict camel case when doing the
          conversion

    Returns:
        tuple[Union[str, BasicError], int]: A tuple
          The first item is either the label or an error object
          The second item is the response status
    """
    result: Union[str, BasicError] = get_property_label_from_display_name(
        display_name=node_display, strict_camel_case=use_strict_camel_case
    )
    status = 200
    return result, status


@handle_exceptions
def get_schema_attributes(
    schema_url: str, display_label_type: DisplayLabelType = "class_label"
) -> tuple[Union[str, BasicError], int]:
    """
    Get all the attributes associated with a data model formatted as a dataframe
    (stored as a JSON String).

    Args:
        schema_url (str): The URL of the schema in json form
        display_label_type (DisplayLabelType):
          The type of label to use as display
          Defaults to "class_label"

    Returns:
        tuple[Union[str, BasicError], int]: A tuple
          The first item is either the attributes or an error object
          The second item is the response status
    """
    schema_path = download_schema_file_as_jsonld(schema_url)
    explorer = AttributesExplorer(
        path_to_jsonld=schema_path, data_model_labels=display_label_type
    )
    result: Union[str, BasicError] = explorer.parse_attributes(save_file=False)  # type: ignore
    status = 200
    return result, status


def get_node_properties_from_schematic(
    node_label: str,
    schema_url: str,
    display_label_type: DisplayLabelType = "class_label",
) -> list[str]:
    """Gets the properties associated with the node

    Args:
        schema_url (str): The URL of the schema in jsonld form
        node_label (str): The label of the node
        display_label_type (DisplayLabelType):
           The type of label to use as display
           Defaults to "class_label"

    Returns:
        list[str]: A list of properties of the node
    """
    dmge = create_data_model_graph_explorer(schema_url, display_label_type)
    properties = dmge.find_class_specific_properties(node_label)
    return properties


@handle_exceptions
def get_node_properties(
    node_label: str,
    schema_url: str,
    display_label_type: DisplayLabelType = "class_label",
) -> tuple[Union[NodePropertyArray, BasicError], int]:
    """Gets the properties associated with the node

    Args:
        schema_url (str): The URL of the schema in jsonld form
        node_label (str): The label of the node
        display_label_type (DisplayLabelType):
           The type of label to use as display
           Defaults to "class_label"

    Returns:
        tuple[Union[NodePropertyArray, BasicError], int]: A tuple
          The first item is either the node properties or an error object
          The second item is the response status
    """

    properties = get_node_properties_from_schematic(
        node_label, schema_url, display_label_type
    )
    result: Union[NodePropertyArray, BasicError] = NodePropertyArray(properties)
    status = 200
    return result, status


def get_node_validation_rules_from_schematic(
    node_display: str,
    schema_url: str,
    display_label_type: DisplayLabelType = "class_label",
) -> list[ValidationRule]:
    """Gets the validation_rules associated with the node

    Args:
        schema_url (str): The URL of the schema in jsonld form
        node_display (str): The display name of the node
        display_label_type (DisplayLabelType):
           The type of label to use as display
           Defaults to "class_label"

    Returns:
        list[ValidationRule]: A list of validation_rules of the node
    """
    dmge = create_data_model_graph_explorer(schema_url, display_label_type)
    rules: list[str] = dmge.get_node_validation_rules(node_display)  # type: ignore
    return [ValidationRule(rule) for rule in rules]


@handle_exceptions
def get_node_validation_rules(
    node_display: str,
    schema_url: str,
    display_label_type: DisplayLabelType = "class_label",
) -> tuple[Union[ValidationRuleArray, BasicError], int]:
    """Gets the validation rules associated with the node

    Args:
        schema_url (str): The URL of the schema in jsonld form
        node_display(str): The display name of the node
    display_label_type (DisplayLabelType):
           The type of label to use as display
           Defaults to "class_label"

    Returns:
        tuple[Union[ValidationRuleArray, BasicError], int]: A tuple
          The first item is either the validation rules or an error object
          The second item is the response status
    """
    validation_rules = get_node_validation_rules_from_schematic(
        node_display, schema_url, display_label_type
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
    display_label_type: DisplayLabelType = "class_label",
) -> list[Node]:
    """Gets the nodes that the input node is dependent on

    Args:
        node_label (str): The label of the node to get dependencies for
        schema_url (str): The URL of the schema in json form
        return_display_names (bool): Whether or not to return the display names of the node,
          otherwise the label
        return_ordered_by_schema (bool):Whether or not to order the nodes by their order in
          the schema, otherwise random
        display_label_type (DisplayLabelType):
           The type of label to use as display
           Defaults to "class_label"

    Returns:
        list[Node]: A list of nodes

    """
    dmge = create_data_model_graph_explorer(schema_url, display_label_type)
    nodes = dmge.get_node_dependencies(
        node_label, return_display_names, return_ordered_by_schema
    )
    return [Node(node) for node in nodes]


@handle_exceptions
def get_node_dependency_array(
    node_label: str,
    schema_url: str,
    return_display_names: bool = True,
    return_ordered_by_schema: bool = True,
    display_label_type: DisplayLabelType = "class_label",
) -> tuple[Union[NodeArray, BasicError], int]:
    """Gets the nodes that the input node is dependent on

    Args:
        node_label (str): The label of the node to get dependencies for
        schema_url (str): The URL of the schema in json form
        return_display_names (bool): Whether or not to return the display names of the dependencies,
          otherwise the label
        return_ordered_by_schema (bool):Whether or not to order the dependencies by their order in
          the schema, otherwise random
        display_label_type (DisplayLabelType):
           The type of label to use as display
           Defaults to "class_label"

    Returns:
        tuple[Union[NodeArray, BasicError], int]: A tuple
          The first item is either the nodes or an error object
          The second item is the response status
    """

    nodes = get_node_dependencies_from_schematic(
        node_label,
        schema_url,
        return_display_names,
        return_ordered_by_schema,
        display_label_type,
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
    display_label_type: DisplayLabelType = "class_label",
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
        display_label_type (DisplayLabelType):
           The type of label to use as display
           Defaults to "class_label"

    Returns:
        tuple[Union[NodePage, BasicError], int]: A tuple
          The first item is either the nodes or an error object
          The second item is the response status
    """
    # pylint: disable=duplicate-code
    nodes = get_node_dependencies_from_schematic(
        node_label,
        schema_url,
        return_display_names,
        return_ordered_by_schema,
        display_label_type,
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
