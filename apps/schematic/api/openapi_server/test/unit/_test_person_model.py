# coding: utf-8

from __future__ import absolute_import
import unittest

from openapi_server.models.person import Person
from openapi_server.test.integration import BaseTestCase


class TestPersonModel(BaseTestCase):

    def setUp(self):
        self.person = Person(
            first_name="John",
            last_name="Smith",
            email="john.smith@example.org"
        )

    def test_missing_first_name(self):
        """Test case for Person

        Set the first name to None
        """
        with self.assertRaises(ValueError):
            self.person.first_name = None

    def test_missing_last_name(self):
        """Test case for Person

        Set the last name to None
        """
        with self.assertRaises(ValueError):
            self.person.last_name = None


if __name__ == "__main__":
    unittest.main()
