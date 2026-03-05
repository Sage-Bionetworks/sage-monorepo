"""Runtime implementation for battle validation endpoint.

This file lives under the ``impl`` package which is scanned dynamically by the
generated router (see ``apis/battle_validation_api.py``). Any subclass of
``BaseBattleValidationApi`` discovered at import time is used to service incoming
requests. By placing our implementation here we avoid modifying generated
code that would be overwritten on regeneration.
"""

from __future__ import annotations

import json
import logging
from pathlib import Path

from openai import AsyncOpenAI

from bixarena_ai_service.apis.battle_validation_api_base import (
    BaseBattleValidationApi,
)
from bixarena_ai_service.cache import (
    get_cached_battle_validation,
    set_cached_battle_validation,
)
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

# Sentinel value used for the inconclusive / fallback case.
_FALLBACK_CONFIDENCE = 0.0


def _build_user_message(prompts: list[str]) -> str:
    """Wrap the user-supplied prompts in numbered delimiters for the classifier."""
    parts = []
    for i, prompt in enumerate(prompts, 1):
        parts.append(f'PROMPT {i}:\n"""\n{prompt}\n"""')
    return "\n\n".join(parts)


def _parse_confidence(raw: str) -> float:
    """Extract and clamp the confidence value from the LLM response."""
    try:
        cleaned = raw.strip()
        if cleaned.startswith("```"):
            cleaned = cleaned.split("\n", 1)[-1].rsplit("```", 1)[0].strip()

        data = json.loads(cleaned)
        confidence = float(data["confidence"])
        return max(0.0, min(1.0, confidence))
    except (json.JSONDecodeError, KeyError, TypeError, ValueError) as exc:
        logger.warning(
            "Failed to parse LLM confidence response: %s — raw: %s",
            exc,
            raw[:200],
        )
        return _FALLBACK_CONFIDENCE


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
        confidence = await self._classify(sanitized, settings)
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

    async def _classify(self, prompts: list[str], settings) -> float:
        """Call the OpenRouter LLM and return a confidence score."""
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
                    {
                        "role": "user",
                        "content": _build_user_message(prompts),
                    },
                ],
                temperature=0.0,
                max_tokens=50,
            )

            raw = response.choices[0].message.content or ""
            logger.debug("LLM raw response: %s", raw[:200])
            return _parse_confidence(raw)

        except Exception:
            logger.exception(
                "OpenRouter API call failed — returning inconclusive fallback"
            )
            return _FALLBACK_CONFIDENCE
