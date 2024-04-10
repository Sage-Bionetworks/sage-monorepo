# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from schematic_api.models.base_model_ import Model
from schematic_api.models.component_requirement_subgraph import ComponentRequirementSubgraph
from schematic_api import util

from schematic_api.models.component_requirement_subgraph import ComponentRequirementSubgraph  # noqa: E501

class ComponentRequirementGraph(Model):
    """NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).

    Do not edit the class manually.
    """

    def __init__(self, component_requirements_graph=None):  # noqa: E501
        """ComponentRequirementGraph - a model defined in OpenAPI

        :param component_requirements_graph: The component_requirements_graph of this ComponentRequirementGraph.  # noqa: E501
        :type component_requirements_graph: List[ComponentRequirementSubgraph]
        """
        self.openapi_types = {
            'component_requirements_graph': List[ComponentRequirementSubgraph]
        }

        self.attribute_map = {
            'component_requirements_graph': 'componentRequirementsGraph'
        }

        self._component_requirements_graph = component_requirements_graph

    @classmethod
    def from_dict(cls, dikt) -> 'ComponentRequirementGraph':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The ComponentRequirementGraph of this ComponentRequirementGraph.  # noqa: E501
        :rtype: ComponentRequirementGraph
        """
        return util.deserialize_model(dikt, cls)

    @property
    def component_requirements_graph(self):
        """Gets the component_requirements_graph of this ComponentRequirementGraph.


        :return: The component_requirements_graph of this ComponentRequirementGraph.
        :rtype: List[ComponentRequirementSubgraph]
        """
        return self._component_requirements_graph

    @component_requirements_graph.setter
    def component_requirements_graph(self, component_requirements_graph):
        """Sets the component_requirements_graph of this ComponentRequirementGraph.


        :param component_requirements_graph: The component_requirements_graph of this ComponentRequirementGraph.
        :type component_requirements_graph: List[ComponentRequirementSubgraph]
        """

        self._component_requirements_graph = component_requirements_graph
