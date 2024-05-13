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
from pydantic import BaseModel, ConfigDict, Field, StrictInt, field_validator
from typing import Any, ClassVar, Dict, List, Optional
from typing_extensions import Annotated
from openchallenges_client.models.challenge_category import ChallengeCategory
from openchallenges_client.models.challenge_incentive import ChallengeIncentive
from openchallenges_client.models.challenge_status import ChallengeStatus
from openchallenges_client.models.challenge_submission_type import ChallengeSubmissionType
from openchallenges_client.models.edam_concept import EdamConcept
from openchallenges_client.models.simple_challenge_platform import SimpleChallengePlatform
from typing import Optional, Set
from typing_extensions import Self

class Challenge(BaseModel):
    """
    A challenge
    """ # noqa: E501
    id: StrictInt = Field(description="The unique identifier of the challenge.")
    slug: Annotated[str, Field(min_length=3, strict=True, max_length=255)] = Field(description="The unique slug of the challenge.")
    name: Annotated[str, Field(min_length=3, strict=True, max_length=255)] = Field(description="The name of the challenge.")
    headline: Optional[Annotated[str, Field(min_length=0, strict=True, max_length=80)]] = Field(default=None, description="The headline of the challenge.")
    description: Annotated[str, Field(min_length=0, strict=True, max_length=1000)] = Field(description="The description of the challenge.")
    doi: Optional[Annotated[str, Field(strict=True, max_length=120)]] = Field(default=None, description="The DOI of the challenge.")
    status: ChallengeStatus
    platform: Optional[SimpleChallengePlatform] = None
    website_url: Optional[Annotated[str, Field(strict=True, max_length=500)]] = Field(default=None, description="A URL to the website or image.", alias="websiteUrl")
    avatar_url: Optional[Annotated[str, Field(strict=True, max_length=500)]] = Field(default=None, description="A URL to the website or image.", alias="avatarUrl")
    incentives: List[ChallengeIncentive]
    submission_types: List[ChallengeSubmissionType] = Field(alias="submissionTypes")
    input_data_types: Optional[List[Optional[EdamConcept]]] = Field(default=None, alias="inputDataTypes")
    categories: List[ChallengeCategory]
    start_date: Optional[date] = Field(default=None, description="The start date of the challenge.", alias="startDate")
    end_date: Optional[date] = Field(default=None, description="The end date of the challenge.", alias="endDate")
    starred_count: Annotated[int, Field(strict=True, ge=0)] = Field(description="The number of times the challenge has been starred by users.", alias="starredCount")
    operation: Optional[EdamConcept] = None
    created_at: datetime = Field(description="Datetime when the object was added to the database.", alias="createdAt")
    updated_at: datetime = Field(description="Datetime when the object was last modified in the database.", alias="updatedAt")
    __properties: ClassVar[List[str]] = ["id", "slug", "name", "headline", "description", "doi", "status", "platform", "websiteUrl", "avatarUrl", "incentives", "submissionTypes", "inputDataTypes", "categories", "startDate", "endDate", "starredCount", "operation", "createdAt", "updatedAt"]

    @field_validator('slug')
    def slug_validate_regular_expression(cls, value):
        """Validates the regular expression"""
        if not re.match(r"^[a-z0-9]+(?:-[a-z0-9]+)*$", value):
            raise ValueError(r"must validate the regular expression /^[a-z0-9]+(?:-[a-z0-9]+)*$/")
        return value

    model_config = ConfigDict(
        populate_by_name=True,
        validate_assignment=True,
        protected_namespaces=(),
    )


    def to_str(self) -> str:
        """Returns the string representation of the model using alias"""
        return pprint.pformat(self.model_dump(by_alias=True))

    def to_json(self) -> str:
        """Returns the JSON representation of the model using alias"""
        # TODO: pydantic v2: use .model_dump_json(by_alias=True, exclude_unset=True) instead
        return json.dumps(self.to_dict())

    @classmethod
    def from_json(cls, json_str: str) -> Optional[Self]:
        """Create an instance of Challenge from a JSON string"""
        return cls.from_dict(json.loads(json_str))

    def to_dict(self) -> Dict[str, Any]:
        """Return the dictionary representation of the model using alias.

        This has the following differences from calling pydantic's
        `self.model_dump(by_alias=True)`:

        * `None` is only added to the output dict for nullable fields that
          were set at model initialization. Other fields with value `None`
          are ignored.
        """
        excluded_fields: Set[str] = set([
        ])

        _dict = self.model_dump(
            by_alias=True,
            exclude=excluded_fields,
            exclude_none=True,
        )
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
        # override the default output from pydantic by calling `to_dict()` of operation
        if self.operation:
            _dict['operation'] = self.operation.to_dict()
        # set to None if headline (nullable) is None
        # and model_fields_set contains the field
        if self.headline is None and "headline" in self.model_fields_set:
            _dict['headline'] = None

        # set to None if doi (nullable) is None
        # and model_fields_set contains the field
        if self.doi is None and "doi" in self.model_fields_set:
            _dict['doi'] = None

        # set to None if platform (nullable) is None
        # and model_fields_set contains the field
        if self.platform is None and "platform" in self.model_fields_set:
            _dict['platform'] = None

        # set to None if website_url (nullable) is None
        # and model_fields_set contains the field
        if self.website_url is None and "website_url" in self.model_fields_set:
            _dict['websiteUrl'] = None

        # set to None if avatar_url (nullable) is None
        # and model_fields_set contains the field
        if self.avatar_url is None and "avatar_url" in self.model_fields_set:
            _dict['avatarUrl'] = None

        # set to None if start_date (nullable) is None
        # and model_fields_set contains the field
        if self.start_date is None and "start_date" in self.model_fields_set:
            _dict['startDate'] = None

        # set to None if end_date (nullable) is None
        # and model_fields_set contains the field
        if self.end_date is None and "end_date" in self.model_fields_set:
            _dict['endDate'] = None

        # set to None if operation (nullable) is None
        # and model_fields_set contains the field
        if self.operation is None and "operation" in self.model_fields_set:
            _dict['operation'] = None

        return _dict

    @classmethod
    def from_dict(cls, obj: Optional[Dict[str, Any]]) -> Optional[Self]:
        """Create an instance of Challenge from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "id": obj.get("id"),
            "slug": obj.get("slug"),
            "name": obj.get("name"),
            "headline": obj.get("headline"),
            "description": obj.get("description"),
            "doi": obj.get("doi"),
            "status": obj.get("status"),
            "platform": SimpleChallengePlatform.from_dict(obj["platform"]) if obj.get("platform") is not None else None,
            "websiteUrl": obj.get("websiteUrl"),
            "avatarUrl": obj.get("avatarUrl"),
            "incentives": obj.get("incentives"),
            "submissionTypes": obj.get("submissionTypes"),
            "inputDataTypes": [EdamConcept.from_dict(_item) for _item in obj["inputDataTypes"]] if obj.get("inputDataTypes") is not None else None,
            "categories": obj.get("categories"),
            "startDate": obj.get("startDate"),
            "endDate": obj.get("endDate"),
            "starredCount": obj.get("starredCount") if obj.get("starredCount") is not None else 0,
            "operation": EdamConcept.from_dict(obj["operation"]) if obj.get("operation") is not None else None,
            "createdAt": obj.get("createdAt"),
            "updatedAt": obj.get("updatedAt")
        })
        return _obj


