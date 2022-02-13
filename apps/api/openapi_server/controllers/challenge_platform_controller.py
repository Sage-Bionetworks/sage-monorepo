import connexion
import six

from openapi_server.models.challenge_platform import ChallengePlatform  # noqa: E501
from openapi_server.models.challenge_platform_create_request import ChallengePlatformCreateRequest  # noqa: E501
from openapi_server.models.challenge_platform_create_response import ChallengePlatformCreateResponse  # noqa: E501
from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.page_of_challenge_platforms import PageOfChallengePlatforms  # noqa: E501
from openapi_server import util


def create_challenge_platform(challenge_platform_create_request):  # noqa: E501
    """Create a challenge platform

    Create a challenge platform with the specified ID # noqa: E501

    :param challenge_platform_create_request: 
    :type challenge_platform_create_request: dict | bytes

    :rtype: ChallengePlatformCreateResponse
    """
    if connexion.request.is_json:
        challenge_platform_create_request = ChallengePlatformCreateRequest.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def delete_all_challenge_platforms():  # noqa: E501
    """Delete all challenge platforms

    Delete all challenge platforms # noqa: E501


    :rtype: object
    """
    return 'do some magic!'


def delete_challenge_platform(challenge_platform_id):  # noqa: E501
    """Delete a challenge platform

    Deletes the challenge platform specified # noqa: E501

    :param challenge_platform_id: The unique identifier of the challenge platform
    :type challenge_platform_id: str

    :rtype: object
    """
    return 'do some magic!'


def get_challenge_platform(challenge_platform_id):  # noqa: E501
    """Get a challenge platform

    Returns the challenge platform specified # noqa: E501

    :param challenge_platform_id: The unique identifier of the challenge platform
    :type challenge_platform_id: str

    :rtype: ChallengePlatform
    """
    return 'do some magic!'


def list_challenge_platforms(limit=None, offset=None):  # noqa: E501
    """Get all challenge platforms

    Returns the challenge platforms # noqa: E501

    :param limit: Maximum number of results returned
    :type limit: int
    :param offset: Index of the first result that must be returned
    :type offset: int

    :rtype: PageOfChallengePlatforms
    """
    return 'do some magic!'
