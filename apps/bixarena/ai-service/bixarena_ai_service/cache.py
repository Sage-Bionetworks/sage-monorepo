"""Valkey cache for prompt validation results.

Provides async get/set operations with a key format that includes
the validation method ID, so changing the model or prompt invalidates
old entries automatically.

Key format: bixarena:ai:prompt-validation:{method}:{sha256(normalized_prompt)}
"""

from __future__ import annotations

import hashlib
import json
import logging

import valkey.asyncio as valkey

from bixarena_ai_service.config import Settings

logger = logging.getLogger(__name__)

_KEY_PREFIX = "bixarena:ai:prompt-validation"

# Module-level client — initialized lazily on first use.
_client: valkey.Valkey | None = None


def _make_key(method: str, prompt: str) -> str:
    """Build a cache key from the method ID and normalized prompt."""
    normalized = prompt.strip().lower()
    prompt_hash = hashlib.sha256(normalized.encode("utf-8")).hexdigest()
    return f"{_KEY_PREFIX}:{method}:{prompt_hash}"


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


async def get_cached_validation(
    prompt: str, settings: Settings
) -> dict | None:
    """Look up a cached validation result.

    Returns a dict with 'confidence' and 'is_biomedical' keys,
    or None on cache miss or any error.
    """
    try:
        client = await _get_client(settings)
        key = _make_key(settings.prompt_validation_method, prompt)
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
        key = _make_key(settings.prompt_validation_method, prompt)
        value = json.dumps(
            {"confidence": confidence, "is_biomedical": is_biomedical}
        )
        await client.set(key, value, ex=settings.valkey_cache_ttl)
        logger.debug("Cached prompt validation result")
    except Exception:
        logger.warning(
            "Valkey cache write failed — result not cached",
            exc_info=True,
        )
