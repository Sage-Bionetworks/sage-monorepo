# coding: utf-8

from __future__ import absolute_import
import unittest

from flask import json
from six import BytesIO

from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.local_auth_request import LocalAuthRequest  # noqa: E501
from openapi_server.models.local_auth_response import LocalAuthResponse  # noqa: E501
from openapi_server.test import BaseTestCase


class TestAuthController(BaseTestCase):
    """AuthController integration test stubs"""

    def test_auth_google(self):
        """Test case for auth_google

        Authentify a local account with Google OAuth 2.1
        """
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/auth/google',
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_auth_local(self):
        """Test case for auth_local

        Authentify a local account
        """
        local_auth_request = {
  "login" : "awesome-user",
  "password" : "yourpassword"
}
        headers = { 
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        }
        response = self.client.open(
            '/api/v1/auth/local',
            method='POST',
            headers=headers,
            data=json.dumps(local_auth_request),
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    unittest.main()
