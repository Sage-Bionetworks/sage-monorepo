"""Configuration management for BixArena CDK infrastructure."""

import os

from platform_infra_cdk_common.constants import VALID_ENVIRONMENTS


def get_environment() -> str:
    """
    Get deployment environment.

    Returns:
        str: Environment name (dev, stage, or prod)

    Raises:
        ValueError: If ENV is not set or invalid
    """
    env = os.environ.get("ENV")
    if not env:
        raise ValueError("ENV environment variable is required")

    if env not in VALID_ENVIRONMENTS:
        raise ValueError(
            f"Invalid environment: {env}. Must be one of {VALID_ENVIRONMENTS}"
        )

    return env


def get_developer_name() -> str | None:
    """
    Get developer name for dev deployments.

    Returns:
        str | None: Developer name (GitHub username) for dev environment,
            None otherwise. Returns None when not provided (for CI/CD deployments).
    """
    env = get_environment()
    developer_name = os.environ.get("DEVELOPER_NAME")

    # Developer name is optional for dev environment
    # When provided: Creates isolated stack like 'bixarena-dev-jsmith'
    # When omitted: Creates shared dev stack 'bixarena-dev' (for CI/CD)

    # Validate developer name format if provided
    # (alphanumeric, hyphens, underscores only)
    if developer_name and not all(c.isalnum() or c in "-_" for c in developer_name):
        raise ValueError(
            f"Invalid DEVELOPER_NAME: {developer_name}. "
            "Must contain only alphanumeric characters, hyphens, and underscores"
        )

    return developer_name if env == "dev" else None


def get_stack_prefix() -> str:
    """
    Generate stack name prefix based on environment and developer.

    Returns:
        str: Stack prefix (e.g., 'bixarena-dev-jsmith' or 'bixarena-stage')

    Examples:
        Dev (DEVELOPER_NAME=jsmith): 'bixarena-dev-jsmith'
        Stage: 'bixarena-stage'
        Prod: 'bixarena-prod'
    """
    env = get_environment()
    developer_name = get_developer_name()

    if env == "dev" and developer_name:
        return f"bixarena-dev-{developer_name}"
    else:
        return f"bixarena-{env}"
