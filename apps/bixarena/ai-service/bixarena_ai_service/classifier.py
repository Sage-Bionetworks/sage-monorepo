"""Shared LLM classification logic for biomedical validation.

Both prompt and battle validation use the same structured-output schema,
response parsing, and OpenRouter call pattern.  This module centralises
that logic so each endpoint only needs to supply its own system prompt
and user-message formatting.
"""

from __future__ import annotations

import json
import logging

from bixarena_ai_service.config import get_openai_client, get_settings

logger = logging.getLogger(__name__)

FALLBACK_CONFIDENCE = 0.0

CONFIDENCE_SCHEMA = {
    "name": "classification",
    "strict": True,
    "schema": {
        "type": "object",
        "properties": {
            "confidence": {
                "type": "number",
                "description": "0.0 = not biomedical, 1.0 = clearly biomedical",
            }
        },
        "required": ["confidence"],
        "additionalProperties": False,
    },
}


def parse_confidence(raw: str) -> float:
    """Extract and clamp the confidence value from the LLM response.

    Returns ``FALLBACK_CONFIDENCE`` if parsing fails.
    """
    try:
        data = json.loads(raw)
        confidence = float(data["confidence"])
        return max(0.0, min(1.0, confidence))
    except (json.JSONDecodeError, KeyError, TypeError, ValueError) as exc:
        logger.warning(
            "Failed to parse LLM confidence response: %s — raw: %s",
            exc,
            raw[:200],
        )
        return FALLBACK_CONFIDENCE


async def classify(system_prompt: str, user_message: str) -> float:
    """Call the OpenRouter LLM and return a biomedical confidence score.

    Returns ``FALLBACK_CONFIDENCE`` on any error so the service
    degrades gracefully rather than failing hard.
    """
    settings = get_settings()

    if not settings.openrouter_api_key:
        logger.warning(
            "BIXARENA_AI_OPENROUTER_API_KEY is not set "
            "— returning inconclusive fallback"
        )
        return FALLBACK_CONFIDENCE

    try:
        client = get_openai_client()

        response = await client.chat.completions.create(
            model=settings.openrouter_model,
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_message},
            ],
            temperature=0.0,
            max_tokens=50,
            response_format={
                "type": "json_schema",
                "json_schema": CONFIDENCE_SCHEMA,
            },
        )

        raw = response.choices[0].message.content or ""
        logger.debug("LLM raw response: %s", raw[:200])
        return parse_confidence(raw)

    except Exception:
        logger.exception("OpenRouter API call failed — returning inconclusive fallback")
        return FALLBACK_CONFIDENCE
