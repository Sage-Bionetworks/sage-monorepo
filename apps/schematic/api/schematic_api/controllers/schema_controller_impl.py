"""Implementation of all endpoints"""
from typing import Union

from schematic.schemas.explorer import SchemaExplorer  # type: ignore
from schematic.schemas.generator import SchemaGenerator  # type: ignore

from schematic_api.models.basic_error import BasicError
from schematic_api.models.attributes_page import AttributesPage
from schematic_api.models.attribute import Attribute
from schematic_api.models.validation_rules_page import ValidationRulesPage
from schematic_api.models.validation_rule import ValidationRule
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


def get_component_validation_rules(
    component_display: str,
    schema_url: str,
) -> list[str]:
    """Gets the validation_rules associated with the component

    Args:
        schema_url (str): The URL of the schema in jsonld form
        component_display (str): The display name of the component

    Returns:
        list[str]: A list of validation_rules of the component
    """
    schema_generator = SchemaGenerator(path_to_json_ld=schema_url)
    return schema_generator.get_node_validation_rules(component_display)


@handle_exceptions
def list_component_validation_rules(
    component_display: str,
    schema_url: str,
) -> tuple[Union[ValidationRulesPage, BasicError], int]:
    """Lists the validation_rules associated with the component

    Args:
        schema_url (str): The URL of the schema in jsonld form
        component_display(str): The display name of the component

    Returns:
        tuple[Union[AttributesPage, BasicError], int]: A tuple
          The first item is either the validation_rules or an error object
          The second item is the response status
    """

    validation_rules = [
        ValidationRule(attribute)
        for attribute in get_component_validation_rules(component_display, schema_url)
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
