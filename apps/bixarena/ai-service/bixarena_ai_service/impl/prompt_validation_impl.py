"""Runtime implementation for prompt validation endpoint.

This file lives under the ``impl`` package which is scanned dynamically by the
generated router (see ``apis/prompt_validation_api.py``). Any subclass of
``BasePromptValidationApi`` discovered at import time is used to service incoming
requests. By placing our implementation here we avoid modifying generated
code that would be overwritten on regeneration.
"""

from __future__ import annotations

import json
import logging
from pathlib import Path

from openai import AsyncOpenAI

from bixarena_ai_service.apis.prompt_validation_api_base import BasePromptValidationApi
from bixarena_ai_service.config import get_settings
from bixarena_ai_service.models.prompt_validation import PromptValidation
from bixarena_ai_service.models.prompt_validation_request import PromptValidationRequest

logger = logging.getLogger(__name__)

# Load the classification system prompt once at module level.
_PROMPT_PATH = (
    Path(__file__).resolve().parent.parent / "prompts" / "biomedical_classifier.md"
)
_SYSTEM_PROMPT: str = _PROMPT_PATH.read_text(encoding="utf-8").strip()

# Sentinel value used for the inconclusive / fallback case.
# Must be strictly below the default threshold (0.5) so is_biomedical=False.
_FALLBACK_CONFIDENCE = 0.0


def _build_user_message(prompt: str) -> str:
    """Wrap the user-supplied text in delimiters for the classifier.

    The triple-quote fence clearly separates the opaque text from the
    system instructions, making prompt-injection harder.
    """
    return f'TEXT:\n"""\n{prompt}\n"""'


def _parse_confidence(raw: str) -> float:
    """Extract and clamp the confidence value from the LLM response.

    Returns _FALLBACK_CONFIDENCE if parsing fails.
    """
    try:
        # Strip markdown code fences if the model wraps its response.
        cleaned = raw.strip()
        if cleaned.startswith("```"):
            cleaned = cleaned.split("\n", 1)[-1].rsplit("```", 1)[0].strip()

        data = json.loads(cleaned)
        confidence = float(data["confidence"])
        return max(0.0, min(1.0, confidence))
    except (json.JSONDecodeError, KeyError, TypeError, ValueError) as exc:
        logger.warning(
            "Failed to parse LLM confidence response: %s — raw: %s", exc, raw[:200]
        )
        return _FALLBACK_CONFIDENCE


class PromptValidationApiImpl(BasePromptValidationApi):
    """Concrete prompt validation using an LLM via OpenRouter."""

    async def validate_prompt(
        self,
        prompt_validation_request: PromptValidationRequest,
    ) -> PromptValidation:
        """Validate whether a prompt is biomedically related.

        The prompt text is sent to an LLM classifier via OpenRouter.  The LLM
        is instructed to treat the text as opaque data (not as instructions) and
        return a confidence score.  On any failure the endpoint returns an
        *inconclusive* result (confidence=0.0, is_biomedical=False) so callers
        can decide how to proceed.
        """
        settings = get_settings()
        prompt = prompt_validation_request.prompt

        logger.info("Prompt validation requested")

        # Sanitize: strip whitespace and enforce max length (defense-in-depth;
        # the API layer already validates length).
        sanitized_prompt = prompt.strip()[: settings.prompt_max_length]

        confidence = await self._classify(sanitized_prompt, settings)

        validation_result = PromptValidation(
            prompt=prompt,
            confidence=confidence,
            is_biomedical=confidence >= settings.prompt_validation_confidence_threshold,
        )

        logger.info(
            "Validation result: confidence=%s, isBiomedical=%s",
            validation_result.confidence,
            validation_result.is_biomedical,
        )

        return validation_result

    # ------------------------------------------------------------------
    # Private helpers
    # ------------------------------------------------------------------

    async def _classify(self, prompt: str, settings) -> float:
        """Call the OpenRouter LLM and return a confidence score.

        Returns ``_FALLBACK_CONFIDENCE`` on any error so the service degrades
        gracefully rather than failing hard.
        """
        if not settings.openrouter_api_key:
            logger.warning(
                "BIXARENA_AI_OPENROUTER_API_KEY is not set "
                "— returning inconclusive fallback"
            )
            return _FALLBACK_CONFIDENCE

        try:
            client = AsyncOpenAI(
                api_key=settings.openrouter_api_key,
                base_url=settings.openrouter_base_url,
                timeout=settings.openrouter_timeout,
                max_retries=settings.openrouter_max_retries,
            )

            response = await client.chat.completions.create(
                model=settings.openrouter_model,
                messages=[
                    {"role": "system", "content": _SYSTEM_PROMPT},
                    {"role": "user", "content": _build_user_message(prompt)},
                ],
                temperature=0.0,
                max_tokens=50,
            )

            raw_content = response.choices[0].message.content or ""
            logger.debug("LLM raw response: %s", raw_content[:200])
            return _parse_confidence(raw_content)

        except Exception:
            logger.exception(
                "OpenRouter API call failed — returning inconclusive fallback"
            )
            return _FALLBACK_CONFIDENCE
