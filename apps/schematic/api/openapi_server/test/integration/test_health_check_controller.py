# coding: utf-8

from __future__ import absolute_import
import unittest

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


class TestHealthCheckController(BaseTestCase):
    """HealthCheckController integration test stubs"""

    def setUp(self):
        util.connect_db()

    def tearDown(self):
        util.disconnect_db()

    def test_get_health_check_with_status200(self):
        """Test case for get_health_check

        Create a health check (200)
        """
        response = self.client.open(
            "/api/v1/healthcheck",
            method="GET",
            headers=RESPONSE_HEADERS
        )
        self.assert200(
            response,
            f"Response body is: {response.data.decode('utf-8')}"
        )


if __name__ == "__main__":
    unittest.main()
