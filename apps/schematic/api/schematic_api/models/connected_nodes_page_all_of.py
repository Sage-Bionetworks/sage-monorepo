# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from schematic_api.models.base_model_ import Model
from schematic_api.models.connected_nodes import ConnectedNodes
from schematic_api import util

from schematic_api.models.connected_nodes import ConnectedNodes  # noqa: E501

class ConnectedNodesPageAllOf(Model):
    """NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).

    Do not edit the class manually.
    """

    def __init__(self, connected_nodes=None):  # noqa: E501
        """ConnectedNodesPageAllOf - a model defined in OpenAPI

        :param connected_nodes: The connected_nodes of this ConnectedNodesPageAllOf.  # noqa: E501
        :type connected_nodes: List[ConnectedNodes]
        """
        self.openapi_types = {
            'connected_nodes': List[ConnectedNodes]
        }

        self.attribute_map = {
            'connected_nodes': 'connected_nodes'
        }

        self._connected_nodes = connected_nodes

    @classmethod
    def from_dict(cls, dikt) -> 'ConnectedNodesPageAllOf':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The ConnectedNodesPage_allOf of this ConnectedNodesPageAllOf.  # noqa: E501
        :rtype: ConnectedNodesPageAllOf
        """
        return util.deserialize_model(dikt, cls)

    @property
    def connected_nodes(self):
        """Gets the connected_nodes of this ConnectedNodesPageAllOf.

        A list of conncted node pairs.  # noqa: E501

        :return: The connected_nodes of this ConnectedNodesPageAllOf.
        :rtype: List[ConnectedNodes]
        """
        return self._connected_nodes

    @connected_nodes.setter
    def connected_nodes(self, connected_nodes):
        """Sets the connected_nodes of this ConnectedNodesPageAllOf.

        A list of conncted node pairs.  # noqa: E501

        :param connected_nodes: The connected_nodes of this ConnectedNodesPageAllOf.
        :type connected_nodes: List[ConnectedNodes]
        """
        if connected_nodes is None:
            raise ValueError("Invalid value for `connected_nodes`, must not be `None`")  # noqa: E501

        self._connected_nodes = connected_nodes
