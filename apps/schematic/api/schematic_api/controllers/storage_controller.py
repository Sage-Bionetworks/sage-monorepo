import connexion
import six
from typing import Dict
from typing import Tuple
from typing import Union

from schematic_api.models.basic_error import BasicError  # noqa: E501
from schematic_api.models.datasets_page import DatasetsPage  # noqa: E501
from schematic_api.models.manifests_page import ManifestsPage  # noqa: E501
from schematic_api import util
from schematic_api.controllers import storage_controller_impl


def list_storage_project_datasets(project_id):  # noqa: E501
    """Gets all datasets in folder under a given storage project that the current user has access to.

    Gets all datasets in folder under a given storage project that the current user has access to. # noqa: E501

    :param project_id: The Synapse ID of a storage project.
    :type project_id: str

    :rtype: Union[DatasetsPage, Tuple[DatasetsPage, int], Tuple[DatasetsPage, int, Dict[str, str]]
    """
    return storage_controller_impl.list_storage_project_datasets(project_id)


def list_storage_project_manifests(project_id, asset_view):  # noqa: E501
    """Gets all manifests in a project folder that users have access to

    Gets all manifests in a project folder that the current user has access to. # noqa: E501

    :param project_id: The Synapse ID of a storage project.
    :type project_id: str
    :param asset_view: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view: str

    :rtype: Union[ManifestsPage, Tuple[ManifestsPage, int], Tuple[ManifestsPage, int, Dict[str, str]]
    """
    return storage_controller_impl.list_storage_project_manifests(project_id, asset_view)
