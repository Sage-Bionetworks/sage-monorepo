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
from openchallenges_client.models.challenge_platform import ChallengePlatform  # noqa: E501
from openchallenges_client.rest import ApiException

class TestChallengePlatform(unittest.TestCase):
    """ChallengePlatform unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional):
        """Test ChallengePlatform
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `ChallengePlatform`
        """
        model = openchallenges_client.models.challenge_platform.ChallengePlatform()  # noqa: E501
        if include_optional :
            return ChallengePlatform(
                id = 1, 
                slug = 'example-challenge-platform', 
                name = '012', 
                avatar_url = 'https://via.placeholder.com/300.png', 
                website_url = 'https://example.com', 
                created_at = '2022-07-04T22:19:11Z', 
                updated_at = '2022-07-04T22:19:11Z'
            )
        else :
            return ChallengePlatform(
                id = 1,
                slug = 'example-challenge-platform',
                name = '012',
                avatar_url = 'https://via.placeholder.com/300.png',
                website_url = 'https://example.com',
                created_at = '2022-07-04T22:19:11Z',
                updated_at = '2022-07-04T22:19:11Z',
        )
        """

    def testChallengePlatform(self):
        """Test ChallengePlatform"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
