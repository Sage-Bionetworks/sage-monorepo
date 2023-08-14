import connexion
import six
from typing import Dict
from typing import Tuple
from typing import Union

from schematic_api.models.asset_type import AssetType  # noqa: E501
from schematic_api.models.basic_error import BasicError  # noqa: E501
from schematic_api.models.datasets_page import DatasetsPage  # noqa: E501
from schematic_api.models.files_page import FilesPage  # noqa: E501
from schematic_api.models.manifests_page import ManifestsPage  # noqa: E501
from schematic_api.models.projects_page import ProjectsPage  # noqa: E501
from schematic_api import util
from schematic_api.controllers import storage_controller_impl


def list_dataset_files(
    dataset_id, asset_view_id, asset_type, file_names=None, use_full_file_path=None
):  # noqa: E501
    """Gets all files associated with a dataset.

    Gets all files associated with a dataset. # noqa: E501

    :param dataset_id: The ID of a dataset.
    :type dataset_id: str
    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str
    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes
    :param file_names: A list of file names used to filter the output.
    :type file_names: List[str]
    :param use_full_file_path: Whether or not to return the full path of output, or just the basename.
    :type use_full_file_path: bool

    :rtype: Union[FilesPage, Tuple[FilesPage, int], Tuple[FilesPage, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.list_dataset_files(
        dataset_id, asset_view_id, asset_type, file_names, use_full_file_path
    )


def list_projects(asset_view_id, asset_type):  # noqa: E501
    """Gets all storage projects the current user has access to.

    Gets all storage projects the current user has access to. # noqa: E501

    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str
    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes

    :rtype: Union[ProjectsPage, Tuple[ProjectsPage, int], Tuple[ProjectsPage, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.list_projects(asset_view_id, asset_type)


def list_storage_project_datasets(project_id, asset_view_id, asset_type):  # noqa: E501
    """Gets all datasets in folder under a given storage project that the current user has access to.

    Gets all datasets in folder under a given storage project that the current user has access to. # noqa: E501

    :param project_id: The Synapse ID of a storage project.
    :type project_id: str
    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str
    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes

    :rtype: Union[DatasetsPage, Tuple[DatasetsPage, int], Tuple[DatasetsPage, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.list_storage_project_datasets(
        project_id, asset_view_id, asset_type
    )


def list_storage_project_manifests(project_id, asset_view_id, asset_type):  # noqa: E501
    """Gets all manifests in a project folder that users have access to

    Gets all manifests in a project folder that the current user has access to. # noqa: E501

    :param project_id: The Synapse ID of a storage project.
    :type project_id: str
    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str
    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes

    :rtype: Union[ManifestsPage, Tuple[ManifestsPage, int], Tuple[ManifestsPage, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.list_storage_project_manifests(
        project_id, asset_view_id, asset_type
    )
