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

from datetime import date, datetime
from typing import List, Optional
from pydantic import BaseModel, Field, StrictInt, StrictStr, conlist, constr, validator
from openchallenges_client.models.challenge_difficulty import ChallengeDifficulty
from openchallenges_client.models.challenge_incentive import ChallengeIncentive
from openchallenges_client.models.challenge_status import ChallengeStatus
from openchallenges_client.models.challenge_submission_type import ChallengeSubmissionType
from openchallenges_client.models.simple_challenge_input_data_type import SimpleChallengeInputDataType
from openchallenges_client.models.simple_challenge_platform import SimpleChallengePlatform

class Challenge(BaseModel):
    """
    A challenge
    """
    id: StrictInt = Field(..., description="The unique identifier of the challenge.")
    slug: constr(strict=True, max_length=60, min_length=3) = Field(..., description="The slug of the challenge.")
    name: constr(strict=True, max_length=60, min_length=3) = Field(..., description="The name of the challenge.")
    headline: Optional[constr(strict=True, max_length=80, min_length=0)] = Field(None, description="The headline of the challenge.")
    description: constr(strict=True, max_length=280, min_length=0) = Field(..., description="The description of the challenge.")
    doi: Optional[StrictStr] = None
    status: ChallengeStatus = Field(...)
    difficulty: ChallengeDifficulty = Field(...)
    platform: SimpleChallengePlatform = Field(...)
    website_url: Optional[StrictStr] = Field(None, alias="websiteUrl")
    avatar_url: Optional[StrictStr] = Field(None, alias="avatarUrl")
    incentives: conlist(ChallengeIncentive) = Field(...)
    submission_types: conlist(ChallengeSubmissionType) = Field(..., alias="submissionTypes")
    input_data_types: Optional[conlist(SimpleChallengeInputDataType)] = Field(None, alias="inputDataTypes")
    start_date: Optional[date] = Field(None, alias="startDate", description="The start date of the challenge.")
    end_date: Optional[date] = Field(None, alias="endDate", description="The end date of the challenge.")
    starred_count: StrictInt = Field(..., alias="starredCount", description="The number of times the challenge has been starred by users.")
    created_at: datetime = Field(..., alias="createdAt")
    updated_at: datetime = Field(..., alias="updatedAt")
    __properties = ["id", "slug", "name", "headline", "description", "doi", "status", "difficulty", "platform", "websiteUrl", "avatarUrl", "incentives", "submissionTypes", "inputDataTypes", "startDate", "endDate", "starredCount", "createdAt", "updatedAt"]

    @validator('slug')
    def slug_validate_regular_expression(cls, value):
        """Validates the regular expression"""
        if not re.match(r"^[a-z0-9]+(?:-[a-z0-9]+)*$", value):
            raise ValueError(r"must validate the regular expression /^[a-z0-9]+(?:-[a-z0-9]+)*$/")
        return value

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
    def from_json(cls, json_str: str) -> Challenge:
        """Create an instance of Challenge from a JSON string"""
        return cls.from_dict(json.loads(json_str))

    def to_dict(self):
        """Returns the dictionary representation of the model using alias"""
        _dict = self.dict(by_alias=True,
                          exclude={
                          },
                          exclude_none=True)
        # override the default output from pydantic by calling `to_dict()` of platform
        if self.platform:
            _dict['platform'] = self.platform.to_dict()
        # override the default output from pydantic by calling `to_dict()` of each item in input_data_types (list)
        _items = []
        if self.input_data_types:
            for _item in self.input_data_types:
                if _item:
                    _items.append(_item.to_dict())
            _dict['inputDataTypes'] = _items
        # set to None if start_date (nullable) is None
        # and __fields_set__ contains the field
        if self.start_date is None and "start_date" in self.__fields_set__:
            _dict['startDate'] = None

        # set to None if end_date (nullable) is None
        # and __fields_set__ contains the field
        if self.end_date is None and "end_date" in self.__fields_set__:
            _dict['endDate'] = None

        return _dict

    @classmethod
    def from_dict(cls, obj: dict) -> Challenge:
        """Create an instance of Challenge from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return Challenge.parse_obj(obj)

        _obj = Challenge.parse_obj({
            "id": obj.get("id"),
            "slug": obj.get("slug"),
            "name": obj.get("name"),
            "headline": obj.get("headline"),
            "description": obj.get("description"),
            "doi": obj.get("doi"),
            "status": obj.get("status"),
            "difficulty": obj.get("difficulty"),
            "platform": SimpleChallengePlatform.from_dict(obj.get("platform")) if obj.get("platform") is not None else None,
            "website_url": obj.get("websiteUrl"),
            "avatar_url": obj.get("avatarUrl"),
            "incentives": obj.get("incentives"),
            "submission_types": obj.get("submissionTypes"),
            "input_data_types": [SimpleChallengeInputDataType.from_dict(_item) for _item in obj.get("inputDataTypes")] if obj.get("inputDataTypes") is not None else None,
            "start_date": obj.get("startDate"),
            "end_date": obj.get("endDate"),
            "starred_count": obj.get("starredCount") if obj.get("starredCount") is not None else 0,
            "created_at": obj.get("createdAt"),
            "updated_at": obj.get("updatedAt")
        })
        return _obj


