import connexion
import six
from typing import Dict
from typing import Tuple
from typing import Union

from schematic_api.models.basic_error import BasicError  # noqa: E501
from schematic_api import util
from schematic_api.controllers import versions_controller_impl


def get_schematic_version():  # noqa: E501
    """Gets the version of the schematic library currently used by the API

    Gets the version of the schematic library currently used by the API # noqa: E501


    :rtype: Union[str, Tuple[str, int], Tuple[str, int, Dict[str, str]]
    """
    return versions_controller_impl.get_schematic_version()
