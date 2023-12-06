"""Tests for utils"""

import pytest

from synapseclient.core.exceptions import (  # type: ignore
    SynapseNoCredentialsError,
    SynapseAuthenticationError,
)
from schematic.exceptions import AccessCredentialsError  # type: ignore

from schematic_api.controllers.utils import handle_exceptions, download_schema_file_as_jsonld, InvalidSchemaURL
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
            InvalidSchemaURL,
            match="The provided URL is incorrect: xxx"
        ):
            download_schema_file_as_jsonld("xxx")

        with pytest.raises(
            InvalidSchemaURL,
            match="The provided URL could not be found: https://raw.github.com/model.jsonld"
        ):
            download_schema_file_as_jsonld("https://raw.github.com/model.jsonld")
