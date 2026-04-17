"""Runtime implementation for battle categorization endpoint.

This file lives under the ``impl`` package which is scanned dynamically by the
generated router (see ``apis/battle_categorization_api.py``). Any subclass of
``BaseBattleCategorizationApi`` discovered at import time is used to service
incoming requests. By placing our implementation here we avoid modifying
generated code that would be overwritten on regeneration.
"""

from __future__ import annotations

import logging
from pathlib import Path

from fastapi import HTTPException, status

from bixarena_ai_service.apis.battle_categorization_api_base import (
    BaseBattleCategorizationApi,
)
from bixarena_ai_service.cache import (
    get_cached_battle_categorization,
    set_cached_battle_categorization,
)
from bixarena_ai_service.classifier import categorize
from bixarena_ai_service.config import get_settings
from bixarena_ai_service.models.battle_categorization import (
    BattleCategorization,
)
from bixarena_ai_service.models.battle_categorization_request import (
    BattleCategorizationRequest,
)

logger = logging.getLogger(__name__)

# Load the categorization system prompt once at module level.
_PROMPT_PATH = (
    Path(__file__).resolve().parent.parent
    / "prompts"
    / "biomedical_battle_category_classifier.md"
)
_SYSTEM_PROMPT: str = _PROMPT_PATH.read_text(encoding="utf-8").strip()


def _build_user_message(prompts: list[str]) -> str:
    """Wrap the user-supplied prompts in numbered labels for the classifier."""
    parts = []
    for i, prompt in enumerate(prompts, 1):
        parts.append(f"PROMPT {i}:\n{prompt}")
    return "\n\n".join(parts)


class BattleCategorizationApiImpl(BaseBattleCategorizationApi):
    """Concrete battle categorization using an LLM via OpenRouter."""

    async def categorize_battle(
        self,
        battle_categorization_request: BattleCategorizationRequest,
    ) -> BattleCategorization:
        """Classify all user prompts in a battle into biomedical categories."""
        settings = get_settings()
        prompts = battle_categorization_request.prompts

        logger.info("Battle categorization requested (%d prompts)", len(prompts))

        # Sanitize each prompt
        sanitized = [p.strip()[: settings.prompt_max_length] for p in prompts]
        method = settings.battle_categorization_method

        # Check Valkey cache first.
        cached = await get_cached_battle_categorization(sanitized, settings)
        if cached is not None and len(cached) > 0:
            return BattleCategorization(categories=cached, method=method)

        # Cache miss — classify via LLM.
        user_message = _build_user_message(sanitized)
        categories = await categorize(_SYSTEM_PROMPT, user_message)

        if not categories:
            logger.warning("Battle categorization empty — LLM unavailable or invalid")
            raise HTTPException(
                status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
                detail="Categorization unavailable: LLM returned no valid categories",
            )

        await set_cached_battle_categorization(sanitized, categories, settings)

        result = BattleCategorization(categories=categories, method=method)

        logger.info("Battle categorization result: categories=%s", result.categories)
        return result
