import os
from flask import request
from flask import current_app as app
from schematic_api.models.basic_error import BasicError  # noqa: E501
from schematic_api.models.dataset import Dataset  # noqa: E501
from schematic_api.models.datasets_page import DatasetsPage  # noqa: E501
from schematic.store.synapse import SynapseStorage
from schematic import CONFIG


def config_handler(asset_view:str=None) -> None:
    """Load config file and update asset view if needded
    Args: 
        asset_view (str): asset view 
    """
    CONFIG.load_config("../../schematic/api/schematic_api/config.yml", asset_view=asset_view)

def get_access_token() -> str:
    """Get access token from header
    """
    bearer_token = None
    # Check if the Authorization header is present
    if 'Authorization' in request.headers:
        auth_header = request.headers['Authorization']

        # Ensure the header starts with 'Bearer ' and retrieve the token
        if auth_header.startswith('Bearer '):
            bearer_token = auth_header.split(' ')[1]
    return bearer_token

def list_storage_project_datasets(project_id):  # noqa: E501
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
        res = page
        status = 200
    # except DoesNotExist:
    #     status = 404
    #     res = BasicError("The specified resource was not found", status)
    except Exception as error:
        status = 500
        res = BasicError("Internal error", status, str(error))
    return res, status


def list_storage_project_manifests(project_id, asset_view):
    """List manifests in a given storage project
    """
    # load config 
    config_handler(asset_view=asset_view)

    # get access token
    bearer_token = get_access_token()

    # load token to synapse storage
    store = SynapseStorage(access_token=bearer_token)

    lst_storage_projects = store.getProjectManifests(projectId=project_id)
    return lst_storage_projects
