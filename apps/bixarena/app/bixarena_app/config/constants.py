"""
Global constants.
"""

import os

##### For the gradio web server
SLOW_MODEL_MSG = "⚠️  Both models will show the responses all at once. Please stay patient as it may take over 30 seconds."

# User input limits
PROMPT_LEN_LIMIT = int(os.getenv("PROMPT_LEN_LIMIT", 5000))
BATTLE_ROUND_LIMIT = int(os.getenv("BATTLE_ROUND_LIMIT", 20))

# Generation parameters
DEFAULT_TEMPERATURE = 0.7
DEFAULT_TOP_P = 1.0
MAX_RESPONSE_TOKENS = int(os.getenv("MAX_RESPONSE_TOKENS", 1024))
