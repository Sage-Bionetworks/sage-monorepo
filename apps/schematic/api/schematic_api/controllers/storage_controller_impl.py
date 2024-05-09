"""Implementation of all endpoints"""
import os
from typing import Callable
import tempfile

import pandas as pd
import synapseclient  # type: ignore
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
from schematic_api.controllers.utils import (
    SYNAPSE_CACHE_PATH,
    handle_exceptions,
    get_access_token,
    purge_synapse_cache,
)
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


def get_store(
    asset_type: str,  # pylint: disable=unused-argument
) -> SynapseStorage:
    """Creates a SynapseStorage class and purges its synapse cache

    Args:
        asset_type (str): The type of storage class (will be used in the future)

    Returns:
        SynapseStorage: A synapse storage class
    """
    access_token = get_access_token()
    store = SynapseStorage(
        access_token=access_token, synapse_cache_path=SYNAPSE_CACHE_PATH
    )
    purge_synapse_cache(store)
    return store


def get_asset_view_from_schematic(asset_type: str) -> pd.DataFrame:
    """Gets the asset view in pandas.Dataframe form

    Args:
        asset_view_id (str): The d of the asset view
        asset_type (str): The type of asset, ie "synapse"

     Returns:
        pandas.DataFrame: The asset view
    """
    store = get_store(asset_type)
    return store.getStorageFileviewTable()


@handle_exceptions
def get_asset_view_csv(
    asset_view_id: str, asset_type: str
) -> tuple[str | BasicError, int]:
    """Gets the asset view in csv form

    Args:
        asset_view_id (str): The id of the asset view
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        tuple[str | BasicError, int]: A tuple
          The first item is either the the path of the file or an error object
          The second item is the response status
    """
    CONFIG.synapse_master_fileview_id = asset_view_id
    asset_view = get_asset_view_from_schematic(asset_type)
    with tempfile.NamedTemporaryFile(
        delete=False, suffix=".asset_view.csv"
    ) as tmp_file:
        export_path = tmp_file.name
        asset_view.to_csv(tmp_file.name, index=False)
    return export_path, 200


@handle_exceptions
def get_asset_view_json(
    asset_view_id: str, asset_type: str
) -> tuple[str | BasicError, int]:
    """Gets the asset view in json form

    Args:
        asset_view_id (str): The id of the asset view
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        tuple[str | BasicError, int]: A tuple
          The first item is either the fileview or an error object
          The second item is the response status
    """
    CONFIG.synapse_master_fileview_id = asset_view_id
    asset_view = get_asset_view_from_schematic(asset_type)
    result: str | BasicError = asset_view.to_json()
    status = 200
    return result, status


def get_dataset_file_metadata_from_schematic(
    dataset_id: str,
    asset_type: str,
    file_names: list[str] | None,
    use_full_file_path: bool,
) -> list[FileMetadata]:
    """Gets a list of datasets from the project

    Args:
        dataset_id (str): The Id for the dataset to get the files from
        asset_type (str): The type of asset, ie "synapse"
        file_names: (list[str] | None): An optional list of file names to filter the output by
        use_full_file_path (str): Whether or not to return the full file path of each file

    Returns:
        list[FileMetadata]: A list of file metadata
    """
    store = get_store(asset_type)
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
    file_names: list[str] | None = None,
    use_full_file_path: bool = False,
) -> tuple[FileMetadataArray | BasicError, int]:
    """Gets file metadata associated with a dataset

    Args:
        dataset_id (str): The Id for the dataset to get the files from
        asset_view_id (str): The id for the asset view of the project
        asset_type (str): The type of asset, ie "synapse"
        file_names (Optional[list[str]]): An optional list of file names to filter the output by
        use_full_file_path: Whether or not to return the full file path of each file

    Returns:
        tuple[FileMetadataArray | BasicError, int]: A tuple
          The first item is either the file metadata or an error object
          The second item is the response status
    """
    CONFIG.synapse_master_fileview_id = asset_view_id
    file_metadata_list = get_dataset_file_metadata_from_schematic(
        dataset_id, asset_type, file_names, use_full_file_path
    )

    result: FileMetadataArray | BasicError = FileMetadataArray(file_metadata_list)
    status = 200

    return result, status


@handle_exceptions
def get_dataset_file_metadata_page(  # pylint: disable=too-many-arguments
    dataset_id: str,
    asset_type: str,
    asset_view_id: str,
    file_names: list[str] | None = None,
    use_full_file_path: bool = False,
    page_number: int = 1,
    page_max_items: int = 100_000,
) -> tuple[FileMetadataPage | BasicError, int]:
    """Gets file metadata associated with a dataset

    Args:
        dataset_id (str): The Id for the dataset to get the files from
        asset_view_id (str): The id for the asset view of the project
        asset_type (str): The type of asset, ie "synapse"
        file_names (list[str] | None): An optional list of file names to filter the output by
        use_full_file_path: Whether or not to return the full file path of each file
        page_number (int): The page number the current request is for
        page_max_items (int): The maximum number of items per page

    Returns:
        tuple[FileMetadataPage | BasicError, int]: A tuple
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

    result: FileMetadataPage | BasicError = file_page
    status = 200

    return result, status


def load_manifest_from_synapse_metadata(
    manifest_data: synapseclient.File,
) -> pd.DataFrame:
    """Loads a manifest from a csv file

    Args:
        manifest_data (synapseclient.File):
          Manifest metadata from doing syanpseclient.get on a file entity

    Returns:
        pandas.DataFrame: The manifest

    """
    manifest_local_file_path = manifest_data["path"]
    manifest = load_df(manifest_local_file_path)
    os.remove(manifest_local_file_path)
    return manifest


def get_dataset_manifest_from_schematic(
    asset_type: str, dataset_id: str
) -> pd.DataFrame:
    """Gets a manifest in pandas.Dataframe format

    Args:
        asset_type (str): The type of asset, ie "synapse"
        manifest_id (str): The unique id for the manifest file
        dataset_id (str): The id of the dataset the manifest is in

    Returns:
        pandas.DataFrame: The manifest
    """
    store = get_store(asset_type)
    manifest_data = store.getDatasetManifest(
        datasetId=dataset_id, downloadFile=True, newManifestName="manifest.csv"
    )
    assert isinstance(manifest_data, synapseclient.File)
    return load_manifest_from_synapse_metadata(manifest_data)


@handle_exceptions
def get_dataset_manifest_csv(
    asset_type: str,
    dataset_id: str,
    asset_view_id: str,
) -> tuple[str | BasicError, int]:
    """Gets a manifest in csv file form

    Args:
        asset_type (str): The type of asset, ie "synapse"
        asset_view_id (str): The id of the asst view the dataset is in
        dataset_id (str): The id of the dataset the manifest is in

    Returns:
        tuple[str | BasicError, int]: A tuple
          The first item is either the path of the manifest or an error object
          The second item is the response status
    """
    CONFIG.synapse_master_fileview_id = asset_view_id
    manifest = get_dataset_manifest_from_schematic(asset_type, dataset_id)
    with tempfile.NamedTemporaryFile(delete=False, suffix=".manifest.csv") as tmp_file:
        export_path = tmp_file.name
        manifest.to_csv(tmp_file.name, index=False)
    return export_path, 200


@handle_exceptions
def get_dataset_manifest_json(
    asset_type: str,
    dataset_id: str,
    asset_view_id: str,
) -> tuple[str | BasicError, int]:
    """Gets a manifest in json form

    Args:
        asset_type (str): The type of asset, ie "synapse"
        asset_view_id (str): The id of the asst view the dataset is in
        dataset_id (str): The id of the dataset the manifest is in

    Returns:
        tuple[str | BasicError, int]: A tuple
          The first item is either the manifest or an error object
          The second item is the response status
    """
    CONFIG.synapse_master_fileview_id = asset_view_id
    manifest = get_dataset_manifest_from_schematic(asset_type, dataset_id)
    result: str | BasicError = manifest.to_json()
    status = 200

    return result, status


def get_manifest_from_schematic(asset_type: str, manifest_id: str) -> pd.DataFrame:
    """Gets a manifest in pandas.Dataframe format

    Args:
        asset_type (str): The type of asset, ie "synapse"
        manifest_id (str): The unique id for the manifest file

    Returns:
        pandas.DataFrame: The manifest
    """
    # The storage object isn't needed but this purges the synapse cache
    get_store(asset_type)
    access_token = get_access_token()
    synapse = SynapseStorage.login(access_token=access_token)
    manifest_download = ManifestDownload(synapse, manifest_id)
    manifest_data = ManifestDownload.download_manifest(
        manifest_download, "manifest.csv"
    )
    assert isinstance(manifest_data, synapseclient.File)
    return load_manifest_from_synapse_metadata(manifest_data)


@handle_exceptions
def get_manifest_csv(asset_type: str, manifest_id: str) -> tuple[str | BasicError, int]:
    """Gets a manifest in json form

    Args:
        asset_type (str): The type of asset, ie "synapse"
        manifest_id (str): The unique id for the manifest file

    Returns:
        tuple[str | BasicError, int]: A tuple
          The first item is either the path to the manifest or an error object
          The second item is the response status
    """
    manifest = get_manifest_from_schematic(asset_type, manifest_id)
    with tempfile.NamedTemporaryFile(delete=False, suffix=".manifest.csv") as tmp_file:
        export_path = tmp_file.name
        manifest.to_csv(tmp_file.name, index=False)
    return export_path, 200


@handle_exceptions
def get_manifest_json(
    asset_type: str, manifest_id: str
) -> tuple[str | BasicError, int]:
    """Gets a manifest in json form

    Args:
        asset_type (str): The type of asset, ie "synapse"
        manifest_id (str): The unique id for the manifest file

    Returns:
        tuple[str | BasicError, int]: A tuple
          The first item is either the manifest or an error object
          The second item is the response status
    """
    manifest = get_manifest_from_schematic(asset_type, manifest_id)
    result: str | BasicError = manifest.to_json()
    status = 200

    return result, status


def get_project_dataset_metadata_from_schematic(
    project_id: str, asset_type: str
) -> list[DatasetMetadata]:
    """Gets a list of dataset metadata from the project

    Args:
        project_id (str): The id for the project
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        list[DatasetMetadata]: A list of dataset metadata
    """
    store = get_store(asset_type)
    tuples = store.getStorageDatasetsInProject(projectId=project_id)
    return [DatasetMetadata(id=item[0], name=item[1]) for item in tuples]


@handle_exceptions
def get_project_dataset_metadata_array(
    project_id: str, asset_type: str, asset_view_id: str
) -> tuple[DatasetMetadataArray | BasicError, int]:
    """Creates a list of dataset metadata from the project

    Args:
        project_id (str): The Id for the project to get datasets from
        asset_view_id (str): The id for the asset view of the project
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        tuple[DatasetMetadataArray | BasicError, int]: A tuple
          The first item is either the dataset metadata or an error object
          The second item is the response status
    """

    CONFIG.synapse_master_fileview_id = asset_view_id
    dataset_metadata_list = get_project_dataset_metadata_from_schematic(
        project_id, asset_type
    )
    result: DatasetMetadataArray | BasicError = DatasetMetadataArray(
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
) -> tuple[DatasetMetadataPage | BasicError, int]:
    """Creates a page of dataset metadata from the project

    Args:
        project_id (str): The Id for the project to get datasets from
        asset_view_id (str): The id for the asset view of the project
        asset_type (str): The type of asset, ie "synapse"
        page_number (int): The page number the current request is for
        page_max_items (int): The maximum number of items per page

    Returns:
        tuple[DatasetMetadataPage | BasicError, int]: A tuple
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

    result: DatasetMetadataPage | BasicError = dataset_page
    status = 200

    return result, status


def get_project_manifest_metadata_from_schematic(
    project_id: str,
    asset_type: str,
) -> list[ManifestMetadata]:
    """Gets manifest metadata from the project

    Args:
        project_id (str): The id for the project
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        list[ManifestMetadata]: A list of manifest metadata
    """
    store = get_store(asset_type)
    manifest_tuple_list = store.getProjectManifests(projectId=project_id)
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
) -> tuple[ManifestMetadataArray | BasicError, int]:
    """Gets a list of manifest metadata from a project

    Args:
        project_id (str): The id of the project
        asset_view_id (str): The id of the asset view
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        tuple[ManifestMetadataArray | BasicError, int]: A tuple
          The first item is either the manifests or an error object
          The second item is the response status
    """
    CONFIG.synapse_master_fileview_id = asset_view_id
    manifest_metadata = get_project_manifest_metadata_from_schematic(
        project_id, asset_type
    )
    result: ManifestMetadataArray | BasicError = ManifestMetadataArray(
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
) -> tuple[ManifestMetadataPage | BasicError, int]:
    """Gets a page of manifest metadata from a project

    Args:
        project_id (str): The id of the project
        asset_view_id (str): The id of the asset view
        asset_type (str): The type of asset, ie "synapse"
        page_number (int): The page number the current request is for
        page_max_items (int): The maximum number of items per page

    Returns:
        tuple[ManifestMetadataPage | BasicError, int]: A tuple
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

    result: ManifestMetadataPage | BasicError = manifest_page
    status = 200

    return result, status


def get_project_metadata_from_schematic(
    asset_type: str,
) -> list[ProjectMetadata]:
    """Gets a list of projects

    Args:
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        list[ProjectMetadata]: A list of project metadata
    """
    store = get_store(asset_type)
    metadata_tuple_list = store.getStorageProjects()
    return [ProjectMetadata(id=item[0], name=item[1]) for item in metadata_tuple_list]


@handle_exceptions
def get_project_metadata_array(
    asset_view_id: str,
    asset_type: str,
) -> tuple[ProjectMetadataArray | BasicError, int]:
    """Gets a list of project metadata the user has access to

    Args:
        asset_view_id (str): The id for the asset view of the project
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        tuple[ProjectMetadataArray, BasicError, int]: A tuple
          The first item is either the projects or an error object
          The second item is the response status
    """

    CONFIG.synapse_master_fileview_id = asset_view_id
    project_metadata = get_project_metadata_from_schematic(asset_type)
    result: ProjectMetadataArray | BasicError = ProjectMetadataArray(project_metadata)
    status = 200
    return result, status


@handle_exceptions
def get_project_metadata_page(
    asset_view_id: str,
    asset_type: str,
    page_number: int = 1,
    page_max_items: int = 100_000,
) -> tuple[ProjectMetadataPage | BasicError, int]:
    """Gets a list of project metadata the user has access to

    Args:
        asset_view_id (str): The id for the asset view of the project
        asset_type (str): The type of asset, ie "synapse"
        page_number (int): The page number the current request is for
        page_max_items (int): The maximum number of items per page

    Returns:
        tuple[ProjectMetadataPage | BasicError, int]: A tuple
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
    result: ProjectMetadataPage | BasicError = manifest_page
    status = 200
    return result, status
