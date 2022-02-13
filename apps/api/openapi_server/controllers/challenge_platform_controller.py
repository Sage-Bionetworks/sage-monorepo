import connexion
from mongoengine.errors import DoesNotExist, NotUniqueError

from openapi_server.dbmodels.challenge_platform import (
    ChallengePlatform as DbChallengePlatform,
)  # noqa: E501
from openapi_server.models.error import Error
from openapi_server.models.page_of_challenge_platforms import (
    PageOfChallengePlatforms,
)  # noqa: E501
from openapi_server.models.challenge_platform import ChallengePlatform
from openapi_server.models.challenge_platform_create_response import (
    ChallengePlatformCreateResponse,
)  # noqa: E501
from openapi_server.models.challenge_platform_create_request import (
    ChallengePlatformCreateRequest,
)  # noqa: E501
from openapi_server.config import config


def create_challenge_platform():  # noqa: E501
    """Create a challenge platform

    Create a challenge platform with the specified ID # noqa: E501

    :rtype: ChallengePlatformCreateResponse
    """
    if connexion.request.is_json:
        try:
            challenge_platform_create_request = (
                ChallengePlatformCreateRequest.from_dict(connexion.request.get_json())
            )  # noqa: E501
            challenge_platform = DbChallengePlatform(
                name=challenge_platform_create_request.name,
                displayName=challenge_platform_create_request.display_name,
                websiteUrl=challenge_platform_create_request.website_url,
                avatarUrl=challenge_platform_create_request.avatar_url,
            ).save()
            challenge_platform_id = challenge_platform.to_dict().get("id")
            res = ChallengePlatformCreateResponse(id=challenge_platform_id)
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


def delete_all_challenge_platforms():  # noqa: E501
    """Delete all challenge platforms

    Delete all challenge platforms # noqa: E501

    :rtype: object
    """
    try:
        DbChallengePlatform.objects.delete()
        res = {}
        status = 200
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status


def delete_challenge_platform(challenge_platform_id):  # noqa: E501
    """Delete a challenge platform

    Deletes the challenge platform specified # noqa: E501

    :param challenge_platform_id: The unique identifier of the challenge platform
    :type challenge_platform_id: str

    :rtype: object
    """
    try:
        DbChallengePlatform.objects.get(id=challenge_platform_id).delete()
        res = {}
        status = 200
    except DoesNotExist:
        status = 404
        res = Error("The specified resource was not found", status)
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status


def get_challenge_platform(challenge_platform_id):  # noqa: E501
    """Get a challenge platform

    Returns the challenge platform specified # noqa: E501

    :param challenge_platform_id: The unique identifier of the challenge platform
    :type challenge_platform_id: str

    :rtype: ChallengePlatform
    """
    try:
        db_challenge_platform = DbChallengePlatform.objects.get(
            id=challenge_platform_id
        )  # noqa: E501
        res = ChallengePlatform.from_dict(db_challenge_platform.to_dict())
        status = 200
    except DoesNotExist:
        status = 404
        res = Error("The specified resource was not found", status)
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status


def list_challenge_platforms(limit=None, offset=None):  # noqa: E501
    """Get all challenge platforms

    Returns the challenge platforms # noqa: E501

    :param limit: Maximum number of results returned
    :type limit: int
    :param offset: Index of the first result that must be returned
    :type offset: int

    :rtype: PageOfChallengePlatforms
    """
    try:
        db_challenge_platforms = DbChallengePlatform.objects.skip(offset).limit(
            limit
        )  # noqa: E501
        challenge_platforms = [
            ChallengePlatform.from_dict(d.to_dict()) for d in db_challenge_platforms
        ]  # noqa: E501
        next_ = ""
        if len(challenge_platforms) == limit:
            next_ = "%s/challengePlatforms?limit=%s&offset=%s" % (
                config.server_api_url,
                limit,
                offset + limit,
            )

        total = db_challenge_platforms.count()
        res = PageOfChallengePlatforms(
            offset=offset,
            limit=limit,
            paging={"next": next_},
            total_results=total,
            challenge_platforms=challenge_platforms,
        )
        status = 200
    except TypeError:  # TODO: may need include different exceptions for 400
        status = 400
        res = Error("Bad request", status)
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status
