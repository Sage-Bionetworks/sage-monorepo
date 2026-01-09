# Integration Plan: Python FastAPI Microservice with JWT Authentication

## Overview

This document outlines the plan to integrate the Python AI service into the BixArena microservices architecture with JWT-based authentication. The AI service will provide a prompt validation endpoint that checks if a prompt is biomedically related.

## Current Architecture Summary

BixArena uses a **Gateway-Minted JWT pattern**:

1. API Gateway receives session cookie from Gradio web app
2. Gateway validates session with auth service (`/userinfo` endpoint)
3. Gateway mints audience-specific JWT via auth service (`/oauth2/token?audience=urn:bixarena:ai`)
4. Gateway strips session cookie and forwards request with JWT in `Authorization: Bearer <token>` header
5. Microservice validates JWT using JWKS endpoint from auth service

## Phase 1: OpenAPI Specification (API-First Design)

### 1.1 Define New Authenticated Endpoint

Create a new endpoint definition in `libs/bixarena/api-description/src/paths/validate-prompt.yaml`:

```yaml
get:
  tags:
    - Prompt Validation
  summary: Validate biomedical prompt
  description: Validates whether a prompt is biomedically related and returns a confidence score (requires authentication)
  operationId: validatePrompt
  parameters:
    - name: prompt
      in: query
      required: true
      description: The prompt text to validate
      schema:
        type: string
        minLength: 1
        maxLength: 10000
  responses:
    '200':
      description: Success
      content:
        application/json:
          schema:
            $ref: ../components/schemas/PromptValidation.yaml
    '400':
      $ref: ../components/responses/BadRequest.yaml
    '401':
      $ref: ../components/responses/Unauthorized.yaml
    '500':
      $ref: ../components/responses/InternalServerError.yaml
```

**Key characteristics**:

- ❌ No `x-anonymous-access: true` (requires authentication)
- ✅ Includes `401` response for unauthorized access
- ✅ Uses `jwtBearer` security (inherited from global `security` in `ai.openapi.yaml`)
- ✅ Query parameter for prompt text with validation constraints

### 1.2 Create Response Schema

Create `libs/bixarena/api-description/src/components/schemas/PromptValidation.yaml`:

```yaml
type: object
properties:
  prompt:
    type: string
    description: The original prompt that was validated
  confidence:
    type: number
    format: float
    minimum: 0.0
    maximum: 1.0
    description: Confidence score indicating biomedical relevance (0.0 = not biomedical, 1.0 = definitely biomedical)
  isBiomedical:
    type: boolean
    description: Whether the prompt is considered biomedically related (confidence >= 0.5)
required:
  - prompt
  - confidence
  - isBiomedical
```

### 1.3 Update Main OpenAPI File

Add reference to new path in `libs/bixarena/api-description/src/ai.openapi.yaml`:

```yaml
paths:
  /health-check:
    $ref: paths/health-check.yaml
  /validate-prompt:
    $ref: paths/validate-prompt.yaml
```

### 1.4 Add Tag Definition

Add to the `tags` section in `libs/bixarena/api-description/src/ai.openapi.yaml`:

```yaml
tags:
  - name: Health Check
    description: Operations about health checks
    x-sage-internal: true
  - name: Prompt Validation
    description: Operations for validating biomedical prompts
```

## Phase 2: Code Generation

### 2.1 Build OpenAPI Descriptions

```bash
nx build bixarena-api-description
```

This processes the OpenAPI YAML files and prepares them for generation.

### 2.2 Generate All Server Stubs and API Clients

```bash
nx run-many -t=generate -p=bixarena-*
```

This will generate:

- **Python server stubs**:
  - `apps/bixarena/ai-service/bixarena_ai_service/apis/prompt_validation_api.py` (router)
  - `apps/bixarena/ai-service/bixarena_ai_service/apis/prompt_validation_api_base.py` (base class)
  - `apps/bixarena/ai-service/bixarena_ai_service/models/prompt_validation.py` (model)
- **API Gateway routes**: Updated `apps/bixarena/api-gateway/src/main/resources/routes.yml`
- **API clients**: Python, Java, and TypeScript clients (if configured)

Expected route configuration in `routes.yml`:

```yaml
- method: 'GET'
  path: '/validate-prompt'
  audience: 'urn:bixarena:ai'
  # Note: No anonymousAccess: true (defaults to false, requires authentication)
```

## Phase 3: Python JWT Validation Implementation

### 3.1 Add JWT Validation Dependencies

Update `apps/bixarena/ai-service/pyproject.toml`:

```toml
dependencies = [
  # ... existing deps ...
  "python-jose[cryptography]>=3.3.0",  # JWT validation with RSA support
  "httpx>=0.28.1",                      # Already present - for JWKS fetch
]
```

### 3.2 Create JWT Security Module

Create `apps/bixarena/ai-service/bixarena_ai_service/security/__init__.py`:

```python
"""Security modules for JWT validation and authentication."""
```

Create `apps/bixarena/ai-service/bixarena_ai_service/security/jwt_validator.py`:

```python
"""JWT validation using JWKS from auth service.

This module provides FastAPI dependencies for validating JWTs issued by the
BixArena auth service. It fetches the JWKS (JSON Web Key Set) from the auth
service and validates tokens according to the OAuth 2.0 Bearer Token specification.
"""

from typing import Annotated
from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from jose import jwt, JWTError, jwk
from jose.exceptions import JWKError
import httpx
from functools import lru_cache
import logging

logger = logging.getLogger(__name__)

# Configuration (should come from env vars in production)
AUTH_SERVICE_URL = "http://bixarena-auth-service:8115"
JWKS_URL = f"{AUTH_SERVICE_URL}/.well-known/jwks.json"
EXPECTED_ISSUER = "urn:bixarena:auth"
EXPECTED_AUDIENCE = "urn:bixarena:ai"

# HTTP Bearer security scheme
security = HTTPBearer()

@lru_cache(maxsize=1)
def get_jwks() -> dict:
    """Fetch JWKS from auth service with caching.

    The JWKS (JSON Web Key Set) contains the public keys used to verify
    JWT signatures. This function caches the result to avoid repeated
    network calls.

    Returns:
        dict: JWKS document containing public keys

    Raises:
        HTTPException: If JWKS cannot be fetched (503 Service Unavailable)
    """
    try:
        response = httpx.get(JWKS_URL, timeout=5.0)
        response.raise_for_status()
        logger.info("Successfully fetched JWKS from auth service")
        return response.json()
    except httpx.HTTPError as e:
        logger.error(f"Failed to fetch JWKS from {JWKS_URL}: {e}")
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="Cannot validate token - auth service unavailable"
        )

def validate_jwt(
    credentials: Annotated[HTTPAuthorizationCredentials, Depends(security)]
) -> dict:
    """Validate JWT token and return claims.

    This function:
    1. Extracts the JWT from the Authorization header
    2. Fetches the JWKS from the auth service
    3. Finds the matching public key using the token's key ID (kid)
    4. Validates the token signature, expiration, issuer, and audience
    5. Returns the decoded claims

    Args:
        credentials: HTTP Bearer credentials from Authorization header

    Returns:
        dict: Decoded JWT claims including sub, roles, iat, exp, etc.

    Raises:
        HTTPException: If token is invalid, expired, or has wrong audience (401)
    """
    token = credentials.credentials

    try:
        # Fetch JWKS
        jwks = get_jwks()

        # Decode token header to get key ID
        unverified_header = jwt.get_unverified_header(token)
        kid = unverified_header.get("kid")

        if not kid:
            logger.warning("JWT token missing key ID (kid)")
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Token missing key ID",
                headers={"WWW-Authenticate": "Bearer"},
            )

        # Find matching key in JWKS
        key = None
        for jwk_key in jwks.get("keys", []):
            if jwk_key.get("kid") == kid:
                key = jwk_key
                break

        if not key:
            logger.warning(f"Token key ID {kid} not found in JWKS")
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Token key not found in JWKS",
                headers={"WWW-Authenticate": "Bearer"},
            )

        # Validate and decode JWT
        claims = jwt.decode(
            token,
            key,
            algorithms=["RS256"],
            issuer=EXPECTED_ISSUER,
            audience=EXPECTED_AUDIENCE,
            options={
                "verify_signature": True,
                "verify_exp": True,
                "verify_iat": True,
                "verify_aud": True,
                "verify_iss": True,
            }
        )

        logger.debug(f"Successfully validated JWT for subject: {claims.get('sub')}")
        return claims

    except JWTError as e:
        logger.warning(f"JWT validation failed: {e}")
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or expired token",
            headers={"WWW-Authenticate": "Bearer"},
        )

# Convenience dependencies for accessing specific claims

async def get_current_user_id(
    claims: Annotated[dict, Depends(validate_jwt)]
) -> str:
    """Extract user ID from validated JWT.

    Args:
        claims: Validated JWT claims

    Returns:
        str: User ID (UUID) from the 'sub' claim

    Raises:
        HTTPException: If 'sub' claim is missing (401)
    """
    user_id = claims.get("sub")
    if not user_id:
        logger.error("JWT missing subject (sub) claim")
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token missing subject claim"
        )
    return user_id

async def get_current_user_roles(
    claims: Annotated[dict, Depends(validate_jwt)]
) -> list[str]:
    """Extract roles from validated JWT.

    Args:
        claims: Validated JWT claims

    Returns:
        list[str]: List of role names (e.g., ['user', 'contributor'])
    """
    return claims.get("roles", [])

async def get_jwt_claims(
    claims: Annotated[dict, Depends(validate_jwt)]
) -> dict:
    """Get all validated JWT claims.

    Use this dependency when you need access to the full JWT claims.

    Args:
        claims: Validated JWT claims

    Returns:
        dict: Complete JWT claims
    """
    return claims
```

### 3.3 Create Configuration Module

Create `apps/bixarena/ai-service/bixarena_ai_service/config.py`:

```python
"""Configuration management for AI service.

This module provides a centralized configuration using Pydantic settings
that can be loaded from environment variables.
"""

from pydantic_settings import BaseSettings
from functools import lru_cache

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
```

### 3.4 Implement Authenticated Endpoint

Create `apps/bixarena/ai-service/bixarena_ai_service/impl/prompt_validation_impl.py`:

```python
"""Runtime implementation for prompt validation endpoint.

This file lives under the ``impl`` package which is scanned dynamically by the
generated router (see ``apis/prompt_validation_api.py``). Any subclass of
``BasePromptValidationApi`` discovered at import time is used to service incoming
requests. By placing our implementation here we avoid modifying generated
code that would be overwritten on regeneration.
"""

from __future__ import annotations

import logging
from typing import Annotated

from fastapi import Depends
from bixarena_ai_service.apis.prompt_validation_api_base import BasePromptValidationApi
from bixarena_ai_service.models.prompt_validation import PromptValidation
from bixarena_ai_service.security.jwt_validator import (
    get_current_user_id,
    get_current_user_roles
)
from bixarena_ai_service.config import get_settings, Settings

logger = logging.getLogger(__name__)


class PromptValidationApiImpl(BasePromptValidationApi):
    """Concrete prompt validation implementation.

    This implementation provides a placeholder that returns a static confidence
    value. In the future, this will be replaced with actual ML-based validation
    using biomedical NLP models.
    """

    async def validate_prompt(
        self,
        prompt: str,
        user_id: Annotated[str, Depends(get_current_user_id)],
        roles: Annotated[list[str], Depends(get_current_user_roles)],
        settings: Annotated[Settings, Depends(get_settings)]
    ) -> PromptValidation:
        """Validate whether a prompt is biomedically related.

        Args:
            prompt: The prompt text to validate
            user_id: Authenticated user ID from JWT
            roles: User roles from JWT
            settings: Application settings

        Returns:
            PromptValidation: Validation result with confidence score
        """
        logger.info(
            f"Prompt validation requested by user {user_id} with roles {roles}"
        )
        logger.debug(f"Prompt text (length={len(prompt)}): {prompt[:100]}...")

        # TODO: Replace with actual ML-based validation
        # For now, return a static confidence value
        static_confidence = 0.75

        validation_result = PromptValidation(
            prompt=prompt,
            confidence=static_confidence,
            is_biomedical=static_confidence >= settings.prompt_validation_confidence_threshold
        )

        logger.info(
            f"Validation result: confidence={validation_result.confidence}, "
            f"isBiomedical={validation_result.is_biomedical}"
        )

        return validation_result
```

### 3.5 Update Main Application

Update `apps/bixarena/ai-service/bixarena_ai_service/main.py` to include the new router:

```python
# coding: utf-8

"""
BixArena AI Service

Advance bioinformatics by evaluating and ranking AI agents.

The version of the OpenAPI document: 1.0.0
Generated by OpenAPI Generator (https://openapi-generator.tech)

Do not edit the class manually.
"""  # noqa: E501

from fastapi import FastAPI

from bixarena_ai_service.apis.health_check_api import router as HealthCheckApiRouter
from bixarena_ai_service.apis.prompt_validation_api import router as PromptValidationApiRouter

app = FastAPI(
    title="BixArena AI Service",
    description="Advance bioinformatics by evaluating and ranking AI agents.",
    version="1.0.0",
)

app.include_router(HealthCheckApiRouter)
app.include_router(PromptValidationApiRouter)
```

## Phase 4: API Gateway Configuration

### 4.1 Add AI Service Route

Update `apps/bixarena/api-gateway/src/main/resources/application.yml`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        # ... existing routes ...

        - id: bixarena-ai-service
          uri: http://bixarena-ai-service:8116
          predicates:
            - Path=/ai/**,/health-check,/validate-prompt
```

### 4.2 Gateway JWT Minting (No Changes Required)

The API Gateway's `SessionToJwtFilter` already supports audience-specific JWT minting. When it processes a request to `/validate-prompt`, it will:

1. Check route config and find `audience: urn:bixarena:ai`
2. Verify the endpoint does not have `anonymousAccess: true`
3. Extract session cookie from request
4. Validate session by calling auth service `/userinfo` endpoint
5. Mint audience-specific JWT by calling auth service `POST /oauth2/token?audience=urn:bixarena:ai`
6. Strip session cookie and add JWT as `Authorization: Bearer <token>` header
7. Forward request to AI service

**JWT Structure Received by Python Service:**

```json
{
  "iss": "urn:bixarena:auth",
  "aud": "urn:bixarena:ai",
  "sub": "550e8400-e29b-41d4-a716-446655440000",
  "roles": ["user", "contributor"],
  "iat": 1234567890,
  "exp": 1234568490
}
```

## Phase 5: Docker & Deployment

### 5.1 Create Dockerfile

Create `apps/bixarena/ai-service/Dockerfile`:

```dockerfile
FROM python:3.13.3-slim

WORKDIR /app

# Copy dependency files
COPY pyproject.toml ./

# Install dependencies
RUN pip install --no-cache-dir -e .

# Copy application code
COPY bixarena_ai_service ./bixarena_ai_service

# Expose port
EXPOSE 8116

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD python -c "import httpx; httpx.get('http://localhost:8116/health-check', timeout=2.0)"

# Run with uvicorn (using uvloop and httptools for performance)
CMD ["uvicorn", "bixarena_ai_service.main:app", \
     "--host", "0.0.0.0", \
     "--port", "8116", \
     "--loop", "uvloop", \
     "--http", "httptools"]
```

### 5.2 Update Docker Compose

Add to your docker-compose file:

```yaml
services:
  # ... existing services ...

  bixarena-ai-service:
    build:
      context: ./apps/bixarena/ai-service
      dockerfile: Dockerfile
    container_name: bixarena-ai-service
    ports:
      - '8116:8116'
    environment:
      - BIXARENA_AI_AUTH_SERVICE_URL=http://bixarena-auth-service:8115
      - BIXARENA_AI_JWT_EXPECTED_ISSUER=urn:bixarena:auth
      - BIXARENA_AI_JWT_EXPECTED_AUDIENCE=urn:bixarena:ai
      - BIXARENA_AI_PROMPT_VALIDATION_CONFIDENCE_THRESHOLD=0.5
    depends_on:
      - bixarena-auth-service
    networks:
      - bixarena-network
    restart: unless-stopped
```

## Phase 6: Testing Strategy

### 6.1 Unit Tests

Create `apps/bixarena/ai-service/tests/test_jwt_validator.py`:

```python
"""Tests for JWT validation logic."""

import pytest
from unittest.mock import Mock, patch, MagicMock
from fastapi import HTTPException
from jose import jwt
from bixarena_ai_service.security.jwt_validator import (
    validate_jwt,
    get_current_user_id,
    get_current_user_roles,
    get_jwks
)

# Test JWKS response
MOCK_JWKS = {
    "keys": [
        {
            "kid": "test-key-id",
            "kty": "RSA",
            "use": "sig",
            "n": "...",
            "e": "AQAB"
        }
    ]
}

@patch('bixarena_ai_service.security.jwt_validator.httpx.get')
def test_get_jwks_success(mock_get):
    """Test successful JWKS fetch."""
    mock_response = MagicMock()
    mock_response.json.return_value = MOCK_JWKS
    mock_get.return_value = mock_response

    # Clear cache
    get_jwks.cache_clear()

    result = get_jwks()
    assert result == MOCK_JWKS
    mock_get.assert_called_once()

@patch('bixarena_ai_service.security.jwt_validator.httpx.get')
def test_get_jwks_failure(mock_get):
    """Test JWKS fetch failure."""
    mock_get.side_effect = Exception("Connection error")

    # Clear cache
    get_jwks.cache_clear()

    with pytest.raises(HTTPException) as exc_info:
        get_jwks()

    assert exc_info.value.status_code == 503

def test_validate_jwt_missing_kid():
    """Test JWT validation with missing key ID."""
    mock_credentials = Mock()
    mock_credentials.credentials = "fake.jwt.token"

    with patch('bixarena_ai_service.security.jwt_validator.jwt.get_unverified_header') as mock_header:
        mock_header.return_value = {}  # No 'kid' field

        with pytest.raises(HTTPException) as exc_info:
            validate_jwt(mock_credentials)

        assert exc_info.value.status_code == 401
        assert "key ID" in exc_info.value.detail

@pytest.mark.asyncio
async def test_get_current_user_id_success():
    """Test extracting user ID from claims."""
    claims = {"sub": "test-user-123", "roles": ["user"]}

    user_id = await get_current_user_id(claims)
    assert user_id == "test-user-123"

@pytest.mark.asyncio
async def test_get_current_user_id_missing():
    """Test handling missing subject claim."""
    claims = {"roles": ["user"]}

    with pytest.raises(HTTPException) as exc_info:
        await get_current_user_id(claims)

    assert exc_info.value.status_code == 401

@pytest.mark.asyncio
async def test_get_current_user_roles():
    """Test extracting roles from claims."""
    claims = {"sub": "test-user-123", "roles": ["user", "admin"]}

    roles = await get_current_user_roles(claims)
    assert roles == ["user", "admin"]

@pytest.mark.asyncio
async def test_get_current_user_roles_empty():
    """Test handling missing roles claim."""
    claims = {"sub": "test-user-123"}

    roles = await get_current_user_roles(claims)
    assert roles == []
```

Create `apps/bixarena/ai-service/tests/test_prompt_validation_impl.py`:

```python
"""Tests for prompt validation implementation."""

import pytest
from unittest.mock import AsyncMock, MagicMock
from bixarena_ai_service.impl.prompt_validation_impl import PromptValidationApiImpl
from bixarena_ai_service.config import Settings

@pytest.mark.asyncio
async def test_validate_prompt_static_response():
    """Test that validate_prompt returns static confidence value."""
    impl = PromptValidationApiImpl()
    settings = Settings()

    result = await impl.validate_prompt(
        prompt="What are the effects of aspirin on cardiovascular health?",
        user_id="test-user-123",
        roles=["user"],
        settings=settings
    )

    assert result.prompt == "What are the effects of aspirin on cardiovascular health?"
    assert result.confidence == 0.75
    assert result.is_biomedical is True

@pytest.mark.asyncio
async def test_validate_prompt_long_text():
    """Test validation with long prompt text."""
    impl = PromptValidationApiImpl()
    settings = Settings()

    long_prompt = "A" * 5000

    result = await impl.validate_prompt(
        prompt=long_prompt,
        user_id="test-user-123",
        roles=["user"],
        settings=settings
    )

    assert result.prompt == long_prompt
    assert 0.0 <= result.confidence <= 1.0
```

### 6.2 Integration Tests

Create `apps/bixarena/ai-service/tests/test_integration.py`:

```python
"""Integration tests for authenticated endpoints."""

import pytest
from fastapi.testclient import TestClient
from unittest.mock import patch, MagicMock
from bixarena_ai_service.main import app

client = TestClient(app)

def test_health_check_no_auth():
    """Health check should work without authentication."""
    response = client.get("/health-check")
    assert response.status_code == 200
    assert response.json()["status"] == "pass"

def test_validate_prompt_without_auth():
    """Endpoint should reject requests without JWT."""
    response = client.get("/validate-prompt?prompt=test")
    assert response.status_code == 403  # No Authorization header

def test_validate_prompt_with_invalid_jwt():
    """Endpoint should reject invalid JWT."""
    response = client.get(
        "/validate-prompt?prompt=test",
        headers={"Authorization": "Bearer invalid-token"}
    )
    assert response.status_code in [401, 503]  # 503 if JWKS fetch fails

@patch('bixarena_ai_service.security.jwt_validator.get_jwks')
@patch('bixarena_ai_service.security.jwt_validator.jwt.decode')
def test_validate_prompt_with_valid_jwt(mock_jwt_decode, mock_get_jwks):
    """Endpoint should accept valid JWT."""
    # Mock JWKS response
    mock_get_jwks.return_value = {
        "keys": [{"kid": "test-key", "kty": "RSA"}]
    }

    # Mock JWT decode to return valid claims
    mock_jwt_decode.return_value = {
        "sub": "test-user-123",
        "roles": ["user"],
        "iss": "urn:bixarena:auth",
        "aud": "urn:bixarena:ai"
    }

    response = client.get(
        "/validate-prompt?prompt=What+is+diabetes",
        headers={"Authorization": "Bearer valid-test-token"}
    )

    assert response.status_code == 200
    data = response.json()
    assert "prompt" in data
    assert "confidence" in data
    assert "isBiomedical" in data
    assert data["prompt"] == "What is diabetes"
    assert 0.0 <= data["confidence"] <= 1.0

def test_validate_prompt_missing_parameter():
    """Endpoint should reject requests without prompt parameter."""
    with patch('bixarena_ai_service.security.jwt_validator.get_jwks'):
        with patch('bixarena_ai_service.security.jwt_validator.jwt.decode') as mock_decode:
            mock_decode.return_value = {
                "sub": "test-user",
                "roles": ["user"],
                "iss": "urn:bixarena:auth",
                "aud": "urn:bixarena:ai"
            }

            response = client.get(
                "/validate-prompt",  # Missing prompt parameter
                headers={"Authorization": "Bearer valid-test-token"}
            )

            assert response.status_code == 422  # Validation error
```

### 6.3 End-to-End Tests

Test the full flow through the API Gateway (requires running infrastructure):

```python
"""E2E tests requiring full stack (API Gateway + Auth Service + AI Service)."""

import pytest
import httpx

# These tests require the full infrastructure to be running
pytestmark = pytest.mark.e2e

@pytest.fixture
def session_cookie():
    """Fixture that performs login and returns session cookie."""
    # TODO: Implement login flow to get real session cookie
    # This would interact with the Gradio app or auth service directly
    pass

@pytest.mark.asyncio
async def test_full_authentication_flow(session_cookie):
    """Test complete flow: Session -> Gateway -> JWT -> AI Service."""

    # Make request to API Gateway with session cookie
    async with httpx.AsyncClient(base_url="http://localhost:8080") as client:
        response = await client.get(
            "/validate-prompt",
            params={"prompt": "What causes cancer?"},
            cookies={"JSESSIONID": session_cookie}
        )

        assert response.status_code == 200
        data = response.json()
        assert data["prompt"] == "What causes cancer?"
        assert "confidence" in data
        assert "isBiomedical" in data

@pytest.mark.asyncio
async def test_anonymous_request_rejected():
    """Test that requests without session cookie are rejected."""
    async with httpx.AsyncClient(base_url="http://localhost:8080") as client:
        response = await client.get(
            "/validate-prompt",
            params={"prompt": "test"}
        )

        assert response.status_code == 401
```

## Phase 7: Observability & Monitoring

### 7.1 Structured Logging

Configure logging in `apps/bixarena/ai-service/bixarena_ai_service/logging_config.py`:

```python
"""Logging configuration for AI service."""

import logging
import sys
from typing import Any

def configure_logging(log_level: str = "INFO") -> None:
    """Configure structured logging for the application.

    Args:
        log_level: Logging level (DEBUG, INFO, WARNING, ERROR, CRITICAL)
    """
    logging.basicConfig(
        level=getattr(logging, log_level.upper()),
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
        handlers=[
            logging.StreamHandler(sys.stdout)
        ]
    )

    # Set specific logger levels
    logging.getLogger("uvicorn").setLevel(logging.INFO)
    logging.getLogger("fastapi").setLevel(logging.INFO)
    logging.getLogger("httpx").setLevel(logging.WARNING)
```

Update `main.py` to configure logging on startup:

```python
from bixarena_ai_service.logging_config import configure_logging

# Configure logging
configure_logging(log_level="INFO")
```

### 7.2 Health & Info Endpoints

The health check endpoint already exists. Consider adding an info endpoint in the OpenAPI spec:

```yaml
# In ai.openapi.yaml
paths:
  /actuator/info:
    get:
      x-anonymous-access: true
      x-sage-internal: true
      tags:
        - Actuator
      summary: Get service information
      description: Returns information about the service version and configuration
      operationId: getInfo
      responses:
        '200':
          description: Success
          content:
            application/json:
              schema:
                type: object
                properties:
                  name:
                    type: string
                  version:
                    type: string
                  environment:
                    type: string
```

### 7.3 Request Logging Middleware

Add middleware to log all requests:

```python
# In main.py
from fastapi import Request
import time
import logging

logger = logging.getLogger(__name__)

@app.middleware("http")
async def log_requests(request: Request, call_next):
    """Log all HTTP requests with timing information."""
    start_time = time.time()

    # Log request
    logger.info(f"Request: {request.method} {request.url.path}")

    # Process request
    response = await call_next(request)

    # Log response
    process_time = time.time() - start_time
    logger.info(
        f"Response: {request.method} {request.url.path} "
        f"status={response.status_code} duration={process_time:.3f}s"
    )

    return response
```

### 7.4 Metrics (Future Enhancement)

Consider adding Prometheus metrics for:

- JWT validation attempts (success/failure)
- JWT validation duration
- JWKS fetch duration
- Prompt validation request count
- Prompt validation duration

Libraries to consider:

- `prometheus-client` - Official Prometheus Python client
- `prometheus-fastapi-instrumentator` - FastAPI-specific metrics

## Phase 8: Documentation

### 8.1 API Documentation

The FastAPI application automatically generates interactive API documentation:

- **Swagger UI**: `http://localhost:8116/docs`
- **ReDoc**: `http://localhost:8116/redoc`
- **OpenAPI JSON**: `http://localhost:8116/openapi.json`

### 8.2 README

Create `apps/bixarena/ai-service/README.md`:

````markdown
# BixArena AI Service

Python-based microservice for AI-powered features in the BixArena platform.

## Features

- **Prompt Validation**: Validates whether prompts are biomedically related
- **JWT Authentication**: Integrates with BixArena auth service
- **OpenAPI-Driven**: API spec-first development with code generation

## Running Locally

### Prerequisites

- Python 3.13.3
- Running BixArena auth service

### Installation

```bash
cd apps/bixarena/ai-service
pip install -e .
```
````

### Configuration

Set environment variables:

```bash
export BIXARENA_AI_AUTH_SERVICE_URL=http://localhost:8115
export BIXARENA_AI_JWT_EXPECTED_ISSUER=urn:bixarena:auth
export BIXARENA_AI_JWT_EXPECTED_AUDIENCE=urn:bixarena:ai
```

### Running

```bash
uvicorn bixarena_ai_service.main:app --reload --port 8116
```

### Testing

```bash
pytest
```

## API Endpoints

### `GET /health-check`

Health check endpoint (no authentication required).

### `GET /validate-prompt?prompt={text}`

Validates if a prompt is biomedically related (requires authentication).

**Parameters:**

- `prompt` (query, required): The prompt text to validate

**Response:**

```json
{
  "prompt": "What causes diabetes?",
  "confidence": 0.75,
  "isBiomedical": true
}
```

## Architecture

This service integrates with the BixArena authentication architecture:

1. Client sends request with session cookie to API Gateway
2. Gateway validates session and mints JWT with audience `urn:bixarena:ai`
3. Gateway forwards request to AI service with JWT in Authorization header
4. AI service validates JWT using JWKS from auth service
5. AI service processes request and returns response

## Development

### Code Generation

When OpenAPI specs change, regenerate code:

```bash
nx build bixarena-api-description
nx run-many -t=generate -p=bixarena-*
```

### Project Structure

```
apps/bixarena/ai-service/
├── bixarena_ai_service/
│   ├── apis/              # Generated API routers
│   ├── models/            # Generated Pydantic models
│   ├── impl/              # Custom implementations (not generated)
│   ├── security/          # JWT validation logic
│   ├── config.py          # Configuration management
│   └── main.py            # FastAPI application
├── tests/                 # Unit and integration tests
├── pyproject.toml         # Python dependencies
└── README.md
```

### Implementation Pattern

1. OpenAPI spec defines the API contract
2. Code generation creates router stubs in `apis/`
3. Custom implementations go in `impl/` as subclasses
4. Routers automatically discover and use implementations

**Do not edit generated files** - they are marked in `.openapi-generator/FILES`.

```

## Summary Checklist

### Phase 1: OpenAPI Specification
- [ ] Create `libs/bixarena/api-description/src/paths/validate-prompt.yaml`
- [ ] Create `libs/bixarena/api-description/src/components/schemas/PromptValidation.yaml`
- [ ] Update `libs/bixarena/api-description/src/ai.openapi.yaml` with new path reference
- [ ] Add "Prompt Validation" tag to OpenAPI spec

### Phase 2: Code Generation
- [ ] Run `nx build bixarena-api-description`
- [ ] Run `nx run-many -t=generate -p=bixarena-*`
- [ ] Verify generated files in `apps/bixarena/ai-service/bixarena_ai_service/`
- [ ] Verify updated `apps/bixarena/api-gateway/src/main/resources/routes.yml`

### Phase 3: Python Implementation
- [ ] Update `apps/bixarena/ai-service/pyproject.toml` with `python-jose` dependency
- [ ] Create `bixarena_ai_service/security/__init__.py`
- [ ] Create `bixarena_ai_service/security/jwt_validator.py`
- [ ] Create `bixarena_ai_service/config.py`
- [ ] Create `bixarena_ai_service/impl/prompt_validation_impl.py`
- [ ] Update `bixarena_ai_service/main.py` to include new router

### Phase 4: API Gateway Configuration
- [ ] Update `apps/bixarena/api-gateway/src/main/resources/application.yml` with AI service route
- [ ] Verify route configuration in generated `routes.yml`

### Phase 5: Docker & Deployment
- [ ] Create `apps/bixarena/ai-service/Dockerfile`
- [ ] Update docker-compose configuration
- [ ] Test local deployment

### Phase 6: Testing
- [ ] Create unit tests for JWT validation
- [ ] Create unit tests for prompt validation implementation
- [ ] Create integration tests for API endpoints
- [ ] Create E2E tests for full authentication flow
- [ ] Run test suite and verify coverage

### Phase 7: Observability
- [ ] Configure structured logging
- [ ] Add request logging middleware
- [ ] Verify health check endpoint
- [ ] (Optional) Add Prometheus metrics

### Phase 8: Documentation
- [ ] Create `apps/bixarena/ai-service/README.md`
- [ ] Document API endpoints
- [ ] Document authentication flow
- [ ] Document development workflow

## Key Architecture Decisions

### Why This Approach Works

1. **Leverages Existing Infrastructure**: Uses auth service's `/oauth2/token?audience=urn:bixarena:ai` endpoint without any changes to auth service
2. **Consistent Security Model**: Same JWT validation pattern as Java services, just implemented in Python
3. **API-First Design**: OpenAPI spec drives both server stubs and route configuration, ensuring consistency
4. **Zero Gateway Changes**: Gateway's `SessionToJwtFilter` already supports audience-specific JWTs through route configuration
5. **Python-Native**: Uses FastAPI + `python-jose` instead of trying to replicate Spring Security
6. **Separation of Concerns**: Generated code stays separate from custom implementations via the `impl/` pattern

### Authentication Flow Diagram

```

┌──────────┐ Session Cookie ┌─────────────┐
│ Gradio │ ───────────────────> │ API Gateway │
│ App │ │ │
└──────────┘ └──────┬──────┘
│
│ 1. Validate session
↓
┌─────────────┐
│ Auth Service│
│ /userinfo │
└──────┬──────┘
│
│ 2. Mint JWT
↓
┌─────────────┐
│ Auth Service│
│ /oauth2/ │
│ token?aud= │
│ urn:bix │
│ arena:ai │
└──────┬──────┘
│
│ JWT (Bearer token)
↓
┌─────────────┐
│ AI Service │
│ (Python) │
└──────┬──────┘
│
│ 3. Validate JWT
↓
┌─────────────┐
│ Auth Service│
│ /.well-known│
│ /jwks.json │
└─────────────┘

````

### JWT Structure

The AI service receives JWTs with the following structure:

```json
{
  "iss": "urn:bixarena:auth",
  "aud": "urn:bixarena:ai",
  "sub": "550e8400-e29b-41d4-a716-446655440000",
  "roles": ["user", "contributor"],
  "iat": 1234567890,
  "exp": 1234568490,
  "nbf": 1234567890
}
````

### Security Considerations

1. **JWT Validation**: Every protected endpoint validates the JWT signature, expiration, issuer, and audience
2. **JWKS Caching**: JWKS is cached using `@lru_cache` to reduce load on auth service
3. **No Session Cookies**: API Gateway strips all cookies before forwarding to AI service
4. **Audience Isolation**: Each microservice has its own audience, preventing JWT reuse across services
5. **Short-Lived Tokens**: JWTs expire after 10 minutes, requiring fresh minting

## Next Steps After Implementation

1. **Replace Static Implementation**: Update `prompt_validation_impl.py` with actual ML model
2. **Add Rate Limiting**: Consider per-user rate limits on prompt validation
3. **Add Caching**: Cache validation results for identical prompts
4. **Add Metrics**: Implement Prometheus metrics for monitoring
5. **Add More Endpoints**: Expand AI service with additional ML-powered features
6. **Performance Testing**: Load test the authentication flow and JWT validation
7. **Security Audit**: Review JWT handling and validation logic
