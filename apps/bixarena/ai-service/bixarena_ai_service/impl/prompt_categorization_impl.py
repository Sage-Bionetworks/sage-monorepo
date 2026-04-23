"""Runtime implementation for prompt categorization endpoint.

This file lives under the ``impl`` package which is scanned dynamically by the
generated router (see ``apis/prompt_categorization_api.py``). Any subclass of
``BasePromptCategorizationApi`` discovered at import time is used to service
incoming requests. By placing our implementation here we avoid modifying
generated code that would be overwritten on regeneration.
"""

from __future__ import annotations

import logging
from pathlib import Path

from fastapi import HTTPException

from bixarena_ai_service.apis.prompt_categorization_api_base import (
    BasePromptCategorizationApi,
)
from bixarena_ai_service.cache import (
    get_cached_prompt_categorization,
    set_cached_prompt_categorization,
)
from bixarena_ai_service.classifier import categorize_single
from bixarena_ai_service.config import get_settings
from bixarena_ai_service.models.prompt_categorization import PromptCategorization
from bixarena_ai_service.models.prompt_categorization_request import (
    PromptCategorizationRequest,
)

logger = logging.getLogger(__name__)

# Load the categorization system prompt once at module level.
_PROMPT_PATH = (
    Path(__file__).resolve().parent.parent
    / "prompts"
    / "biomedical_prompt_category_classifier.md"
)
_SYSTEM_PROMPT: str = _PROMPT_PATH.read_text(encoding="utf-8").strip()


class PromptCategorizationApiImpl(BasePromptCategorizationApi):
    """Concrete prompt categorization using an LLM via OpenRouter."""

    async def categorize_prompt(
        self,
        prompt_categorization_request: PromptCategorizationRequest,
    ) -> PromptCategorization:
        """Classify a prompt into a single biomedical subject category.

        The prompt text is sent to an LLM classifier via OpenRouter with a
        structured-output schema restricting the response to allowed slugs.
        The LLM is instructed to treat the text as opaque data. The returned
        ``category`` is ``None`` when the classifier could not assign any
        category.
        """
        settings = get_settings()
        prompt = prompt_categorization_request.prompt

        logger.info("Prompt categorization requested")

        # Sanitize: strip whitespace and enforce max length
        # (defence-in-depth; the API layer already validates length).
        sanitized = prompt.strip()[: settings.prompt_max_length]
        method = settings.prompt_categorization_method

        # Check Valkey cache first. A cached ``{"category": null}`` is a valid
        # "no fit" result and short-circuits the LLM call.
        cached = await get_cached_prompt_categorization(sanitized, settings)
        if cached is not None:
            return PromptCategorization(
                prompt=prompt,
                category=cached["category"],
                method=method,
            )

        # Cache miss — classify via LLM.
        user_message = f"TEXT:\n{sanitized}"
        try:
            category = await categorize_single(_SYSTEM_PROMPT, user_message)
        except Exception as exc:
            # Surface classifier failures as 503 so the caller persists a
            # `status=failed` row and can retry. Returning 200-with-null would
            # be indistinguishable from a legitimate "no fit" result, polluting
            # the audit trail. Not cached — a transient error must not be
            # burned into the cache.
            logger.exception("Categorization failed for prompt")
            raise HTTPException(
                status_code=503,
                detail="AI classifier temporarily unavailable",
            ) from exc

        await set_cached_prompt_categorization(sanitized, category, settings)

        logger.info("Categorization result: category=%s", category)
        return PromptCategorization(prompt=prompt, category=category, method=method)
