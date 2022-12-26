import jwt

from openapi_server.config import config


def info_from_ApiKeyAuth(api_key, required_scopes):
    """
    Check and retrieve authentication information from api_key. Returned value
    will be passed in 'token_info' parameter of your operation function, if
    there is one. 'sub' or 'uid' will be set in 'user' parameter of your
    operation function, if there is one.

    :param api_key API key provided by Authorization header :type api_key: str
    :param required_scopes Always None. Used for other authentication method
    :type required_scopes: None :return: Information attached to provided
    api_key or None if api_key is invalid or does not allow access to called
    API
    :rtype: dict | None
    """
    try:
        if api_key == config.secret_key:
            return {"uid": "user_id"}
    except Exception as error:
        print("Invalid API key", error)

    return None


def info_from_BearerAuth(token):
    """
    Check and retrieve authentication information from custom bearer token.
    Returned value will be passed in 'token_info' parameter of your operation
    function, if there is one. 'sub' or 'uid' will be set in 'user' parameter
    of your operation function, if there is one.

    :param token Token provided by Authorization header :type token: str
    :return: Decoded token information or None if token is invalid :rtype: dict
    | None
    """
    try:
        payload = jwt.decode(token, config.secret_key, algorithms=["HS256"])
        return {"sub": payload["sub"]}
    except jwt.ExpiredSignatureError as error:
        print("Signature expired. Please log in again.", error)
    except jwt.InvalidTokenError as error:
        print("Invalid token. Please log in again.", error)

    return None
