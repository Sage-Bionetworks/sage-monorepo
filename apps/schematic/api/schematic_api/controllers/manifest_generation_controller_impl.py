"""Manifest generation functions"""
# pylint: disable=too-many-arguments
import os

from schematic import CONFIG  # type: ignore
from schematic.manifest.generator import ManifestGenerator  # type: ignore
from schematic.utils.schema_utils import DisplayLabelType  # type: ignore
from flask import send_from_directory, Response

from schematic_api.models.basic_error import BasicError
from schematic_api.models.google_sheet_links import GoogleSheetLinks
from schematic_api.controllers.utils import (
    handle_exceptions,
    get_access_token,
    download_schema_file_as_jsonld,
)


@handle_exceptions
def generate_excel_manifest_file(
    schema_url: str,
    dataset_id: str | None,
    add_annotations: bool,
    manifest_title: str | None,
    data_type: str | None,
    display_label_type: DisplayLabelType,
    asset_view_id: str | None,
) -> tuple[str | BasicError, int]:
    """Creates an excel version of the manifest and returns the path

    Args:
        schema_url (str): The URL of the schema
        dataset_id (str | None): Use this to get the existing manifest in the
          dataset
        add_annotations (bool): Whether or not annotations get added to the manifest
        manifest_title (str | None): Title of the manifest
        data_type (str | None): The datatype of the manifest to generate
        display_label_type (DisplayLabelType):
          The type of label to use as display
          Defaults to "class_label"
        asset_view_id (str | None): ID of the asset view

    Returns:
        tuple[str | BasicError, int]: A tuple
           The first item is the path to the excel file or an error object
           The second item is the response status
    """
    if asset_view_id:
        CONFIG.synapse_master_fileview_id = asset_view_id

    access_token = get_access_token()
    path_list = ManifestGenerator.create_manifests(
        path_to_data_model=schema_url,
        output_format="excel",
        data_types=[data_type],
        title=manifest_title,
        access_token=access_token,
        dataset_ids=[dataset_id],
        use_annotations=add_annotations,
        data_model_labels=display_label_type,
    )
    assert len(path_list) == 1
    path = path_list[0]
    assert isinstance(path, str)
    return path, 200


def generate_excel_manifest(
    schema_url: str,
    data_type: str | None,
    add_annotations: bool,
    manifest_title: str | None,
    display_label_type: DisplayLabelType,
    dataset_id: str | None,
    asset_view_id: str | None,
) -> Response | tuple[BasicError, int]:
    """Creates a a flask response for an excel manifest file

    Args:
        schema_url (str): The URL of the schema
        dataset_id (str | None): Use this to get the existing manifest in the
          dataset
        add_annotations (bool): Whether or not annotations get added to the manifest
        manifest_title (str | None): Title of the manifest
        data_type (str | None): The datatype of the manifest to generate
        display_label_type (DisplayLabelType):
          The type of label to use as display
          Defaults to "class_label"
        asset_view_id (str | None): ID of the asset view

    Returns:
        Response | tuple[BasicError, int]:
          Either A repsonse crated by Flask
          or a tuple with a Error and a response status
    """
    result, status = generate_excel_manifest_file(
        schema_url=schema_url,
        dataset_id=dataset_id,
        add_annotations=add_annotations,
        manifest_title=manifest_title,
        data_type=data_type,
        display_label_type=display_label_type,
        asset_view_id=asset_view_id,
    )

    if isinstance(result, BasicError):
        return result, status

    dir_name = os.path.dirname(result)
    file_name = os.path.basename(result)
    mimetype = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    return send_from_directory(
        directory=dir_name,
        path=file_name,
        as_attachment=True,
        mimetype=mimetype,
        max_age=0,
    )


@handle_exceptions
def generate_google_sheet_manifests(
    schema_url: str,
    add_annotations: bool,
    manifest_title: str | None,
    display_label_type: DisplayLabelType,
    use_strict_validation: bool,
    dataset_id_array: list[str] | None,
    data_type_array: list[str] | None,
    asset_view_id: str | None,
    generate_all_manifests: bool,
) -> tuple[GoogleSheetLinks | BasicError, int]:
    """Generates a list of links to manifests in google sheet form

    Args:
        schema_url (str): The URL of the schema
        dataset_id_array (list[str] | None): Use this to get the existing manifests in the
          datasets. Must be of same type as the data_type_array, same order, and same length
        asset_view_id (str | None): ID of the asset view
        data_type_array (list[str] | None): The datatypes of the manifests to generate
        add_annotations (bool): Whether or not annotations get added to the manifest
        manifest_title (str | None): Title of the manifest
        use_strict_validation (bool): Whether or not to use google sheet strict validation
        generate_all_manifests (bool): Will generate a manifest for all data types
        display_label_type (DisplayLabelType):
          The type of label to use as display
          Defaults to "class_label"

    Raises:
        ValueError: When generate_all_manifests is true and either dataset_id_array or
          data_type_array are provided
        ValueError: When generate_all_manifests is false and data_type_array is not provided
        ValueError: When generate_all_manifests is false and dataset_id_array is provided,
          but it doesn't match the length of data_type_array

    Returns:
        tuple[GoogleSheetLinks | BasicError, int]: A tuple
           The first item is either the google sheet links of the manifests or an error object
           The second item is the response status
    """

    if generate_all_manifests:
        data_type_array = ["all manifests"]
    if not data_type_array:
        data_type_array = []

    access_token = get_access_token()
    if asset_view_id:
        CONFIG.synapse_master_fileview_id = asset_view_id
    schema_path = download_schema_file_as_jsonld(schema_url)
    links = ManifestGenerator.create_manifests(
        path_to_data_model=schema_path,
        output_format="google_sheet",
        data_types=data_type_array,
        title=manifest_title,
        access_token=access_token,
        dataset_ids=dataset_id_array,
        strict=use_strict_validation,
        use_annotations=add_annotations,
        data_model_labels=display_label_type,
    )
    result: GoogleSheetLinks | BasicError = GoogleSheetLinks(links)
    status = 200
    return result, status
