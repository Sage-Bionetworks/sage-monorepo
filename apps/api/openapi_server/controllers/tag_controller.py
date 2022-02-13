import connexion
from mongoengine.errors import DoesNotExist, NotUniqueError

from openapi_server.dbmodels.tag import Tag as DbTag
from openapi_server.models.error import Error
from openapi_server.models.page_of_tags import PageOfTags
from openapi_server.models.tag import Tag
from openapi_server.models.tag_create_response import TagCreateResponse
from openapi_server.models.tag_create_request import TagCreateRequest
from openapi_server.config import config


def create_tag():  # noqa: E501
    """Create a tag

    Create a tag with the specified name # noqa: E501

    :rtype: TagCreateResponse
    """
    if connexion.request.is_json:
        try:
            tag_create_request = TagCreateRequest.from_dict(
                connexion.request.get_json()
            )  # noqa: E501
            tag = DbTag(name=tag_create_request.name).save()
            tag_id = tag.to_dict().get("id")
            res = TagCreateResponse(id=tag_id)
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


def delete_all_tags():  # noqa: E501
    """Delete all tags

    Delete all tags # noqa: E501

    :rtype: object
    """
    try:
        DbTag.objects.delete()
        res = {}
        status = 200
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status


def delete_tag(tag_id):  # noqa: E501
    """Delete a tag

    Deletes the tag specified # noqa: E501

    :param tag_id: The unique identifier of the tag
    :type tag_id: str

    :rtype: object
    """
    try:
        DbTag.objects.get(id=tag_id).delete()
        res = {}
        status = 200
    except DoesNotExist:
        status = 404
        res = Error("The specified resource was not found", status)
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status


def get_tag(tag_id):  # noqa: E501
    """Get a tag

    Returns the tag specified # noqa: E501

    :param tag_id: The unique identifier of the tag
    :type tag_id: str

    :rtype: Tag
    """
    try:
        db_tag = DbTag.objects.get(id=tag_id)
        res = Tag.from_dict(db_tag.to_dict())
        status = 200
    except DoesNotExist:
        status = 404
        res = Error("The specified resource was not found", status)
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status


def list_tags(limit=None, offset=None):  # noqa: E501
    """Get all tags

    Returns the tags # noqa: E501

    :param limit: Maximum number of results returned
    :type limit: int
    :param offset: Index of the first result that must be returned
    :type offset: int

    :rtype: PageOfTags
    """
    try:
        db_tags = DbTag.objects.skip(offset).limit(limit)
        tags = [Tag.from_dict(d.to_dict()) for d in db_tags]
        next_ = ""
        if len(tags) == limit:
            next_ = "%s/tags?limit=%s&offset=%s" % (
                config.server_api_url,
                limit,
                offset + limit,
            )

        total = db_tags.count()
        res = PageOfTags(
            offset=offset,
            limit=limit,
            paging={"next": next_},
            total_results=total,
            tags=tags,
        )
        status = 200
    except TypeError:  # TODO: may need include different exceptions for 400
        status = 400
        res = Error("Bad request", status)
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status
