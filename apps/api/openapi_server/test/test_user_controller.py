# coding: utf-8

from __future__ import absolute_import
import unittest

from flask import json
from six import BytesIO

from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.page_of_challenges import PageOfChallenges  # noqa: E501
from openapi_server.models.page_of_organizations import PageOfOrganizations  # noqa: E501
from openapi_server.models.page_of_users import PageOfUsers  # noqa: E501
from openapi_server.models.user import User  # noqa: E501
from openapi_server.models.user_create_request import UserCreateRequest  # noqa: E501
from openapi_server.models.user_create_response import UserCreateResponse  # noqa: E501
from openapi_server.test import BaseTestCase


class TestUserController(BaseTestCase):
    """UserController integration test stubs"""

    def test_create_user(self):
        """Test case for create_user

        Create a user
        """
        user_create_request = {
  "login" : "awesome-user",
  "email" : "awesome-user@example.org",
  "password" : "yourpassword",
  "name" : "Awesome User",
  "avatarUrl" : "https://example.com/awesome-avatar.png",
  "bio" : "A great bio"
}
        headers = { 
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        }
        response = self.client.open(
            '/api/v1/users',
            method='POST',
            headers=headers,
            data=json.dumps(user_create_request),
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_delete_all_users(self):
        """Test case for delete_all_users

        Delete all users
        """
        headers = { 
            'Accept': 'application/json',
            'ApiKeyAuth': 'special-key',
        }
        response = self.client.open(
            '/api/v1/users',
            method='DELETE',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_delete_user(self):
        """Test case for delete_user

        Delete a user
        """
        headers = { 
            'Accept': 'application/json',
            'ApiKeyAuth': 'special-key',
        }
        response = self.client.open(
            '/api/v1/users/{user_id}'.format(user_id='user_id_example'),
            method='DELETE',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_get_authenticated_user(self):
        """Test case for get_authenticated_user

        Get the authenticated user
        """
        headers = { 
            'Accept': 'application/json',
            'Authorization': 'Bearer special-key',
        }
        response = self.client.open(
            '/api/v1/user',
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_get_user(self):
        """Test case for get_user

        Get a user
        """
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/users/{user_id}'.format(user_id='user_id_example'),
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_is_starred_challenge(self):
        """Test case for is_starred_challenge

        Check if a challenge is starred by the authenticated user
        """
        headers = { 
            'Accept': 'application/json',
            'Authorization': 'Bearer special-key',
        }
        response = self.client.open(
            '/api/v1/user/starred/{account_name}/{challenge_name}'.format(account_name='account_name_example', challenge_name='challenge_name_example'),
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_list_authenticated_user_organizations(self):
        """Test case for list_authenticated_user_organizations

        List organizations of the authenticated user
        """
        query_string = [('limit', 10),
                        ('offset', 0)]
        headers = { 
            'Accept': 'application/json',
            'Authorization': 'Bearer special-key',
        }
        response = self.client.open(
            '/api/v1/user/orgs',
            method='GET',
            headers=headers,
            query_string=query_string)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_list_starred_challenges(self):
        """Test case for list_starred_challenges

        List challenges starred by the authenticated user
        """
        query_string = [('limit', 10),
                        ('offset', 0)]
        headers = { 
            'Accept': 'application/json',
            'Authorization': 'Bearer special-key',
        }
        response = self.client.open(
            '/api/v1/user/starred',
            method='GET',
            headers=headers,
            query_string=query_string)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_list_user_organizations(self):
        """Test case for list_user_organizations

        List orgsnizations of a user
        """
        query_string = [('limit', 10),
                        ('offset', 0)]
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/users/{user_id}/orgs'.format(user_id='user_id_example'),
            method='GET',
            headers=headers,
            query_string=query_string)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_list_user_starred_challenges(self):
        """Test case for list_user_starred_challenges

        List challenges starred by a user
        """
        query_string = [('limit', 10),
                        ('offset', 0)]
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/users/{user_id}/starred'.format(user_id='user_id_example'),
            method='GET',
            headers=headers,
            query_string=query_string)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_list_users(self):
        """Test case for list_users

        Get all users
        """
        query_string = [('limit', 10),
                        ('offset', 0)]
        headers = { 
            'Accept': 'application/json',
            'Authorization': 'Bearer special-key',
            'Authorization': 'Bearer special-key',
        }
        response = self.client.open(
            '/api/v1/users',
            method='GET',
            headers=headers,
            query_string=query_string)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_star_challenge(self):
        """Test case for star_challenge

        Star a challenge for the authenticated user
        """
        headers = { 
            'Accept': 'application/json',
            'Authorization': 'Bearer special-key',
        }
        response = self.client.open(
            '/api/v1/user/starred/{account_name}/{challenge_name}'.format(account_name='account_name_example', challenge_name='challenge_name_example'),
            method='PUT',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_unstar_challenge(self):
        """Test case for unstar_challenge

        Unstar a challenge for the authenticated user
        """
        headers = { 
            'Accept': 'application/json',
            'Authorization': 'Bearer special-key',
        }
        response = self.client.open(
            '/api/v1/user/starred/{account_name}/{challenge_name}'.format(account_name='account_name_example', challenge_name='challenge_name_example'),
            method='DELETE',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    unittest.main()
