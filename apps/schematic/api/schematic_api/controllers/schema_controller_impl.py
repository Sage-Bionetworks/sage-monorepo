"""Implementation of all endpoints"""
from typing import Union

from schematic.schemas.generator import SchemaGenerator, SchemaExplorer  # type: ignore

from schematic_api.models.basic_error import BasicError
from schematic_api.models.node_properties_page import NodePropertiesPage
from schematic_api.models.node_property import NodeProperty
from schematic_api.models.validation_rules_page import ValidationRulesPage
from schematic_api.models.validation_rule import ValidationRule
from schematic_api.models.nodes_page import NodesPage
from schematic_api.models.node import Node
from schematic_api.controllers.utils import handle_exceptions


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
def get_node_properties(
    node_label: str,
    schema_url: str,
) -> tuple[Union[NodePropertiesPage, BasicError], int]:
    """Gets the properties associated with the node

    Args:
        schema_url (str): The URL of the schema in jsonld form
        node_label (str): The label of the node

    Returns:
        tuple[Union[NodePropertiesPage, BasicError], int]: A tuple
          The first item is either the node properties or an error object
          The second item is the response status
    """

    properties = [
        NodeProperty(property)
        for property in get_node_properties_from_schematic(node_label, schema_url)
    ]

    page = NodePropertiesPage(
        number=0,
        size=100,
        total_elements=len(properties),
        total_pages=1,
        has_next=False,
        has_previous=False,
        node_properties=properties,
    )
    result: Union[NodePropertiesPage, BasicError] = page
    status = 200

    return result, status


def get_node_validation_rules(
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
    return schema_generator.get_node_validation_rules(node_display)


@handle_exceptions
def list_node_validation_rules(
    node_display: str,
    schema_url: str,
) -> tuple[Union[ValidationRulesPage, BasicError], int]:
    """Lists the validation rules associated with the node

    Args:
        schema_url (str): The URL of the schema in jsonld form
        node_display(str): The display name of the node

    Returns:
        tuple[Union[AttributesPage, BasicError], int]: A tuple
          The first item is either the validation rules or an error object
          The second item is the response status
    """

    validation_rules = [
        ValidationRule(attribute)
        for attribute in get_node_validation_rules(node_display, schema_url)
    ]

    page = ValidationRulesPage(
        number=0,
        size=100,
        total_elements=len(validation_rules),
        total_pages=1,
        has_next=False,
        has_previous=False,
        validation_rules=validation_rules,
    )
    result: Union[ValidationRulesPage, BasicError] = page
    status = 200

    return result, status


def get_node_dependencies(
    node_label: str,
    schema_url: str,
    return_display_names: bool = True,
    return_ordered_by_schema: bool = True,
) -> list[str]:
    """Gets the nodes that the input node is dependent on

    Args:
        node_label (str): The label of the node to get dependencies for
        schema_url (str): The URL of the schema in json form
        return_display_names (bool): Whether or not to return the display names of the node,
          otherwise the label
        return_ordered_by_schema (bool):Whether or not to order the nodes by their order in
          the schema, otherwise random

    Returns:
        list[str]: A list of node labels or display names

    """
    schema_generator = SchemaGenerator(path_to_json_ld=schema_url)
    return schema_generator.get_node_dependencies(
        source_node=node_label,
        display_names=return_display_names,
        schema_ordered=return_ordered_by_schema,
    )


@handle_exceptions
def list_node_dependencies(
    node_label: str,
    schema_url: str,
    return_display_names: bool = True,
    return_ordered_by_schema: bool = True,
) -> tuple[Union[NodesPage, BasicError], int]:
    """Lists the attributes that the input attribute is dependent on

    Args:
        node_label (str): The label of the node to get dependencies for
        schema_url (str): The URL of the schema in json form
        return_display_names (bool): Whether or not to return the display names of the dependencies,
          otherwise the label
        return_ordered_by_schema (bool):Whether or not to order the dependencies by their order in
          the schema, otherwise random

    Returns:
        tuple[Union[NodesPage, BasicError], int]: A tuple
          The first item is either the attributes or an error object
          The second item is the response status
    """

    nodes = [
        Node(node)
        for node in get_node_dependencies(
            node_label, schema_url, return_display_names, return_ordered_by_schema
        )
    ]

    page = NodesPage(
        number=0,
        size=100,
        total_elements=len(nodes),
        total_pages=1,
        has_next=False,
        has_previous=False,
        nodes=nodes,
    )
    result: Union[NodesPage, BasicError] = page
    status = 200

    return result, status
