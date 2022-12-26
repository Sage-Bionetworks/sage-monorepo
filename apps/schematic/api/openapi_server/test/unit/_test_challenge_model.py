# coding: utf-8

from __future__ import absolute_import
from datetime import date
import unittest

from openapi_server.models.challenge import Challenge
from openapi_server.test.integration import BaseTestCase


class TestChallengeModel(BaseTestCase):

    def setUp(self):
        self.challenge = Challenge(
            name="Awesome Challenge",
            start_date=date(2021, 1, 1),
            end_date=date(2021, 1, 31),
            status="closed"
        )

    def test_missing_challenge_name(self):
        """Test case for Challenge

        Set the name to None
        """
        with self.assertRaises(ValueError):
            self.challenge.name = None

    # def test_missing_challenge_start(self):
    #     """Test case for Challenge

    #     Set the state date to None
    #     """
    #     with self.assertRaises(ValueError):
    #         self.challenge.start_date = None

    # def test_missing_challenge_end(self):
    #     """Test case for Challenge

    #     Set the end date to None
    #     """
    #     with self.assertRaises(ValueError):
    #         self.challenge.end_date = None

    def test_missing_challenge_status(self):
        """Test case for Challenge

        Set the status to None
        """
        with self.assertRaises(ValueError):
            self.challenge.status = None


if __name__ == "__main__":
    unittest.main()
