"""Resource naming utilities for OpenChallenges infrastructure."""


def generate_resource_name(
    resource_type: str,
    environment: str,
    developer_name: str | None = None,
) -> str:
    """
    Generate consistent resource names.

    Args:
        resource_type: Type of resource (e.g., 'img', 'alb-logs', 'vpc')
        environment: Environment name (dev, stage, prod)
        developer_name: Developer name for dev environment (optional)

    Returns:
        str: Resource name following naming convention

    Examples:
        Dev (developer_name='jsmith'):
            'openchallenges-dev-jsmith-img'
        Stage:
            'openchallenges-stage-img'
        Prod:
            'openchallenges-prod-img'

    Raises:
        ValueError: If resource_type or environment is empty
    """
    if not resource_type:
        raise ValueError("resource_type cannot be empty")
    if not environment:
        raise ValueError("environment cannot be empty")

    # Construct base name
    if environment == "dev" and developer_name:
        base_name = f"openchallenges-dev-{developer_name}-{resource_type}"
    else:
        base_name = f"openchallenges-{environment}-{resource_type}"

    # Validate length (AWS resource names have various limits)
    # S3 bucket names: 3-63 characters
    # Most other resources: typically 1-255 characters
    # We'll warn if the base name is too long before suffixes
    if len(base_name) > 50:
        raise ValueError(
            f"Generated resource name is too long ({len(base_name)} chars): "
            f"{base_name}. Consider using a shorter developer name or "
            "resource type."
        )

    return base_name


def validate_aws_name(name: str, max_length: int = 63) -> None:
    """
    Validate AWS resource name against common constraints.

    Args:
        name: Resource name to validate
        max_length: Maximum allowed length (default 63 for S3 buckets)

    Raises:
        ValueError: If name violates AWS naming constraints
    """
    if not name:
        raise ValueError("Resource name cannot be empty")

    if len(name) > max_length:
        raise ValueError(
            f"Resource name exceeds maximum length ({len(name)} > {max_length}): {name}"
        )

    # Check for invalid characters (varies by resource type, this is a general check)
    if not all(c.isalnum() or c in "-_." for c in name):
        raise ValueError(
            f"Resource name contains invalid characters: {name}. "
            "Only alphanumeric, hyphens, underscores, and periods allowed."
        )

    # S3-specific: must start and end with alphanumeric
    if name[0] not in "abcdefghijklmnopqrstuvwxyz0123456789":
        raise ValueError(
            f"Resource name must start with alphanumeric character: {name}"
        )

    if name[-1] not in "abcdefghijklmnopqrstuvwxyz0123456789":
        raise ValueError(f"Resource name must end with alphanumeric character: {name}")
