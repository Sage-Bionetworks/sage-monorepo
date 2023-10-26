# coding: utf-8

"""
    OpenChallenges REST API

    No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)

    The version of the OpenAPI document: 1.0.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import json
import pprint
import re  # noqa: F401
from aenum import Enum, no_arg





class ImageAspectRatio(str, Enum):
    """
    The aspect ratio of the image (the height of the image must be specified).
    """

    """
    allowed enum values
    """
    ORIGINAL = 'original'
    ENUM_16_9 = '16_9'
    ENUM_1_1 = '1_1'
    ENUM_3_2 = '3_2'
    ENUM_2_3 = '2_3'

    @classmethod
    def from_json(cls, json_str: str) -> ImageAspectRatio:
        """Create an instance of ImageAspectRatio from a JSON string"""
        return ImageAspectRatio(json.loads(json_str))

