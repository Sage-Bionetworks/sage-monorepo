# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from schematic_api.models.base_model_ import Model
from schematic_api.models.file import File
from schematic_api import util

from schematic_api.models.file import File  # noqa: E501

class FilesPageAllOf(Model):
    """NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).

    Do not edit the class manually.
    """

    def __init__(self, files=None):  # noqa: E501
        """FilesPageAllOf - a model defined in OpenAPI

        :param files: The files of this FilesPageAllOf.  # noqa: E501
        :type files: List[File]
        """
        self.openapi_types = {
            'files': List[File]
        }

        self.attribute_map = {
            'files': 'files'
        }

        self._files = files

    @classmethod
    def from_dict(cls, dikt) -> 'FilesPageAllOf':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The FilesPage_allOf of this FilesPageAllOf.  # noqa: E501
        :rtype: FilesPageAllOf
        """
        return util.deserialize_model(dikt, cls)

    @property
    def files(self):
        """Gets the files of this FilesPageAllOf.

        A list of files.  # noqa: E501

        :return: The files of this FilesPageAllOf.
        :rtype: List[File]
        """
        return self._files

    @files.setter
    def files(self, files):
        """Sets the files of this FilesPageAllOf.

        A list of files.  # noqa: E501

        :param files: The files of this FilesPageAllOf.
        :type files: List[File]
        """
        if files is None:
            raise ValueError("Invalid value for `files`, must not be `None`")  # noqa: E501

        self._files = files
