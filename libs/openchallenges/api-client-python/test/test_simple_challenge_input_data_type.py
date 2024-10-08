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
from openchallenges_client.models.simple_challenge_input_data_type import (
    SimpleChallengeInputDataType,
)  # noqa: E501
from openchallenges_client.rest import ApiException


class TestSimpleChallengeInputDataType(unittest.TestCase):
    """SimpleChallengeInputDataType unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional):
        """Test SimpleChallengeInputDataType
        include_option is a boolean, when False only required
        params are included, when True both required and
        optional params are included"""
        # uncomment below to create an instance of `SimpleChallengeInputDataType`
        """
        model = openchallenges_client.models.simple_challenge_input_data_type.SimpleChallengeInputDataType()  # noqa: E501
        if include_optional :
            return SimpleChallengeInputDataType(
                id = 1, 
                slug = 'gene-expression', 
                name = 'gene expression'
            )
        else :
            return SimpleChallengeInputDataType(
                id = 1,
                slug = 'gene-expression',
                name = 'gene expression',
        )
        """

    def testSimpleChallengeInputDataType(self):
        """Test SimpleChallengeInputDataType"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)


if __name__ == "__main__":
    unittest.main()
