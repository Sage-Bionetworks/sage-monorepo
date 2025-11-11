"""Utility for loading Docker images from local tarballs or remote registries."""

import os

from aws_cdk import aws_ecr_assets as ecr_assets
from constructs import Construct


def load_container_image(
    scope: Construct,
    construct_id: str,
    service_name: str,
    remote_image: str,
    env_var_name: str | None = None,
) -> str:
    """
    Load container image from local tarball or remote registry.

    This function checks if a local image tarball should be used (via environment
    variable) and returns the appropriate image URI for ECS deployment.

    Environment variable precedence (first match wins):
    1. Service-specific: USE_LOCAL_{SERVICE_NAME}_IMAGE=true
    2. Global: USE_LOCAL_IMAGES=true (applies to all services)
    3. Default: false (use remote GHCR image)

    Args:
        scope: CDK construct scope (typically 'self' from the stack)
        construct_id: Unique ID for the image asset construct
        service_name: Service name (e.g., 'bixarena-api', 'bixarena-auth-service')
        remote_image: Full GHCR image URI (e.g., 'ghcr.io/org/image:tag')
        env_var_name: Environment variable name to check (defaults to
                      USE_LOCAL_{SERVICE_NAME}_IMAGE)

    Returns:
        str: Image URI to use for the ECS service

    Raises:
        FileNotFoundError: If local image is requested but tarball doesn't exist

    Example:
        >>> # Use global flag for all services
        >>> os.environ['USE_LOCAL_IMAGES'] = 'true'
        >>> image = load_container_image(
        ...     self,
        ...     "ApiServiceImage",
        ...     "bixarena-api",
        ...     "ghcr.io/sage-bionetworks/bixarena-api:edge"
        ... )
        # Returns ECR URI from /tmp/bixarena-api.tar

        >>> # Override for specific service
        >>> os.environ['USE_LOCAL_BIXARENA_API_IMAGE'] = 'false'
        >>> # This service will use GHCR even if USE_LOCAL_IMAGES=true
    """
    # Default env var name based on service name
    if env_var_name is None:
        # Convert service-name to SERVICE_NAME format
        env_var_name = f"USE_LOCAL_{service_name.upper().replace('-', '_')}_IMAGE"

    # Check service-specific flag first, then global flag
    use_local_service = os.getenv(env_var_name, "").lower()
    use_local_global = os.getenv("USE_LOCAL_IMAGES", "false").lower()

    # Service-specific flag takes precedence
    if use_local_service:
        use_local = use_local_service == "true"
    else:
        use_local = use_local_global == "true"

    if use_local:
        tarball_path = f"/tmp/{service_name}.tar"

        # Check if tarball exists
        if not os.path.exists(tarball_path):
            raise FileNotFoundError(
                f"Local image tarball not found at {tarball_path}. "
                f"Build and export first: nx export-image-tarball {service_name}"
            )

        print(f"Using local {service_name} image from {tarball_path}")
        image_asset = ecr_assets.TarballImageAsset(
            scope,
            construct_id,
            tarball_file=tarball_path,
        )
        return image_asset.image_uri
    else:
        # Use remote GHCR image (default behavior)
        return remote_image
