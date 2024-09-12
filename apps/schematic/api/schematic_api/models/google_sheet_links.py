# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from schematic_api.models.base_model_ import Model
from schematic_api import util


class GoogleSheetLinks(Model):
    """NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).

    Do not edit the class manually.
    """

    def __init__(self, links=None):  # noqa: E501
        """GoogleSheetLinks - a model defined in OpenAPI

        :param links: The links of this GoogleSheetLinks.  # noqa: E501
        :type links: List[str]
        """
        self.openapi_types = {"links": List[str]}

        self.attribute_map = {"links": "links"}

        self._links = links

    @classmethod
    def from_dict(cls, dikt) -> "GoogleSheetLinks":
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The GoogleSheetLinks of this GoogleSheetLinks.  # noqa: E501
        :rtype: GoogleSheetLinks
        """
        return util.deserialize_model(dikt, cls)

    @property
    def links(self):
        """Gets the links of this GoogleSheetLinks.


        :return: The links of this GoogleSheetLinks.
        :rtype: List[str]
        """
        return self._links

    @links.setter
    def links(self, links):
        """Sets the links of this GoogleSheetLinks.


        :param links: The links of this GoogleSheetLinks.
        :type links: List[str]
        """

        self._links = links
