# coding: utf-8

"""
    OpenChallenges REST API

    No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)

    The version of the OpenAPI document: 1.0.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


from __future__ import annotations
import json
from enum import Enum
from typing_extensions import Self


class ChallengeIncentive(str, Enum):
    """
    The incentive type of the challenge.
    """

    """
    allowed enum values
    """
    MONETARY = "monetary"
    PUBLICATION = "publication"
    SPEAKING_ENGAGEMENT = "speaking_engagement"
    OTHER = "other"

    @classmethod
    def from_json(cls, json_str: str) -> Self:
        """Create an instance of ChallengeIncentive from a JSON string"""
        return cls(json.loads(json_str))
