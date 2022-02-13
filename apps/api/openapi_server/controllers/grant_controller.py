import connexion
import six

from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.grant import Grant  # noqa: E501
from openapi_server.models.grant_create_request import GrantCreateRequest  # noqa: E501
from openapi_server.models.grant_create_response import GrantCreateResponse  # noqa: E501
from openapi_server.models.page_of_grants import PageOfGrants  # noqa: E501
from openapi_server import util


def create_grant(grant_create_request):  # noqa: E501
    """Create a grant

    Create a grant with the specified name # noqa: E501

    :param grant_create_request: 
    :type grant_create_request: dict | bytes

    :rtype: GrantCreateResponse
    """
    if connexion.request.is_json:
        grant_create_request = GrantCreateRequest.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def delete_all_grants():  # noqa: E501
    """Delete all grants

    Delete all grants # noqa: E501


    :rtype: object
    """
    return 'do some magic!'


def delete_grant(grant_id):  # noqa: E501
    """Delete a grant

    Deletes the grant specified # noqa: E501

    :param grant_id: The ID of the grant that is being created
    :type grant_id: str

    :rtype: object
    """
    return 'do some magic!'


def get_grant(grant_id):  # noqa: E501
    """Get a grant

    Returns the grant specified # noqa: E501

    :param grant_id: The ID of the grant that is being created
    :type grant_id: str

    :rtype: Grant
    """
    return 'do some magic!'


def list_grants(limit=None, offset=None):  # noqa: E501
    """Get all grants

    Returns the grants # noqa: E501

    :param limit: Maximum number of results returned
    :type limit: int
    :param offset: Index of the first result that must be returned
    :type offset: int

    :rtype: PageOfGrants
    """
    return 'do some magic!'
