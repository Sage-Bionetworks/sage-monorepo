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


from typing import List
from pydantic import BaseModel, Field, StrictBool, StrictInt, conlist
from openchallenges_client.models.challenge_input_data_type import (
    ChallengeInputDataType,
)


class ChallengeInputDataTypesPage(BaseModel):
    """
    A page of challenge input data types.
    """

    number: StrictInt = Field(..., description="The page number.")
    size: StrictInt = Field(..., description="The number of items in a single page.")
    total_elements: StrictInt = Field(
        ...,
        alias="totalElements",
        description="Total number of elements in the result set.",
    )
    total_pages: StrictInt = Field(
        ..., alias="totalPages", description="Total number of pages in the result set."
    )
    has_next: StrictBool = Field(
        ..., alias="hasNext", description="Returns if there is a next page."
    )
    has_previous: StrictBool = Field(
        ..., alias="hasPrevious", description="Returns if there is a previous page."
    )
    challenge_input_data_types: conlist(ChallengeInputDataType) = Field(
        ...,
        alias="challengeInputDataTypes",
        description="A list of challenge input data types.",
    )
    __properties = [
        "number",
        "size",
        "totalElements",
        "totalPages",
        "hasNext",
        "hasPrevious",
        "challengeInputDataTypes",
    ]

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
    def from_json(cls, json_str: str) -> ChallengeInputDataTypesPage:
        """Create an instance of ChallengeInputDataTypesPage from a JSON string"""
        return cls.from_dict(json.loads(json_str))

    def to_dict(self):
        """Returns the dictionary representation of the model using alias"""
        _dict = self.dict(by_alias=True, exclude={}, exclude_none=True)
        # override the default output from pydantic by calling `to_dict()` of each item in challenge_input_data_types (list)
        _items = []
        if self.challenge_input_data_types:
            for _item in self.challenge_input_data_types:
                if _item:
                    _items.append(_item.to_dict())
            _dict["challengeInputDataTypes"] = _items
        return _dict

    @classmethod
    def from_dict(cls, obj: dict) -> ChallengeInputDataTypesPage:
        """Create an instance of ChallengeInputDataTypesPage from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return ChallengeInputDataTypesPage.parse_obj(obj)

        _obj = ChallengeInputDataTypesPage.parse_obj(
            {
                "number": obj.get("number"),
                "size": obj.get("size"),
                "total_elements": obj.get("totalElements"),
                "total_pages": obj.get("totalPages"),
                "has_next": obj.get("hasNext"),
                "has_previous": obj.get("hasPrevious"),
                "challenge_input_data_types": [
                    ChallengeInputDataType.from_dict(_item)
                    for _item in obj.get("challengeInputDataTypes")
                ]
                if obj.get("challengeInputDataTypes") is not None
                else None,
            }
        )
        return _obj
