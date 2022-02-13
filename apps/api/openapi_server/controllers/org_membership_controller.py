import connexion
import six

from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.org_membership import OrgMembership  # noqa: E501
from openapi_server.models.org_membership_create_request import OrgMembershipCreateRequest  # noqa: E501
from openapi_server.models.org_membership_create_response import OrgMembershipCreateResponse  # noqa: E501
from openapi_server.models.page_of_org_memberships import PageOfOrgMemberships  # noqa: E501
from openapi_server import util


def create_org_membership(org_membership_create_request):  # noqa: E501
    """Create an org membership

    Create an org membership # noqa: E501

    :param org_membership_create_request: 
    :type org_membership_create_request: dict | bytes

    :rtype: OrgMembershipCreateResponse
    """
    if connexion.request.is_json:
        org_membership_create_request = OrgMembershipCreateRequest.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def delete_all_org_memberships():  # noqa: E501
    """Delete all org memberships

    Delete all org memberships # noqa: E501


    :rtype: object
    """
    return 'do some magic!'


def delete_org_membership(org_membership_id):  # noqa: E501
    """Delete an org membership

    Deletes the org membership specified # noqa: E501

    :param org_membership_id: The unique identifier of the org membership
    :type org_membership_id: str

    :rtype: object
    """
    return 'do some magic!'


def get_org_membership(org_membership_id):  # noqa: E501
    """Get an org membership

    Returns the org membership specified # noqa: E501

    :param org_membership_id: The unique identifier of the org membership
    :type org_membership_id: str

    :rtype: OrgMembership
    """
    return 'do some magic!'


def list_org_memberships(limit=None, offset=None, org_id=None, user_id=None):  # noqa: E501
    """List all the org memberships

    Returns the org memberships # noqa: E501

    :param limit: Maximum number of results returned
    :type limit: int
    :param offset: Index of the first result that must be returned
    :type offset: int
    :param org_id: An organization identifier used to filter the results
    :type org_id: str
    :param user_id: A user identifier used to filter the results
    :type user_id: str

    :rtype: PageOfOrgMemberships
    """
    return 'do some magic!'
