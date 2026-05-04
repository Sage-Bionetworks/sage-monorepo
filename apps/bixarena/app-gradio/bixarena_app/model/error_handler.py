"""
Error message sanitization to prevent information leakage.

Maps exceptions to user-friendly messages without leaking sensitive information.
"""

import logging

logger = logging.getLogger(__name__)


class StreamError(Exception):
    """Raised when the backend SSE stream returns an error or empty response."""


class ModelTimeoutError(Exception):
    """Raised when a model does not respond within the allowed time."""


def get_user_error_message(error: Exception) -> str:
    """Map any error from the streaming pipeline to a user-friendly message.

    Handles both SSE stream errors (StreamError) and HTTP exceptions from
    the generated API client (status_code-based).
    """
    logger.error("Error: %s", error, exc_info=True)

    if isinstance(error, ModelTimeoutError):
        return (
            "The model response timed out.<br>Please start a new battle and try again."
        )

    if isinstance(error, StreamError):
        return (
            "An error occurred while generating the response.<br>"
            "Please try submitting your prompt again."
        )

    status_code = getattr(error, "status_code", None) or getattr(error, "status", None)

    if status_code == 400:
        return (
            "The request could not be processed due to a formatting issue "
            "(code: 400).<br>"
            "Please try rephrasing your prompt or report this issue if it persists."
        )
    if status_code == 401:
        return (
            "The service is currently unavailable (code: 401).<br>"
            "Please start a new battle to try again."
        )
    if status_code == 403:
        return (
            "The service is currently unavailable (code: 403).<br>"
            "Please start a new battle to try again."
        )
    if status_code == 404:
        return (
            "The service is currently unavailable (code: 404).<br>"
            "Please start a new battle and try again."
        )
    if status_code == 429:
        return (
            "The model provider rate limit has been exceeded (code: 429).<br>"
            "Please try submitting your prompt again."
        )
    if status_code == 500:
        return (
            "An internal server error occurred (code: 500).<br>"
            "Please try submitting your prompt again."
        )
    if status_code:
        return (
            f"An error occurred while processing the request "
            f"(code: {status_code}).<br>"
            f"Please try again or report this issue if it persists."
        )

    return (
        "An unexpected error occurred.<br>"
        "Please start a new battle, "
        "and report this issue if it persists."
    )
