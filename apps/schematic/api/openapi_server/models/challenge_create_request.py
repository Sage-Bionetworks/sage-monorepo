# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from openapi_server.models.base_model_ import Model
from openapi_server.models.challenge_difficulty import ChallengeDifficulty
from openapi_server.models.challenge_incentive_type import ChallengeIncentiveType
from openapi_server.models.challenge_status import ChallengeStatus
from openapi_server.models.challenge_submission_type import ChallengeSubmissionType
import re
from openapi_server import util

from openapi_server.models.challenge_difficulty import ChallengeDifficulty  # noqa: E501
from openapi_server.models.challenge_incentive_type import ChallengeIncentiveType  # noqa: E501
from openapi_server.models.challenge_status import ChallengeStatus  # noqa: E501
from openapi_server.models.challenge_submission_type import ChallengeSubmissionType  # noqa: E501
import re  # noqa: E501

class ChallengeCreateRequest(Model):
    """NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).

    Do not edit the class manually.
    """

    def __init__(self, name=None, display_name=None, description=None, website_url=None, status=None, start_date=None, end_date=None, incentive_types=None, platform_id=None, difficulty=None, submission_types=None, topics=None, input_data_types=None, doi=None, participant_count=None):  # noqa: E501
        """ChallengeCreateRequest - a model defined in OpenAPI

        :param name: The name of this ChallengeCreateRequest.  # noqa: E501
        :type name: str
        :param display_name: The display_name of this ChallengeCreateRequest.  # noqa: E501
        :type display_name: str
        :param description: The description of this ChallengeCreateRequest.  # noqa: E501
        :type description: str
        :param website_url: The website_url of this ChallengeCreateRequest.  # noqa: E501
        :type website_url: str
        :param status: The status of this ChallengeCreateRequest.  # noqa: E501
        :type status: ChallengeStatus
        :param start_date: The start_date of this ChallengeCreateRequest.  # noqa: E501
        :type start_date: date
        :param end_date: The end_date of this ChallengeCreateRequest.  # noqa: E501
        :type end_date: date
        :param incentive_types: The incentive_types of this ChallengeCreateRequest.  # noqa: E501
        :type incentive_types: List[ChallengeIncentiveType]
        :param platform_id: The platform_id of this ChallengeCreateRequest.  # noqa: E501
        :type platform_id: str
        :param difficulty: The difficulty of this ChallengeCreateRequest.  # noqa: E501
        :type difficulty: ChallengeDifficulty
        :param submission_types: The submission_types of this ChallengeCreateRequest.  # noqa: E501
        :type submission_types: List[ChallengeSubmissionType]
        :param topics: The topics of this ChallengeCreateRequest.  # noqa: E501
        :type topics: List[str]
        :param input_data_types: The input_data_types of this ChallengeCreateRequest.  # noqa: E501
        :type input_data_types: List[str]
        :param doi: The doi of this ChallengeCreateRequest.  # noqa: E501
        :type doi: str
        :param participant_count: The participant_count of this ChallengeCreateRequest.  # noqa: E501
        :type participant_count: int
        """
        self.openapi_types = {
            'name': str,
            'display_name': str,
            'description': str,
            'website_url': str,
            'status': ChallengeStatus,
            'start_date': date,
            'end_date': date,
            'incentive_types': List[ChallengeIncentiveType],
            'platform_id': str,
            'difficulty': ChallengeDifficulty,
            'submission_types': List[ChallengeSubmissionType],
            'topics': List[str],
            'input_data_types': List[str],
            'doi': str,
            'participant_count': int
        }

        self.attribute_map = {
            'name': 'name',
            'display_name': 'displayName',
            'description': 'description',
            'website_url': 'websiteUrl',
            'status': 'status',
            'start_date': 'startDate',
            'end_date': 'endDate',
            'incentive_types': 'incentiveTypes',
            'platform_id': 'platformId',
            'difficulty': 'difficulty',
            'submission_types': 'submissionTypes',
            'topics': 'topics',
            'input_data_types': 'inputDataTypes',
            'doi': 'doi',
            'participant_count': 'participantCount'
        }

        self._name = name
        self._display_name = display_name
        self._description = description
        self._website_url = website_url
        self._status = status
        self._start_date = start_date
        self._end_date = end_date
        self._incentive_types = incentive_types
        self._platform_id = platform_id
        self._difficulty = difficulty
        self._submission_types = submission_types
        self._topics = topics
        self._input_data_types = input_data_types
        self._doi = doi
        self._participant_count = participant_count

    @classmethod
    def from_dict(cls, dikt) -> 'ChallengeCreateRequest':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The ChallengeCreateRequest of this ChallengeCreateRequest.  # noqa: E501
        :rtype: ChallengeCreateRequest
        """
        return util.deserialize_model(dikt, cls)

    @property
    def name(self):
        """Gets the name of this ChallengeCreateRequest.

        The name of the a challenge  # noqa: E501

        :return: The name of this ChallengeCreateRequest.
        :rtype: str
        """
        return self._name

    @name.setter
    def name(self, name):
        """Sets the name of this ChallengeCreateRequest.

        The name of the a challenge  # noqa: E501

        :param name: The name of this ChallengeCreateRequest.
        :type name: str
        """
        if name is None:
            raise ValueError("Invalid value for `name`, must not be `None`")  # noqa: E501
        if name is not None and len(name) > 60:
            raise ValueError("Invalid value for `name`, length must be less than or equal to `60`")  # noqa: E501
        if name is not None and len(name) < 3:
            raise ValueError("Invalid value for `name`, length must be greater than or equal to `3`")  # noqa: E501
        if name is not None and not re.search(r'^[a-z0-9]+(?:-[a-z0-9]+)*$', name):  # noqa: E501
            raise ValueError("Invalid value for `name`, must be a follow pattern or equal to `/^[a-z0-9]+(?:-[a-z0-9]+)*$/`")  # noqa: E501

        self._name = name

    @property
    def display_name(self):
        """Gets the display_name of this ChallengeCreateRequest.


        :return: The display_name of this ChallengeCreateRequest.
        :rtype: str
        """
        return self._display_name

    @display_name.setter
    def display_name(self, display_name):
        """Sets the display_name of this ChallengeCreateRequest.


        :param display_name: The display_name of this ChallengeCreateRequest.
        :type display_name: str
        """
        if display_name is not None and len(display_name) > 60:
            raise ValueError("Invalid value for `display_name`, length must be less than or equal to `60`")  # noqa: E501
        if display_name is not None and len(display_name) < 3:
            raise ValueError("Invalid value for `display_name`, length must be greater than or equal to `3`")  # noqa: E501

        self._display_name = display_name

    @property
    def description(self):
        """Gets the description of this ChallengeCreateRequest.

        A short description of the challenge  # noqa: E501

        :return: The description of this ChallengeCreateRequest.
        :rtype: str
        """
        return self._description

    @description.setter
    def description(self, description):
        """Sets the description of this ChallengeCreateRequest.

        A short description of the challenge  # noqa: E501

        :param description: The description of this ChallengeCreateRequest.
        :type description: str
        """
        if description is None:
            raise ValueError("Invalid value for `description`, must not be `None`")  # noqa: E501
        if description is not None and len(description) > 280:
            raise ValueError("Invalid value for `description`, length must be less than or equal to `280`")  # noqa: E501

        self._description = description

    @property
    def website_url(self):
        """Gets the website_url of this ChallengeCreateRequest.


        :return: The website_url of this ChallengeCreateRequest.
        :rtype: str
        """
        return self._website_url

    @website_url.setter
    def website_url(self, website_url):
        """Sets the website_url of this ChallengeCreateRequest.


        :param website_url: The website_url of this ChallengeCreateRequest.
        :type website_url: str
        """

        self._website_url = website_url

    @property
    def status(self):
        """Gets the status of this ChallengeCreateRequest.


        :return: The status of this ChallengeCreateRequest.
        :rtype: ChallengeStatus
        """
        return self._status

    @status.setter
    def status(self, status):
        """Sets the status of this ChallengeCreateRequest.


        :param status: The status of this ChallengeCreateRequest.
        :type status: ChallengeStatus
        """

        self._status = status

    @property
    def start_date(self):
        """Gets the start_date of this ChallengeCreateRequest.


        :return: The start_date of this ChallengeCreateRequest.
        :rtype: date
        """
        return self._start_date

    @start_date.setter
    def start_date(self, start_date):
        """Sets the start_date of this ChallengeCreateRequest.


        :param start_date: The start_date of this ChallengeCreateRequest.
        :type start_date: date
        """

        self._start_date = start_date

    @property
    def end_date(self):
        """Gets the end_date of this ChallengeCreateRequest.


        :return: The end_date of this ChallengeCreateRequest.
        :rtype: date
        """
        return self._end_date

    @end_date.setter
    def end_date(self, end_date):
        """Sets the end_date of this ChallengeCreateRequest.


        :param end_date: The end_date of this ChallengeCreateRequest.
        :type end_date: date
        """

        self._end_date = end_date

    @property
    def incentive_types(self):
        """Gets the incentive_types of this ChallengeCreateRequest.


        :return: The incentive_types of this ChallengeCreateRequest.
        :rtype: List[ChallengeIncentiveType]
        """
        return self._incentive_types

    @incentive_types.setter
    def incentive_types(self, incentive_types):
        """Sets the incentive_types of this ChallengeCreateRequest.


        :param incentive_types: The incentive_types of this ChallengeCreateRequest.
        :type incentive_types: List[ChallengeIncentiveType]
        """

        self._incentive_types = incentive_types

    @property
    def platform_id(self):
        """Gets the platform_id of this ChallengeCreateRequest.

        The unique identifier of a challenge platform  # noqa: E501

        :return: The platform_id of this ChallengeCreateRequest.
        :rtype: str
        """
        return self._platform_id

    @platform_id.setter
    def platform_id(self, platform_id):
        """Sets the platform_id of this ChallengeCreateRequest.

        The unique identifier of a challenge platform  # noqa: E501

        :param platform_id: The platform_id of this ChallengeCreateRequest.
        :type platform_id: str
        """

        self._platform_id = platform_id

    @property
    def difficulty(self):
        """Gets the difficulty of this ChallengeCreateRequest.


        :return: The difficulty of this ChallengeCreateRequest.
        :rtype: ChallengeDifficulty
        """
        return self._difficulty

    @difficulty.setter
    def difficulty(self, difficulty):
        """Sets the difficulty of this ChallengeCreateRequest.


        :param difficulty: The difficulty of this ChallengeCreateRequest.
        :type difficulty: ChallengeDifficulty
        """

        self._difficulty = difficulty

    @property
    def submission_types(self):
        """Gets the submission_types of this ChallengeCreateRequest.


        :return: The submission_types of this ChallengeCreateRequest.
        :rtype: List[ChallengeSubmissionType]
        """
        return self._submission_types

    @submission_types.setter
    def submission_types(self, submission_types):
        """Sets the submission_types of this ChallengeCreateRequest.


        :param submission_types: The submission_types of this ChallengeCreateRequest.
        :type submission_types: List[ChallengeSubmissionType]
        """

        self._submission_types = submission_types

    @property
    def topics(self):
        """Gets the topics of this ChallengeCreateRequest.


        :return: The topics of this ChallengeCreateRequest.
        :rtype: List[str]
        """
        return self._topics

    @topics.setter
    def topics(self, topics):
        """Sets the topics of this ChallengeCreateRequest.


        :param topics: The topics of this ChallengeCreateRequest.
        :type topics: List[str]
        """
        if topics is not None and len(topics) > 30:
            raise ValueError("Invalid value for `topics`, number of items must be less than or equal to `30`")  # noqa: E501

        self._topics = topics

    @property
    def input_data_types(self):
        """Gets the input_data_types of this ChallengeCreateRequest.


        :return: The input_data_types of this ChallengeCreateRequest.
        :rtype: List[str]
        """
        return self._input_data_types

    @input_data_types.setter
    def input_data_types(self, input_data_types):
        """Sets the input_data_types of this ChallengeCreateRequest.


        :param input_data_types: The input_data_types of this ChallengeCreateRequest.
        :type input_data_types: List[str]
        """
        if input_data_types is not None and len(input_data_types) > 10:
            raise ValueError("Invalid value for `input_data_types`, number of items must be less than or equal to `10`")  # noqa: E501

        self._input_data_types = input_data_types

    @property
    def doi(self):
        """Gets the doi of this ChallengeCreateRequest.


        :return: The doi of this ChallengeCreateRequest.
        :rtype: str
        """
        return self._doi

    @doi.setter
    def doi(self, doi):
        """Sets the doi of this ChallengeCreateRequest.


        :param doi: The doi of this ChallengeCreateRequest.
        :type doi: str
        """

        self._doi = doi

    @property
    def participant_count(self):
        """Gets the participant_count of this ChallengeCreateRequest.


        :return: The participant_count of this ChallengeCreateRequest.
        :rtype: int
        """
        return self._participant_count

    @participant_count.setter
    def participant_count(self, participant_count):
        """Sets the participant_count of this ChallengeCreateRequest.


        :param participant_count: The participant_count of this ChallengeCreateRequest.
        :type participant_count: int
        """
        if participant_count is not None and participant_count < 0:  # noqa: E501
            raise ValueError("Invalid value for `participant_count`, must be a value greater than or equal to `0`")  # noqa: E501

        self._participant_count = participant_count
