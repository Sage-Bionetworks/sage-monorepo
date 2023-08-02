# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from schematic_api.models.base_model_ import Model
from schematic_api import util


class AssetType(Model):
    """NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).

    Do not edit the class manually.
    """

    """
    allowed enum values
    """
    SYNAPSE = "synapse"
    def __init__(self):  # noqa: E501
        """AssetType - a model defined in OpenAPI

        """
        self.openapi_types = {
        }

        self.attribute_map = {
        }

    @classmethod
    def from_dict(cls, dikt) -> 'AssetType':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The AssetType of this AssetType.  # noqa: E501
        :rtype: AssetType
        """
        return util.deserialize_model(dikt, cls)
