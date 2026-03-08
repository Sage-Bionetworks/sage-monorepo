"""Runtime implementation for battle validation endpoint.

This file lives under the ``impl`` package which is scanned dynamically by the
generated router (see ``apis/battle_validation_api.py``). Any subclass of
``BaseBattleValidationApi`` discovered at import time is used to service incoming
requests. By placing our implementation here we avoid modifying generated
code that would be overwritten on regeneration.
"""

from __future__ import annotations

import logging
from pathlib import Path

from bixarena_ai_service.apis.battle_validation_api_base import (
    BaseBattleValidationApi,
)
from bixarena_ai_service.cache import (
    get_cached_battle_validation,
    set_cached_battle_validation,
)
from bixarena_ai_service.classifier import classify
from bixarena_ai_service.config import get_settings
from bixarena_ai_service.models.battle_validation import BattleValidation
from bixarena_ai_service.models.battle_validation_request import (
    BattleValidationRequest,
)

logger = logging.getLogger(__name__)

# Load the classification system prompt once at module level.
_PROMPT_PATH = (
    Path(__file__).resolve().parent.parent
    / "prompts"
    / "biomedical_battle_classifier.md"
)
_SYSTEM_PROMPT: str = _PROMPT_PATH.read_text(encoding="utf-8").strip()


def _build_user_message(prompts: list[str]) -> str:
    """Wrap the user-supplied prompts in numbered labels for the classifier."""
    parts = []
    for i, prompt in enumerate(prompts, 1):
        parts.append(f"PROMPT {i}:\n{prompt}")
    return "\n\n".join(parts)


class BattleValidationApiImpl(BaseBattleValidationApi):
    """Concrete battle validation using an LLM via OpenRouter."""

    async def validate_battle(
        self,
        battle_validation_request: BattleValidationRequest,
    ) -> BattleValidation:
        settings = get_settings()
        prompts = battle_validation_request.prompts

        logger.info("Battle validation requested (%d prompts)", len(prompts))

        # Sanitize each prompt
        sanitized = [p.strip()[: settings.prompt_max_length] for p in prompts]

        method = settings.battle_validation_method

        # Check Valkey cache first.
        cached = await get_cached_battle_validation(sanitized, settings)
        if cached is not None:
            return BattleValidation(
                confidence=cached["confidence"],
                is_biomedical=cached["is_biomedical"],
                method=method,
            )

        # Cache miss — classify via LLM.
        user_message = _build_user_message(sanitized)
        confidence = await classify(_SYSTEM_PROMPT, user_message)
        is_biomedical = confidence >= settings.prompt_validation_confidence_threshold

        # Store in cache (fire-and-forget on failure).
        await set_cached_battle_validation(
            sanitized, confidence, is_biomedical, settings
        )

        result = BattleValidation(
            confidence=confidence,
            is_biomedical=is_biomedical,
            method=method,
        )

        logger.info(
            "Battle validation result: confidence=%s, isBiomedical=%s",
            result.confidence,
            result.is_biomedical,
        )

        return result
