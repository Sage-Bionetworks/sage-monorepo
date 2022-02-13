import connexion
import six

from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.organization import Organization  # noqa: E501
from openapi_server.models.organization_create_request import OrganizationCreateRequest  # noqa: E501
from openapi_server.models.organization_create_response import OrganizationCreateResponse  # noqa: E501
from openapi_server.models.page_of_organizations import PageOfOrganizations  # noqa: E501
from openapi_server import util


def create_organization(organization_create_request):  # noqa: E501
    """Create an organization

    Create an organization with the specified account name # noqa: E501

    :param organization_create_request: 
    :type organization_create_request: dict | bytes

    :rtype: OrganizationCreateResponse
    """
    if connexion.request.is_json:
        organization_create_request = OrganizationCreateRequest.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def delete_all_organizations():  # noqa: E501
    """Delete all organizations

    Delete all organizations # noqa: E501


    :rtype: object
    """
    return 'do some magic!'


def delete_organization(organization_id):  # noqa: E501
    """Delete an organization

    Deletes the organization specified # noqa: E501

    :param organization_id: The unique identifier of the organization, either the user ID or account name
    :type organization_id: str

    :rtype: object
    """
    return 'do some magic!'


def get_organization(organization_id):  # noqa: E501
    """Get an organization

    Returns the organization specified # noqa: E501

    :param organization_id: The unique identifier of the organization, either the user ID or account name
    :type organization_id: str

    :rtype: Organization
    """
    return 'do some magic!'


def list_organizations(limit=None, offset=None):  # noqa: E501
    """Get all organizations

    Returns the organizations # noqa: E501

    :param limit: Maximum number of results returned
    :type limit: int
    :param offset: Index of the first result that must be returned
    :type offset: int

    :rtype: PageOfOrganizations
    """
    return 'do some magic!'
