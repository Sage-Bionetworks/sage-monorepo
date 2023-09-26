"""Implementation of validation endpoints"""
import tempfile
import os
import io
import json
import urllib.request
import shutil
from typing import Union, Any, Optional

import pandas as pd
from schematic import CONFIG  # type: ignore
from schematic.models.metadata import MetadataModel  # type: ignore

from schematic_api.models.manifest_validation_result import ManifestValidationResult
from schematic_api.models.basic_error import BasicError
from schematic_api.controllers.utils import handle_exceptions, get_access_token


def download_schema_file_as_jsonld(schema_url: str) -> str:
    """Downloads a schema and saves it as temp file

    Args:
        schema_url (str): The URL of the schema

    Returns:
        str: The path fo the schema jsonld file
    """
    with urllib.request.urlopen(schema_url) as response:
        with tempfile.NamedTemporaryFile(
            delete=False, suffix=".model.jsonld"
        ) as tmp_file:
            shutil.copyfileobj(response, tmp_file)
            return tmp_file.name


def save_manifest_json_string_as_csv(manifest_json_string: str) -> str:
    """Takes a manifest json string and converts it to a csv file

    Args:
        manifest_json_string (str): The manifest in json string form

    Returns:
        str: The path of the csv file
    """
    temp_dir = tempfile.gettempdir()
    temp_path = os.path.join(temp_dir, "manifest.csv")
    json_dict = json.loads(manifest_json_string)
    mainfest_df = pd.DataFrame(json_dict)
    mainfest_df.to_csv(temp_path, encoding="utf-8", index=False)
    return temp_path


def save_manifest_csv_string_as_csv(manifest_csv_string: bytes) -> str:
    """Takes a manifest csv string and converts it to a csv file

    Args:
        manifest_csv_string (bytes): The manifest in csv string form

    Returns:
        str: The path of the csv file
    """
    temp_dir = tempfile.gettempdir()
    temp_path = os.path.join(temp_dir, "manifest.csv")
    mainfest_df = pd.read_csv(io.BytesIO(manifest_csv_string), sep=",")
    mainfest_df.to_csv(temp_path, encoding="utf-8", index=False)
    return temp_path


def submit_manifest_with_schematic(  # pylint: disable=too-many-arguments
    schema_path: str,
    manifest_path: str,
    component: Optional[str],
    dataset_id: str,
    restrict_rules: bool = False,
    storage_method: str = "table_file_and_entities",
    hide_blanks: bool = False,
    table_manipulation_method: str = "replace",
    use_schema_label: bool = True,
) -> str:
    """Submits a manifest csv

    Args:
        schema_path (str): The path to a schema in jsonld form
        manifest_path (str): The path to a manifest in csv form
        component (Optional[str]):
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
        use_schema_label (bool, optional):
          Whetehr or not the schema label will be used.
          Defaults to True.

    Returns:
         str: The id of the manifest
    """
    access_token = get_access_token()
    metadata_model = MetadataModel(
        inputMModelLocation=schema_path, inputMModelLocationType="local"
    )

    manifest_id = metadata_model.submit_metadata_manifest(
        path_to_json_ld=schema_path,
        manifest_path=manifest_path,
        dataset_id=dataset_id,
        validate_component=component,
        access_token=access_token,
        manifest_record_type=storage_method,
        restrict_rules=restrict_rules,
        hide_blanks=hide_blanks,
        table_manipulation=table_manipulation_method,
        use_schema_label=use_schema_label,
    )
    return manifest_id


@handle_exceptions
def submit_manifest_csv(  # pylint: disable=too-many-arguments
    schema_url: str,
    component: Optional[str],
    dataset_id: str,
    asset_view_id: str,
    body: bytes,
    restrict_rules: bool = False,
    storage_method: str = "table_file_and_entities",
    hide_blanks: bool = False,
    table_manipulation_method: str = "replace",
    use_schema_label: bool = True,
) -> tuple[Union[str, BasicError], int]:
    """Submits a manifest csv in bytes form

     Args:
         schema_url (str): The url to schema the component is in
         component (Optional[str]):
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
         use_schema_label (bool, optional):
           Whetehr or not the schema label will be used.
           Defaults to True.

    Returns:
         tuple[Union[str, BasicError], int]: A tuple
           The first item is either the id of the manifest or an error object
           The second item is the response status
    """
    CONFIG.synapse_master_fileview_id = asset_view_id
    manifest_path = save_manifest_csv_string_as_csv(body)
    schema_path = download_schema_file_as_jsonld(schema_url)

    result: Union[str, BasicError] = submit_manifest_with_schematic(
        schema_path=schema_path,
        manifest_path=manifest_path,
        component=component,
        dataset_id=dataset_id,
        restrict_rules=restrict_rules,
        storage_method=storage_method,
        hide_blanks=hide_blanks,
        table_manipulation_method=table_manipulation_method,
        use_schema_label=use_schema_label,
    )

    status = 200
    return result, status


@handle_exceptions
def submit_manifest_json(  # pylint: disable=too-many-arguments
    schema_url: str,
    component: Optional[str],
    dataset_id: str,
    asset_view_id: str,
    body: Any,
    restrict_rules: bool = False,
    storage_method: str = "table_file_and_entities",
    hide_blanks: bool = False,
    table_manipulation_method: str = "replace",
    use_schema_label: bool = True,
) -> tuple[Union[str, BasicError], int]:
    """Submits a manifest csv in bytes form

    Args:
        schema_url (str): The url to schema the component is in
        component (Optional[str]):
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
        use_schema_label (bool, optional):
          Whetehr or not the schema label will be used.
          Defaults to True.

    Returns:
         tuple[Union[str, BasicError], int]: A tuple
           The first item is either the id of the manifest or an error object
           The second item is the response status
    """
    CONFIG.synapse_master_fileview_id = asset_view_id
    manifest_path = save_manifest_json_string_as_csv(body)
    schema_path = download_schema_file_as_jsonld(schema_url)

    result: Union[str, BasicError] = submit_manifest_with_schematic(
        schema_path=schema_path,
        manifest_path=manifest_path,
        component=component,
        dataset_id=dataset_id,
        restrict_rules=restrict_rules,
        storage_method=storage_method,
        hide_blanks=hide_blanks,
        table_manipulation_method=table_manipulation_method,
        use_schema_label=use_schema_label,
    )

    status = 200
    return result, status


def validate_manifest_with_schematic(
    manifest_path: str, schema_url: str, component_label: str, restrict_rules: bool
) -> tuple[list, list]:
    """Validates a manifest csv file

    Args:
        manifest_path (str): The path to the manifest
        schema_url (str): The url of the schema to validate the manifest against
        component_label (str): The label of the component being validated
        restrict_rules (bool): Weather or not to restrict the rules used

    Returns:
        tuple[list, list]: A tuple
          The first item is a list of validation errors
          The second item is a list of validation warnings
    """
    schema_path = download_schema_file_as_jsonld(schema_url)

    metadata_model = MetadataModel(
        inputMModelLocation=schema_path, inputMModelLocationType="local"
    )

    errors, warnings = metadata_model.validateModelManifest(
        manifestPath=manifest_path,
        rootNode=component_label,
        restrict_rules=restrict_rules,
    )
    return errors, warnings


@handle_exceptions
def validate_manifest_csv(
    schema_url: str, component_label: str, body: bytes, restrict_rules: bool
) -> tuple[Union[ManifestValidationResult, BasicError], int]:
    """Validates a manifest csv file

    Args:
        schema_url (str): The url of the schema to validate the manifest against
        component_label (str): The label of the component being validated
        body (bytes): The body of the request, a manifest csv in bytes form
        restrict_rules (bool): Weather or not to restrict the rules used

    Returns:
        tuple[Union[ManifestValidationResult, BasicError], int]: A tuple
          The first item is the results of the validation attempt or an error
          The second item is the status of the request
    """
    manifest_path = save_manifest_csv_string_as_csv(body)

    errors, warnings = validate_manifest_with_schematic(
        manifest_path, schema_url, component_label, restrict_rules
    )

    result: Union[ManifestValidationResult, BasicError] = ManifestValidationResult(
        errors=errors, warnings=warnings
    )
    return result, 200


@handle_exceptions
def validate_manifest_json(
    schema_url: str,
    component_label: str,
    restrict_rules: bool,
    body: Any,
) -> tuple[Union[ManifestValidationResult, BasicError], int]:
    """Validates a manifest ins jsonstring form

    Args:
        schema_url (str): The url of the schema to validate the manifest against
        component_label (str): The label of the component being validated
        body (Any): The body of the request
        restrict_rules (bool): Weather or not to restrict the rules used

    Returns:
        tuple[Union[ManifestValidationResult, BasicError], int]: A tuple
          The first item is the results of the validation attempt or an error
          The second item is the status of the request
    """
    manifest_path = save_manifest_json_string_as_csv(body)

    errors, warnings = validate_manifest_with_schematic(
        manifest_path, schema_url, component_label, restrict_rules
    )

    result: Union[ManifestValidationResult, BasicError] = ManifestValidationResult(
        errors=errors, warnings=warnings
    )
    return result, 200
