# coding: utf-8

from __future__ import absolute_import
import unittest

from flask import json
from six import BytesIO

from openapi_server.models.challenge_platform import ChallengePlatform  # noqa: E501
from openapi_server.models.challenge_platform_create_request import ChallengePlatformCreateRequest  # noqa: E501
from openapi_server.models.challenge_platform_create_response import ChallengePlatformCreateResponse  # noqa: E501
from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.page_of_challenge_platforms import PageOfChallengePlatforms  # noqa: E501
from openapi_server.test import BaseTestCase


class TestChallengePlatformController(BaseTestCase):
    """ChallengePlatformController integration test stubs"""

    def test_create_challenge_platform(self):
        """Test case for create_challenge_platform

        Create a challenge platform
        """
        challenge_platform_create_request = {
  "name" : "Awesome Challenge Platform",
  "url" : "https://awesome-challenge-platform.io"
}
        query_string = [('challengePlatformId', 'challenge_platform_id_example')]
        headers = { 
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        }
        response = self.client.open(
            '/api/v1/challengePlatforms',
            method='POST',
            headers=headers,
            data=json.dumps(challenge_platform_create_request),
            content_type='application/json',
            query_string=query_string)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_delete_all_challenge_platforms(self):
        """Test case for delete_all_challenge_platforms

        Delete all challenge platforms
        """
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/challengePlatforms',
            method='DELETE',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_delete_challenge_platform(self):
        """Test case for delete_challenge_platform

        Delete a challenge platform
        """
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/challengePlatforms/{challenge_platform_id}'.format(challenge_platform_id='challenge_platform_id_example'),
            method='DELETE',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_get_challenge_platform(self):
        """Test case for get_challenge_platform

        Get a challenge platform
        """
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/challengePlatforms/{challenge_platform_id}'.format(challenge_platform_id='challenge_platform_id_example'),
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_list_challenge_platforms(self):
        """Test case for list_challenge_platforms

        Get all challenge platforms
        """
        query_string = [('limit', 10),
                        ('offset', 0)]
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/challengePlatforms',
            method='GET',
            headers=headers,
            query_string=query_string)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    unittest.main()
