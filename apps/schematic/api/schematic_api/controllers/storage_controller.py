import connexion
import six
from typing import Dict
from typing import Tuple
from typing import Union

from schematic_api.models.asset_type import AssetType  # noqa: E501
from schematic_api.models.basic_error import BasicError  # noqa: E501
from schematic_api.models.dataset_metadata_array import (
    DatasetMetadataArray,
)  # noqa: E501
from schematic_api.models.dataset_metadata_page import DatasetMetadataPage  # noqa: E501
from schematic_api.models.file_metadata_array import FileMetadataArray  # noqa: E501
from schematic_api.models.file_metadata_page import FileMetadataPage  # noqa: E501
from schematic_api.models.manifest_metadata_array import (
    ManifestMetadataArray,
)  # noqa: E501
from schematic_api.models.manifest_metadata_page import (
    ManifestMetadataPage,
)  # noqa: E501
from schematic_api.models.project_metadata_array import (
    ProjectMetadataArray,
)  # noqa: E501
from schematic_api.models.project_metadata_page import ProjectMetadataPage  # noqa: E501
from schematic_api import util
from schematic_api.controllers import storage_controller_impl


def get_asset_view_csv(asset_view_id, asset_type):  # noqa: E501
    """Gets the asset view table in csv file form

    Gets the asset view table in csv file form # noqa: E501

    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str
    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes

    :rtype: Union[str, Tuple[str, int], Tuple[str, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.get_asset_view_csv(asset_view_id, asset_type)


def get_asset_view_json(asset_view_id, asset_type):  # noqa: E501
    """Gets the asset view table in json form

    Gets the asset view table in json form # noqa: E501

    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str
    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes

    :rtype: Union[object, Tuple[object, int], Tuple[object, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.get_asset_view_json(asset_view_id, asset_type)


def get_dataset_file_metadata_array(
    dataset_id, asset_type, asset_view_id, file_names=None, use_full_file_path=None
):  # noqa: E501
    """Gets all files associated with a dataset.

    Gets all files associated with a dataset. # noqa: E501

    :param dataset_id: The ID of a dataset.
    :type dataset_id: str
    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes
    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str
    :param file_names: A list of file names used to filter the output.
    :type file_names: List[str]
    :param use_full_file_path: Whether or not to return the full path of output, or just the basename.
    :type use_full_file_path: bool

    :rtype: Union[FileMetadataArray, Tuple[FileMetadataArray, int], Tuple[FileMetadataArray, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.get_dataset_file_metadata_array(
        dataset_id, asset_type, asset_view_id, file_names, use_full_file_path
    )


def get_dataset_file_metadata_page(
    dataset_id,
    asset_type,
    asset_view_id,
    file_names=None,
    use_full_file_path=None,
    page_number=None,
    page_max_items=None,
):  # noqa: E501
    """Gets all files associated with a dataset.

    Gets all files associated with a dataset. # noqa: E501

    :param dataset_id: The ID of a dataset.
    :type dataset_id: str
    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes
    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str
    :param file_names: A list of file names used to filter the output.
    :type file_names: List[str]
    :param use_full_file_path: Whether or not to return the full path of output, or just the basename.
    :type use_full_file_path: bool
    :param page_number: The page number to get for a paginated query
    :type page_number: int
    :param page_max_items: The maximum number of items per page (up to 100,000) for paginated endpoints
    :type page_max_items: int

    :rtype: Union[FileMetadataPage, Tuple[FileMetadataPage, int], Tuple[FileMetadataPage, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.get_dataset_file_metadata_page(
        dataset_id,
        asset_type,
        asset_view_id,
        file_names,
        use_full_file_path,
        page_number,
        page_max_items,
    )


def get_dataset_manifest_csv(asset_type, dataset_id, asset_view_id):  # noqa: E501
    """Gets the manifest in csv form

    Gets the manifest in csv form # noqa: E501

    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes
    :param dataset_id: The ID of a dataset.
    :type dataset_id: str
    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str

    :rtype: Union[str, Tuple[str, int], Tuple[str, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.get_dataset_manifest_csv(
        asset_type, dataset_id, asset_view_id
    )


def get_dataset_manifest_json(asset_type, dataset_id, asset_view_id):  # noqa: E501
    """Gets the manifest in json form

    Gets the manifest in json form # noqa: E501

    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes
    :param dataset_id: The ID of a dataset.
    :type dataset_id: str
    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str

    :rtype: Union[object, Tuple[object, int], Tuple[object, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.get_dataset_manifest_json(
        asset_type, dataset_id, asset_view_id
    )


def get_manifest_csv(asset_type, manifest_id):  # noqa: E501
    """Gets the manifest in csv form

    Gets the manifest in csv form # noqa: E501

    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes
    :param manifest_id: ID of a manifest
    :type manifest_id: str

    :rtype: Union[str, Tuple[str, int], Tuple[str, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.get_manifest_csv(asset_type, manifest_id)


def get_manifest_json(asset_type, manifest_id):  # noqa: E501
    """Gets the manifest in json form

    Gets the manifest in json form # noqa: E501

    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes
    :param manifest_id: ID of a manifest
    :type manifest_id: str

    :rtype: Union[object, Tuple[object, int], Tuple[object, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.get_manifest_json(asset_type, manifest_id)


def get_project_dataset_metadata_array(
    project_id, asset_type, asset_view_id
):  # noqa: E501
    """Gets all dataset metadata in folder under a given storage project that the current user has access to.

    Gets all dataset meatdata in folder under a given storage project that the current user has access to. # noqa: E501

    :param project_id: The Synapse ID of a storage project.
    :type project_id: str
    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes
    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str

    :rtype: Union[DatasetMetadataArray, Tuple[DatasetMetadataArray, int], Tuple[DatasetMetadataArray, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.get_project_dataset_metadata_array(
        project_id, asset_type, asset_view_id
    )


def get_project_dataset_metadata_page(
    project_id, asset_type, asset_view_id, page_number=None, page_max_items=None
):  # noqa: E501
    """Gets a page of dataset metadata in folder under a given storage project that the current user has access to.

    Gets a page of dataset meatdata in folder under a given storage project that the current user has access to. # noqa: E501

    :param project_id: The Synapse ID of a storage project.
    :type project_id: str
    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes
    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str
    :param page_number: The page number to get for a paginated query
    :type page_number: int
    :param page_max_items: The maximum number of items per page (up to 100,000) for paginated endpoints
    :type page_max_items: int

    :rtype: Union[DatasetMetadataPage, Tuple[DatasetMetadataPage, int], Tuple[DatasetMetadataPage, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.get_project_dataset_metadata_page(
        project_id, asset_type, asset_view_id, page_number, page_max_items
    )


def get_project_manifest_metadata_array(
    project_id, asset_type, asset_view_id
):  # noqa: E501
    """Gets all manifests in a project folder that users have access to

    Gets all manifests in a project folder that the current user has access to. # noqa: E501

    :param project_id: The Synapse ID of a storage project.
    :type project_id: str
    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes
    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str

    :rtype: Union[ManifestMetadataArray, Tuple[ManifestMetadataArray, int], Tuple[ManifestMetadataArray, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.get_project_manifest_metadata_array(
        project_id, asset_type, asset_view_id
    )


def get_project_manifest_metadata_page(
    project_id, asset_type, asset_view_id, page_number=None, page_max_items=None
):  # noqa: E501
    """Gets all manifests in a project folder that users have access to

    Gets all manifests in a project folder that the current user has access to. # noqa: E501

    :param project_id: The Synapse ID of a storage project.
    :type project_id: str
    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes
    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str
    :param page_number: The page number to get for a paginated query
    :type page_number: int
    :param page_max_items: The maximum number of items per page (up to 100,000) for paginated endpoints
    :type page_max_items: int

    :rtype: Union[ManifestMetadataPage, Tuple[ManifestMetadataPage, int], Tuple[ManifestMetadataPage, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.get_project_manifest_metadata_page(
        project_id, asset_type, asset_view_id, page_number, page_max_items
    )


def get_project_metadata_array(asset_view_id, asset_type):  # noqa: E501
    """Gets all storage projects the current user has access to.

    Gets all storage projects the current user has access to. # noqa: E501

    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str
    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes

    :rtype: Union[ProjectMetadataArray, Tuple[ProjectMetadataArray, int], Tuple[ProjectMetadataArray, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.get_project_metadata_array(asset_view_id, asset_type)


def get_project_metadata_page(
    asset_view_id, asset_type, page_number=None, page_max_items=None
):  # noqa: E501
    """Gets all storage projects the current user has access to.

    Gets all storage projects the current user has access to. # noqa: E501

    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str
    :param asset_type: Type of asset, such as Synapse
    :type asset_type: dict | bytes
    :param page_number: The page number to get for a paginated query
    :type page_number: int
    :param page_max_items: The maximum number of items per page (up to 100,000) for paginated endpoints
    :type page_max_items: int

    :rtype: Union[ProjectMetadataPage, Tuple[ProjectMetadataPage, int], Tuple[ProjectMetadataPage, int, Dict[str, str]]
    """
    if connexion.request.is_json:
        asset_type = AssetType.from_dict(connexion.request.get_json())  # noqa: E501
    return storage_controller_impl.get_project_metadata_page(
        asset_view_id, asset_type, page_number, page_max_items
    )
