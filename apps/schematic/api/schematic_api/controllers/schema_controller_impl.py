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
