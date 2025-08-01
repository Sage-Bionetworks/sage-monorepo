# coding: utf-8

"""
    OpenChallenges API

    Discover, explore, and contribute to open biomedical challenges.

    The version of the OpenAPI document: 1.0.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from openchallenges_api_client_python.models.image import Image

class TestImage(unittest.TestCase):
    """Image unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> Image:
        """Test Image
            include_optional is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `Image`
        """
        model = Image()
        if include_optional:
            return Image(
                url = 'http://example.com/an-image.png'
            )
        else:
            return Image(
                url = 'http://example.com/an-image.png',
        )
        """

    def testImage(self):
        """Test Image"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
