"""Implementation of all endpoints"""
from typing import Optional, Union
from flask import request  # type: ignore

from schematic_api.models.basic_error import BasicError
from schematic_api.models.dataset import Dataset
from schematic_api.models.datasets_page import DatasetsPage
from schematic_api.models.manifests_page import ManifestsPage

from synapseclient.core.exceptions import (  # type: ignore
    SynapseNoCredentialsError,
    SynapseAuthenticationError,
)

from schematic.store.synapse import SynapseStorage  # type: ignore
from schematic.exceptions import AccessCredentialsError  # type: ignore
from schematic import CONFIG  # type: ignore


def config_handler(asset_view: Optional[str] = None) -> None:
    """Load config file and update asset view if needed
    Args:
        asset_view (str): asset view
    """
    CONFIG.load_config(
        "../../schematic/api/schematic_api/config.yml", asset_view=asset_view
    )


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


def list_storage_project_datasets(
    project_id: str,
) -> tuple[Union[DatasetsPage, BasicError], int]:
    """Attempts to get a list of datasets from a Synapse project

    Args:
        project_id (str): A synapse id

    Returns:
        tuple[Union[DatasetsPage, BasicError], int]: A tuple
          The first item is either the datasets or an error object
          The second item is the response status
    """

    try:
        datasets = []
        datasets.append(Dataset(name="dataset-1"))
        datasets.append(Dataset(name="dataset-2"))
        datasets.append(Dataset(name="dataset-3"))

        page = DatasetsPage(
            number=0,
            size=100,
            total_elements=len(datasets),
            total_pages=1,
            has_next=False,
            has_previous=False,
            datasets=datasets,
        )
        res: Union[DatasetsPage, BasicError] = page
        status = 200

    except SynapseAuthenticationError as error:
        status = 401
        res = BasicError("Unauthorized error", status, str(error))

    # except DoesNotExist:
    #     status = 404
    #     res = BasicError("The specified resource was not found", status)

    except Exception as error:  # pylint: disable=broad-exception-caught
        status = 500
        res = BasicError("Internal error", status, str(error))

    return res, status


def list_storage_project_manifests(
    project_id: str, asset_view: str
) -> tuple[Union[ManifestsPage, BasicError], int]:
    """Attempts to get a list of manifests from a Synapse project

    Args:
        project_id (str): A Synapse id
        asset_view (str): A Synapse id

    Returns:
        tuple[Union[ManifestsPage, BasicError], int]: A tuple
          The first item is either the manifests or an error object
          The second item is the response status
    """
    # load config
    config_handler(asset_view=asset_view)

    # get access token
    bearer_token = get_access_token()

    # load token to synapse storage
    try:
        store = SynapseStorage(access_token=bearer_token)
        lst_storage_projects = store.getProjectManifests(projectId=project_id)

        page = ManifestsPage(
            number=0,
            size=100,
            total_elements=len(lst_storage_projects),
            total_pages=1,
            has_next=False,
            has_previous=False,
            manifests=lst_storage_projects,
        )
        res: Union[ManifestsPage, BasicError] = page
        status = 200

    except SynapseNoCredentialsError as error:
        status = 401
        res = BasicError(
            "Missing or invalid Synapse credentials error", status, str(error)
        )

    except SynapseAuthenticationError as error:
        status = 403
        res = BasicError("Forbidden Synapse access error", status, str(error))

    except AccessCredentialsError as error:
        status = 404
        res = BasicError("Synapse entity access error", status, str(error))

    except Exception as error:  # pylint: disable=broad-exception-caught
        status = 500
        res = BasicError("Internal error", status, str(error))

    return res, status
