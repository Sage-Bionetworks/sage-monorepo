"""Configuration management for AI service.

This module provides a centralized configuration using Pydantic settings
that can be loaded from environment variables.
"""

from functools import lru_cache

from openai import AsyncOpenAI
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    """Application settings loaded from environment variables.

    All settings can be overridden using environment variables with the
    BIXARENA_AI_ prefix. For example:
    - BIXARENA_AI_AUTH_SERVICE_URL=http://auth:8115
    - BIXARENA_AI_JWT_EXPECTED_ISSUER=urn:bixarena:auth
    """

    # Auth service configuration
    auth_service_url: str = "http://bixarena-auth-service:8115"

    # JWT validation configuration
    jwt_expected_issuer: str = "urn:bixarena:auth"
    jwt_expected_audience: str = "urn:bixarena:ai"

    # Validation confidence thresholds
    prompt_validation_confidence_threshold: float = 0.5
    battle_validation_confidence_threshold: float = 0.2
    prompt_max_length: int = 10000

    # OpenRouter / LLM configuration
    openrouter_api_key: str = ""
    openrouter_base_url: str = "https://openrouter.ai/api/v1"
    openrouter_model: str = "anthropic/claude-haiku-4.5"
    openrouter_timeout: float = 30.0
    openrouter_max_retries: int = 2

    # Chat completion configuration
    chat_default_temperature: float = 0.7
    chat_default_top_p: float = 1.0
    chat_max_response_tokens: int = 4096
    chat_timeout: float = 60.0
    chat_max_retries: int = 2

    # OpenRouter app attribution configuration
    app_url: str = "https://bioarena.io"
    app_title: str = "BioArena"

    # Validation / categorization method IDs — used as part of the cache key.
    # Bump when changing the classification prompt or model.
    prompt_validation_method: str = "openrouter-haiku-v1"
    battle_validation_method: str = "openrouter-haiku-v1"
    prompt_categorization_method: str = "openrouter-haiku-v1"
    battle_categorization_method: str = "openrouter-haiku-v1"

    # Valkey configuration (DB 3 — DB 0/1/2 used by api/gateway/auth)
    valkey_host: str = "bixarena-valkey"
    valkey_port: int = 8116
    valkey_db: int = 3
    valkey_cache_ttl: int = 2592000  # 30 days in seconds

    class Config:
        env_prefix = "BIXARENA_AI_"
        case_sensitive = False


@lru_cache
def get_settings() -> Settings:
    """Get cached settings instance.

    Returns:
        Settings: Application settings singleton
    """
    return Settings()


@lru_cache
def get_openai_client() -> AsyncOpenAI:
    """Get a cached AsyncOpenAI client for OpenRouter.

    Reuses the same HTTP connection pool across requests.
    """
    settings = get_settings()
    headers = {}
    if settings.app_url:
        headers["HTTP-Referer"] = settings.app_url
    if settings.app_title:
        headers["X-OpenRouter-Title"] = settings.app_title
    return AsyncOpenAI(
        api_key=settings.openrouter_api_key,
        base_url=settings.openrouter_base_url,
        timeout=settings.openrouter_timeout,
        max_retries=settings.openrouter_max_retries,
        default_headers=headers if headers else None,
    )
