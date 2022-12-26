# coding: utf-8

from __future__ import absolute_import
import unittest

from openapi_server.models.tag import Tag
from openapi_server.test.integration import BaseTestCase


class TestTagModel(BaseTestCase):

    def setUp(self):
        self.tag = Tag(
            id="awesome-tag"
        )

    def test_long_tag_id(self):
        """Test case for Tag

        Set the tag ID to have >60 length
        """
        with self.assertRaises(ValueError):
            self.tag.id = "abcde" * 15

    def test_empty_tag_id(self):
        """Test case for Tag

        Set the tag ID to have <1 length
        """
        with self.assertRaises(ValueError):
            self.tag.id = ""

    def test_invalid_tag_id(self):
        """Test case for Tag

        Set the tag ID as an invalid string (should be all lowercase
        and single-dash delimited)
        """
        with self.assertRaises(ValueError):
            self.tag.id = "Awesome-tag"


if __name__ == "__main__":
    unittest.main()
