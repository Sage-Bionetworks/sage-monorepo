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
from schematic_api.controllers import validation_controller_impl


def submit_manifest_csv(
    schema_url, component_label, body, restrict_rules=None
):  # noqa: E501
    """Validates manifest in csv form, then submits it

    Validates manifest in csv form, then submits it # noqa: E501

    :param schema_url: The URL of a schema in jsonld form
    :type schema_url: str
    :param component_label: The label of a component in a schema
    :type component_label: str
    :param body: .csv file
    :type body: str
    :param restrict_rules: If True, validation suite will only run with in-house validation rule. If False, the Great Expectations suite will be utilized and all rules will be available.
    :type restrict_rules: bool

    :rtype: Union[ManifestValidationResult, Tuple[ManifestValidationResult, int], Tuple[ManifestValidationResult, int, Dict[str, str]]
    """
    return validation_controller_impl.submit_manifest_csv(
        schema_url, component_label, body, restrict_rules
    )


def submit_manifest_json(
    schema_url, component_label, restrict_rules=None, body=None
):  # noqa: E501
    """Validates a manifest in json form, then submits it in csv form

    Validates a manifest in json form, then submits it in csv form # noqa: E501

    :param schema_url: The URL of a schema in jsonld form
    :type schema_url: str
    :param component_label: The label of a component in a schema
    :type component_label: str
    :param restrict_rules: If True, validation suite will only run with in-house validation rule. If False, the Great Expectations suite will be utilized and all rules will be available.
    :type restrict_rules: bool
    :param body: A manifest in json form
    :type body:

    :rtype: Union[ManifestValidationResult, Tuple[ManifestValidationResult, int], Tuple[ManifestValidationResult, int, Dict[str, str]]
    """
    return validation_controller_impl.submit_manifest_json(
        schema_url, component_label, restrict_rules, body
    )


def validate_manifest_csv(
    schema_url, component_label, body, restrict_rules=None
):  # noqa: E501
    """Validates a manifest in csv form

    Validates a manifest in csv form # noqa: E501

    :param schema_url: The URL of a schema in jsonld form
    :type schema_url: str
    :param component_label: The label of a component in a schema
    :type component_label: str
    :param body: .csv file
    :type body: str
    :param restrict_rules: If True, validation suite will only run with in-house validation rule. If False, the Great Expectations suite will be utilized and all rules will be available.
    :type restrict_rules: bool

    :rtype: Union[ManifestValidationResult, Tuple[ManifestValidationResult, int], Tuple[ManifestValidationResult, int, Dict[str, str]]
    """
    return validation_controller_impl.validate_manifest_csv(
        schema_url, component_label, body, restrict_rules
    )


def validate_manifest_json(
    schema_url, component_label, restrict_rules=None, body=None
):  # noqa: E501
    """Validates a manifest in json form

    Validates a manifest in json form # noqa: E501

    :param schema_url: The URL of a schema in jsonld form
    :type schema_url: str
    :param component_label: The label of a component in a schema
    :type component_label: str
    :param restrict_rules: If True, validation suite will only run with in-house validation rule. If False, the Great Expectations suite will be utilized and all rules will be available.
    :type restrict_rules: bool
    :param body: A manifest in json form
    :type body:

    :rtype: Union[ManifestValidationResult, Tuple[ManifestValidationResult, int], Tuple[ManifestValidationResult, int, Dict[str, str]]
    """
    return validation_controller_impl.validate_manifest_json(
        schema_url, component_label, restrict_rules, body
    )
