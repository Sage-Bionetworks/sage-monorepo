import connexion
from mongoengine.errors import DoesNotExist, NotUniqueError

from openapi_server.dbmodels.organization import (
    Organization as DbOrganization,
)  # noqa: E501
from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.organization import Organization  # noqa: E501
from openapi_server.models.organization_create_request import (
    OrganizationCreateRequest,
)  # noqa: E501
from openapi_server.models.organization_create_response import (
    OrganizationCreateResponse,
)  # noqa: E501
from openapi_server.models.page_of_organizations import (
    PageOfOrganizations,
)  # noqa: E501
from openapi_server.config import config


def create_organization():  # noqa: E501
    """Create an organization

    Create an organization with the specified account name # noqa: E501

    :rtype: OrganizationCreateResponse
    """
    if connexion.request.is_json:
        try:
            org_create_request = OrganizationCreateRequest.from_dict(
                connexion.request.get_json()
            )  # noqa: E501
            org = DbOrganization(
                login=org_create_request.login,
                email=org_create_request.email,
                name=org_create_request.name,
                avatarUrl=org_create_request.avatar_url,
                websiteUrl=org_create_request.website_url,
                description=org_create_request.description,
                type="Organization",  # TODO: Use enum value
            ).save()
            org_id = org.to_dict().get("id")
            res = OrganizationCreateResponse(id=org_id)
            status = 201
        except NotUniqueError as error:
            status = 409
            res = Error("Conflict", status, str(error))
        except Exception as error:
            status = 500
            res = Error("Internal error", status, str(error))
    else:
        status = 400
        res = Error("Bad request", status, "Missing body")
    return res, status


def delete_all_organizations():  # noqa: E501
    """Delete all organizations

    Delete all organizations # noqa: E501


    :rtype: object
    """
    try:
        DbOrganization.objects.delete()
        res = {}
        status = 200
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status


def delete_organization(organization_id):  # noqa: E501
    """Delete an organization

    Deletes the organization specified # noqa: E501

    :param organization_id: The unique identifier of the organization, either the user ID or account name
    :type organization_id: str

    :rtype: object
    """
    try:
        DbOrganization.objects.get(id=organization_id).delete()
        res = {}
        status = 200
    except DoesNotExist:
        status = 404
        res = Error("The specified resource was not found", status)
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status


def get_organization(organization_id):  # noqa: E501
    """Get an organization

    Returns the organization specified # noqa: E501

    :param organization_id: The unique identifier of the organization, either the user ID or account name
    :type organization_id: str

    :rtype: Organization
    """
    try:
        db_user = DbOrganization.objects.get(id=organization_id)
        res = Organization.from_dict(db_user.to_dict())
        status = 200
    except DoesNotExist:
        status = 404
        res = Error("The specified resource was not found", status)
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status


def list_organizations(limit=None, offset=None):  # noqa: E501
    """Get all organizations

    Returns the organizations # noqa: E501

    :param limit: Maximum number of results returned
    :type limit: int
    :param offset: Index of the first result that must be returned
    :type offset: int

    :rtype: PageOfOrganizations
    """
    try:
        db_orgs = DbOrganization.objects.skip(offset).limit(limit)
        orgs = [Organization.from_dict(d.to_dict()) for d in db_orgs]
        next_ = ""
        if len(orgs) == limit:
            next_ = "%s/organizations?limit=%s&offset=%s" % (
                config.server_api_url,
                limit,
                offset + limit,
            )

        total = db_orgs.count()
        res = PageOfOrganizations(
            offset=offset,
            limit=limit,
            paging={"next": next_},
            total_results=total,
            organizations=orgs,
        )
        status = 200
    except TypeError:  # TODO: may need include different exceptions for 400
        status = 400
        res = Error("Bad request", status)
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status
