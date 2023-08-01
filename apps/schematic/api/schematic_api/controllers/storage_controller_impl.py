"""Implementation of all endpoints"""
from typing import Optional, Union, Callable, Any
import os

from flask import request  # type: ignore
from synapseclient.core.exceptions import (  # type: ignore
    SynapseNoCredentialsError,
    SynapseAuthenticationError,
)
from schematic.store.synapse import SynapseStorage  # type: ignore
from schematic.exceptions import AccessCredentialsError  # type: ignore
from schematic import CONFIG  # type: ignore

from schematic_api.models.basic_error import BasicError
from schematic_api.models.dataset import Dataset
from schematic_api.models.datasets_page import DatasetsPage
from schematic_api.models.manifests_page import ManifestsPage
from schematic_api.models.manifest import Manifest


def get_access_token() -> Optional[str]:
    """Get access token from header"""
    bearer_token = None
    # Check if the Authorization header is present
    if "Authorization" in request.headers:
        auth_header = request.headers["Authorization"]

        # Ensure the header starts with 'Bearer ' and retrieve the token
        if auth_header.startswith("Bearer "):
            bearer_token = auth_header.split(" ")[1]
    return bearer_token


def handle_exceptions(endpoint_function: Callable) -> Callable:
    """
    This is designed to be used as a decorator for endpoint functions.
    The endpoint function is called in a try block, and then various
      Synapse and Schematic exceptions are handled and returned as the
      BasicError object.

    Args:
        f (Callable): A function that calls the input function
    """

    def func(*args: Any, **kwargs: Any) -> tuple[Union[Any, BasicError], int]:
        try:
            return endpoint_function(*args, **kwargs)

        except SynapseNoCredentialsError as error:
            status = 401
            res = BasicError(
                "Missing or invalid Synapse credentials error", status, str(error)
            )
            return res, status

        except SynapseAuthenticationError as error:
            status = 401
            res = BasicError("Forbidden Synapse access error", status, str(error))
            return res, status

        except AccessCredentialsError as error:
            status = 403
            res = BasicError("Synapse entity access error", status, str(error))
            return res, status

        except Exception as error:  # pylint: disable=broad-exception-caught
            status = 500
            res = BasicError("Internal error", status, str(error))
            return res, status

    return func


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


def get_asset_view(asset_view_id: str, asset_type: str, return_type: str) -> Any:
    """_summary_

    Args:
        asset_view_id (str): _description_
        asset_type (str): _description_
        return_type (str): _description_

    Raises:
        ValueError: _description_

    Returns:
        Any: _description_
    """
    config_handler(asset_view=asset_view_id)
    access_token = get_access_token()
    asset_type_object = get_asset_storage_class(asset_type)
    store = asset_type_object(access_token=access_token)
    file_view_table_df = store.getStorageFileviewTable()

    if return_type == "json":
        result = file_view_table_df.to_json()
    elif return_type == "csv":
        path = os.getcwd()
        export_path = os.path.join(path, "tests/data/file_view_table.csv")
        result = file_view_table_df.to_csv(export_path, index=False)
    else:
        raise ValueError(f"{return_type} must be one of ['json', 'csv']")

    status = 200
    return result, status


def get_project_datasets(project_id: str, asset_type: str) -> list[tuple[str, str]]:
    """Gets a list of datasets from the project

    Args:
        project_id (str): The id for the project
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        list[tuple(str, str)]: A list of datasets in tuple form
    """
    access_token = get_access_token()
    asset_type_object = get_asset_storage_class(asset_type)
    store = asset_type_object(access_token=access_token)
    return store.getStorageDatasetsInProject(projectId=project_id)


@handle_exceptions
def list_storage_project_datasets(
    project_id: str, asset_view_id: str, asset_type: str
) -> tuple[Union[DatasetsPage, BasicError], int]:
    """Attempts to get a list of datasets from a Synapse project

    Args:
        project_id (str): The Id for the project to get datasets from
        asset_view_id (str): The id for the asset view of the project
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        tuple[Union[DatasetsPage, BasicError], int]: A tuple
          The first item is either the datasets or an error object
          The second item is the response status
    """

    CONFIG.synapse_master_fileview_id = asset_view_id
    dataset_tuples = get_project_datasets(project_id, asset_type)
    datasets = [Dataset(id=item[0], name=item[1]) for item in dataset_tuples]

    page = DatasetsPage(
        number=0,
        size=100,
        total_elements=len(datasets),
        total_pages=1,
        has_next=False,
        has_previous=False,
        datasets=datasets,
    )
    result: Union[DatasetsPage, BasicError] = page
    status = 200

    return result, status


def get_project_manifests(
    project_id: str, asset_type: str
) -> list[tuple[tuple[str, str], tuple[str, str], tuple[str, str]]]:
    """Gets a list of manifests from the project

    Args:
        project_id (str): The id for the project
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        list[tuple[tuple[str, str], tuple[str, str], tuple[str, str]]]: A list of manifests
    """
    access_token = get_access_token()
    asset_type_object = get_asset_storage_class(asset_type)
    store = asset_type_object(access_token=access_token)
    return store.getProjectManifests(projectId=project_id)


@handle_exceptions
def list_storage_project_manifests(
    project_id: str, asset_view_id: str, asset_type: str
) -> tuple[Union[ManifestsPage, BasicError], int]:
    """Attempts to get a list of manifests from a Synapse project

    Args:
        project_id (str): A Synapse id
        asset_view_id (str): A Synapse id
        asset_type (str): The type of asset, ie "synapse"

    Returns:
        tuple[Union[ManifestsPage, BasicError], int]: A tuple
          The first item is either the manifests or an error object
          The second item is the response status
    """
    # load config
    CONFIG.synapse_master_fileview_id = asset_view_id
    project_manifests = get_project_manifests(project_id, asset_type)
    manifests = [
        Manifest(
            name=item[1][1],
            id=item[1][0],
            dataset_name=item[0][1],
            dataset_id=item[0][0],
            component_name=item[2][0],
        )
        for item in project_manifests
    ]

    page = ManifestsPage(
        number=0,
        size=100,
        total_elements=len(manifests),
        total_pages=1,
        has_next=False,
        has_previous=False,
        manifests=manifests,
    )
    result: Union[ManifestsPage, BasicError] = page
    status = 200

    return result, status
