# coding: utf-8

# flake8: noqa

"""
    OpenChallenges REST API

    No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)

    The version of the OpenAPI document: 1.0.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


__version__ = "1.0.0"

# import apis into sdk package
from openchallenges_client.api.challenge_api import ChallengeApi
from openchallenges_client.api.challenge_analytics_api import ChallengeAnalyticsApi
from openchallenges_client.api.challenge_contribution_api import (
    ChallengeContributionApi,
)
from openchallenges_client.api.challenge_platform_api import ChallengePlatformApi
from openchallenges_client.api.edam_concept_api import EdamConceptApi
from openchallenges_client.api.image_api import ImageApi
from openchallenges_client.api.organization_api import OrganizationApi
from openchallenges_client.api.user_api import UserApi

# import ApiClient
from openchallenges_client.api_response import ApiResponse
from openchallenges_client.api_client import ApiClient
from openchallenges_client.configuration import Configuration
from openchallenges_client.exceptions import OpenApiException
from openchallenges_client.exceptions import ApiTypeError
from openchallenges_client.exceptions import ApiValueError
from openchallenges_client.exceptions import ApiKeyError
from openchallenges_client.exceptions import ApiAttributeError
from openchallenges_client.exceptions import ApiException

# import models into sdk package
from openchallenges_client.models.basic_error import BasicError
from openchallenges_client.models.challenge import Challenge
from openchallenges_client.models.challenge_category import ChallengeCategory
from openchallenges_client.models.challenge_contribution import ChallengeContribution
from openchallenges_client.models.challenge_contribution_role import (
    ChallengeContributionRole,
)
from openchallenges_client.models.challenge_contributions_page import (
    ChallengeContributionsPage,
)
from openchallenges_client.models.challenge_direction import ChallengeDirection
from openchallenges_client.models.challenge_incentive import ChallengeIncentive
from openchallenges_client.models.challenge_platform import ChallengePlatform
from openchallenges_client.models.challenge_platform_direction import (
    ChallengePlatformDirection,
)
from openchallenges_client.models.challenge_platform_search_query import (
    ChallengePlatformSearchQuery,
)
from openchallenges_client.models.challenge_platform_sort import ChallengePlatformSort
from openchallenges_client.models.challenge_platforms_page import ChallengePlatformsPage
from openchallenges_client.models.challenge_search_query import ChallengeSearchQuery
from openchallenges_client.models.challenge_sort import ChallengeSort
from openchallenges_client.models.challenge_status import ChallengeStatus
from openchallenges_client.models.challenge_submission_type import (
    ChallengeSubmissionType,
)
from openchallenges_client.models.challenges_page import ChallengesPage
from openchallenges_client.models.challenges_per_year import ChallengesPerYear
from openchallenges_client.models.edam_concept import EdamConcept
from openchallenges_client.models.edam_concept_direction import EdamConceptDirection
from openchallenges_client.models.edam_concept_search_query import (
    EdamConceptSearchQuery,
)
from openchallenges_client.models.edam_concept_sort import EdamConceptSort
from openchallenges_client.models.edam_concepts_page import EdamConceptsPage
from openchallenges_client.models.edam_section import EdamSection
from openchallenges_client.models.image import Image
from openchallenges_client.models.image_aspect_ratio import ImageAspectRatio
from openchallenges_client.models.image_height import ImageHeight
from openchallenges_client.models.image_query import ImageQuery
from openchallenges_client.models.organization import Organization
from openchallenges_client.models.organization_category import OrganizationCategory
from openchallenges_client.models.organization_direction import OrganizationDirection
from openchallenges_client.models.organization_search_query import (
    OrganizationSearchQuery,
)
from openchallenges_client.models.organization_sort import OrganizationSort
from openchallenges_client.models.organizations_page import OrganizationsPage
from openchallenges_client.models.page_metadata import PageMetadata
from openchallenges_client.models.simple_challenge_platform import (
    SimpleChallengePlatform,
)
from openchallenges_client.models.user import User
from openchallenges_client.models.user_create_request import UserCreateRequest
from openchallenges_client.models.user_create_response import UserCreateResponse
from openchallenges_client.models.user_status import UserStatus
from openchallenges_client.models.users_page import UsersPage
