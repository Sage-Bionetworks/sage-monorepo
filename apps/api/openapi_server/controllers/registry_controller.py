from openapi_server.dbmodels.challenge import (
    Challenge as DbChallenge,
)  # noqa: E501
from openapi_server.dbmodels.organization import (
    Organization as DbOrg,
)  # noqa: E501
from openapi_server.dbmodels.user import User as DbUser  # noqa: E501
from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.registry import Registry  # noqa: E501


def get_registry():  # noqa: E501
    """Get registry information

    Get information about the registry # noqa: E501


    :rtype: Registry
    """
    try:
        res = Registry(
            name="Challenge Registry",
            description="A great challenge registry",
            user_count=DbUser.objects.count(),
            org_count=DbOrg.objects.count(),
            challenge_count=DbChallenge.objects.count(),
        )
        status = 200
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status
