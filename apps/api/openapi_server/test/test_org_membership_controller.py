# coding: utf-8

from __future__ import absolute_import
import unittest

from flask import json
from six import BytesIO

from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.org_membership import OrgMembership  # noqa: E501
from openapi_server.models.org_membership_create_request import OrgMembershipCreateRequest  # noqa: E501
from openapi_server.models.org_membership_create_response import OrgMembershipCreateResponse  # noqa: E501
from openapi_server.models.page_of_org_memberships import PageOfOrgMemberships  # noqa: E501
from openapi_server.test import BaseTestCase


class TestOrgMembershipController(BaseTestCase):
    """OrgMembershipController integration test stubs"""

    def test_create_org_membership(self):
        """Test case for create_org_membership

        Create an org membership
        """
        org_membership_create_request = {
  "state" : "active",
  "role" : "admin",
  "organizationId" : "507f1f77bcf86cd799439011",
  "userId" : "507f1f77bcf86cd799439012"
}
        headers = { 
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        }
        response = self.client.open(
            '/api/v1/orgMemberships',
            method='POST',
            headers=headers,
            data=json.dumps(org_membership_create_request),
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_delete_all_org_memberships(self):
        """Test case for delete_all_org_memberships

        Delete all org memberships
        """
        headers = { 
            'Accept': 'application/json',
            'ApiKeyAuth': 'special-key',
        }
        response = self.client.open(
            '/api/v1/orgMemberships',
            method='DELETE',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_delete_org_membership(self):
        """Test case for delete_org_membership

        Delete an org membership
        """
        headers = { 
            'Accept': 'application/json',
            'ApiKeyAuth': 'special-key',
        }
        response = self.client.open(
            '/api/v1/orgMemberships/{org_membership_id}'.format(org_membership_id='org_membership_id_example'),
            method='DELETE',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_get_org_membership(self):
        """Test case for get_org_membership

        Get an org membership
        """
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/orgMemberships/{org_membership_id}'.format(org_membership_id='org_membership_id_example'),
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_list_org_memberships(self):
        """Test case for list_org_memberships

        List all the org memberships
        """
        query_string = [('limit', 10),
                        ('offset', 0),
                        ('orgId', 'org_id_example'),
                        ('userId', 'user_id_example')]
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/orgMemberships',
            method='GET',
            headers=headers,
            query_string=query_string)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    unittest.main()
