#!/usr/bin/env python3
"""CDK app for prod environment."""

import os

import aws_cdk as cdk

from openchallenges_infra_cdk.shared.config import (
    get_developer_name,
    get_environment,
    get_stack_prefix,
)
from openchallenges_infra_cdk.shared.stacks.alb_stack import AlbStack
from openchallenges_infra_cdk.shared.stacks.bucket_stack import BucketStack
from openchallenges_infra_cdk.shared.stacks.vpc_stack import VpcStack


def main() -> None:
    """Main entry point for prod environment CDK app."""
    app = cdk.App()

    environment = get_environment()
    developer_name = get_developer_name()  # Will be None for prod
    stack_prefix = get_stack_prefix()

    # Get optional configuration
    vpc_cidr = os.getenv("VPC_CIDR", "10.0.0.0/16")
    certificate_arn = os.getenv("CERTIFICATE_ARN", "")
    max_azs = int(os.getenv("MAX_AZS", "2"))  # Number of Availability Zones

    # Add common tags
    cdk.Tags.of(app).add("Environment", environment)
    cdk.Tags.of(app).add("Product", "OpenChallenges")
    cdk.Tags.of(app).add("ManagedBy", "CDK")

    # Create VPC stack
    # Production uses NAT gateways matching the number of AZs for high availability
    vpc_stack = VpcStack(
        app,
        f"{stack_prefix}-vpc",
        stack_prefix=stack_prefix,
        environment=environment,
        vpc_cidr=vpc_cidr,
        max_azs=max_azs,
        nat_gateways=max_azs,  # One NAT per AZ for high availability
        description=f"VPC for OpenChallenges {environment} environment",
    )

    # Create ALB stack (depends on VPC)
    alb_stack = AlbStack(
        app,
        f"{stack_prefix}-alb",
        stack_prefix=stack_prefix,
        environment=environment,
        vpc=vpc_stack.vpc,
        certificate_arn=certificate_arn if certificate_arn else None,
        description=(
            f"Application Load Balancer for OpenChallenges {environment} environment"
        ),
    )
    alb_stack.add_dependency(vpc_stack)

    # Create bucket stack
    BucketStack(
        app,
        stack_prefix,  # Use stack_prefix directly as stack ID
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        description=f"S3 buckets for OpenChallenges {environment} environment",
    )

    app.synth()


if __name__ == "__main__":
    main()
