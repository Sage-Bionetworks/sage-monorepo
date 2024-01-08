"""Manifest generation functions"""
# pylint: disable=too-many-arguments

from typing import Union, BinaryIO
from schematic import CONFIG  # type: ignore
from schematic.manifest.generator import ManifestGenerator  # type: ignore

from schematic_api.models.basic_error import BasicError
from schematic_api.models.google_sheet_links import GoogleSheetLinks
from schematic_api.controllers.utils import (
    handle_exceptions,
    get_access_token,
    download_schema_file_as_jsonld,
)


@handle_exceptions
def generate_excel_manifest(
    schema_url: str,
    dataset_id: str,
    asset_view_id: str,
    node_label: str,
    add_annotations: bool,
    manifest_title: str,
) -> tuple[Union[BinaryIO, BasicError], int]:
    """Generates a manifest in excel form

    Args:
        schema_url (str): _description_
        dataset_id (str): _description_
        asset_view_id (str): _description_
        node_label (str): _description_
        add_annotations (bool): _description_
        manifest_title (str): _description_

    Returns:
        tuple[Union[BinaryIO, BasicError], int]: _description_
    """
    access_token = get_access_token()
    CONFIG.synapse_master_fileview_id = asset_view_id
    schema_path = download_schema_file_as_jsonld(schema_url)

    attempt = ManifestGenerator.create_single_manifest(
        jsonld=schema_path,
        output_format="excel",
        data_type=node_label,
        title=manifest_title,
        access_token=access_token,
        dataset_id=dataset_id,
        use_annotations=add_annotations,
    )
    assert isinstance(attempt, BinaryIO)
    result: Union[BinaryIO, BasicError] = attempt
    status = 200
    return result, status


@handle_exceptions
def generate_google_sheet_manifests(
    schema_url: str,
    dataset_id_array: list[str],
    asset_view_id: str,
    node_label_array: list[str],
    add_annotations: bool,
    manifest_title: str,
    use_strict_validation: bool,
    generate_all_manifests: bool,
) -> tuple[Union[GoogleSheetLinks, BasicError], int]:
    """Generates a list of links to manifets in google sheet form

    Args:
        schema_url (str): The URL of the schema in jsonld form
        dataset_id_array (list[str]): _description_
        asset_view_id (str): _description_
        node_label_array (list[str]): _description_
        add_annotations (bool): _description_
        manifest_title (str): _description_
        use_strict_validation (bool): _description_
        generate_all_manifests (bool): _description_

    Raises:
        ValueError: _description_
        ValueError: _description_

    Returns:
        tuple[Union[GoogleSheetLinks, BasicError], int]: _description_
    """
    access_token = get_access_token()
    CONFIG.synapse_master_fileview_id = asset_view_id
    schema_path = download_schema_file_as_jsonld(schema_url)

    if generate_all_manifests:
        if dataset_id_array or node_label_array:
            raise ValueError(
                "When generate_all_manifests is true don't submit dataset_id_array or "
                "nodel_label_array. "
                "Please check your submission and try again."
            )
        dataset_id_array = ["all_manifests"]

    else:
        if len(dataset_id_array) != len(node_label_array):
            raise ValueError(
                "There is a mismatch in the number of data_types and dataset_id's that "
                "submitted. Please check your submission and try again."
            )

    manifest_links = ManifestGenerator.create_manifests(
        jsonld=schema_path,
        output_format="google_sheet",
        data_types=node_label_array,
        title=manifest_title,
        access_token=access_token,
        dataset_ids=dataset_id_array,
        strict=use_strict_validation,
        use_annotations=add_annotations,
    )
    result = GoogleSheetLinks(manifest_links)
    status = 200
    return result, status
