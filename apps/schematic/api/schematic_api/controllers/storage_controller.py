import connexion
import six
from typing import Dict
from typing import Tuple
from typing import Union

from schematic_api.models.basic_error import BasicError  # noqa: E501
from schematic_api import util


def list_storage_project_datasets(project_id):  # noqa: E501
    """Gets all datasets in folder under a given storage project that the current user has access to.

    Gets all datasets in folder under a given storage project that the current user has access to. # noqa: E501

    :param project_id: The Synapse ID of a storage project.
    :type project_id: str

    :rtype: Union[None, Tuple[None, int], Tuple[None, int, Dict[str, str]]
    """
    return 'do some magic!'
