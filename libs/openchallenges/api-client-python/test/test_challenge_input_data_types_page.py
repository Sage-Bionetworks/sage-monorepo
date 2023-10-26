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
from openchallenges_client.models.challenge_input_data_types_page import ChallengeInputDataTypesPage  # noqa: E501
from openchallenges_client.rest import ApiException

class TestChallengeInputDataTypesPage(unittest.TestCase):
    """ChallengeInputDataTypesPage unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional):
        """Test ChallengeInputDataTypesPage
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `ChallengeInputDataTypesPage`
        """
        model = openchallenges_client.models.challenge_input_data_types_page.ChallengeInputDataTypesPage()  # noqa: E501
        if include_optional :
            return ChallengeInputDataTypesPage(
                number = 99, 
                size = 99, 
                total_elements = 99, 
                total_pages = 99, 
                has_next = True, 
                has_previous = True, 
                challenge_input_data_types = [
                    openchallenges_client.models.challenge_input_data_type.ChallengeInputDataType(
                        id = 1, 
                        slug = 'gene-expression', 
                        name = 'gene expression', 
                        created_at = '2022-07-04T22:19:11Z', 
                        updated_at = '2022-07-04T22:19:11Z', )
                    ]
            )
        else :
            return ChallengeInputDataTypesPage(
                number = 99,
                size = 99,
                total_elements = 99,
                total_pages = 99,
                has_next = True,
                has_previous = True,
                challenge_input_data_types = [
                    openchallenges_client.models.challenge_input_data_type.ChallengeInputDataType(
                        id = 1, 
                        slug = 'gene-expression', 
                        name = 'gene expression', 
                        created_at = '2022-07-04T22:19:11Z', 
                        updated_at = '2022-07-04T22:19:11Z', )
                    ],
        )
        """

    def testChallengeInputDataTypesPage(self):
        """Test ChallengeInputDataTypesPage"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
