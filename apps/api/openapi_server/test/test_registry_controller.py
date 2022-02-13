# coding: utf-8

from __future__ import absolute_import
import unittest

from flask import json
from six import BytesIO

from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.registry import Registry  # noqa: E501
from openapi_server.test import BaseTestCase


class TestRegistryController(BaseTestCase):
    """RegistryController integration test stubs"""

    def test_get_registry(self):
        """Test case for get_registry

        Get registry information
        """
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/registry',
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    unittest.main()
