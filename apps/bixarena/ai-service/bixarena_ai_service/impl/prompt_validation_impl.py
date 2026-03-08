"""Runtime implementation for prompt validation endpoint.

This file lives under the ``impl`` package which is scanned dynamically by the
generated router (see ``apis/prompt_validation_api.py``). Any subclass of
``BasePromptValidationApi`` discovered at import time is used to service incoming
requests. By placing our implementation here we avoid modifying generated
code that would be overwritten on regeneration.
"""

from __future__ import annotations

import logging
from pathlib import Path

from bixarena_ai_service.apis.prompt_validation_api_base import (
    BasePromptValidationApi,
)
from bixarena_ai_service.cache import get_cached_validation, set_cached_validation
from bixarena_ai_service.classifier import classify
from bixarena_ai_service.config import get_settings
from bixarena_ai_service.models.prompt_validation import PromptValidation
from bixarena_ai_service.models.prompt_validation_request import (
    PromptValidationRequest,
)

logger = logging.getLogger(__name__)

# Load the classification system prompt once at module level.
_PROMPT_PATH = (
    Path(__file__).resolve().parent.parent
    / "prompts"
    / "biomedical_prompt_classifier.md"
)
_SYSTEM_PROMPT: str = _PROMPT_PATH.read_text(encoding="utf-8").strip()


class PromptValidationApiImpl(BasePromptValidationApi):
    """Concrete prompt validation using an LLM via OpenRouter."""

    async def validate_prompt(
        self,
        prompt_validation_request: PromptValidationRequest,
    ) -> PromptValidation:
        """Validate whether a prompt is biomedically related.

        The prompt text is sent to an LLM classifier via OpenRouter.
        The LLM is instructed to treat the text as opaque data (not as
        instructions) and return a confidence score.  On any failure
        the endpoint returns an *inconclusive* result
        (confidence=0.0, is_biomedical=False) so callers can decide
        how to proceed.
        """
        settings = get_settings()
        prompt = prompt_validation_request.prompt

        logger.info("Prompt validation requested")

        # Sanitize: strip whitespace and enforce max length
        # (defense-in-depth; the API layer already validates length).
        sanitized = prompt.strip()[: settings.prompt_max_length]

        method = settings.prompt_validation_method

        # Check Valkey cache first.
        cached = await get_cached_validation(sanitized, settings)
        if cached is not None:
            return PromptValidation(
                prompt=prompt,
                confidence=cached["confidence"],
                is_biomedical=cached["is_biomedical"],
                method=method,
            )

        # Cache miss — classify via LLM.
        user_message = f"TEXT:\n{sanitized}"
        confidence = await classify(_SYSTEM_PROMPT, user_message)
        is_biomedical = confidence >= settings.prompt_validation_confidence_threshold

        # Store in cache (fire-and-forget on failure).
        await set_cached_validation(sanitized, confidence, is_biomedical, settings)

        result = PromptValidation(
            prompt=prompt,
            confidence=confidence,
            is_biomedical=is_biomedical,
            method=method,
        )

        logger.info(
            "Validation result: confidence=%s, isBiomedical=%s",
            result.confidence,
            result.is_biomedical,
        )

        return result
