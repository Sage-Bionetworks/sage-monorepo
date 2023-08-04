"""Implementation of all endpoints"""
from typing import Union, Any

from schematic.schemas.generator import SchemaGenerator, SchemaExplorer  # type: ignore

from schematic_api.models.basic_error import BasicError
from schematic_api.models.attributes_page import AttributesPage
from schematic_api.models.attribute import Attribute
from schematic_api.models.components_page import ComponentsPage
from schematic_api.models.component import Component
from schematic_api.models.relationships_page import RelationshipsPage
from schematic_api.models.relationship import Relationship
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


def get_component_parents(
    component_label: str,
    schema_url: str,
    return_display_names: bool = True,
    return_ordered_by_schema: bool = True,
) -> list[str]:
    """Gets the components that the input component is dependent on

    Args:
        schema_url (str): The URL of the schema in json form
        component_label (str): The label of the component to get dependencies for
        return_display_names (bool): Whether or not to return the display names of the component,
          otherwise the label
        return_ordered_by_schema (bool):Whether or not to order the components by their order in
          the schema, otherwise random

    Returns:
        list[str]: A list of component labels or display names
    """
    schema_generator = SchemaGenerator(path_to_json_ld=schema_url)
    return schema_generator.get_node_dependencies(
        source_node=component_label,
        display_names=return_display_names,
        schema_ordered=return_ordered_by_schema,
    )


@handle_exceptions
def list_component_parents(
    component_label: str,
    schema_url: str,
    return_display_names: bool = True,
    return_ordered_by_schema: bool = True,
) -> tuple[Union[ComponentsPage, BasicError], int]:
    """Lists the components that the input component is dependent on

    Args:
        schema_url (str): The URL of the schema in json form
        component_label (str): The label of the component to get dependencies for
        return_display_names (bool): Whether or not to return the display names of the component,
          otherwise the label
        return_ordered_by_schema (bool):Whether or not to order the components by their order in
          the schema, otherwise random

    Returns:
        tuple[Union[ComponentsPage, BasicError], int]: A tuple
          The first item is either the components or an error object
          The second item is the response status
    """

    components = [
        Component(component)
        for component in get_component_parents(
            component_label, schema_url, return_display_names, return_ordered_by_schema
        )
    ]

    page = ComponentsPage(
        number=0,
        size=100,
        total_elements=len(components),
        total_pages=1,
        has_next=False,
        has_previous=False,
        components=components,
    )
    result: Union[ComponentsPage, BasicError] = page
    status = 200

    return result, status


def get_relationships(
    relationship_type: str,
    schema_url: str,
) -> list[list[Any]]:
    """Gets the relationships asked for

    Args:
        relationship_type (str): the type of relationship in the schema to get
        schema_url (str): The URL of the schema in jsonld form

    Returns:
        list[list[Any]]: A list of relationships
    """
    schema_explorer = SchemaExplorer()
    schema_explorer.load_schema(schema_url)
    schema_graph = schema_explorer.get_nx_schema()

    schema_generator = SchemaGenerator(path_to_json_ld=schema_url)
    relationship_subgraph = schema_generator.get_subgraph_by_edge_type(
        schema_graph, relationship_type
    )

    return [list(edge) for edge in relationship_subgraph.edges]


def list_relationships(
    relationship_type: str,
    schema_url: str,
) -> tuple[Union[RelationshipsPage, BasicError], int]:
    """Lists the relationships asked for

    Args:
        relationship_type (str): the type of relationship in the schema to get
        schema_url (str): The URL of the schema in json form

    Returns:
        tuple[Union[RelationshipsPage, BasicError], int]: A tuple
          The first item is either the relationships or an error object
          The second item is the response status
    """
    relationships = [
        Relationship(relationship[0], relationship[1])
        for relationship in get_relationships(relationship_type, schema_url)
    ]

    page = RelationshipsPage(
        number=0,
        size=100,
        total_elements=len(relationships),
        total_pages=1,
        has_next=False,
        has_previous=False,
        relationships=relationships,
    )
    result: Union[RelationshipsPage, BasicError] = page
    status = 200

    return result, status
