# coding: utf-8

"""
    OpenChallenges REST API

    No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)

    The version of the OpenAPI document: 1.0.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


from __future__ import annotations
import pprint
import re  # noqa: F401
import json


from typing import Optional
from pydantic import BaseModel, Field, StrictInt, StrictStr


class BasicError(BaseModel):
    """
    Problem details (tools.ietf.org/html/rfc7807)
    """

    title: StrictStr = Field(
        ..., description="A human readable documentation for the problem type"
    )
    status: StrictInt = Field(..., description="The HTTP status code")
    detail: Optional[StrictStr] = Field(
        None,
        description="A human readable explanation specific to this occurrence of the problem",
    )
    type: Optional[StrictStr] = Field(
        None, description="An absolute URI that identifies the problem type"
    )
    __properties = ["title", "status", "detail", "type"]

    class Config:
        """Pydantic configuration"""

        allow_population_by_field_name = True
        validate_assignment = True

    def to_str(self) -> str:
        """Returns the string representation of the model using alias"""
        return pprint.pformat(self.dict(by_alias=True))

    def to_json(self) -> str:
        """Returns the JSON representation of the model using alias"""
        return json.dumps(self.to_dict())

    @classmethod
    def from_json(cls, json_str: str) -> BasicError:
        """Create an instance of BasicError from a JSON string"""
        return cls.from_dict(json.loads(json_str))

    def to_dict(self):
        """Returns the dictionary representation of the model using alias"""
        _dict = self.dict(by_alias=True, exclude={}, exclude_none=True)
        return _dict

    @classmethod
    def from_dict(cls, obj: dict) -> BasicError:
        """Create an instance of BasicError from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return BasicError.parse_obj(obj)

        _obj = BasicError.parse_obj(
            {
                "title": obj.get("title"),
                "status": obj.get("status"),
                "detail": obj.get("detail"),
                "type": obj.get("type"),
            }
        )
        return _obj
