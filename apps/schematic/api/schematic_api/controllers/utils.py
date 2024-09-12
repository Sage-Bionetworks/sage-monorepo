"""utils for multiple controllers"""

from typing import Callable, Any, Optional
import urllib.request
import shutil
import tempfile
from urllib.error import HTTPError
import tempfile
import os
import io
import json
import logging
import subprocess
from datetime import datetime, timedelta
import re
from math import ceil
import yaml

import pandas as pd
from flask import request  # type: ignore
from synapseclient.core.exceptions import (  # type: ignore
    SynapseNoCredentialsError,
    SynapseAuthenticationError,
)
from schematic.store import SynapseStorage
from schematic.exceptions import AccessCredentialsError  # type: ignore

from schematic_api.models.basic_error import BasicError

# Config for various settable global values
# Will use config.yaml if it exists, otherwise uses the example file
# config.yaml is ignored by git so can be changed locally without accidentaly commiting it
# To do so copy default_cofnig.yaml to config.yaml and make changes there
if os.path.exists("config.yaml"):
    with open("config.yaml", "r", encoding="utf-8") as file:
        API_CONFIG = yaml.safe_load(file)
else:
    with open("default_config.yaml", "r", encoding="utf-8") as file:
        API_CONFIG = yaml.safe_load(file)

PURGE_SYNAPSE_CACHE = API_CONFIG["purge_synapse_cache"]
SYNAPSE_CACHE_PATH = API_CONFIG["synapse_cache_path"]

LOGGER = logging.getLogger("Synapse cache")


def save_manifest_json_string_as_csv(manifest_json_string: str) -> str:
    """Takes a manifest json string and converts it to a csv file

    Args:
        manifest_json_string (str): The manifest in json string form

    Returns:
        str: The path of the csv file
    """
    temp_dir = tempfile.gettempdir()
    temp_path = os.path.join(temp_dir, "manifest.csv")
    json_dict = json.loads(manifest_json_string)
    manifest_df = pd.DataFrame(json_dict)
    manifest_df.to_csv(temp_path, encoding="utf-8", index=False)
    return temp_path


def save_manifest_csv_string_as_csv(manifest_csv_string: bytes) -> str:
    """Takes a manifest csv string and converts it to a csv file

    Args:
        manifest_csv_string (bytes): The manifest in csv string form

    Returns:
        str: The path of the csv file
    """
    temp_dir = tempfile.gettempdir()
    temp_path = os.path.join(temp_dir, "manifest.csv")
    manifest_df = pd.read_csv(io.BytesIO(manifest_csv_string), sep=",")
    manifest_df.to_csv(temp_path, encoding="utf-8", index=False)
    return temp_path


def get_access_token() -> str | None:
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

    def func(*args: Any, **kwargs: Any) -> tuple[Any | BasicError, int]:
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

        except InvalidValueError as error:
            status = 422
            res = BasicError("Invalid data", status, str(error))
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


class InvalidValueError(Exception):
    """Raised when a provided value for an endpoint is invalid"""

    def __init__(self, message: str, values: dict[str, Any]):
        """
        Args:
            message (str): The error message
            values (dict[str, Any]): A dict where the argument names are keys and
              argument values are values
        """
        self.message = message
        self.values = values

        super().__init__(self.message)

    def __str__(self) -> str:
        return f"{self.message}: {self.values}"


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


def purge_synapse_cache(
    store: SynapseStorage,
    maximum_storage_allowed_cache_gb: float = 1,
    minute_buffer: int = 15,
) -> None:
    """
    Purge synapse cache if it exceeds a certain size. Default to 1GB.
    Args:
        maximum_storage_allowed_cache_gb (float): the maximum storage allowed
          before purging cache. Default is 1 GB.
        minute_buffer (int): All files created this amount of time or older will be deleted
    """
    # try clearing the cache
    # scan a directory and check size of files
    if os.path.exists(store.root_synapse_cache):
        maximum_storage_allowed_cache_bytes = maximum_storage_allowed_cache_gb * (
            1024**3
        )
        dir_size_bytes = check_synapse_cache_size(directory=store.root_synapse_cache)
        # Check if cache is bigger than the allowed size and if so delete all files in cache
        # older than the buffer time
        if dir_size_bytes >= maximum_storage_allowed_cache_bytes:
            minutes_earlier = calculate_datetime(minute_buffer)
            num_of_deleted_files = store.syn.cache.purge(before_date=minutes_earlier)
            LOGGER.info(
                f"{num_of_deleted_files}  files have been deleted from {store.root_synapse_cache}"
            )
        else:
            # on AWS, OS takes around 14-17% of our ephemeral storage (20GiB)
            # instead of guessing how much space that we left, print out .synapseCache here
            LOGGER.info(f"the total size of .synapseCache is: {dir_size_bytes} bytes")


def check_synapse_cache_size(directory: str) -> float:
    """use du --sh command to calculate size of the Synapse cache

    Args:
        directory (str, optional): The Synapse cache directory

    Returns:
        float: returns size of the Synapse directory in bytes
    """
    # Note: this command might fail on windows user.
    # But since this command is primarily for running on AWS, it is fine.
    command = ["du", "-sh", directory]
    output = subprocess.run(command, capture_output=True, check=False).stdout.decode(
        "utf-8"
    )

    # Parsing the output to extract the directory size
    size = output.split("\t")[0]
    return calculate_byte_size(size)


def calculate_byte_size(size_string: str) -> int:
    """
    Calculates the size in bytes of a size returned from the "du" command

    Args:
        size_string (str):
          The input must be a string such as 4B, or 1.2K.
          Sizes up to GB allowed.

    Raises:
        ValueError: When the input doesn't match the allowed paterns

    Returns:
        int: The size in bytes
    """
    if size_string.isnumeric() and int(size_string) == 0:
        return 0

    size_dict: dict[str, int] = {"B": 0, "K": 1, "M": 2, "G": 3}

    size_letter_string = "".join(size_dict.keys())
    int_size_match = re.match(f"^[0-9]+[{size_letter_string}]$", size_string)
    float_size_match = re.match(f"^[0-9]+\.[0-9]+[{size_letter_string}]$", size_string)
    if not (int_size_match or float_size_match):
        LOGGER.error("Cannot recognize the file size unit")
        raise ValueError("The size string doesn't match the allowed type:", size_string)

    size_letter = size_string[-1]
    size = float(size_string[:-1])
    multiple = 1024 ** size_dict[size_letter]
    byte_size: int = ceil(size * multiple)
    return byte_size


def calculate_datetime(
    minutes: int, input_date_time: Optional[datetime] = None
) -> datetime:
    """
    Calculates the datetime x minutes before the input date time
    If no datetime is given, the current datetime is used.

    Args:
        minutes (int): How much time to subtract from the input date time.
        input_date_time (Optional[datetime], optional): The datetime to start with. Defaults to None.

    Returns:
        datetime: The new datetime
    """
    if input_date_time is None:
        date_time = datetime.now()
    else:
        date_time = input_date_time
    return date_time - timedelta(minutes=minutes)
