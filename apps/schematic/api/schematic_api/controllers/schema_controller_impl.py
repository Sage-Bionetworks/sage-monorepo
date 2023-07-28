"""Implementation of all endpoints"""
from typing import Union

from schematic.schemas.generator import SchemaGenerator, SchemaExplorer  # type: ignore

from schematic_api.models.basic_error import BasicError
from schematic_api.models.attributes_page import AttributesPage
from schematic_api.models.attribute import Attribute
from schematic_api.models.components_page import ComponentsPage
from schematic_api.models.component import Component
from schematic_api.controllers.utils import handle_exceptions


def get_component_attributes(schema_url: str, component_label: str) -> list[str]:
    """Gets the attributes associated with the component

    Args:
        schema_url (str): _description_
        component_label (str): _description_

    Returns:
        list[str]: _description_
    """
    schema_explorer = SchemaExplorer()
    schema_explorer.load_schema(schema_url)
    return schema_explorer.find_class_specific_properties(component_label)


@handle_exceptions
def list_component_attributes(
    schema_url: str, component_label: str
) -> tuple[Union[AttributesPage, BasicError], int]:
    """Lists the attributes associated with the component

    Args:
        schema_url (str): _description_
        component_label (str): _description_

    Returns:
        tuple[Union[AttributesPage, BasicError], int]: _description_
    """

    attributes = [
        Attribute(attribute)
        for attribute in get_component_attributes(schema_url, component_label)
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


def get_component_dependencies(
    schema_url: str,
    component_label: str,
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
def list_component_dependencies(
    schema_url: str,
    component_label: str,
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
        for component in get_component_dependencies(
            schema_url, component_label, return_display_names, return_ordered_by_schema
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
