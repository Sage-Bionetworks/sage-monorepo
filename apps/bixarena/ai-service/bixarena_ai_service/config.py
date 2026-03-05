"""Configuration management for AI service.

This module provides a centralized configuration using Pydantic settings
that can be loaded from environment variables.
"""

from functools import lru_cache

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

    # Prompt validation configuration
    prompt_validation_confidence_threshold: float = 0.5
    prompt_max_length: int = 10000

    # OpenRouter / LLM configuration
    openrouter_api_key: str = ""
    openrouter_base_url: str = "https://openrouter.ai/api/v1"
    openrouter_model: str = "anthropic/claude-haiku-4.5"
    openrouter_timeout: float = 30.0
    openrouter_max_retries: int = 2

    # Validation method IDs — used as part of the cache key.
    # Bump when changing the classification prompt or model.
    prompt_validation_method: str = "openrouter-haiku-v1"
    battle_validation_method: str = "openrouter-haiku-v1"

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
