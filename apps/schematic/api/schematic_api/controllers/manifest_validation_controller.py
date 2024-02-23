import connexion
import six
from typing import Dict
from typing import Tuple
from typing import Union

from schematic_api.models.basic_error import BasicError  # noqa: E501
from schematic_api.models.manifest_validation_result import (
    ManifestValidationResult,
)  # noqa: E501
from schematic_api import util
from schematic_api.controllers import manifest_validation_controller_impl


def submit_manifest_csv(
    schema_url,
    component,
    dataset_id,
    asset_view_id,
    body,
    restrict_rules=None,
    storage_method=None,
    hide_blanks=None,
    table_manipulation_method=None,
    display_label_type=None,
):  # noqa: E501
    """Validates manifest in csv form, then submits it

    Validates manifest in csv form, then submits it # noqa: E501

    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param component: A component in a schema, either the dsplay label or schema label
    :type component: str
    :param dataset_id: The ID of a dataset.
    :type dataset_id: str
    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str
    :param body: .csv file
    :type body: str
    :param restrict_rules: If True, validation suite will only run with in-house validation rule. If False, the Great Expectations suite will be utilized and all rules will be available.
    :type restrict_rules: bool
    :param storage_method: file_and_entities will store the manifest as a csv and create Synapse files for each row in the manifest. table_and_file will store the manifest as a table and a csv on Synapse. file_only will store the manifest as a csv only on Synapse. table_file_and_entities will perform the options file_with_entites and table in combination.
    :type storage_method: str
    :param hide_blanks: If true, annotations with blank values will be hidden from a dataset&#39;s annotation list in Synaspe. If false, annotations with blank values will be displayed.
    :type hide_blanks: bool
    :param table_manipulation_method: replace will remove the rows and columns from the existing table and store the new rows and columns, preserving the name and synID. upsert will add the new rows to the table and preserve the exisitng rows and columns in the existing table.
    :type table_manipulation_method: str
    :param display_label_type: The type of label to display
    :type display_label_type: str

    :rtype: Union[str, Tuple[str, int], Tuple[str, int, Dict[str, str]]
    """
    return manifest_validation_controller_impl.submit_manifest_csv(
        schema_url,
        component,
        dataset_id,
        asset_view_id,
        body,
        restrict_rules,
        storage_method,
        hide_blanks,
        table_manipulation_method,
        display_label_type,
    )


def submit_manifest_json(
    schema_url,
    component,
    dataset_id,
    asset_view_id,
    restrict_rules=None,
    storage_method=None,
    hide_blanks=None,
    table_manipulation_method=None,
    display_label_type=None,
    body=None,
):  # noqa: E501
    """Validates a manifest in json form, then submits it

    Validates a manifest in json form, then submits it in csv form # noqa: E501

    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param component: A component in a schema, either the dsplay label or schema label
    :type component: str
    :param dataset_id: The ID of a dataset.
    :type dataset_id: str
    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str
    :param restrict_rules: If True, validation suite will only run with in-house validation rule. If False, the Great Expectations suite will be utilized and all rules will be available.
    :type restrict_rules: bool
    :param storage_method: file_and_entities will store the manifest as a csv and create Synapse files for each row in the manifest. table_and_file will store the manifest as a table and a csv on Synapse. file_only will store the manifest as a csv only on Synapse. table_file_and_entities will perform the options file_with_entites and table in combination.
    :type storage_method: str
    :param hide_blanks: If true, annotations with blank values will be hidden from a dataset&#39;s annotation list in Synaspe. If false, annotations with blank values will be displayed.
    :type hide_blanks: bool
    :param table_manipulation_method: replace will remove the rows and columns from the existing table and store the new rows and columns, preserving the name and synID. upsert will add the new rows to the table and preserve the exisitng rows and columns in the existing table.
    :type table_manipulation_method: str
    :param display_label_type: The type of label to display
    :type display_label_type: str
    :param body: A manifest in json form
    :type body: str

    :rtype: Union[str, Tuple[str, int], Tuple[str, int, Dict[str, str]]
    """
    return manifest_validation_controller_impl.submit_manifest_json(
        schema_url,
        component,
        dataset_id,
        asset_view_id,
        restrict_rules,
        storage_method,
        hide_blanks,
        table_manipulation_method,
        display_label_type,
        body,
    )


def validate_manifest_csv(
    schema_url, component_label, body, restrict_rules=None, display_label_type=None
):  # noqa: E501
    """Validates a manifest in csv form

    Validates a manifest in csv form # noqa: E501

    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param component_label: The label of a component in a schema
    :type component_label: str
    :param body: .csv file
    :type body: str
    :param restrict_rules: If True, validation suite will only run with in-house validation rule. If False, the Great Expectations suite will be utilized and all rules will be available.
    :type restrict_rules: bool
    :param display_label_type: The type of label to display
    :type display_label_type: str

    :rtype: Union[ManifestValidationResult, Tuple[ManifestValidationResult, int], Tuple[ManifestValidationResult, int, Dict[str, str]]
    """
    return manifest_validation_controller_impl.validate_manifest_csv(
        schema_url, component_label, body, restrict_rules, display_label_type
    )


def validate_manifest_json(
    schema_url, component_label, restrict_rules=None, display_label_type=None, body=None
):  # noqa: E501
    """Validates a manifest in json form

    Validates a manifest in json form # noqa: E501

    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param component_label: The label of a component in a schema
    :type component_label: str
    :param restrict_rules: If True, validation suite will only run with in-house validation rule. If False, the Great Expectations suite will be utilized and all rules will be available.
    :type restrict_rules: bool
    :param display_label_type: The type of label to display
    :type display_label_type: str
    :param body: A manifest in json form
    :type body: str

    :rtype: Union[ManifestValidationResult, Tuple[ManifestValidationResult, int], Tuple[ManifestValidationResult, int, Dict[str, str]]
    """
    return manifest_validation_controller_impl.validate_manifest_json(
        schema_url, component_label, restrict_rules, display_label_type, body
    )
