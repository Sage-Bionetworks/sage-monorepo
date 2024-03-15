"""Manifest generation functions"""
# pylint: disable=too-many-arguments

from schematic import CONFIG  # type: ignore
from schematic.manifest.generator import ManifestGenerator  # type: ignore
from schematic.utils.schema_utils import DisplayLabelType  # type: ignore

from schematic_api.models.basic_error import BasicError
from schematic_api.models.google_sheet_links import GoogleSheetLinks
from schematic_api.controllers.utils import (
    handle_exceptions,
    get_access_token,
    download_schema_file_as_jsonld,
    InvalidValueError,
)


@handle_exceptions
def generate_excel_manifest(
    schema_url: str,
    dataset_id: str | None,
    add_annotations: bool,
    manifest_title: str,
    data_type: str | None,
    display_label_type: DisplayLabelType,
    use_strict_validation: bool,
    asset_view_id: str | None,
) -> tuple[str | BasicError, int]:
    """Generates a manifest in excel form

    Args:
        schema_url (str): The URL of the schema
        dataset_id (str | None): Use this to get the existing manifest in the dataset.
          Must be of same type as the data_type
        add_annotations (bool): Whether or not annotatiosn get added to the manifest
        manifest_title (str): Title of the manifest
        data_type (str | None): The datatype of the manifest to generate
        display_label_type (DisplayLabelType): The type of label to use as display
        use_strict_validation (bool): Whether or not to use google sheet strict validation
        asset_view_id (str): ID of the asset view

    Returns:
        tuple[str | BasicError], int]: A tuple
           The first item is a path to an manifest in Excel form or an error object
           The second item is the response status
    """
    access_token = get_access_token()
    if asset_view_id:
        CONFIG.synapse_master_fileview_id = asset_view_id
    schema_path = download_schema_file_as_jsonld(schema_url)
    manifest_path_list = ManifestGenerator.create_manifests(
        path_to_data_model=schema_path,
        output_format="excel",
        data_types=[data_type],
        title=manifest_title,
        access_token=access_token,
        dataset_ids=[dataset_id],
        strict=use_strict_validation,
        use_annotations=add_annotations,
        data_model_labels=display_label_type,
    )
    manifest = manifest_path_list[0]
    assert isinstance(manifest, str)
    result: str | BasicError = manifest
    status = 200
    return result, status


@handle_exceptions
def generate_google_sheet_manifests(
    schema_url: str,
    add_annotations: bool,
    dataset_id_array: list[str] | None,
    manifest_title: str | None,
    data_type_array: list[str] | None,
    display_label_type: DisplayLabelType = "class_label",
    use_strict_validation: bool = True,
    asset_view_id: str | None = None,
    generate_all_manifests: bool = False,
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
        if dataset_id_array:
            raise InvalidValueError(
                "When generate_all_manifests is True dataset_id_array must be None",
                {"dataset_id_array": dataset_id_array},
            )
        if data_type_array:
            raise InvalidValueError(
                "When generate_all_manifests is True data_type_array must be None",
                {"data_type_array": data_type_array},
            )
        data_type_array = ["all manifests"]

    else:
        if not data_type_array:
            raise InvalidValueError(
                (
                    "When generate_all_manifests is False data_type_array must be a list with "
                    "at least one item"
                ),
                {"data_type_array": data_type_array},
            )
        if dataset_id_array and len(dataset_id_array) != len(data_type_array):
            raise InvalidValueError(
                (
                    "When generate_all_manifests is False data_type_array and dataset_id_array "
                    "must both lists with the same length"
                ),
                {
                    "data_type_array": data_type_array,
                    "dataset_id_array": dataset_id_array,
                },
            )

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
