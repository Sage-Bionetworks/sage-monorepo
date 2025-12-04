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
        HTML formatted message for system message display.
    """
    return (
        f"You've reached the round limit ({BATTLE_ROUND_LIMIT}) for this battle.<br>"
        "Please submit your evaluation of the model."
    )


def get_empty_response_message() -> str:
    """
    Handle empty response errors from API providers.

    Returns:
        A user-friendly error message for empty responses.
    """
    return "Something went wrong.<br>Please try submitting your prompt again."


def get_finish_error_message() -> str:
    """
    Handle model error from API providers.

    Returns:
        A user-friendly error message for model errors.
    """
    return (
        "An error occurred while generating the response.<br>"
        "Please try submitting your prompt again."
    )


def handle_api_error_message(error: Exception) -> str:
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
            "<em>Connection Issue (code: 401)</em><br>"
            "The service is currently unavailable.<br>"
            "Please start a new battle to try again."
        )

    # 403 - Permission Denied
    if isinstance(error, PermissionDeniedError):
        return (
            "<em>Connection Issue (code: 403)</em><br>"
            "The service is currently unavailable.<br>"
            "Please start a new battle to try again."
        )

    # 404 - Not Found
    if isinstance(error, NotFoundError):
        return (
            "<em>Service Unavailable (code: 404)</em><br>"
            "The service is currently unavailable.<br>"
            "Please start a new battle and try again."
        )

    # 429 - Rate Limit
    if isinstance(error, RateLimitError):
        return (
            "<em>Rate Limit Exceeded (code: 429)</em><br>"
            "The request rate limit has been exceeded.<br>"
            "Please try submitting your prompt again later."
        )

    # 500 - Internal Server Error
    if isinstance(error, InternalServerError):
        return (
            "<em>Internal Server Error (code: 500)</em><br>"
            "An internal server error occurred.<br>"
            "Please try submitting your prompt again later."
        )

    # 400 - Bad Request
    if isinstance(error, BadRequestError):
        return (
            "<em>Invalid Request (code: 400)</em><br>"
            "The request could not be processed due to a formatting issue.<br>"
            "Please try rephrasing your prompt or report this issue if it persists."
        )

    # Connection errors (network issues)
    if isinstance(error, APIConnectionError):
        return (
            "<em>Network Connection Error</em><br>"
            "Unable to establish a network connection.<br>"
            "Please try submitting your prompt again later."
        )

    # Generic API errors
    if isinstance(error, APIError):
        if status_code:
            return (
                f"<em>Request Failed (code: {status_code})</em><br>"
                f"An error occurred while processing the request.<br>"
                f"Please try again or report this issue if it persists."
            )
        return (
            "<em>Request Failed</em><br>"
            "An error occurred while processing the request.<br>"
            "Please try again or report this issue if it persists."
        )

    # Fallback for any other exception type
    return (
        "<em>Service Error</em><br>"
        "An unexpected error occurred.<br>"
        "Please start a new battle, "
        "and report this issue if it persists."
    )
