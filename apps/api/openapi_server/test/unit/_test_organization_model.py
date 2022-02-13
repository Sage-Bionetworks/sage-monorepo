# coding: utf-8

from __future__ import absolute_import
import unittest

from openapi_server.models.organization import Organization
from openapi_server.test.integration import BaseTestCase


class TestOrganizationModel(BaseTestCase):

    def setUp(self):
        self.organization = Organization(
            id="awesome-organization",
            name="Awesome Organization",
            url="www.awesome-organization.org"
        )

    def test_long_organization_id(self):
        """Test case for Organization

        Set the organization ID to have >60 length
        """
        with self.assertRaises(ValueError):
            self.organization.id = "abcde" * 15

    def test_short_organization_id(self):
        """Test case for Organization

        Set the organization ID to have <3 length
        """
        with self.assertRaises(ValueError):
            self.organization.id = "x"

    def test_invalid_organization_id(self):
        """Test case for Organization

        Set the organization ID as an invalid string (should be all
        lowercase and single-dash delimited)
        """
        with self.assertRaises(ValueError):
            self.organization.id = "awesome--organization"

    def test_missing_organization_name(self):
        """Test case for Organization

        Set the name to None
        """
        with self.assertRaises(ValueError):
            self.organization.name = None

    def test_missing_organization_url(self):
        """Test case for Organization

        Set the url to None
        """
        with self.assertRaises(ValueError):
            self.organization.url = None


if __name__ == "__main__":
    unittest.main()
