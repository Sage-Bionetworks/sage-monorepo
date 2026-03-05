"""Valkey cache for validation results (prompt and battle).

Provides async get/set operations with a key format that includes
the validation method ID, so changing the model or prompt invalidates
old entries automatically.

Key formats:
  bixarena:ai:prompt-validation:{method}:{sha256(normalized_prompt)}
  bixarena:ai:battle-validation:{method}:{sha256(concatenated_prompts)}
"""

from __future__ import annotations

import hashlib
import json
import logging

import valkey.asyncio as valkey

from bixarena_ai_service.config import Settings

logger = logging.getLogger(__name__)

_PROMPT_KEY_PREFIX = "bixarena:ai:prompt-validation"
_BATTLE_KEY_PREFIX = "bixarena:ai:battle-validation"

# Module-level client — initialized lazily on first use.
_client: valkey.Valkey | None = None


def _make_key(prefix: str, method: str, text: str) -> str:
    """Build a cache key from the prefix, method ID, and normalized text."""
    normalized = text.strip().lower()
    text_hash = hashlib.sha256(normalized.encode("utf-8")).hexdigest()
    return f"{prefix}:{method}:{text_hash}"


async def _get_client(settings: Settings) -> valkey.Valkey:
    """Get or create the async Valkey client."""
    global _client
    if _client is None:
        _client = valkey.Valkey(
            host=settings.valkey_host,
            port=settings.valkey_port,
            db=settings.valkey_db,
            decode_responses=True,
            socket_connect_timeout=2,
            socket_timeout=2,
        )
    return _client


async def get_cached_validation(prompt: str, settings: Settings) -> dict | None:
    """Look up a cached validation result.

    Returns a dict with 'confidence' and 'is_biomedical' keys,
    or None on cache miss or any error.
    """
    try:
        client = await _get_client(settings)
        key = _make_key(_PROMPT_KEY_PREFIX, settings.prompt_validation_method, prompt)
        raw = await client.get(key)
        if raw is not None:
            logger.info("Cache hit for prompt validation")
            return json.loads(raw)
    except Exception:
        logger.warning(
            "Valkey cache read failed — proceeding without cache",
            exc_info=True,
        )
    return None


async def set_cached_validation(
    prompt: str,
    confidence: float,
    is_biomedical: bool,
    settings: Settings,
) -> None:
    """Store a validation result in the cache."""
    try:
        client = await _get_client(settings)
        key = _make_key(_PROMPT_KEY_PREFIX, settings.prompt_validation_method, prompt)
        value = json.dumps({"confidence": confidence, "is_biomedical": is_biomedical})
        await client.set(key, value, ex=settings.valkey_cache_ttl)
        logger.debug("Cached prompt validation result")
    except Exception:
        logger.warning(
            "Valkey cache write failed — result not cached",
            exc_info=True,
        )


def _make_battle_cache_text(prompts: list[str]) -> str:
    """Concatenate prompts into a single string for cache key generation."""
    return "\n---\n".join(prompts)


async def get_cached_battle_validation(
    prompts: list[str], settings: Settings
) -> dict | None:
    """Look up a cached battle validation result."""
    try:
        client = await _get_client(settings)
        text = _make_battle_cache_text(prompts)
        key = _make_key(_BATTLE_KEY_PREFIX, settings.battle_validation_method, text)
        raw = await client.get(key)
        if raw is not None:
            logger.info("Cache hit for battle validation")
            return json.loads(raw)
    except Exception:
        logger.warning(
            "Valkey cache read failed — proceeding without cache",
            exc_info=True,
        )
    return None


async def set_cached_battle_validation(
    prompts: list[str],
    confidence: float,
    is_biomedical: bool,
    settings: Settings,
) -> None:
    """Store a battle validation result in the cache."""
    try:
        client = await _get_client(settings)
        text = _make_battle_cache_text(prompts)
        key = _make_key(_BATTLE_KEY_PREFIX, settings.battle_validation_method, text)
        value = json.dumps({"confidence": confidence, "is_biomedical": is_biomedical})
        await client.set(key, value, ex=settings.valkey_cache_ttl)
        logger.debug("Cached battle validation result")
    except Exception:
        logger.warning(
            "Valkey cache write failed — result not cached",
            exc_info=True,
        )
