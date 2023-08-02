"""Implementation of all endpoints"""
from typing import Union

from schematic.schemas.explorer import SchemaExplorer  # type: ignore

from schematic_api.models.basic_error import BasicError
from schematic_api.models.attributes_page import AttributesPage
from schematic_api.models.attribute import Attribute
from schematic_api.controllers.utils import handle_exceptions


def get_component_attributes(schema_url: str, component_label: str) -> list[str]:
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
    schema_url: str, component_label: str
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
