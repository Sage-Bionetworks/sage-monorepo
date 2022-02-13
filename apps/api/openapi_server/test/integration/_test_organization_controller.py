# coding: utf-8

from __future__ import absolute_import
import unittest

from flask import json

from openapi_server.dbmodels.organization import Organization as DbOrganization  # noqa: E501
from openapi_server.test.integration import BaseTestCase
from openapi_server.test.integration import util


ID_QUERY = [("organizationId", "awesome-organization")]
REQUEST_HEADERS = {
    'Accept': "application/json",
    'Content-Type': "application/json",
}
RESPONSE_HEADERS = {
    'Accept': "application/json",
}

# TODO: mock 500 responses


class TestOrganizationController(BaseTestCase):
    """OrganizationController integration test stubs"""

    def setUp(self):
        util.connect_db()
        DbOrganization.objects.delete()

    def tearDown(self):
        util.disconnect_db()

    def test_create_organization_with_status201(self):
        """Test case for create_organization

        Create an organization (201)
        """
        organization = {
            'name': "name",
            'shortName': "shortName",
            'url': "https://openapi-generator.tech"
        }
        response = self.client.open(
            "/api/v1/organizations",
            method="POST",
            headers=REQUEST_HEADERS,
            data=json.dumps(organization),
            query_string=ID_QUERY
        )
        self.assertStatus(
            response, 201,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    # TODO: update to test for non-JSON connexion request
    def test_create_organization_with_status400(self):
        """Test case for create_organization

        Create a (non-JSON) organization (400)
        """
        organization = {
            'name': "name",
            'shortName': "shortName",
            'url': "https://openapi-generator.tech"
        }
        response = self.client.open(
            "/api/v1/organizations",
            method="POST",
            headers=REQUEST_HEADERS,
            data=organization,
            query_string=ID_QUERY
        )
        self.assert400(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_create_empty_organization_with_status400(self):
        """Test case for create_organization

        Create an empty organization with missing required properties (400)
        """
        organization = {}
        response = self.client.open(
            "/api/v1/organizations",
            method="POST",
            headers=REQUEST_HEADERS,
            data=json.dumps(organization),
            query_string=ID_QUERY
        )
        self.assert400(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_create_organization_with_status409(self):
        """Test case for create_organization

        Create a duplicate organization (409)
        """
        util.create_test_organization("awesome-organization")  # duplicated org
        organization = {
            'name': "name",
            'shortName': "shortName",
            'url': "https://openapi-generator.tech"
        }
        response = self.client.open(
            "/api/v1/organizations",
            method="POST",
            headers=REQUEST_HEADERS,
            data=json.dumps(organization),
            query_string=ID_QUERY
        )
        self.assertStatus(
            response, 409,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_delete_organization_with_status200(self):
        """Test case for delete_organization

        Delete an existing organization (200)
        """
        organization = util.create_test_organization("awesome-organization")
        response = self.client.open(
            f"/api/v1/organizations/{organization.id}",
            method="DELETE",
            headers=RESPONSE_HEADERS
        )
        self.assert200(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_delete_organization_with_status404(self):
        """Test case for delete_organization

        Delete an unknown organization (404)
        """
        organization_id = "foo"
        response = self.client.open(
            f"/api/v1/organizations/{organization_id}",
            method="DELETE",
            headers=RESPONSE_HEADERS
        )
        self.assert404(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_get_organization_with_status200(self):
        """Test case for get_organization

        Get an existing organization (200)
        """
        organization = util.create_test_organization("awesome-organization")
        response = self.client.open(
            f"/api/v1/organizations/{organization.id}",
            method="GET",
            headers=RESPONSE_HEADERS
        )
        self.assert200(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_get_organization_with_status404(self):
        """Test case for get_organization

        Get an existing organization (200)
        """
        organization_id = "foo"
        response = self.client.open(
            f"/api/v1/organizations/{organization_id}",
            method="GET",
            headers=RESPONSE_HEADERS
        )
        self.assert404(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_list_organizations_with_status200(self):
        """Test case for list_organizations

        Get all organizations (200)
        """
        util.create_test_organization("awesome-organization")
        query_string = [("limit", 10),
                        ("offset", 0)]
        response = self.client.open(
            "/api/v1/organizations",
            method="GET",
            headers=RESPONSE_HEADERS,
            query_string=query_string
        )
        self.assert200(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )

    def test_list_organizations_with_status400(self):
        """Test case for list_organizations

        Get all organizations using an invalid query (400)
        """
        util.create_test_organization("awesome-organization")
        query_string = [("limit", "no-limit"),
                        ("offset", "none")]
        response = self.client.open(
            "/api/v1/organizations",
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
