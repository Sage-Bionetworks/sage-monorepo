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
CONVERSATION_LIMIT_MSG = "YOU HAVE REACHED THE CONVERSATION LENGTH LIMIT. PLEASE CLEAR HISTORY AND START A NEW CONVERSATION."
INACTIVE_MSG = "THIS SESSION HAS BEEN INACTIVE FOR TOO LONG. PLEASE REFRESH THIS PAGE."
SLOW_MODEL_MSG = "⚠️  Both models will show the responses all at once. Please stay patient as it may take over 30 seconds."
RATE_LIMIT_MSG = "**RATE LIMIT OF THIS MODEL IS REACHED. PLEASE COME BACK LATER OR TRY OTHER MODELS.**"
# Maximum input length
INPUT_CHAR_LEN_LIMIT = int(os.getenv("FASTCHAT_INPUT_CHAR_LEN_LIMIT", 12000))
# Maximum conversation turns
CONVERSATION_TURN_LIMIT = 50
# Session expiration time
SESSION_EXPIRATION_TIME = 3600
# The output dir of log files (default to print console logging only)
LOGDIR = os.getenv("LOGDIR", "")
# CPU Instruction Set Architecture
CPU_ISA = os.getenv("CPU_ISA")


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
