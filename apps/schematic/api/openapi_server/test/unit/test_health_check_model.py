# coding: utf-8

from __future__ import absolute_import
import unittest

from openapi_server.models.health_check import HealthCheck
from openapi_server.test.integration import BaseTestCase


class TestHealthCheckModel(BaseTestCase):

    def setUp(self):
        self.health_check = HealthCheck(
            status="pass"
        )

    def test_status_allowed_values(self):
        """Test case for HealthCheck

        Set the status to "warning" (accepted value: "warn")
        """
        with self.assertRaises(ValueError):
            self.health_check.status = "warning"


if __name__ == "__main__":
    unittest.main()
