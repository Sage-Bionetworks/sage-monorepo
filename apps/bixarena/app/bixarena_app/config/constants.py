"""
Global constants.
"""

import os
from enum import IntEnum

##### For the gradio web server
SERVER_ERROR_MSG = (
    "**NETWORK ERROR DUE TO HIGH TRAFFIC. PLEASE REGENERATE OR REFRESH THIS PAGE.**"
)
MODERATION_MSG = "$MODERATION$ YOUR INPUT VIOLATES OUR CONTENT MODERATION GUIDELINES."
SLOW_MODEL_MSG = "⚠️  Both models will show the responses all at once. Please stay patient as it may take over 30 seconds."
# Maximum characters allowed in a single user prompt to keep requests lightweight.
PROMPT_LEN_LIMIT = int(os.getenv("PROMPT_LEN_LIMIT", 5000))
# Maximum number of turns (user + model pairs) permitted within one battle session.
BATTLE_ROUND_LIMIT = int(os.getenv("BATTLE_ROUND_LIMIT", 20))
# Maximum tokens generated per model response.
MAX_RESPONSE_TOKENS = int(os.getenv("MAX_RESPONSE_TOKENS", 1024))
# The output dir of log files (default to print console logging only)
LOGDIR = os.getenv("LOGDIR", "")


class ErrorCode(IntEnum):
    """
    Error codes based on OpenAI API error codes:
    https://platform.openai.com/docs/guides/error-codes/api-errors
    """

    # Standard OpenAI API error codes
    INVALID_AUTH_KEY = 40101
    INCORRECT_AUTH_KEY = 40102
    NO_PERMISSION = 40103

    INVALID_MODEL = 40301
    PARAM_OUT_OF_RANGE = 40302
    CONTEXT_OVERFLOW = 40303

    RATE_LIMIT = 42901
    QUOTA_EXCEEDED = 42902
    ENGINE_OVERLOADED = 42903

    INTERNAL_ERROR = 50001

    # Custom error codes
    GRADIO_REQUEST_ERROR = 50003
    GRADIO_STREAM_UNKNOWN_ERROR = 50004
