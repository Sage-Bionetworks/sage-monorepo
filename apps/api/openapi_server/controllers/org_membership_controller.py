import connexion
from mongoengine.errors import DoesNotExist, NotUniqueError
from mongoengine.queryset.visitor import Q

from openapi_server.dbmodels.org_membership import (
    OrgMembership as DbOrgMembership,
)  # noqa: E501
from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.org_membership import OrgMembership  # noqa: E501
from openapi_server.models.org_membership_create_request import (
    OrgMembershipCreateRequest,
)  # noqa: E501
from openapi_server.models.org_membership_create_response import (
    OrgMembershipCreateResponse,
)  # noqa: E501
from openapi_server.models.page_of_org_memberships import (
    PageOfOrgMemberships,
)  # noqa: E501
from openapi_server.config import config


def create_org_membership():  # noqa: E501
    """Create an org membership

    Create an org membership # noqa: E501

    :param org_membership_create_request:
    :type org_membership_create_request: dict | bytes

    :rtype: OrgMembershipCreateResponse
    """
    if connexion.request.is_json:
        try:
            org_membership_request = OrgMembershipCreateRequest.from_dict(
                connexion.request.get_json()
            )  # noqa: E501
            org = DbOrgMembership(
                state=org_membership_request.state,
                role=org_membership_request.role,
                organizationId=org_membership_request.organization_id,
                userId=org_membership_request.user_id,
            ).save()
            org_id = org.to_dict().get("id")
            res = OrgMembershipCreateResponse(id=org_id)
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


def delete_all_org_memberships():  # noqa: E501
    """Delete all org memberships

    Delete all org memberships # noqa: E501


    :rtype: object
    """
    try:
        DbOrgMembership.objects.delete()
        res = {}
        status = 200
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status


def delete_org_membership(org_membership_id):  # noqa: E501
    """Delete an org membership

    Deletes the org membership specified # noqa: E501

    :param org_membership_id: The unique identifier of the org membership
    :type org_membership_id: str

    :rtype: object
    """
    try:
        DbOrgMembership.objects.get(id=org_membership_id).delete()
        res = {}
        status = 200
    except DoesNotExist:
        status = 404
        res = Error("The specified resource was not found", status)
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status


def get_org_membership(org_membership_id):  # noqa: E501
    """Get an org membership

    Returns the org membership specified # noqa: E501

    :param org_membership_id: The unique identifier of the org membership
    :type org_membership_id: str

    :rtype: OrgMembership
    """
    try:
        db_user = DbOrgMembership.objects.get(id=org_membership_id)
        res = OrgMembership.from_dict(db_user.to_dict())
        status = 200
    except DoesNotExist:
        status = 404
        res = Error("The specified resource was not found", status)
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status


def list_org_memberships(
    limit=None, offset=None, org_id=None, user_id=None
):  # noqa: E501
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
    try:
        org_id_q = Q(organizationId=org_id) if org_id is not None else Q()
        user_id_q = Q(userId=user_id) if user_id is not None else Q()

        db_org_memberships = (
            DbOrgMembership.objects(org_id_q & user_id_q).skip(offset).limit(limit)
        )
        org_memberships = [
            OrgMembership.from_dict(d.to_dict()) for d in db_org_memberships
        ]  # noqa: E501
        next_ = ""
        if len(org_memberships) == limit:
            next_ = "%s/orgMemberships?limit=%s&offset=%s" % (
                config.server_api_url,
                limit,
                offset + limit,
            )

        total = db_org_memberships.count()
        res = PageOfOrgMemberships(
            offset=offset,
            limit=limit,
            paging={"next": next_},
            total_results=total,
            org_memberships=org_memberships,
        )
        status = 200
    except TypeError:  # TODO: may need include different exceptions for 400
        status = 400
        res = Error("Bad request", status)
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status
