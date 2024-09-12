"""Tests for utils"""

from datetime import datetime

import pytest

from synapseclient.core.exceptions import (  # type: ignore
    SynapseNoCredentialsError,
    SynapseAuthenticationError,
)
from schematic.exceptions import AccessCredentialsError  # type: ignore

from schematic_api.controllers.utils import (
    handle_exceptions,
    download_schema_file_as_jsonld,
    InvalidSchemaURL,
    calculate_datetime,
    calculate_byte_size,
)
from schematic_api.models.basic_error import BasicError


@handle_exceptions
def func(exc: BaseException, raise_error: bool = True) -> tuple[str, int]:
    """used to test decorator"""
    if raise_error:
        raise exc
    return ("xxx", 200)


class TestHandleExceptions:
    """Tests handle_exceptions"""

    def test_401(self) -> None:
        "Tests for 401 status"
        res, status = func(SynapseNoCredentialsError)
        assert status == 401
        assert isinstance(res, BasicError)

        res, status = func(SynapseAuthenticationError)
        assert status == 401
        assert isinstance(res, BasicError)

    def test_403(self) -> None:
        "Tests for 403 status"
        res, status = func(AccessCredentialsError("project"))
        assert status == 403
        assert isinstance(res, BasicError)

    def test_404(self) -> None:
        "Tests for 404 status"
        res, status = func(InvalidSchemaURL("message", "url"))
        assert status == 404
        assert isinstance(res, BasicError)

    def test_500(self) -> None:
        "Tests for 500 status"
        res, status = func(TypeError)
        assert status == 500
        assert isinstance(res, BasicError)


class TestDownloadSchemaFileAsJsonLD:
    "tests download_schema_file_as_jsonld"

    def test_success(self, test_schema_url: str) -> None:
        "tests for successful download"
        file_path = download_schema_file_as_jsonld(test_schema_url)
        assert file_path

    def test_failure(self) -> None:
        "tests for exception"
        with pytest.raises(
            InvalidSchemaURL, match="The provided URL is incorrectly formatted: xxx"
        ):
            download_schema_file_as_jsonld("xxx")

        with pytest.raises(
            InvalidSchemaURL,
            match="The provided URL could not be found: https://raw.github.com/model.jsonld",
        ):
            download_schema_file_as_jsonld("https://raw.github.com/model.jsonld")


class TestCalculateByteSize:
    """Tests calculate_byte_size"""

    def test_success(self) -> None:
        """Tests sucessful results"""
        assert calculate_byte_size("1B") == 1
        assert calculate_byte_size("2B") == 2
        assert calculate_byte_size("1K") == 1024
        assert calculate_byte_size("1.0K") == 1024
        assert calculate_byte_size("1.1K") == 1127
        assert calculate_byte_size("0") == 0

    def test_errors(self) -> None:
        """Tests for raised exceptions"""

        with pytest.raises(ValueError):
            calculate_byte_size("1")

        with pytest.raises(ValueError):
            calculate_byte_size("1X")


class TestCalculateDatetime:
    """Tests calculate_datetime"""

    def test_with_defaults(self) -> None:
        """Tests sucessful results"""
        assert isinstance(calculate_datetime(0), datetime)
        assert isinstance(calculate_datetime(1), datetime)
        assert isinstance(calculate_datetime(-1), datetime)

    def test_with_input_datetime(self) -> None:
        """Tests sucessful results"""
        datetime1 = datetime(2024, 1, 15, 10, 0)
        datetime2 = datetime(2024, 1, 15, 9, 50)
        assert calculate_datetime(0, datetime1) == datetime1
        assert calculate_datetime(10, datetime1) == datetime2
        assert calculate_datetime(-10, datetime2) == datetime1
