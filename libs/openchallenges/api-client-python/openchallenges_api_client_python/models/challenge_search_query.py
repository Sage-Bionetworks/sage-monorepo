# coding: utf-8

"""
    OpenChallenges API

    Discover, explore, and contribute to open biomedical challenges.

    The version of the OpenAPI document: 1.0.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


from __future__ import annotations
import pprint
import re  # noqa: F401
import json

from datetime import date
from pydantic import BaseModel, ConfigDict, Field, StrictInt, StrictStr, field_validator
from typing import Any, ClassVar, Dict, List, Optional
from typing_extensions import Annotated
from openchallenges_api_client_python.models.challenge_category import ChallengeCategory
from openchallenges_api_client_python.models.challenge_direction import ChallengeDirection
from openchallenges_api_client_python.models.challenge_incentive import ChallengeIncentive
from openchallenges_api_client_python.models.challenge_sort import ChallengeSort
from openchallenges_api_client_python.models.challenge_status import ChallengeStatus
from openchallenges_api_client_python.models.challenge_submission_type import ChallengeSubmissionType
from typing import Optional, Set
from typing_extensions import Self

class ChallengeSearchQuery(BaseModel):
    """
    A challenge search query.
    """ # noqa: E501
    page_number: Optional[Annotated[int, Field(strict=True, ge=0)]] = Field(default=0, description="The page number.", alias="pageNumber")
    page_size: Optional[Annotated[int, Field(strict=True, ge=1)]] = Field(default=100, description="The number of items in a single page.", alias="pageSize")
    sort: Optional[ChallengeSort] = ChallengeSort.RELEVANCE
    sort_seed: Optional[Annotated[int, Field(le=2147483647, strict=True, ge=0)]] = Field(default=None, description="The seed that initializes the random sorter.", alias="sortSeed")
    direction: Optional[ChallengeDirection] = None
    incentives: Optional[List[ChallengeIncentive]] = Field(default=None, description="An array of challenge incentive types used to filter the results.")
    min_start_date: Optional[date] = Field(default=None, description="Keep the challenges that start at this date or later.", alias="minStartDate")
    max_start_date: Optional[date] = Field(default=None, description="Keep the challenges that start at this date or sooner.", alias="maxStartDate")
    platforms: Optional[List[Annotated[str, Field(min_length=3, strict=True, max_length=30)]]] = Field(default=None, description="An array of challenge platform ids used to filter the results.")
    organizations: Optional[List[StrictInt]] = Field(default=None, description="An array of organization ids used to filter the results.")
    status: Optional[List[ChallengeStatus]] = Field(default=None, description="An array of challenge status used to filter the results.")
    submission_types: Optional[List[ChallengeSubmissionType]] = Field(default=None, description="An array of challenge submission types used to filter the results.", alias="submissionTypes")
    input_data_types: Optional[List[StrictInt]] = Field(default=None, description="An array of EDAM concept ID used to filter the results.", alias="inputDataTypes")
    operations: Optional[List[StrictInt]] = Field(default=None, description="An array of EDAM concept ID used to filter the results.")
    categories: Optional[List[ChallengeCategory]] = Field(default=None, description="The array of challenge categories used to filter the results.")
    search_terms: Optional[StrictStr] = Field(default=None, description="A string of search terms used to filter the results.", alias="searchTerms")
    __properties: ClassVar[List[str]] = ["pageNumber", "pageSize", "sort", "sortSeed", "direction", "incentives", "minStartDate", "maxStartDate", "platforms", "organizations", "status", "submissionTypes", "inputDataTypes", "operations", "categories", "searchTerms"]

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
        """Create an instance of ChallengeSearchQuery from a JSON string"""
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
        # set to None if sort_seed (nullable) is None
        # and model_fields_set contains the field
        if self.sort_seed is None and "sort_seed" in self.model_fields_set:
            _dict['sortSeed'] = None

        # set to None if direction (nullable) is None
        # and model_fields_set contains the field
        if self.direction is None and "direction" in self.model_fields_set:
            _dict['direction'] = None

        # set to None if min_start_date (nullable) is None
        # and model_fields_set contains the field
        if self.min_start_date is None and "min_start_date" in self.model_fields_set:
            _dict['minStartDate'] = None

        # set to None if max_start_date (nullable) is None
        # and model_fields_set contains the field
        if self.max_start_date is None and "max_start_date" in self.model_fields_set:
            _dict['maxStartDate'] = None

        return _dict

    @classmethod
    def from_dict(cls, obj: Optional[Dict[str, Any]]) -> Optional[Self]:
        """Create an instance of ChallengeSearchQuery from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "pageNumber": obj.get("pageNumber") if obj.get("pageNumber") is not None else 0,
            "pageSize": obj.get("pageSize") if obj.get("pageSize") is not None else 100,
            "sort": obj.get("sort") if obj.get("sort") is not None else ChallengeSort.RELEVANCE,
            "sortSeed": obj.get("sortSeed"),
            "direction": obj.get("direction"),
            "incentives": obj.get("incentives"),
            "minStartDate": obj.get("minStartDate"),
            "maxStartDate": obj.get("maxStartDate"),
            "platforms": obj.get("platforms"),
            "organizations": obj.get("organizations"),
            "status": obj.get("status"),
            "submissionTypes": obj.get("submissionTypes"),
            "inputDataTypes": obj.get("inputDataTypes"),
            "operations": obj.get("operations"),
            "categories": obj.get("categories"),
            "searchTerms": obj.get("searchTerms")
        })
        return _obj


