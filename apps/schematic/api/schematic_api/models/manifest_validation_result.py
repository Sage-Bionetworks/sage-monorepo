# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from schematic_api.models.base_model_ import Model
from schematic_api import util


class ManifestValidationResult(Model):
    """NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).

    Do not edit the class manually.
    """

    def __init__(self, errors=None, warnings=None):  # noqa: E501
        """ManifestValidationResult - a model defined in OpenAPI

        :param errors: The errors of this ManifestValidationResult.  # noqa: E501
        :type errors: List[str]
        :param warnings: The warnings of this ManifestValidationResult.  # noqa: E501
        :type warnings: List[str]
        """
        self.openapi_types = {"errors": List[str], "warnings": List[str]}

        self.attribute_map = {"errors": "errors", "warnings": "warnings"}

        self._errors = errors
        self._warnings = warnings

    @classmethod
    def from_dict(cls, dikt) -> "ManifestValidationResult":
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The ManifestValidationResult of this ManifestValidationResult.  # noqa: E501
        :rtype: ManifestValidationResult
        """
        return util.deserialize_model(dikt, cls)

    @property
    def errors(self):
        """Gets the errors of this ManifestValidationResult.

        Any errors from validation  # noqa: E501

        :return: The errors of this ManifestValidationResult.
        :rtype: List[str]
        """
        return self._errors

    @errors.setter
    def errors(self, errors):
        """Sets the errors of this ManifestValidationResult.

        Any errors from validation  # noqa: E501

        :param errors: The errors of this ManifestValidationResult.
        :type errors: List[str]
        """

        self._errors = errors

    @property
    def warnings(self):
        """Gets the warnings of this ManifestValidationResult.

        Any warnings from validation  # noqa: E501

        :return: The warnings of this ManifestValidationResult.
        :rtype: List[str]
        """
        return self._warnings

    @warnings.setter
    def warnings(self, warnings):
        """Sets the warnings of this ManifestValidationResult.

        Any warnings from validation  # noqa: E501

        :param warnings: The warnings of this ManifestValidationResult.
        :type warnings: List[str]
        """

        self._warnings = warnings
