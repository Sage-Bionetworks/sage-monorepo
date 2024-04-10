import connexion
import six
from typing import Dict
from typing import Tuple
from typing import Union

from schematic_api.models.basic_error import BasicError  # noqa: E501
from schematic_api.models.component_requirement_array import (
    ComponentRequirementArray,
)  # noqa: E501
from schematic_api.models.component_requirement_graph import (
    ComponentRequirementGraph,
)  # noqa: E501
from schematic_api.models.connected_node_pair_array import (
    ConnectedNodePairArray,
)  # noqa: E501
from schematic_api.models.connected_node_pair_page import (
    ConnectedNodePairPage,
)  # noqa: E501
from schematic_api.models.node_array import NodeArray  # noqa: E501
from schematic_api.models.node_page import NodePage  # noqa: E501
from schematic_api.models.node_property_array import NodePropertyArray  # noqa: E501
from schematic_api.models.validation_rule_array import ValidationRuleArray  # noqa: E501
from schematic_api import util
from schematic_api.controllers import schema_controller_impl


def get_component(
    component_label, schema_url, include_index=None, display_label_type=None
):  # noqa: E501
    """Get all the attributes associated with a specific data model component formatted as a dataframe (stored as a JSON String).

    Get all the attributes associated with a specific data model component formatted as a dataframe (stored as a JSON String). # noqa: E501

    :param component_label: The label of a component in a schema
    :type component_label: str
    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param include_index: Whether to include the indexes of the dataframe in the returned JSON string.
    :type include_index: bool
    :param display_label_type: The type of label to display
    :type display_label_type: str

    :rtype: Union[str, Tuple[str, int], Tuple[str, int, Dict[str, str]]
    """
    return schema_controller_impl.get_component(
        component_label, schema_url, include_index, display_label_type
    )


def get_component_requirements_array(
    component_label, schema_url, display_label_type=None
):  # noqa: E501
    """Given a source model component (see https://w3id.org/biolink/vocab/category for definnition of component), return all components required by it in array form.

    Given a source model component (see https://w3id.org/biolink/vocab/category for definnition of component), return all components required by it in array form. # noqa: E501

    :param component_label: The label of a component in a schema
    :type component_label: str
    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param display_label_type: The type of label to display
    :type display_label_type: str

    :rtype: Union[ComponentRequirementArray, Tuple[ComponentRequirementArray, int], Tuple[ComponentRequirementArray, int, Dict[str, str]]
    """
    return schema_controller_impl.get_component_requirements_array(
        component_label, schema_url, display_label_type
    )


def get_component_requirements_graph(
    component_label, schema_url, display_label_type=None
):  # noqa: E501
    """Given a source model component (see https://w3id.org/biolink/vocab/category for definnition of component), return all components required by it in graph form.

    Given a source model component (see https://w3id.org/biolink/vocab/category for definnition of component), return all components required by it in graph form. # noqa: E501

    :param component_label: The label of a component in a schema
    :type component_label: str
    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param display_label_type: The type of label to display
    :type display_label_type: str

    :rtype: Union[ComponentRequirementGraph, Tuple[ComponentRequirementGraph, int], Tuple[ComponentRequirementGraph, int, Dict[str, str]]
    """
    return schema_controller_impl.get_component_requirements_graph(
        component_label, schema_url, display_label_type
    )


def get_connected_node_pair_array(
    schema_url, relationship_type, display_label_type=None
):  # noqa: E501
    """Gets an array of connected node pairs

    Gets a array of connected node pairs # noqa: E501

    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param relationship_type: Type of relationship in a schema, such as requiresDependency
    :type relationship_type: str
    :param display_label_type: The type of label to display
    :type display_label_type: str

    :rtype: Union[ConnectedNodePairArray, Tuple[ConnectedNodePairArray, int], Tuple[ConnectedNodePairArray, int, Dict[str, str]]
    """
    return schema_controller_impl.get_connected_node_pair_array(
        schema_url, relationship_type, display_label_type
    )


def get_connected_node_pair_page(
    schema_url,
    relationship_type,
    page_number=None,
    page_max_items=None,
    display_label_type=None,
):  # noqa: E501
    """Gets a page of connected node pairs

    Gets a page of connected node pairs # noqa: E501

    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param relationship_type: Type of relationship in a schema, such as requiresDependency
    :type relationship_type: str
    :param page_number: The page number to get for a paginated query
    :type page_number: int
    :param page_max_items: The maximum number of items per page (up to 100,000) for paginated endpoints
    :type page_max_items: int
    :param display_label_type: The type of label to display
    :type display_label_type: str

    :rtype: Union[ConnectedNodePairPage, Tuple[ConnectedNodePairPage, int], Tuple[ConnectedNodePairPage, int, Dict[str, str]]
    """
    return schema_controller_impl.get_connected_node_pair_page(
        schema_url, relationship_type, page_number, page_max_items, display_label_type
    )


def get_node_dependency_array(
    node_label,
    schema_url,
    return_display_names=None,
    return_ordered_by_schema=None,
    display_label_type=None,
):  # noqa: E501
    """Gets the immediate dependencies that are related to the given source node

    Gets the immediate dependencies that are related to the given source node # noqa: E501

    :param node_label: The label of the source node in a schema to get the dependencies of
    :type node_label: str
    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param return_display_names: Whether or not to return the display names of the component, otherwise the label
    :type return_display_names: bool
    :param return_ordered_by_schema: Whether or not to order the components by their order in the schema, otherwise random
    :type return_ordered_by_schema: bool
    :param display_label_type: The type of label to display
    :type display_label_type: str

    :rtype: Union[NodeArray, Tuple[NodeArray, int], Tuple[NodeArray, int, Dict[str, str]]
    """
    return schema_controller_impl.get_node_dependency_array(
        node_label,
        schema_url,
        return_display_names,
        return_ordered_by_schema,
        display_label_type,
    )


def get_node_dependency_page(
    node_label,
    schema_url,
    return_display_names=None,
    return_ordered_by_schema=None,
    page_number=None,
    page_max_items=None,
    display_label_type=None,
):  # noqa: E501
    """Gets the immediate dependencies that are related to the given source node

    Gets the immediate dependencies that are related to the given source node # noqa: E501

    :param node_label: The label of the source node in a schema to get the dependencies of
    :type node_label: str
    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param return_display_names: Whether or not to return the display names of the component, otherwise the label
    :type return_display_names: bool
    :param return_ordered_by_schema: Whether or not to order the components by their order in the schema, otherwise random
    :type return_ordered_by_schema: bool
    :param page_number: The page number to get for a paginated query
    :type page_number: int
    :param page_max_items: The maximum number of items per page (up to 100,000) for paginated endpoints
    :type page_max_items: int
    :param display_label_type: The type of label to display
    :type display_label_type: str

    :rtype: Union[NodePage, Tuple[NodePage, int], Tuple[NodePage, int, Dict[str, str]]
    """
    return schema_controller_impl.get_node_dependency_page(
        node_label,
        schema_url,
        return_display_names,
        return_ordered_by_schema,
        page_number,
        page_max_items,
        display_label_type,
    )


def get_node_is_required(
    node_display, schema_url, display_label_type=None
):  # noqa: E501
    """Gets whether or not the node is required in the schema

    Gets whether or not the node is required in the schema # noqa: E501

    :param node_display: The display name of the node in a schema
    :type node_display: str
    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param display_label_type: The type of label to display
    :type display_label_type: str

    :rtype: Union[bool, Tuple[bool, int], Tuple[bool, int, Dict[str, str]]
    """
    return schema_controller_impl.get_node_is_required(
        node_display, schema_url, display_label_type
    )


def get_node_properties(node_label, schema_url, display_label_type=None):  # noqa: E501
    """Gets properties associated with a given node

    Gets properties associated with a given node # noqa: E501

    :param node_label: The label of the source node in a schema to get the dependencies of
    :type node_label: str
    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param display_label_type: The type of label to display
    :type display_label_type: str

    :rtype: Union[NodePropertyArray, Tuple[NodePropertyArray, int], Tuple[NodePropertyArray, int, Dict[str, str]]
    """
    return schema_controller_impl.get_node_properties(
        node_label, schema_url, display_label_type
    )


def get_node_validation_rules(
    node_display, schema_url, display_label_type=None
):  # noqa: E501
    """Gets the validation rules, along with the arguments for each given rule associated with a given node

    Gets the validation rules, along with the arguments for each given rule associated with a given node # noqa: E501

    :param node_display: The display name of the node in a schema
    :type node_display: str
    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param display_label_type: The type of label to display
    :type display_label_type: str

    :rtype: Union[ValidationRuleArray, Tuple[ValidationRuleArray, int], Tuple[ValidationRuleArray, int, Dict[str, str]]
    """
    return schema_controller_impl.get_node_validation_rules(
        node_display, schema_url, display_label_type
    )


def get_property_label(node_display, use_strict_camel_case=None):  # noqa: E501
    """Gets the property label of the node

    Gets the property label of the node # noqa: E501

    :param node_display: The display name of the node in a schema
    :type node_display: str
    :param use_strict_camel_case: Whether or not to use the more strict way of converting to camel case
    :type use_strict_camel_case: bool

    :rtype: Union[str, Tuple[str, int], Tuple[str, int, Dict[str, str]]
    """
    return schema_controller_impl.get_property_label(
        node_display, use_strict_camel_case
    )


def get_schema_attributes(schema_url, display_label_type=None):  # noqa: E501
    """Get all the attributes associated with a data model formatted as a dataframe (stored as a JSON String).

    Get all the attributes associated with a data model formatted as a dataframe (stored as a JSON String). # noqa: E501

    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param display_label_type: The type of label to display
    :type display_label_type: str

    :rtype: Union[str, Tuple[str, int], Tuple[str, int, Dict[str, str]]
    """
    return schema_controller_impl.get_schema_attributes(schema_url, display_label_type)
