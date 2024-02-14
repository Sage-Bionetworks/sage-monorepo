import connexion
import six
from typing import Dict
from typing import Tuple
from typing import Union

from schematic_api.models.basic_error import BasicError  # noqa: E501
from schematic_api import util
from schematic_api.controllers import tangled_tree_controller_impl


def get_tangled_tree_layers(schema_url, figure_type=None):  # noqa: E501
    """Get tangled tree node layers to display for a given data model and figure type

    Get tangled tree node layers to display for a given data model and figure type # noqa: E501

    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param figure_type: Figure type to generate.
    :type figure_type: str

    :rtype: Union[str, Tuple[str, int], Tuple[str, int, Dict[str, str]]
    """
    return tangled_tree_controller_impl.get_tangled_tree_layers(schema_url, figure_type)


def get_tangled_tree_text(schema_url, figure_type=None, text_format=None):  # noqa: E501
    """Get tangled tree plain or higlighted text to display for a given data model, text formatting and figure type

    Get tangled tree plain or higlighted text to display for a given data model, text formatting and figure type # noqa: E501

    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param figure_type: Figure type to generate.
    :type figure_type: str
    :param text_format: Text formatting type.
    :type text_format: str

    :rtype: Union[object, Tuple[object, int], Tuple[object, int, Dict[str, str]]
    """
    return tangled_tree_controller_impl.get_tangled_tree_text(
        schema_url, figure_type, text_format
    )
