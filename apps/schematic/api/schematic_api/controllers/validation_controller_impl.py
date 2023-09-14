"""Implementation of validation endpoints"""
import tempfile
import os
import json
import urllib.request
import shutil
from typing import Union, Any

import pandas as pd
from schematic.models.metadata import MetadataModel  # type: ignore

from schematic_api.models.manifest_validation_result import ManifestValidationResult
from schematic_api.models.basic_error import BasicError
from schematic_api.controllers.utils import handle_exceptions


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


# @handle_exceptions
def validate_manifest_csv(
    schema_url: str, component_label: str, body: Any, restrict_rules: bool
) -> tuple[Union[ManifestValidationResult, BasicError], int]:
    """Validates a manifest csv file

    Args:
        schema_url (str): The url of the schema to validate the manifest against
        component_label (str): The label of the component being validated
        body (Any): The body of the request
        restrict_rules (bool): Weather or nt to restrict the rules used

    Returns:
        tuple[Union[ManifestValidationResult, BasicError], int]: A tuple
          The first item is the results of the validation attempt or an error
          The second item is the status of the request
    """
    jsonld = download_schema_file_as_jsonld(schema_url)

    metadata_model = MetadataModel(
        inputMModelLocation=jsonld, inputMModelLocationType="local"
    )

    errors, warnings = metadata_model.validateModelManifest(
        manifestPath=body["path"],
        rootNode=component_label,
        restrict_rules=restrict_rules,
    )

    result: Union[ManifestValidationResult, BasicError] = ManifestValidationResult(
        errors=errors, warnings=warnings
    )
    return result, 200


@handle_exceptions
def validate_manifest_json(
    schema_url: str, component_label: str, body: Any, restrict_rules: bool
) -> tuple[Union[ManifestValidationResult, BasicError], int]:
    """Validates a manifest ins jsonstring form

    Args:
        schema_url (str): The url of the schema to validate the manifest against
        component_label (str): The label of the component being validated
        body (Any): The body of the request
        restrict_rules (bool): Weather or nt to restrict the rules used

    Returns:
        tuple[Union[ManifestValidationResult, BasicError], int]: A tuple
          The first item is the results of the validation attempt or an error
          The second item is the status of the request
    """
    manifest_json = body["json"]
    manifest_path = save_manifest_json_string_as_csv(manifest_json)
    schema_path = download_schema_file_as_jsonld(schema_url)

    metadata_model = MetadataModel(
        inputMModelLocation=schema_path, inputMModelLocationType="local"
    )

    errors, warnings = metadata_model.validateModelManifest(
        manifestPath=manifest_path,
        rootNode=component_label,
        restrict_rules=restrict_rules,
    )

    result: Union[ManifestValidationResult, BasicError] = ManifestValidationResult(
        errors=errors, warnings=warnings
    )
    return result, 200
