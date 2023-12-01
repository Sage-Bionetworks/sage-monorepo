"""Tests for utils"""


from synapseclient.core.exceptions import (  # type: ignore
    SynapseNoCredentialsError,
    SynapseAuthenticationError,
)
from schematic.exceptions import AccessCredentialsError  # type: ignore

from schematic_api.controllers.utils import handle_exceptions
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

    def test_500(self) -> None:
        "Tests for 500 status"
        res, status = func(TypeError)
        assert status == 500
        assert isinstance(res, BasicError)
