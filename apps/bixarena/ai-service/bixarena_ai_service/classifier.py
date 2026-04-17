"""Shared LLM classification logic for biomedical validation and categorization.

Prompt/battle validation and prompt/battle categorization use the same
OpenRouter call pattern. This module centralises that logic; each endpoint
supplies its own system prompt, user-message formatting, and structured-output
schema.
"""

from __future__ import annotations

import json
import logging

from bixarena_ai_service.config import get_openai_client, get_settings

logger = logging.getLogger(__name__)

FALLBACK_CONFIDENCE = 0.0

# The 20 bioRxiv subject categories (stable since 2013).
# Also referenced by the Java API's CHECK constraint and OpenAPI enum.
BIOMEDICAL_CATEGORIES: tuple[str, ...] = (
    "biochemistry",
    "bioengineering",
    "bioinformatics",
    "cancer-biology",
    "cell-biology",
    "clinical-trials",
    "developmental-biology",
    "epidemiology",
    "evolutionary-biology",
    "genetics",
    "genomics",
    "immunology",
    "microbiology",
    "molecular-biology",
    "neuroscience",
    "pathology",
    "pharmacology-and-toxicology",
    "physiology",
    "synthetic-biology",
    "systems-biology",
)

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

CATEGORIZATION_SCHEMA = {
    "name": "categorization",
    "strict": True,
    "schema": {
        "type": "object",
        "properties": {
            "categories": {
                "type": "array",
                "items": {"type": "string", "enum": list(BIOMEDICAL_CATEGORIES)},
                # NOTE: no minItems/maxItems — Anthropic structured-output rejects
                # these keywords on arrays. Count (0-3) is guided by the system
                # prompt and bounded by max_tokens.
                "description": (
                    "Up to three most relevant category slugs from the allowed list; "
                    "empty when no category fits."
                ),
            }
        },
        "required": ["categories"],
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


def parse_categories(raw: str) -> list[str]:
    """Extract and validate the category slugs from the LLM response.

    Filters out any slug that is not in the allowed ``BIOMEDICAL_CATEGORIES``
    list (defence-in-depth even though the structured-output schema already
    restricts the enum). Parse failures propagate as exceptions so the outer
    ``categorize()`` caller can distinguish them from a legit empty-list "no
    fit" result and avoid caching them.
    """
    allowed = set(BIOMEDICAL_CATEGORIES)
    data = json.loads(raw)
    raw_categories = data["categories"]
    if not isinstance(raw_categories, list):
        raise ValueError("categories field is not a list")
    # Preserve order, dedup, keep only allowed slugs, cap at 3.
    seen: set[str] = set()
    result: list[str] = []
    for item in raw_categories:
        if isinstance(item, str) and item in allowed and item not in seen:
            seen.add(item)
            result.append(item)
            if len(result) >= 3:
                break
    return result


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


async def categorize(system_prompt: str, user_message: str) -> list[str] | None:
    """Call the OpenRouter LLM and return a list of bioRxiv category slugs.

    Returns:
      - list of slugs on a successful LLM response (may be empty for legitimate "no fit")
      - None on error (API failure, missing key, parse failure) — callers should
        not cache this; an empty list would be indistinguishable from "no fit".
    """
    settings = get_settings()

    if not settings.openrouter_api_key:
        logger.warning("BIXARENA_AI_OPENROUTER_API_KEY is not set — returning None")
        return None

    try:
        client = get_openai_client()

        response = await client.chat.completions.create(
            model=settings.openrouter_model,
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_message},
            ],
            temperature=0.0,
            max_tokens=100,
            response_format={
                "type": "json_schema",
                "json_schema": CATEGORIZATION_SCHEMA,
            },
        )

        raw = response.choices[0].message.content or ""
        logger.debug("LLM raw response: %s", raw[:200])
        return parse_categories(raw)

    except Exception:
        logger.exception("OpenRouter API call failed — returning None")
        return None
