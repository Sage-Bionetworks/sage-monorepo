import connexion
import six
from typing import Dict
from typing import Tuple
from typing import Union

from schematic_api.models.attributes_page import AttributesPage  # noqa: E501
from schematic_api.models.basic_error import BasicError  # noqa: E501
from schematic_api.models.components_page import ComponentsPage  # noqa: E501
from schematic_api import util
from schematic_api.controllers import schema_controller_impl


def list_component_attributes(schema_url, component_label):  # noqa: E501
    """Gets attributes associated with a given component

    Gets attributes associated with a given component # noqa: E501

    :param schema_url: The URL of a schema in jsonld form
    :type schema_url: str
    :param component_label: The label of a component in a schema
    :type component_label: str

    :rtype: Union[AttributesPage, Tuple[AttributesPage, int], Tuple[AttributesPage, int, Dict[str, str]]
    """
    return schema_controller_impl.list_component_attributes(schema_url, component_label)


def list_component_dependencies(schema_url, component_label, return_display_names=None, return_ordered_by_schema=None):  # noqa: E501
    """Gets the components immediate parent components in the schema.

    Gets the components immediate parent components in the schema. # noqa: E501

    :param schema_url: The URL of a schema in jsonld form
    :type schema_url: str
    :param component_label: The label of a component in a schema
    :type component_label: str
    :param return_display_names: Whether or not to return the display names of the component, otherwise the label
    :type return_display_names: bool
    :param return_ordered_by_schema: Whether or not to order the components by their order in the schema, otherwise random
    :type return_ordered_by_schema: bool

    :rtype: Union[ComponentsPage, Tuple[ComponentsPage, int], Tuple[ComponentsPage, int, Dict[str, str]]
    """
    return schema_controller_impl.list_component_dependencies(schema_url, component_label, return_display_names, return_ordered_by_schema)
