"""
Global constants.
"""

import os

# User input limits
PROMPT_LEN_LIMIT = int(os.getenv("PROMPT_LEN_LIMIT", 5000))
BATTLE_ROUND_LIMIT = int(os.getenv("BATTLE_ROUND_LIMIT", 20))

# Generation parameters
DEFAULT_TEMPERATURE = 0.7
DEFAULT_TOP_P = 1.0
MAX_RESPONSE_TOKENS = int(os.getenv("MAX_RESPONSE_TOKENS", 2048))

# Analytics
GTM_CONTAINER_ID = os.getenv("GTM_CONTAINER_ID", "")

# Features
COMMUNITY_QUEST_ENABLED = (
    os.getenv("APP_COMMUNITY_QUEST_ENABLED", "false").lower() == "true"
)
