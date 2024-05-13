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

from pydantic import BaseModel, ConfigDict, Field, StrictStr
from typing import Any, ClassVar, Dict, List, Optional
from typing_extensions import Annotated
from openchallenges_client.models.edam_concept_direction import EdamConceptDirection
from openchallenges_client.models.edam_concept_sort import EdamConceptSort
from openchallenges_client.models.edam_section import EdamSection
from typing import Optional, Set
from typing_extensions import Self


class EdamConceptSearchQuery(BaseModel):
    """
    An EDAM concept search query.
    """  # noqa: E501

    page_number: Optional[Annotated[int, Field(strict=True, ge=0)]] = Field(
        default=0, description="The page number.", alias="pageNumber"
    )
    page_size: Optional[Annotated[int, Field(strict=True, ge=1)]] = Field(
        default=100,
        description="The number of items in a single page.",
        alias="pageSize",
    )
    sort: Optional[EdamConceptSort] = None
    direction: Optional[EdamConceptDirection] = None
    search_terms: Optional[StrictStr] = Field(
        default=None,
        description="A string of search terms used to filter the results.",
        alias="searchTerms",
    )
    sections: Optional[List[EdamSection]] = Field(
        default=None,
        description="An array of EDAM sections (sub-ontologies) used to filter the results.",
    )
    __properties: ClassVar[List[str]] = [
        "pageNumber",
        "pageSize",
        "sort",
        "direction",
        "searchTerms",
        "sections",
    ]

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
        """Create an instance of EdamConceptSearchQuery from a JSON string"""
        return cls.from_dict(json.loads(json_str))

    def to_dict(self) -> Dict[str, Any]:
        """Return the dictionary representation of the model using alias.

        This has the following differences from calling pydantic's
        `self.model_dump(by_alias=True)`:

        * `None` is only added to the output dict for nullable fields that
          were set at model initialization. Other fields with value `None`
          are ignored.
        """
        excluded_fields: Set[str] = set([])

        _dict = self.model_dump(
            by_alias=True,
            exclude=excluded_fields,
            exclude_none=True,
        )
        # set to None if direction (nullable) is None
        # and model_fields_set contains the field
        if self.direction is None and "direction" in self.model_fields_set:
            _dict["direction"] = None

        return _dict

    @classmethod
    def from_dict(cls, obj: Optional[Dict[str, Any]]) -> Optional[Self]:
        """Create an instance of EdamConceptSearchQuery from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate(
            {
                "pageNumber": obj.get("pageNumber")
                if obj.get("pageNumber") is not None
                else 0,
                "pageSize": obj.get("pageSize")
                if obj.get("pageSize") is not None
                else 100,
                "sort": obj.get("sort"),
                "direction": obj.get("direction"),
                "searchTerms": obj.get("searchTerms"),
                "sections": obj.get("sections"),
            }
        )
        return _obj
