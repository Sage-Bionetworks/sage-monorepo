# coding: utf-8

"""
    OpenChallenges REST API

    No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)

    The version of the OpenAPI document: 1.0.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest
import datetime

import openchallenges_client
from openchallenges_client.models.edam_concepts_page import EdamConceptsPage  # noqa: E501
from openchallenges_client.rest import ApiException

class TestEdamConceptsPage(unittest.TestCase):
    """EdamConceptsPage unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional):
        """Test EdamConceptsPage
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `EdamConceptsPage`
        """
        model = openchallenges_client.models.edam_concepts_page.EdamConceptsPage()  # noqa: E501
        if include_optional :
            return EdamConceptsPage(
                number = 99, 
                size = 99, 
                total_elements = 99, 
                total_pages = 99, 
                has_next = True, 
                has_previous = True, 
                edam_concepts = [
                    openchallenges_client.models.edam_concept.EdamConcept(
                        id = 1, 
                        class_id = 'http://edamontology.org/data_0850', 
                        preferred_label = 'Sequence set', )
                    ]
            )
        else :
            return EdamConceptsPage(
                number = 99,
                size = 99,
                total_elements = 99,
                total_pages = 99,
                has_next = True,
                has_previous = True,
                edam_concepts = [
                    openchallenges_client.models.edam_concept.EdamConcept(
                        id = 1, 
                        class_id = 'http://edamontology.org/data_0850', 
                        preferred_label = 'Sequence set', )
                    ],
        )
        """

    def testEdamConceptsPage(self):
        """Test EdamConceptsPage"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
