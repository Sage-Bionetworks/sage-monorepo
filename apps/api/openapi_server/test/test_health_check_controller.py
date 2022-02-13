# coding: utf-8

from __future__ import absolute_import
import unittest

from flask import json
from six import BytesIO

from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.health_check import HealthCheck  # noqa: E501
from openapi_server.test import BaseTestCase


class TestHealthCheckController(BaseTestCase):
    """HealthCheckController integration test stubs"""

    def test_get_health_check(self):
        """Test case for get_health_check

        Get health check information
        """
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/healthcheck',
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    unittest.main()
