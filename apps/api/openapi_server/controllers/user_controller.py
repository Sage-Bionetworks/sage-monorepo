import connexion
import six

from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.page_of_challenges import PageOfChallenges  # noqa: E501
from openapi_server.models.page_of_organizations import PageOfOrganizations  # noqa: E501
from openapi_server.models.page_of_users import PageOfUsers  # noqa: E501
from openapi_server.models.user import User  # noqa: E501
from openapi_server.models.user_create_request import UserCreateRequest  # noqa: E501
from openapi_server.models.user_create_response import UserCreateResponse  # noqa: E501
from openapi_server import util


def create_user(user_create_request):  # noqa: E501
    """Create a user

    Create a user with the specified account name # noqa: E501

    :param user_create_request: 
    :type user_create_request: dict | bytes

    :rtype: UserCreateResponse
    """
    if connexion.request.is_json:
        user_create_request = UserCreateRequest.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def delete_all_users():  # noqa: E501
    """Delete all users

    Delete all users # noqa: E501


    :rtype: object
    """
    return 'do some magic!'


def delete_user(user_id):  # noqa: E501
    """Delete a user

    Deletes the user specified # noqa: E501

    :param user_id: The unique identifier of the user, either the user account ID or login
    :type user_id: str

    :rtype: object
    """
    return 'do some magic!'


def get_authenticated_user():  # noqa: E501
    """Get the authenticated user

    Get the authenticated user # noqa: E501


    :rtype: User
    """
    return 'do some magic!'


def get_user(user_id):  # noqa: E501
    """Get a user

    Returns the user specified # noqa: E501

    :param user_id: The unique identifier of the user, either the user account ID or login
    :type user_id: str

    :rtype: User
    """
    return 'do some magic!'


def is_starred_challenge(account_name, challenge_name):  # noqa: E501
    """Check if a challenge is starred by the authenticated user

    Check if a challenge is starred by the authenticated user # noqa: E501

    :param account_name: The name of the account that owns the challenge
    :type account_name: str
    :param challenge_name: The name of the challenge
    :type challenge_name: str

    :rtype: object
    """
    return 'do some magic!'


def list_authenticated_user_organizations(limit=None, offset=None):  # noqa: E501
    """List organizations of the authenticated user

    Lists organizations the authenticated user belongs to. # noqa: E501

    :param limit: Maximum number of results returned
    :type limit: int
    :param offset: Index of the first result that must be returned
    :type offset: int

    :rtype: PageOfOrganizations
    """
    return 'do some magic!'


def list_starred_challenges(limit=None, offset=None):  # noqa: E501
    """List challenges starred by the authenticated user

    Lists challenges the authenticated user has starred. # noqa: E501

    :param limit: Maximum number of results returned
    :type limit: int
    :param offset: Index of the first result that must be returned
    :type offset: int

    :rtype: PageOfChallenges
    """
    return 'do some magic!'


def list_user_organizations(user_id, limit=None, offset=None):  # noqa: E501
    """List orgsnizations of a user

    Lists organizations a user belongs to. # noqa: E501

    :param user_id: The unique identifier of the user, either the user account ID or login
    :type user_id: str
    :param limit: Maximum number of results returned
    :type limit: int
    :param offset: Index of the first result that must be returned
    :type offset: int

    :rtype: PageOfOrganizations
    """
    return 'do some magic!'


def list_user_starred_challenges(user_id, limit=None, offset=None):  # noqa: E501
    """List challenges starred by a user

    Lists challenges a user has starred. # noqa: E501

    :param user_id: The unique identifier of the user, either the user account ID or login
    :type user_id: str
    :param limit: Maximum number of results returned
    :type limit: int
    :param offset: Index of the first result that must be returned
    :type offset: int

    :rtype: PageOfChallenges
    """
    return 'do some magic!'


def list_users(limit=None, offset=None):  # noqa: E501
    """Get all users

    Returns the users # noqa: E501

    :param limit: Maximum number of results returned
    :type limit: int
    :param offset: Index of the first result that must be returned
    :type offset: int

    :rtype: PageOfUsers
    """
    return 'do some magic!'


def star_challenge(account_name, challenge_name):  # noqa: E501
    """Star a challenge for the authenticated user

    Star a challenge for the authenticated user # noqa: E501

    :param account_name: The name of the account that owns the challenge
    :type account_name: str
    :param challenge_name: The name of the challenge
    :type challenge_name: str

    :rtype: object
    """
    return 'do some magic!'


def unstar_challenge(account_name, challenge_name):  # noqa: E501
    """Unstar a challenge for the authenticated user

    Unstar a challenge for the authenticated user # noqa: E501

    :param account_name: The name of the account that owns the challenge
    :type account_name: str
    :param challenge_name: The name of the challenge
    :type challenge_name: str

    :rtype: object
    """
    return 'do some magic!'
