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
from openchallenges_client.models.challenges_per_year import (
    ChallengesPerYear,
)  # noqa: E501
from openchallenges_client.rest import ApiException


class TestChallengesPerYear(unittest.TestCase):
    """ChallengesPerYear unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional):
        """Test ChallengesPerYear
        include_option is a boolean, when False only required
        params are included, when True both required and
        optional params are included"""
        # uncomment below to create an instance of `ChallengesPerYear`
        """
        model = openchallenges_client.models.challenges_per_year.ChallengesPerYear()  # noqa: E501
        if include_optional :
            return ChallengesPerYear(
                years = [
                    ''
                    ], 
                challenge_counts = [
                    56
                    ]
            )
        else :
            return ChallengesPerYear(
                years = [
                    ''
                    ],
                challenge_counts = [
                    56
                    ],
        )
        """

    def testChallengesPerYear(self):
        """Test ChallengesPerYear"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)


if __name__ == "__main__":
    unittest.main()
