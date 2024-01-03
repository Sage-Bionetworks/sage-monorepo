"""Implementation of all endpoints"""
import os
from typing import Optional, Union, Callable, Any


import pandas as pd
from schematic.store.synapse import SynapseStorage, ManifestDownload, load_df  # type: ignore
from schematic import CONFIG  # type: ignore

from schematic_api.models.basic_error import BasicError
from schematic_api.models.dataset_metadata import DatasetMetadata
from schematic_api.models.dataset_metadata_array import DatasetMetadataArray
from schematic_api.models.dataset_metadata_page import DatasetMetadataPage
from schematic_api.models.manifest_metadata import ManifestMetadata
from schematic_api.models.manifest_metadata_array import ManifestMetadataArray
from schematic_api.models.manifest_metadata_page import ManifestMetadataPage
from schematic_api.models.project_metadata import ProjectMetadata
from schematic_api.models.project_metadata_array import ProjectMetadataArray
from schematic_api.models.project_metadata_page import ProjectMetadataPage
from schematic_api.models.file_metadata import FileMetadata
from schematic_api.models.file_metadata_array import FileMetadataArray
from schematic_api.models.file_metadata_page import FileMetadataPage
from schematic_api.controllers.utils import handle_exceptions, get_access_token
from schematic_api.controllers.paging import Page


def get_asset_storage_class(asset_type: str) -> Callable:
    """Returns the class associated with the asset type.

    Args:
        asset_type (str): An asset type, such as "synapse".

    Raises:
        ValueError: When the asset_type isn't in the asst_type dictionary

    Returns:
        Callable: A class that has
        - access_token parameter
        - getStorageDatasetsInProject method
        - getProjectManifests method
    """
    asset_type_dict = {"synapse": SynapseStorage}
    asset_type_object = asset_type_dict.get(asset_type)
    if asset_type_object is None:
        msg = f"{asset_type} is not an allowed value: [{list(asset_type_dict.keys())}]"
        raise ValueError(msg)
    return asset_type_object


def get_asset_view_from_schematic(
    asset_type: str,  # pylint: disable=unused-argument
) -> pd.DataFrame:
    """Gets the asset view in pandas.Dataframe form

    Args:
        asset_view_id (str): The d of the asset view
        asset_type (str): The type of asset, ie "synapse"

     Returns:
        pandas.DataFrame: The asset view
    """
    access_token = get_access_token()
    store = SynapseStorage(access_token=access_token)  # type: ignore
    return store.getStorageFileviewTable()


@handle_exceptions
def get_asset_view_json(
    asset_view_id: str, asset_type: str
) -> tuple[Union[str, BasicError], int]:
    """Gets the asset view in json form

    Args:
        asset_view_id (str): The d of the asset view
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        tuple[Union[str, BasicError], int]: A tuple
          The first item is either the fileview or an error object
          The second item is the response status
    """
    CONFIG.synapse_master_fileview_id = asset_view_id
    asset_view = get_asset_view_from_schematic(asset_type)
    result: Union[str, BasicError] = asset_view.to_json()
    status = 200
    return result, status


def get_dataset_file_metadata_from_schematic(
    dataset_id: str,
    asset_type: str,  # pylint: disable=unused-argument
    file_names: Optional[list[str]],
    use_full_file_path: bool,
) -> list[FileMetadata]:
    """Gets a list of datasets from the project

    Args:
        project_id (str): The id for the project
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        list[FileMetadata]: A list of file metadata
    """
    access_token = get_access_token()
    store = SynapseStorage(access_token=access_token)  # type: ignore
    file_tuple_list = store.getFilesInStorageDataset(
        datasetId=dataset_id,
        fileNames=file_names,  # type: ignore
        fullpath=use_full_file_path,
    )
    return [FileMetadata(id=item[0], name=item[1]) for item in file_tuple_list]


@handle_exceptions
def get_dataset_file_metadata_array(
    dataset_id: str,
    asset_type: str,
    asset_view_id: str,
    file_names: Optional[list[str]] = None,
    use_full_file_path: bool = False,
) -> tuple[Union[FileMetadataArray, BasicError], int]:
    """Gets file metadata associated with a dataset

    Args:
        dataset_id (str): The Id for the dataset to get the files from
        asset_view_id (str): The id for the asset view of the project
        asset_type (str): The type of asset, ie "synapse"
        file_names (Optional[list[str]]): An optional list of file names to filter the output by
        use_full_file_path: Whether or not to return the full file path of each file

    Returns:
        tuple[Union[FileMetadataArray, BasicError], int]: A tuple
          The first item is either the file metadata or an error object
          The second item is the response status
    """
    CONFIG.synapse_master_fileview_id = asset_view_id
    file_metadata_list = get_dataset_file_metadata_from_schematic(
        dataset_id, asset_type, file_names, use_full_file_path
    )

    result: Union[FileMetadataArray, BasicError] = FileMetadataArray(file_metadata_list)
    status = 200

    return result, status


@handle_exceptions
def get_dataset_file_metadata_page(  # pylint: disable=too-many-arguments
    dataset_id: str,
    asset_type: str,
    asset_view_id: str,
    file_names: Optional[list[str]] = None,
    use_full_file_path: bool = False,
    page_number: int = 1,
    page_max_items: int = 100_000,
) -> tuple[Union[FileMetadataPage, BasicError], int]:
    """Gets file metadata associated with a dataset

    Args:
        dataset_id (str): The Id for the dataset to get the files from
        asset_view_id (str): The id for the asset view of the project
        asset_type (str): The type of asset, ie "synapse"
        file_names (Optional[list[str]]): An optional list of file names to filter the output by
        use_full_file_path: Whether or not to return the full file path of each file
        page_number (int): The page number the current request is for
        page_max_items (int): The maximum number of items per page

    Returns:
        tuple[Union[FileMetadataPage, BasicError], int]: A tuple
          The first item is either the file metadata or an error object
          The second item is the response status
    """
    CONFIG.synapse_master_fileview_id = asset_view_id
    file_metadata_list = get_dataset_file_metadata_from_schematic(
        dataset_id, asset_type, file_names, use_full_file_path
    )

    page = Page(file_metadata_list, page_number, page_max_items)

    file_page = FileMetadataPage(
        number=page.page_number,
        size=page.page_max_items,
        total_elements=page.total_items,
        total_pages=page.total_pages,
        has_next=page.has_next,
        has_previous=page.has_previous,
        files=page.items,
    )

    result: Union[FileMetadataPage, BasicError] = file_page
    status = 200

    return result, status


def load_manifest_from_synapse_metadata(manifest_data: Any) -> pd.DataFrame:
    """Loads a manifest from a csv file

    Args:
        manifest_data (Any): Manifest metadata from doing syanpseclient.get on a file entity

    Returns:
        pandas.DataFrame: The manifest

    """
    manifest_local_file_path = manifest_data["path"]
    manifest = load_df(manifest_local_file_path)
    os.remove(manifest_local_file_path)
    return manifest


def get_dataset_manifest_from_schematic(
    asset_type: str, dataset_id: str  # pylint: disable=unused-argument
) -> pd.DataFrame:
    """Gets a manifest in pandas.Dataframe format

    Args:
        asset_type (str): The type of asset, ie "synapse"
        manifest_id (str): The unique id for the manifest file
        dataset_id (str): The id of the dataset the manifest is in

    Returns:
        pandas.DataFrame: The manifest
    """
    access_token = get_access_token()
    store = SynapseStorage(access_token=access_token)  # type: ignore
    manifest_data = store.getDatasetManifest(
        datasetId=dataset_id, downloadFile=True, newManifestName="manifest.csv"
    )
    return load_manifest_from_synapse_metadata(manifest_data)


@handle_exceptions
def get_dataset_manifest_json(
    asset_type: str,
    dataset_id: str,
    asset_view_id: str,
) -> tuple[Union[str, BasicError], int]:
    """Gets a manifest in json form

    Args:
        asset_type (str): The type of asset, ie "synapse"
        asset_view_id (str): The id of the asst view the dataset is in
        dataset_id (str): The id of the dataset the manifest is in

    Returns:
        tuple[Union[str, BasicError], int]: A tuple
          The first item is either the manifest or an error object
          The second item is the response status
    """
    CONFIG.synapse_master_fileview_id = asset_view_id
    manifest = get_dataset_manifest_from_schematic(asset_type, dataset_id)
    result: Union[str, BasicError] = manifest.to_json()
    status = 200

    return result, status


def get_manifest_from_schematic(
    asset_type: str, manifest_id: str  # pylint: disable=unused-argument
) -> pd.DataFrame:
    """Gets a manifest in pandas.Dataframe format

    Args:
        asset_type (str): The type of asset, ie "synapse"
        manifest_id (str): The unique id for the manifest file

    Returns:
        pandas.DataFrame: The manifest
    """
    access_token = get_access_token()
    store = SynapseStorage.login(access_token=access_token)
    manifest_download = ManifestDownload(store, manifest_id)
    manifest_data = ManifestDownload.download_manifest(
        manifest_download, "manifest.csv"
    )
    return load_manifest_from_synapse_metadata(manifest_data)


@handle_exceptions
def get_manifest_json(
    asset_type: str, manifest_id: str
) -> tuple[Union[str, BasicError], int]:
    """Gets a manifest in json form

    Args:
        asset_type (str): The type of asset, ie "synapse"
        manifest_id (str): The unique id for the manifest file

    Returns:
        tuple[Union[str, BasicError], int]: A tuple
          The first item is either the manifest or an error object
          The second item is the response status
    """
    manifest = get_manifest_from_schematic(asset_type, manifest_id)
    result: Union[str, BasicError] = manifest.to_json()
    status = 200

    return result, status


def get_project_dataset_metadata_from_schematic(
    project_id: str, asset_type: str  # pylint: disable=unused-argument
) -> list[DatasetMetadata]:
    """Gets a list of dataset metadata from the project

    Args:
        project_id (str): The id for the project
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        list[DatasetMetadata]: A list of dataset metadata
    """
    access_token = get_access_token()
    store = SynapseStorage(access_token=access_token)  # type: ignore
    tuples = store.getStorageDatasetsInProject(projectId=project_id)  # type: ignore
    return [DatasetMetadata(id=item[0], name=item[1]) for item in tuples]


@handle_exceptions
def get_project_dataset_metadata_array(
    project_id: str, asset_type: str, asset_view_id: str
) -> tuple[Union[DatasetMetadataArray, BasicError], int]:
    """Creates a list of dataset metadata from the project

    Args:
        project_id (str): The Id for the project to get datasets from
        asset_view_id (str): The id for the asset view of the project
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        tuple[Union[DatasetMetadataArray, BasicError], int]: A tuple
          The first item is either the dataset metadata or an error object
          The second item is the response status
    """

    CONFIG.synapse_master_fileview_id = asset_view_id
    dataset_metadata_list = get_project_dataset_metadata_from_schematic(
        project_id, asset_type
    )
    result: Union[DatasetMetadataArray, BasicError] = DatasetMetadataArray(
        dataset_metadata_list
    )
    status = 200
    return result, status


@handle_exceptions
def get_project_dataset_metadata_page(
    project_id: str,
    asset_type: str,
    asset_view_id: str,
    page_number: int = 1,
    page_max_items: int = 100_000,
) -> tuple[Union[DatasetMetadataPage, BasicError], int]:
    """Creates a page of dataset metadata from the project

    Args:
        project_id (str): The Id for the project to get datasets from
        asset_view_id (str): The id for the asset view of the project
        asset_type (str): The type of asset, ie "synapse"
        page_number (int): The page number the current request is for
        page_max_items (int): The maximum number of items per page

    Returns:
        tuple[Union[DatasetMetadataPage, BasicError], int]: A tuple
          The first item is either the dataset metadata or an error object
          The second item is the response status
    """
    # pylint: disable=duplicate-code

    CONFIG.synapse_master_fileview_id = asset_view_id
    dataset_metadata_list = get_project_dataset_metadata_from_schematic(
        project_id, asset_type
    )
    page = Page(dataset_metadata_list, page_number, page_max_items)

    dataset_page = DatasetMetadataPage(
        number=page.page_number,
        size=page.page_max_items,
        total_elements=page.total_items,
        total_pages=page.total_pages,
        has_next=page.has_next,
        has_previous=page.has_previous,
        datasets=page.items,
    )

    result: Union[DatasetMetadataPage, BasicError] = dataset_page
    status = 200

    return result, status


def get_project_manifest_metadata_from_schematic(
    project_id: str,
    asset_type: str,  # pylint: disable=unused-argument
) -> list[ManifestMetadata]:
    """Gets manifest metadata from the project

    Args:
        project_id (str): The id for the project
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        list[ManifestMetadata]: A list of manifest metadata
    """
    access_token = get_access_token()
    store = SynapseStorage(access_token=access_token)  # type: ignore
    manifest_tuple_list = store.getProjectManifests(projectId=project_id)  # type: ignore
    return [
        ManifestMetadata(
            name=item[1][1],
            id=item[1][0],
            dataset_name=item[0][1],
            dataset_id=item[0][0],
            component_name=item[2][0],
        )
        for item in manifest_tuple_list
    ]


@handle_exceptions
def get_project_manifest_metadata_array(
    project_id: str,
    asset_type: str,
    asset_view_id: str,
) -> tuple[Union[ManifestMetadataArray, BasicError], int]:
    """Gets a list of manifest metadata from a project

    Args:
        project_id (str): The id of the project
        asset_view_id (str): The id of the asset view
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        [Union[tuple[ManifestMetadataArray, BasicError], int]: A tuple
          The first item is either the manifests or an error object
          The second item is the response status
    """
    CONFIG.synapse_master_fileview_id = asset_view_id
    manifest_metadata = get_project_manifest_metadata_from_schematic(
        project_id, asset_type
    )
    result: Union[ManifestMetadataArray, BasicError] = ManifestMetadataArray(
        manifest_metadata
    )
    status = 200
    return result, status


@handle_exceptions
def get_project_manifest_metadata_page(
    project_id: str,
    asset_type: str,
    asset_view_id: str,
    page_number: int = 1,
    page_max_items: int = 100_000,
) -> tuple[Union[ManifestMetadataPage, BasicError], int]:
    """Gets a page of manifest metadata from a project

    Args:
        project_id (str): The id of the project
        asset_view_id (str): The id of the asset view
        asset_type (str): The type of asset, ie "synapse"
        page_number (int): The page number the current request is for
        page_max_items (int): The maximum number of items per page

    Returns:
        [Union[tuple[ManifestMetadataPage, BasicError], int]: A tuple
          The first item is either the manifests or an error object
          The second item is the response status
    """
    # load config
    CONFIG.synapse_master_fileview_id = asset_view_id
    manifest_metadata = get_project_manifest_metadata_from_schematic(
        project_id, asset_type
    )

    page = Page(manifest_metadata, page_number, page_max_items)

    manifest_page = ManifestMetadataPage(
        number=page.page_number,
        size=page.page_max_items,
        total_elements=page.total_items,
        total_pages=page.total_pages,
        has_next=page.has_next,
        has_previous=page.has_previous,
        manifests=page.items,
    )

    result: Union[ManifestMetadataPage, BasicError] = manifest_page
    status = 200

    return result, status


def get_project_metadata_from_schematic(
    asset_type: str,  # pylint: disable=unused-argument
) -> list[ProjectMetadata]:
    """Gets a list of projects

    Args:
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        list[ProjectMetadata]: A list of project metadata
    """
    access_token = get_access_token()
    store = SynapseStorage(access_token=access_token)  # type: ignore
    metadata_tuple_list = store.getStorageProjects()  # type: ignore
    return [ProjectMetadata(id=item[0], name=item[1]) for item in metadata_tuple_list]


@handle_exceptions
def get_project_metadata_array(
    asset_view_id: str,
    asset_type: str,
) -> tuple[Union[ProjectMetadataArray, BasicError], int]:
    """Gets a list of project metadata the user has access to

    Args:
        asset_view_id (str): The id for the asset view of the project
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        tuple[Union[ProjectMetadataArray, BasicError], int]: A tuple
          The first item is either the projects or an error object
          The second item is the response status
    """

    CONFIG.synapse_master_fileview_id = asset_view_id
    project_metadata = get_project_metadata_from_schematic(asset_type)
    result: Union[ProjectMetadataArray, BasicError] = ProjectMetadataArray(
        project_metadata
    )
    status = 200
    return result, status


@handle_exceptions
def get_project_metadata_page(
    asset_view_id: str,
    asset_type: str,
    page_number: int = 1,
    page_max_items: int = 100_000,
) -> tuple[Union[ProjectMetadataPage, BasicError], int]:
    """Gets a list of project metadata the user has access to

    Args:
        asset_view_id (str): The id for the asset view of the project
        asset_type (str): The type of asset, ie "synapse"
        page_number (int): The page number the current request is for
        page_max_items (int): The maximum number of items per page

    Returns:
        tuple[Union[ProjectMetadataPage, BasicError], int]: A tuple
          The first item is either the projects or an error object
          The second item is the response status
    """

    CONFIG.synapse_master_fileview_id = asset_view_id
    project_metadata = get_project_metadata_from_schematic(asset_type)
    page = Page(project_metadata, page_number, page_max_items)
    manifest_page = ProjectMetadataPage(
        number=page.page_number,
        size=page.page_max_items,
        total_elements=page.total_items,
        total_pages=page.total_pages,
        has_next=page.has_next,
        has_previous=page.has_previous,
        projects=page.items,
    )
    result: Union[ProjectMetadataPage, BasicError] = manifest_page
    status = 200
    return result, status
