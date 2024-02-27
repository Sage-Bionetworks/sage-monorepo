"""Implementation of manifest validation endpoints"""

# pylint: disable=too-many-locals

from typing import Any

from schematic import CONFIG  # type: ignore
from schematic.models.metadata import MetadataModel  # type: ignore
from schematic.utils.schema_utils import DisplayLabelType  # type: ignore

from schematic_api.models.manifest_validation_result import ManifestValidationResult
from schematic_api.models.basic_error import BasicError
from schematic_api.controllers.utils import (
    handle_exceptions,
    get_access_token,
    download_schema_file_as_jsonld,
    save_manifest_json_string_as_csv,
    save_manifest_csv_string_as_csv,
)


def submit_manifest_with_schematic(  # pylint: disable=too-many-arguments
    schema_path: str,
    manifest_path: str,
    component: str | None,
    dataset_id: str,
    restrict_rules: bool = False,
    storage_method: str = "table_file_and_entities",
    hide_blanks: bool = False,
    table_manipulation_method: str = "replace",
    display_label_type: DisplayLabelType = "class_label",
    table_column_name_style: str = "class_label",
    annotation_key_style: str = "class_label",
) -> str:
    """Submits a manifest csv

    Args:
        schema_path (str): The path to a schema in jsonld form
        manifest_path (str): The path to a manifest in csv form
        component (str | None):
          The component, either schema label, or display label
          See use_schema_label
        dataset_id (str): The id of the dataset to submit the manifest to
        restrict_rules (bool, optional):
          Whether or not to restrict rule to non- great expectations.
          Defaults to False.
        storage_method (str, optional):
          Specify what will be updated.
          Defaults to "table_file_and_entities".
        hide_blanks (bool, optional):
          Whether or not annotations with blank values will be hidden from a
            datasets annotation list.
          Defaults to False.
        table_manipulation_method (str, optional):
          Specify the way the manifest tables should be stored.
          Defaults to "replace".
        display_label_type (DisplayLabelType):
          The type of label to use as display
          Defaults to "class_label"
        annotation_key_style (str): Sets labeling syle for annotation keys.
          class_label: will format the display name as upper camelcase, and strip blacklisted
            characters
          display_label: will strip blacklisted characters including spaces, to retain display label
            formatting while ensuring the label is formatted properly for Synapse annotations.
        table_column_name_style: (str): Sets labeling style for table column names.
          display_name: will use the raw display name as the column name.
          class_label will format the display name as upper camelcase, and strip blacklisted
            characters
          display_label: will strip blacklisted characters including spaces, to retain display label
            formatting.

    Returns:
         str: The id of the manifest
    """
    access_token = get_access_token()
    metadata_model = MetadataModel(
        inputMModelLocation=schema_path,
        inputMModelLocationType="local",
        data_model_labels=display_label_type,
    )
    manifest_id: str = metadata_model.submit_metadata_manifest(
        path_to_json_ld=schema_path,
        manifest_path=manifest_path,
        dataset_id=dataset_id,
        validate_component=component,
        access_token=access_token,
        manifest_record_type=storage_method,
        restrict_rules=restrict_rules,
        hide_blanks=hide_blanks,
        table_manipulation=table_manipulation_method,
        table_column_names=table_column_name_style,
        annotation_keys=annotation_key_style,
    )
    return manifest_id


@handle_exceptions
def submit_manifest_csv(  # pylint: disable=too-many-arguments
    schema_url: str,
    component: str | None,
    dataset_id: str,
    asset_view_id: str,
    body: bytes,
    restrict_rules: bool = False,
    storage_method: str = "table_file_and_entities",
    hide_blanks: bool = False,
    table_manipulation_method: str = "replace",
    display_label_type: DisplayLabelType = "class_label",
    annotation_key_style: str = "class_label",
    table_column_name_style: str = "class_label",
) -> tuple[str | BasicError, int]:
    """Submits a manifest csv in bytes form

    Args:
        schema_url (str): The url to schema the component is in
        component (str | None):
          The component, either schema label, or display label
          See use_schema_label
        dataset_id (str): The id of the dataset to submit the manifest to
        asset_view_id (str): The id of the asset view the dataset is in
        body (bytes): The body of the request, contains the manifest in bytes form
        restrict_rules (bool, optional):
          Whether or not to restrict rule to non- great expectations.
          Defaults to False.
        storage_method (str, optional):
          Specify what will be updated.
          Defaults to "table_file_and_entities".
        hide_blanks (bool, optional):
          Whether or not annotations with blank values will be hidden from a
            datasets annotation list.
          Defaults to False.
        table_manipulation_method (str, optional):
          Specify the way the manifest tables should be stored.
          Defaults to "replace".
        display_label_type (DisplayLabelType):
          The type of label to use as display
          Defaults to "class_label"
        annotation_key_style (str): Sets labeling syle for annotation keys.
          class_label: will format the display name as upper camelcase, and strip blacklisted
            characters
          display_label: will strip blacklisted characters including spaces, to retain display label
            formatting while ensuring the label is formatted properly for Synapse annotations.
        table_column_name_style: (str): Sets labeling style for table column names.
          display_name: will use the raw display name as the column name.
          class_label will format the display name as upper camelcase, and strip blacklisted
            characters
          display_label: will strip blacklisted characters including spaces, to retain display label
            formatting.

    Returns:
         tuple[str | BasicError, int]: A tuple
           The first item is either the id of the manifest or an error object
           The second item is the response status
    """
    CONFIG.synapse_master_fileview_id = asset_view_id
    manifest_path = save_manifest_csv_string_as_csv(body)
    schema_path = download_schema_file_as_jsonld(schema_url)

    result: str | BasicError = submit_manifest_with_schematic(
        schema_path=schema_path,
        manifest_path=manifest_path,
        component=component,
        dataset_id=dataset_id,
        restrict_rules=restrict_rules,
        storage_method=storage_method,
        hide_blanks=hide_blanks,
        table_manipulation_method=table_manipulation_method,
        display_label_type=display_label_type,
        table_column_name_style=table_column_name_style,
        annotation_key_style=annotation_key_style,
    )

    status = 200
    return result, status


@handle_exceptions
def submit_manifest_json(  # pylint: disable=too-many-arguments
    schema_url: str,
    component: str | None,
    dataset_id: str,
    asset_view_id: str,
    restrict_rules: bool = False,
    storage_method: str = "table_file_and_entities",
    hide_blanks: bool = False,
    table_manipulation_method: str = "replace",
    display_label_type: DisplayLabelType = "class_label",
    annotation_key_style: str = "class_label",
    table_column_name_style: str = "class_label",
    body: Any = None,
) -> tuple[str | BasicError, int]:
    """Submits a manifest csv in bytes form

    Args:
        schema_url (str): The url to schema the component is in
        component (str | None):
          The component, either schema label, or display label
          See use_schema_label
        dataset_id (str): The id of the dataset to submit the manifest to
        asset_view_id (str): The id of the asset view the dataset is in
        body (Any): The body of the request.
        restrict_rules (bool, optional):
          Whether or not to restrict rule to non- great expectations.
          Defaults to False.
        storage_method (str, optional):
          Specify what will be updated.
          Defaults to "table_file_and_entities".
        hide_blanks (bool, optional):
          Whether or not annotations with blank values will be hidden from a datasets
            annotation list
          Defaults to False.
        table_manipulation_method (str, optional):
          Specify the way the manifest tables should be stored.
          Defaults to "replace".
        display_label_type (DisplayLabelType):
           The type of label to use as display
           Defaults to "class_label"
        annotation_key_style (str): Sets labeling syle for annotation keys.
          class_label: will format the display name as upper camelcase, and strip blacklisted
            characters
          display_label: will strip blacklisted characters including spaces, to retain display label
            formatting while ensuring the label is formatted properly for Synapse annotations.
        table_column_name_style: (str): Sets labeling style for table column names.
          display_name: will use the raw display name as the column name.
          class_label will format the display name as upper camelcase, and strip blacklisted
            characters
          display_label: will strip blacklisted characters including spaces, to retain display label
            formatting.

    Returns:
        tuple[str | BasicError, int]: A tuple
            The first item is either the id of the manifest or an error object
            The second item is the response status
    """
    CONFIG.synapse_master_fileview_id = asset_view_id
    manifest_path = save_manifest_json_string_as_csv(body)
    schema_path = download_schema_file_as_jsonld(schema_url)

    result: str | BasicError = submit_manifest_with_schematic(
        schema_path=schema_path,
        manifest_path=manifest_path,
        component=component,
        dataset_id=dataset_id,
        restrict_rules=restrict_rules,
        storage_method=storage_method,
        hide_blanks=hide_blanks,
        table_manipulation_method=table_manipulation_method,
        display_label_type=display_label_type,
        table_column_name_style=table_column_name_style,
        annotation_key_style=annotation_key_style,
    )

    status = 200
    return result, status


def validate_manifest_with_schematic(
    manifest_path: str,
    schema_url: str,
    component_label: str,
    restrict_rules: bool,
    display_label_type: DisplayLabelType = "class_label",
) -> tuple[list, list]:
    """Validates a manifest csv file

    Args:
        manifest_path (str): The path to the manifest
        schema_url (str): The url of the schema to validate the manifest against
        component_label (str): The label of the component being validated
        restrict_rules (bool): Weather or not to restrict the rules used
        display_label_type (DisplayLabelType):
          The type of label to use as display
          Defaults to "class_label"

    Returns:
        tuple[list, list]: A tuple
          The first item is a list of validation errors
          The second item is a list of validation warnings
    """
    schema_path = download_schema_file_as_jsonld(schema_url)
    access_token = get_access_token()

    metadata_model = MetadataModel(
        inputMModelLocation=schema_path,
        inputMModelLocationType="local",
        data_model_labels=display_label_type,
    )
    result: tuple[list, list] = metadata_model.validateModelManifest(
        manifestPath=manifest_path,
        rootNode=component_label,
        restrict_rules=restrict_rules,
        access_token=access_token,
    )
    return result


@handle_exceptions
def validate_manifest_csv(
    schema_url: str,
    component_label: str,
    body: bytes,
    restrict_rules: bool,
    display_label_type: DisplayLabelType = "class_label",
) -> tuple[ManifestValidationResult | BasicError, int]:
    """Validates a manifest csv file

    Args:
        schema_url (str): The url of the schema to validate the manifest against
        component_label (str): The label of the component being validated
        body (bytes): The body of the request, a manifest csv in bytes form
        restrict_rules (bool): Weather or not to restrict the rules used
        display_label_type (DisplayLabelType):
          The type of label to use as display
          Defaults to "class_label"

    Returns:
        tuple[ManifestValidationResult | BasicError, int]: A tuple
          The first item is the results of the validation attempt or an error
          The second item is the status of the request
    """
    manifest_path = save_manifest_csv_string_as_csv(body)

    errors, warnings = validate_manifest_with_schematic(
        manifest_path,
        schema_url,
        component_label,
        restrict_rules,
        display_label_type=display_label_type,
    )

    result: ManifestValidationResult | BasicError = ManifestValidationResult(
        errors=errors, warnings=warnings
    )
    return result, 200


@handle_exceptions
def validate_manifest_json(
    schema_url: str,
    component_label: str,
    restrict_rules: bool,
    display_label_type: DisplayLabelType = "class_label",
    body: Any = None,
) -> tuple[ManifestValidationResult | BasicError, int]:
    """Validates a manifest in json string form

    Args:
        schema_url (str): The url of the schema to validate the manifest against
        component_label (str): The label of the component being validated
        body (Any): The body of the request
        restrict_rules (bool): Weather or not to restrict the rules used
        display_label_type (DisplayLabelType):
          The type of label to use as display
          Defaults to "class_label"

    Returns:
        tuple[ManifestValidationResult | BasicError, int]: A tuple
          The first item is the results of the validation attempt or an error
          The second item is the status of the request
    """
    manifest_path = save_manifest_json_string_as_csv(body)

    errors, warnings = validate_manifest_with_schematic(
        manifest_path,
        schema_url,
        component_label,
        restrict_rules,
        display_label_type=display_label_type,
    )

    result: ManifestValidationResult | BasicError = ManifestValidationResult(
        errors=errors, warnings=warnings
    )
    return result, 200
