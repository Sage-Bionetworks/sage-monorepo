"""utils for multiple controllers"""
from typing import Callable, Union, Any, Optional
import urllib.request
import shutil
import tempfile
from urllib.error import HTTPError

from flask import request  # type: ignore
from synapseclient.core.exceptions import (  # type: ignore
    SynapseNoCredentialsError,
    SynapseAuthenticationError,
)
from schematic.exceptions import AccessCredentialsError  # type: ignore

from schematic_api.models.basic_error import BasicError


def get_access_token() -> Optional[str]:
    """Get access token from header"""
    bearer_token = None
    # Check if the Authorization header is present
    if "Authorization" in request.headers:
        auth_header = request.headers["Authorization"]

        # Ensure the header starts with 'Bearer ' and retrieve the token
        if auth_header.startswith("Bearer "):
            bearer_token = auth_header.split(" ")[1]
    return bearer_token


def handle_exceptions(endpoint_function: Callable) -> Callable:
    """
    This is designed to be used as a decorator for endpoint functions.
    The endpoint function is called in a try block, and then various
      Synapse and Schematic exceptions are handled and returned as the
      BasicError object.

    Args:
        f (Callable): A function that calls the input function
    """

    def func(*args: Any, **kwargs: Any) -> tuple[Union[Any, BasicError], int]:
        try:
            return endpoint_function(*args, **kwargs)

        except SynapseNoCredentialsError as error:
            status = 401
            res = BasicError(
                "Missing or invalid Synapse credentials error", status, str(error)
            )
            return res, status

        except SynapseAuthenticationError as error:
            status = 401
            res = BasicError("Forbidden Synapse access error", status, str(error))
            return res, status

        except AccessCredentialsError as error:
            status = 403
            res = BasicError("Synapse entity access error", status, str(error))
            return res, status

        except InvalidSchemaURL as error:
            status = 404
            res = BasicError("Invalid URL", status, str(error))
            return res, status

        except Exception as error:  # pylint: disable=broad-exception-caught
            status = 500
            res = BasicError("Internal error", status, str(error))
            return res, status

    return func


class InvalidSchemaURL(Exception):
    """Raised when a provided url for a schema is incorrect"""

    def __init__(self, message: str, url: str):
        """
        Args:
            message (str): The error message
            url (str): The provided incorrect URL
        """
        self.message = message
        self.url = url
        super().__init__(self.message)

    def __str__(self) -> str:
        return f"{self.message}: {self.url}"


def download_schema_file_as_jsonld(schema_url: str) -> str:
    """Downloads a schema and saves it as temp file

    Args:
        schema_url (str): The URL of the schema

    Raises:
        InvalidSchemaURL: When the schema url doesn't exist or is badly formatted

    Returns:
        str: The path fo the schema jsonld file
    """
    try:
        with urllib.request.urlopen(schema_url) as response:
            with tempfile.NamedTemporaryFile(
                delete=False, suffix=".model.jsonld"
            ) as tmp_file:
                shutil.copyfileobj(response, tmp_file)
                return tmp_file.name
    except ValueError as error:
        # checks for specific ValueError where the url isn't correctly formatted
        if str(error).startswith("unknown url type"):
            raise InvalidSchemaURL(
                "The provided URL is incorrectly formatted", schema_url
            ) from error
        # reraises the ValueError if it isn't the specific type above
        else:
            raise
    except HTTPError as error:
        raise InvalidSchemaURL(
            "The provided URL could not be found", schema_url
        ) from error
