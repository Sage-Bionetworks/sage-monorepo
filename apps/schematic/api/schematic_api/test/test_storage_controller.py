# coding: utf-8

from __future__ import absolute_import
import unittest

from flask import json
from six import BytesIO

from schematic_api.models.basic_error import BasicError  # noqa: E501
from schematic_api.test import BaseTestCase


class TestStorageController(BaseTestCase):
    """StorageController integration test stubs"""

    def test_list_storage_project_datasets(self):
        """Test case for list_storage_project_datasets

        Gets all datasets in folder under a given storage project that the current user has access to.
        """
        headers = { 
            'Accept': 'application/problem+json',
        }
        response = self.client.open(
            '/v1/storages/projects/{project_id}/datasets'.format(project_id='project_id_example'),
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    unittest.main()
