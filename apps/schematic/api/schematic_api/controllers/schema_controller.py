import connexion
import six
from typing import Dict
from typing import Tuple
from typing import Union

from schematic_api.models.attributes_page import AttributesPage  # noqa: E501
from schematic_api.models.basic_error import BasicError  # noqa: E501
from schematic_api.models.nodes_page import NodesPage  # noqa: E501
from schematic_api.models.validation_rules_page import ValidationRulesPage  # noqa: E501
from schematic_api import util
from schematic_api.controllers import schema_controller_impl


def get_component_label(
    component_display, schema_url, use_strict_camel_case=None
):  # noqa: E501
    """Gets the label of the component

    Gets the label of the component # noqa: E501

    :param component_display: The display name of a component in a schema
    :type component_display: str
    :param schema_url: The URL of a schema in jsonld form
    :type schema_url: str
    :param use_strict_camel_case: Whether or not to use the more strict way of converting to camel case
    :type use_strict_camel_case: bool

    :rtype: Union[str, Tuple[str, int], Tuple[str, int, Dict[str, str]]
    """
    return schema_controller_impl.get_component_label(
        component_display, schema_url, use_strict_camel_case
    )


def list_component_attributes(component_label, schema_url):  # noqa: E501
    """Gets attributes associated with a given component

    Gets attributes associated with a given component # noqa: E501

    :param component_label: The label of a component in a schema
    :type component_label: str
    :param schema_url: The URL of a schema in jsonld form
    :type schema_url: str

    :rtype: Union[AttributesPage, Tuple[AttributesPage, int], Tuple[AttributesPage, int, Dict[str, str]]
    """
    return schema_controller_impl.list_component_attributes(component_label, schema_url)


def list_node_dependencies(
    node_label, schema_url, return_display_names=None, return_ordered_by_schema=None
):  # noqa: E501
    """Gets the immediate dependencies that are related to the given source node

    Gets the immediate dependencies that are related to the given source node # noqa: E501

    :param node_label: The label of the source node in a schema to get the dependencies of
    :type node_label: str
    :param schema_url: The URL of a schema in jsonld form
    :type schema_url: str
    :param return_display_names: Whether or not to return the display names of the component, otherwise the label
    :type return_display_names: bool
    :param return_ordered_by_schema: Whether or not to order the components by their order in the schema, otherwise random
    :type return_ordered_by_schema: bool

    :rtype: Union[NodesPage, Tuple[NodesPage, int], Tuple[NodesPage, int, Dict[str, str]]
    """
    return schema_controller_impl.list_node_dependencies(
        node_label, schema_url, return_display_names, return_ordered_by_schema
    )


def list_node_validation_rules(node_display, schema_url):  # noqa: E501
    """Gets the validation rules, along with the arguments for each given rule associated with a given node

    Gets the validation rules, along with the arguments for each given rule associated with a given node # noqa: E501

    :param node_display: The display name of the node in a schema
    :type node_display: str
    :param schema_url: The URL of a schema in jsonld form
    :type schema_url: str

    :rtype: Union[ValidationRulesPage, Tuple[ValidationRulesPage, int], Tuple[ValidationRulesPage, int, Dict[str, str]]
    """
    return schema_controller_impl.list_node_validation_rules(node_display, schema_url)
