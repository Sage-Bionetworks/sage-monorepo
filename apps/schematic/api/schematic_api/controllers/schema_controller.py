import connexion
import six
from typing import Dict
from typing import Tuple
from typing import Union

from schematic_api.models.attributes_page import AttributesPage  # noqa: E501
from schematic_api.models.basic_error import BasicError  # noqa: E501
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
