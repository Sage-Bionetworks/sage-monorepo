import connexion
from mongoengine.errors import DoesNotExist
import jwt
import datetime

from openapi_server.dbmodels.user import User as DbUser  # noqa: E501
from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.local_auth_request import (
    LocalAuthRequest,
)  # noqa: E501
from openapi_server.models.local_auth_response import (
    LocalAuthResponse,
)  # noqa: E501
from openapi_server.config import config


def auth_local():  # noqa: E501
    """Authentify a local account

    Authentify a local account with the specified credential # noqa: E501

    :rtype: object
    """
    if connexion.request.is_json:
        try:
            local_auth_request = LocalAuthRequest.from_dict(
                connexion.request.get_json()
            )  # noqa: E501
            user = DbUser.objects.get(login=local_auth_request.login)
            if user.verify_password(local_auth_request.password):
                # Returns a JWT (RFC 7519) signed by the app secret.
                user_id = user.to_dict().get("id")
                payload = {
                    "sub": user_id,
                    "iat": datetime.datetime.utcnow(),
                    "exp": datetime.datetime.utcnow()
                    + datetime.timedelta(days=1),  # noqa: E501
                }
                token = jwt.encode(
                    payload, config.secret_key, algorithm="HS256"
                )  # noqa: E501
                res = LocalAuthResponse(token=token)
                status = 200
            else:
                status = 401
                res = Error("Invalid login or password", status)
        except DoesNotExist:
            status = 404
            res = Error("The specified resource was not found", status)
        except Exception as error:
            status = 500
            res = Error("Internal error", status, str(error))
    else:
        status = 400
        res = Error("Bad request", status, "Missing body")
    return res, status
