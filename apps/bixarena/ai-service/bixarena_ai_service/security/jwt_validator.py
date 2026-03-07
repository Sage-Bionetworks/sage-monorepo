"""JWT validation using JWKS from auth service.

This module provides FastAPI dependencies for validating JWTs issued by the
BixArena auth service. It fetches the JWKS (JSON Web Key Set) from the auth
service and validates tokens according to the OAuth 2.0 Bearer Token specification.
"""

from __future__ import annotations

import logging
from functools import lru_cache
from typing import Annotated

import httpx
from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from jose import JWTError, jwt
from jose.exceptions import JWKError

from bixarena_ai_service.config import get_settings

logger = logging.getLogger(__name__)

# HTTP Bearer security scheme with auto_error=False to handle missing auth ourselves
security = HTTPBearer(auto_error=False)


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
    settings = get_settings()
    jwks_url = f"{settings.auth_service_url}/.well-known/jwks.json"
    try:
        response = httpx.get(jwks_url, timeout=5.0)
        response.raise_for_status()
        logger.info("Successfully fetched JWKS from auth service")
        return response.json()
    except httpx.HTTPError as e:
        logger.error("Failed to fetch JWKS from %s: %s", jwks_url, e)
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="Cannot validate token - auth service unavailable",
        )


def validate_jwt(
    credentials: Annotated[HTTPAuthorizationCredentials | None, Depends(security)],
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
    # Check if Authorization header was provided
    if credentials is None:
        logger.warning("No Authorization header provided")
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Missing authentication credentials",
            headers={"WWW-Authenticate": "Bearer"},
        )

    token = credentials.credentials

    try:
        # Fetch JWKS
        jwks = get_jwks()

        # Decode token header to get key ID
        unverified_header = jwt.get_unverified_header(token)
        kid = unverified_header.get("kid")

        if not kid:
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
            logger.warning("Token key ID %s not found in JWKS", kid)
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Token key not found in JWKS",
                headers={"WWW-Authenticate": "Bearer"},
            )

        # Validate and decode JWT
        settings = get_settings()
        claims = jwt.decode(
            token,
            key,
            algorithms=["RS256"],
            issuer=settings.jwt_expected_issuer,
            audience=settings.jwt_expected_audience,
            options={
                "verify_signature": True,
                "verify_exp": True,
                "verify_iat": True,
                "verify_aud": True,
                "verify_iss": True,
            },
        )

        logger.debug("Successfully validated JWT for subject: %s", claims.get("sub"))
        return claims

    except JWTError as e:
        logger.warning("JWT validation failed: %s", e)
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or expired token",
            headers={"WWW-Authenticate": "Bearer"},
        )


# Convenience dependencies for accessing specific claims


async def get_current_user_id(claims: Annotated[dict, Depends(validate_jwt)]) -> str:
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
            detail="Token missing subject claim",
        )
    return user_id


async def get_current_user_roles(
    claims: Annotated[dict, Depends(validate_jwt)],
) -> list[str]:
    """Extract roles from validated JWT.

    Args:
        claims: Validated JWT claims

    Returns:
        list[str]: List of role names (e.g., ['user', 'contributor'])
    """
    return claims.get("roles", [])


async def get_jwt_claims(claims: Annotated[dict, Depends(validate_jwt)]) -> dict:
    """Get all validated JWT claims.

    Use this dependency when you need access to the full JWT claims.

    Args:
        claims: Validated JWT claims

    Returns:
        dict: Complete JWT claims
    """
    return claims
