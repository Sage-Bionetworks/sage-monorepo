# coding: utf-8

from __future__ import absolute_import
import unittest

from openapi_server.models.user import User
from openapi_server.test.integration import BaseTestCase


class TestUserModel(BaseTestCase):

    def setUp(self):
        self.user = User(
            username="awesome-user",

        )

    def test_long_username(self):
        """Test case for User

        Set the username to have >25 length
        """
        with self.assertRaises(ValueError):
            self.user.username = "abcde" * 15

    def test_empty_username(self):
        """Test case for User

        Set the username to have <3 length
        """
        with self.assertRaises(ValueError):
            self.user.username = ""

    def test_invalid_username(self):
        """Test case for User

        Set the username as an invalid string (should be all lowercase
        and single-dash delimited)
        """
        with self.assertRaises(ValueError):
            self.user.username = "Awesome-user"


if __name__ == "__main__":
    unittest.main()
