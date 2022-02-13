# coding: utf-8

from __future__ import absolute_import
import unittest

from flask import json

from openapi_server.dbmodels.organization import Organization as DbOrganization
from openapi_server.dbmodels.user import User as DbUser
from openapi_server.test.integration import BaseTestCase
from openapi_server.test.integration import util


USERNAME_QUERY = [("username", "awesome-user")]
REQUEST_HEADERS = {
    'Accept': "application/json",
    'Content-Type': "application/json",
}
RESPONSE_HEADERS = {
    'Accept': "application/json",
}

# TODO: mock 409 and 500 reponses


class TestUserController(BaseTestCase):
    """UserController integration test stubs"""

    def setUp(self):
        util.connect_db()
        DbOrganization.objects.delete()
        DbUser.objects.delete()
        util.create_test_organization("awesome-organization")

    def tearDown(self):
        util.disconnect_db()

    def test_create_user_with_status201(self):
        """Test case for create_user

        Create a user (201)
        """
        user = {
            'firstName': "John",
            'lastName': "Smith",
            'organizations': ["awesome-organization"],
            'email': "john.smith@example.com"
        }
        response = self.client.open(
            "/api/v1/users",
            method="POST",
            headers=REQUEST_HEADERS,
            data=json.dumps(user),
            query_string=USERNAME_QUERY
        )
        self.assertStatus(
            response, 201,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_create_user_with_status400(self):
        """Test case for create_user

        Create a (non-JSON) user (400)
        """
        user = {
            'firstName': "John",
            'lastName': "Smith",
            'organizations': ["awesome-organization"],
            'email': "john.smith@example.com"
        }
        response = self.client.open(
            "/api/v1/users",
            method="POST",
            headers=REQUEST_HEADERS,
            data=user,
            query_string=USERNAME_QUERY
        )
        self.assert400(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_create_empty_user_with_status400(self):
        """Test case for create_user

        Create an empty user with missing required properties (400)
        """
        user = {}
        response = self.client.open(
            "/api/v1/users",
            method="POST",
            headers=REQUEST_HEADERS,
            data=user,
            query_string=USERNAME_QUERY
        )
        self.assert400(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_create_user_with_status404(self):
        """Test case for create_user

        Create a user with an unknown organization (404)
        """
        user = {
            'firstName': "John",
            'lastName': "Smith",
            'organizations': ["foo"],
            'email': "john.smith@example.com"
        }
        response = self.client.open(
            "/api/v1/users",
            method="POST",
            headers=REQUEST_HEADERS,
            data=json.dumps(user),
            query_string=USERNAME_QUERY
        )
        self.assert404(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_create_user_with_status409(self):
        """Test case for create_user

        Create a duplicated user (409)
        """
        util.create_test_user(
            username="awesome-user",
            organizations=["awesome-organization"]
        )
        user = {
            'firstName': "John",
            'lastName': "Smith",
            'organizations': ["awesome-organization"],
            'email': "john.smith@example.com"
        }
        response = self.client.open(
            "/api/v1/users",
            method="POST",
            headers=REQUEST_HEADERS,
            data=json.dumps(user),
            query_string=USERNAME_QUERY
        )
        self.assertStatus(
            response, 409,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_delete_user_with_status200(self):
        """Test case for delete_user

        Delete an existing user (200)
        """
        user = util.create_test_user(
            username="awesome-user",
            organizations=["awesome-organization"]
        )
        response = self.client.open(
            f"/api/v1/users/{user.username}",
            method="DELETE",
            headers=RESPONSE_HEADERS)
        self.assert200(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_delete_user_with_status404(self):
        """Test case for delete_user

        Delete an unknown user (404)
        """
        username = "foo"
        response = self.client.open(
            f"/api/v1/users/{username}",
            method="DELETE",
            headers=RESPONSE_HEADERS)
        self.assert404(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_get_user_with_status200(self):
        """Test case for get_user

        Get an existing user (200)
        """
        user = util.create_test_user(
            username="awesome-user",
            organizations=["awesome-organization"]
        )
        response = self.client.open(
            f"/api/v1/users/{user.username}",
            method="GET",
            headers=RESPONSE_HEADERS)
        self.assert200(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_get_user_with_status404(self):
        """Test case for get_user

        Get an unknown user (404)
        """
        username = "foo"
        response = self.client.open(
            f"/api/v1/users/{username}",
            method="GET",
            headers=RESPONSE_HEADERS)
        self.assert404(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_list_users_with_status200(self):
        """Test case for list_users

        Get all users (200)
        """
        util.create_test_user(
            username="awesome-user",
            organizations=["awesome-organization"]
        )
        query_string = [("limit", 10),
                        ("offset", 0),
                        ("filter_", {
                            # TODO: add values to increase coverage
                        })]
        response = self.client.open(
            "/api/v1/users",
            method="GET",
            headers=RESPONSE_HEADERS,
            query_string=query_string)
        self.assert200(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_list_users_with_status400(self):
        """Test case for list_users

        Get all users using an invalid query (400)
        """
        util.create_test_user(
            username="awesome-user",
            organizations=["awesome-organization"]
        )
        query_string = [("limit", "no-limit"),
                        ("offset", "none"),
                        ("filter_", {
                            # TODO: add values to increase coverage
                        })]
        response = self.client.open(
            "/api/v1/users",
            method="GET",
            headers=RESPONSE_HEADERS,
            query_string=query_string)
        self.assert400(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )


if __name__ == "__main__":
    unittest.main()
