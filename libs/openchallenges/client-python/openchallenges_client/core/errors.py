"""Domain-specific exception hierarchy for the simplified client."""

from __future__ import annotations


class OpenChallengesError(Exception):
    """Base error for high-level client."""


class AuthError(OpenChallengesError):
    def __init__(
        self,
        message: str = "Authentication failed",
        *,
        hint: str | None = None,
    ):
        if hint:
            message = f"{message}. Hint: {hint}"
        super().__init__(message)


class NotFoundError(OpenChallengesError):
    pass


class RateLimitError(OpenChallengesError):
    pass


class ClientError(OpenChallengesError):
    pass


class ServerError(OpenChallengesError):
    pass


class NetworkError(OpenChallengesError):
    pass


STATUS_MAP = {
    401: AuthError,
    403: AuthError,
    404: NotFoundError,
    429: RateLimitError,
}


def map_status(
    status: int | None,
) -> type[OpenChallengesError]:  # pragma: no cover (trivial)
    if status in STATUS_MAP:
        return STATUS_MAP[status]
    if status is not None and 400 <= status < 500:
        return ClientError
    if status is not None and status >= 500:
        return ServerError
    return OpenChallengesError
