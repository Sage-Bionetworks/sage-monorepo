#!/usr/bin/env python3
"""CDK app for prod environment."""

import os

import aws_cdk as cdk

from bixarena_infra_cdk.shared.config import (
    get_developer_name,
    get_environment,
    get_stack_prefix,
)
from bixarena_infra_cdk.shared.stacks.alb_stack import AlbStack
from bixarena_infra_cdk.shared.stacks.bucket_stack import BucketStack
from bixarena_infra_cdk.shared.stacks.database_stack import DatabaseStack
from bixarena_infra_cdk.shared.stacks.ecs_cluster_stack import EcsClusterStack
from bixarena_infra_cdk.shared.stacks.valkey_stack import ValkeyStack
from bixarena_infra_cdk.shared.stacks.vpc_stack import VpcStack
from bixarena_infra_cdk.shared.stacks.web_stack import WebStack


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
    app_version = os.getenv("APP_VERSION", "latest")
    # FQDN is optional - if not provided, will use ALB DNS name
    fqdn = os.getenv("FQDN", "")

    # Add common tags
    cdk.Tags.of(app).add("Environment", environment)
    cdk.Tags.of(app).add("Product", "BixArena")
    cdk.Tags.of(app).add("ManagedBy", "CDK")

    # Create VPC stack
    # Production uses NAT gateways matching the number of AZs for high availability
    vpc_stack = VpcStack(
        app,
        f"{stack_prefix}-vpc",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc_cidr=vpc_cidr,
        max_azs=max_azs,
        nat_gateways=max_azs,  # One NAT per AZ for high availability
        description=f"VPC for BixArena {environment} environment",
    )

    # Create database stack (depends on VPC)
    # Production uses Multi-AZ deployment for high availability and disaster recovery
    database_stack = DatabaseStack(
        app,
        f"{stack_prefix}-database",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc,
        description=f"PostgreSQL database for BixArena {environment} environment",
    )
    database_stack.add_dependency(vpc_stack)

    # Create Valkey cache stack (depends on VPC)
    # Production uses multi-node deployment for high availability
    valkey_stack = ValkeyStack(
        app,
        f"{stack_prefix}-valkey",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc,
        description=f"Valkey cache cluster for BixArena {environment} environment",
    )
    valkey_stack.add_dependency(vpc_stack)

    # Create ALB stack (depends on VPC)
    alb_stack = AlbStack(
        app,
        f"{stack_prefix}-alb",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc,
        certificate_arn=certificate_arn if certificate_arn else None,
        description=(
            f"Application Load Balancer for BixArena {environment} environment"
        ),
    )
    alb_stack.add_dependency(vpc_stack)

    # Create ECS cluster stack (depends on VPC)
    ecs_cluster_stack = EcsClusterStack(
        app,
        f"{stack_prefix}-ecs-cluster",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc,
        description=f"ECS cluster for BixArena {environment} environment",
    )
    ecs_cluster_stack.add_dependency(vpc_stack)

    # Create web stack (depends on ECS cluster and ALB)
    # Uses ALB DNS name by default, or custom FQDN if provided
    use_https = certificate_arn is not None and certificate_arn.strip() != ""
    web_stack = WebStack(
        app,
        f"{stack_prefix}-web",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc,
        cluster=ecs_cluster_stack.cluster,
        target_group=alb_stack.web_target_group,
        app_version=app_version,
        alb_dns_name=alb_stack.alb.load_balancer_dns_name,
        fqdn=fqdn if fqdn else None,
        use_https=use_https,
        description=f"Web client for BixArena {environment} environment",
    )
    web_stack.add_dependency(ecs_cluster_stack)
    web_stack.add_dependency(alb_stack)

    # Create bucket stack
    BucketStack(
        app,
        stack_prefix,  # Use stack_prefix directly as stack ID
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        description=f"S3 buckets for BixArena {environment} environment",
    )

    app.synth()


if __name__ == "__main__":
    main()
