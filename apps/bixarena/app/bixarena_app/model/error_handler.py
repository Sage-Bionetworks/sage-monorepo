"""
Error message sanitization to prevent information leakage.

This module maps OpenAI API exception types to user-friendly messages
without leaking sensitive information (model names, providers, org IDs, etc.).

Error codes reference: https://platform.openai.com/docs/guides/error-codes
"""

import logging

from openai import (
    APIConnectionError,
    APIError,
    AuthenticationError,
    BadRequestError,
    InternalServerError,
    NotFoundError,
    PermissionDeniedError,
    RateLimitError,
)

from bixarena_app.config.constants import BATTLE_ROUND_LIMIT

logger = logging.getLogger(__name__)


def get_battle_round_limit_message() -> str:
    """
    Provide a user-facing message when the battle round cap is reached.

    Returns:
        Markdown formatted message consistent with other error responses.
    """
    message = (
        "**You've reached the round limit for this battle.**\n\n"
        "Please wrap up this matchup or start a fresh battle!"
    )
    return f"{message}\n\n_Round limit: {BATTLE_ROUND_LIMIT}_"


def get_empty_response_message() -> str:
    """
    Handle empty response errors from API providers.

    Returns:
        A user-friendly error message for empty responses.
    """
    return (
        "**Empty Response**\n\n"
        "The model did not generate a response.\n"
        "Please wait a moment, then re-enter your prompt."
    )


def get_finish_reason_error_message() -> str:
    """
    Handle finish_reason error from API providers.

    Returns:
        A user-friendly error message for finish_reason errors.
    """
    return (
        "An error occurred while generating the response. "
        "Please wait a moment, then re-enter your prompt."
    )


def handle_error_message(error: Exception) -> str:
    """
    Handle error messages based on OpenAI exception types.

    Maps API exceptions to user-friendly messages.
    Based on OpenAI error codes: https://platform.openai.com/docs/guides/error-codes

    Args:
        error: The exception to handle

    Returns:
        A user-friendly error message with error code reference
    """
    # Log the full error server-side for debugging
    logger.error(f"API Error: {error}", exc_info=True)

    # Extract HTTP status code if available
    status_code = getattr(error, "status_code", None)

    # Map OpenAI exception types to user-friendly messages
    # 401 - Authentication Error
    if isinstance(error, AuthenticationError):
        return (
            "**Connection Issue**\n\n"
            "The service is currently unavailable.\n"
            "Please start a new battle to try again.\n\n"
            "_Error Code: 401_"
        )

    # 403 - Permission Denied
    if isinstance(error, PermissionDeniedError):
        return (
            "**Connection Issue**\n\n"
            "The service is currently unavailable.\n"
            "Please start a new battle to try again.\n\n"
            "_Error Code: 403_"
        )

    # 404 - Not Found
    if isinstance(error, NotFoundError):
        return (
            "**Service Unavailable**\n\n"
            "The service is currently unavailable.\n"
            "Please start a new battle and try again.\n\n"
            "_Error Code: 404_"
        )

    # 429 - Rate Limit
    if isinstance(error, RateLimitError):
        return (
            "**Rate Limit Exceeded**\n\n"
            "The request rate limit has been exceeded.\n"
            "Please wait a moment, then re-enter your prompt.\n\n"
            "_Error Code: 429_"
        )

    # 500 - Internal Server Error
    if isinstance(error, InternalServerError):
        return (
            "**Internal Server Error**\n\n"
            "An internal server error occurred.\n"
            "Please wait a moment, then re-enter your prompt.\n\n"
            "_Error Code: 500_"
        )

    # 400 - Bad Request
    if isinstance(error, BadRequestError):
        return (
            "**Invalid Request**\n\n"
            "The request could not be processed due to a formatting issue.\n"
            "Please try rephrasing your prompt or report this issue if it persists.\n\n"
            "_Error Code: 400_"
        )

    # Connection errors (network issues)
    if isinstance(error, APIConnectionError):
        return (
            "**Network Connection Error**\n\n"
            "Unable to establish a network connection.\n"
            "Please wait a moment, then re-enter your prompt."
        )

    # Generic API errors
    if isinstance(error, APIError):
        if status_code:
            return (
                "**Request Failed**\n\n"
                "An error occurred while processing the request.\n"
                "Please try again or report this issue if it persists.\n\n"
                f"_Error Code: {status_code}_"
            )
        return (
            "**Request Failed**\n\n"
            "An error occurred while processing the request.\n"
            "Please try again or report this issue if it persists."
        )

    # Fallback for any other exception type
    return (
        "**Service Error**\n\n"
        "An unexpected error occurred.\n"
        "Please refresh the page or start a new battle, "
        "and report this issue if it persists."
    )
