# coding: utf-8

from __future__ import absolute_import
import unittest

from flask import json
from bson.objectid import ObjectId

from openapi_server.dbmodels.grant import Grant as DbGrant
from openapi_server.test.integration import BaseTestCase
from openapi_server.test.integration import util


REQUEST_HEADERS = {
    'Accept': "application/json",
    'Content-Type': "application/json",
}
RESPONSE_HEADERS = {
    'Accept': "application/json",
}

# TODO: mock 500 responses


class TestGrantController(BaseTestCase):
    """GrantController integration test stubs"""

    def setUp(self):
        util.connect_db()
        DbGrant.objects.delete()

    def tearDown(self):
        util.disconnect_db()

    def test_create_grant_with_status201(self):
        """Test case for create_grant

        Create a new grant (201)
        """
        grant = {
            'name': "awesome-grant",
            'description': "description",
            'url': "https://report.nih.gov/"
        }
        response = self.client.open(
            "/api/v1/grants",
            method="POST",
            headers=REQUEST_HEADERS,
            data=json.dumps(grant)
        )
        self.assertStatus(
            response, 201,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    # TODO: update to test for non-JSON connexion request
    def test_create_grant_with_status400(self):
        """Test case for create_grant

        Create a (non-JSON) grant (400)
        """
        grant = {
            'name': "awesome-grant",
            'description': "description",
            'url': "https://report.nih.gov/"
        }
        response = self.client.open(
            "/api/v1/grants",
            method="POST",
            headers=REQUEST_HEADERS,
            data=grant
        )
        self.assert400(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_create_empty_grant_with_status400(self):
        """Test case for create_grant

        Create an empty grant with missing required properties (400)
        """
        grant = {}
        response = self.client.open(
            "/api/v1/grants",
            method="POST",
            headers=REQUEST_HEADERS,
            data=json.dumps(grant)
        )
        self.assert400(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_create_grant_with_status409(self):
        """Test case for create_grant

        Create a duplicate grant (409)
        """
        grant = util.create_test_grant()  # duplicated grant
        grant2 = {
            'name': grant.name,
            'description': grant.description
        }
        response = self.client.open(
            "/api/v1/grants",
            method="POST",
            headers=REQUEST_HEADERS,
            data=json.dumps(grant2)
        )
        self.assertStatus(
            response, 409,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_delete_grant_with_status200(self):
        """Test case for delete_grant

        Delete an existing grant (200)
        """
        grant = util.create_test_grant()
        response = self.client.open(
            f"/api/v1/grants/{grant.id}",
            method="DELETE",
            headers=RESPONSE_HEADERS
        )
        self.assert200(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_delete_grant_with_status404(self):
        """Test case for delete_grant

        Delete an unknown grant (404)
        """
        grant_id = ObjectId()
        response = self.client.open(
            f"/api/v1/grants/{grant_id}",
            method="DELETE",
            headers=RESPONSE_HEADERS
        )
        self.assert404(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_get_grant_with_status200(self):
        """Test case for get_grant

        Get an existing grant (200)
        """
        grant = util.create_test_grant()
        response = self.client.open(
            f"/api/v1/grants/{grant.id}",
            method="GET",
            headers=RESPONSE_HEADERS
        )
        self.assert200(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_get_grant_with_status404(self):
        """Test case for get_grant

        Get an unknown grant (404)
        """
        grant_id = ObjectId()
        response = self.client.open(
            f"/api/v1/grants/{grant_id}",
            method="GET",
            headers=RESPONSE_HEADERS
        )
        self.assert404(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_list_grants_with_status200(self):
        """Test case for list_grants

        Get all grants (200)
        """
        util.create_test_grant()
        query_string = [("limit", 10),
                        ("offset", 0)]
        response = self.client.open(
            "/api/v1/grants",
            method="GET",
            headers=RESPONSE_HEADERS,
            query_string=query_string
        )
        self.assert200(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_list_grants_with_status400(self):
        """Test case for list_grants

        Get all grants using an invalid query (400)
        """
        util.create_test_grant()
        query_string = [("limit", "no-limit"),
                        ("offset", "none")]
        response = self.client.open(
            "/api/v1/grants",
            method="GET",
            headers=RESPONSE_HEADERS,
            query_string=query_string
        )
        self.assert400(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )


if __name__ == "__main__":
    unittest.main()
