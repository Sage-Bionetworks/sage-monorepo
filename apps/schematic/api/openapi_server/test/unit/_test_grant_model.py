# coding: utf-8

from __future__ import absolute_import
import unittest

from openapi_server.models.grant import Grant
from openapi_server.test.integration import BaseTestCase


class TestGrantModel(BaseTestCase):

    def setUp(self):
        self.grant = Grant(
            name="awesome-grant",
            url="www.awesome-grant.org"
        )

    def test_missing_grant_name(self):
        """Test case for Grant

        Set the name to None
        """
        with self.assertRaises(ValueError):
            self.grant.name = None


if __name__ == "__main__":
    unittest.main()
