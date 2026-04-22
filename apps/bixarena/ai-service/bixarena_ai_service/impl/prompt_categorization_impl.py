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
        except Exception:
            # Only cache successful LLM responses. An exception here means the
            # call failed (API error, bad schema, etc.); caching ``None`` would
            # poison the cache for 30 days and prevent retry.
            logger.exception("Categorization failed, returning null (not cached)")
            return PromptCategorization(prompt=prompt, category=None, method=method)

        await set_cached_prompt_categorization(sanitized, category, settings)

        logger.info("Categorization result: category=%s", category)
        return PromptCategorization(prompt=prompt, category=category, method=method)
