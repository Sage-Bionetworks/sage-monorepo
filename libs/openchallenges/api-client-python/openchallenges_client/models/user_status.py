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





class UserStatus(str, Enum):
    """
    The account status of a user
    """

    """
    allowed enum values
    """
    PENDING = 'pending'
    APPROVED = 'approved'
    DISABLED = 'disabled'
    BLACKLIST = 'blacklist'

    @classmethod
    def from_json(cls, json_str: str) -> UserStatus:
        """Create an instance of UserStatus from a JSON string"""
        return UserStatus(json.loads(json_str))


