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

from pydantic import BaseModel, ConfigDict, Field, StrictBool, StrictInt
from typing import Any, ClassVar, Dict, List
from openchallenges_client.models.challenge_contribution import ChallengeContribution
from typing import Optional, Set
from typing_extensions import Self

class ChallengeContributionsPage(BaseModel):
    """
    A page of challenge challenge contributions.
    """ # noqa: E501
    number: StrictInt = Field(description="The page number.")
    size: StrictInt = Field(description="The number of items in a single page.")
    total_elements: StrictInt = Field(description="Total number of elements in the result set.", alias="totalElements")
    total_pages: StrictInt = Field(description="Total number of pages in the result set.", alias="totalPages")
    has_next: StrictBool = Field(description="Returns if there is a next page.", alias="hasNext")
    has_previous: StrictBool = Field(description="Returns if there is a previous page.", alias="hasPrevious")
    challenge_contributions: List[ChallengeContribution] = Field(description="A list of challenge contributions.", alias="challengeContributions")
    __properties: ClassVar[List[str]] = ["number", "size", "totalElements", "totalPages", "hasNext", "hasPrevious", "challengeContributions"]

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
        """Create an instance of ChallengeContributionsPage from a JSON string"""
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
        # override the default output from pydantic by calling `to_dict()` of each item in challenge_contributions (list)
        _items = []
        if self.challenge_contributions:
            for _item in self.challenge_contributions:
                if _item:
                    _items.append(_item.to_dict())
            _dict['challengeContributions'] = _items
        return _dict

    @classmethod
    def from_dict(cls, obj: Optional[Dict[str, Any]]) -> Optional[Self]:
        """Create an instance of ChallengeContributionsPage from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "number": obj.get("number"),
            "size": obj.get("size"),
            "totalElements": obj.get("totalElements"),
            "totalPages": obj.get("totalPages"),
            "hasNext": obj.get("hasNext"),
            "hasPrevious": obj.get("hasPrevious"),
            "challengeContributions": [ChallengeContribution.from_dict(_item) for _item in obj["challengeContributions"]] if obj.get("challengeContributions") is not None else None
        })
        return _obj


