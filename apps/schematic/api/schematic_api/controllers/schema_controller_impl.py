"""Implementation of all endpoints"""
from typing import Union

from schematic.schemas.generator import SchemaGenerator, SchemaExplorer  # type: ignore

from schematic_api.models.basic_error import BasicError
from schematic_api.models.attributes_page import AttributesPage
from schematic_api.models.attribute import Attribute
from schematic_api.models.nodes_page import NodesPage
from schematic_api.models.node import Node
from schematic_api.controllers.utils import handle_exceptions


def get_component_attributes(
    component_label: str,
    schema_url: str,
) -> list[str]:
    """Gets the attributes associated with the component

    Args:
        schema_url (str): The URL of the schema in jsonld form
        component_label (str): The label of the component

    Returns:
        list[str]: A list of attributes of the component
    """
    schema_explorer = SchemaExplorer()
    schema_explorer.load_schema(schema_url)
    return schema_explorer.find_class_specific_properties(component_label)


@handle_exceptions
def list_component_attributes(
    component_label: str,
    schema_url: str,
) -> tuple[Union[AttributesPage, BasicError], int]:
    """Lists the attributes associated with the component

    Args:
        schema_url (str): The URL of the schema in jsonld form
        component_label (str): The label of the component

    Returns:
        tuple[Union[AttributesPage, BasicError], int]: A tuple
          The first item is either the attributes or an error object
          The second item is the response status
    """

    attributes = [
        Attribute(attribute)
        for attribute in get_component_attributes(component_label, schema_url)
    ]

    page = AttributesPage(
        number=0,
        size=100,
        total_elements=len(attributes),
        total_pages=1,
        has_next=False,
        has_previous=False,
        attributes=attributes,
    )
    result: Union[AttributesPage, BasicError] = page
    status = 200

    return result, status


def get_node_dependencies(
    node_label: str,
    schema_url: str,
    return_display_names: bool = True,
    return_ordered_by_schema: bool = True,
) -> list[str]:
    """Gets the components that the input component is dependent on

    Args:
        node_label (str): The label of the component to get dependencies for
        schema_url (str): The URL of the schema in json form
        return_display_names (bool): Whether or not to return the display names of the component,
          otherwise the label
        return_ordered_by_schema (bool):Whether or not to order the components by their order in
          the schema, otherwise random

    Returns:
        list[str]: A list of component labels or display names
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
    """Lists the components that the input component is dependent on

    Args:
        node_label (str): The label of the node to get dependencies for
        schema_url (str): The URL of the schema in json form
        return_display_names (bool): Whether or not to return the display names of the dependencies,
          otherwise the label
        return_ordered_by_schema (bool):Whether or not to order the dependencies by their order in
          the schema, otherwise random

    Returns:
        tuple[Union[ComponentsPage, BasicError], int]: A tuple
          The first item is either the components or an error object
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
