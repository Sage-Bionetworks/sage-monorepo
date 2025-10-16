#!/usr/bin/env python3
"""CDK app for prod environment."""

import aws_cdk as cdk

from openchallenges_infra_cdk.shared.config import (
    get_developer_name,
    get_environment,
    get_stack_prefix,
)
from openchallenges_infra_cdk.shared.stacks.bucket_stack import BucketStack


def main() -> None:
    """Main entry point for prod environment CDK app."""
    app = cdk.App()

    environment = get_environment()
    developer_name = get_developer_name()  # Will be None for prod
    stack_prefix = get_stack_prefix()

    # Add common tags
    cdk.Tags.of(app).add("Environment", environment)
    cdk.Tags.of(app).add("Product", "OpenChallenges")
    cdk.Tags.of(app).add("ManagedBy", "CDK")

    # Create bucket stack
    BucketStack(
        app,
        f"{stack_prefix}-buckets",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        description=f"S3 buckets for OpenChallenges {environment} environment",
    )

    app.synth()


if __name__ == "__main__":
    main()
