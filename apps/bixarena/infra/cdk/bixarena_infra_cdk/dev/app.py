#!/usr/bin/env python3
"""CDK app for dev environment."""

import os

import aws_cdk as cdk

from bixarena_infra_cdk.shared.config import (
    get_developer_name,
    get_environment,
    get_stack_prefix,
)
from bixarena_infra_cdk.shared.stacks.alb_stack import AlbStack
from bixarena_infra_cdk.shared.stacks.api_gateway_stack import ApiGatewayStack
from bixarena_infra_cdk.shared.stacks.api_service_stack import ApiServiceStack
from bixarena_infra_cdk.shared.stacks.auth_service_stack import AuthServiceStack
from bixarena_infra_cdk.shared.stacks.bastion_stack import BastionStack
from bixarena_infra_cdk.shared.stacks.bucket_stack import BucketStack
from bixarena_infra_cdk.shared.stacks.database_stack import DatabaseStack
from bixarena_infra_cdk.shared.stacks.ecs_cluster_stack import EcsClusterStack
from bixarena_infra_cdk.shared.stacks.valkey_stack import ValkeyStack
from bixarena_infra_cdk.shared.stacks.vpc_stack import VpcStack
from bixarena_infra_cdk.shared.stacks.web_stack import WebStack


def main() -> None:
    """Main entry point for dev environment CDK app."""
    app = cdk.App()

    environment = get_environment()
    developer_name = get_developer_name()
    stack_prefix = get_stack_prefix()

    # Get optional configuration
    vpc_cidr = os.getenv("VPC_CIDR", "10.0.0.0/16")
    certificate_arn = os.getenv("CERTIFICATE_ARN", "")
    app_version = os.getenv("APP_VERSION", "edge")
    # FQDN is optional - if not provided, will use ALB DNS name
    fqdn = os.getenv("FQDN", "")

    # Add common tags
    cdk.Tags.of(app).add("Environment", environment)
    cdk.Tags.of(app).add("Product", "BixArena")
    cdk.Tags.of(app).add("ManagedBy", "CDK")

    # Add developer tag for dev environment
    if developer_name:
        cdk.Tags.of(app).add("Developer", developer_name)

    # Create VPC stack
    # Dev uses 1 NAT gateway for cost optimization (cheaper but single point of failure)
    vpc_stack = VpcStack(
        app,
        f"{stack_prefix}-vpc",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc_cidr=vpc_cidr,
        nat_gateways=1,  # Single NAT for cost savings in dev
        description=f"VPC for BixArena {environment} environment",
    )

    # Create database stack (depends on VPC)
    # Dev uses cost-optimized single-AZ deployment
    database_stack = DatabaseStack(
        app,
        f"{stack_prefix}-database",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc_construct.vpc,
        description=f"PostgreSQL database for BixArena {environment} environment",
    )
    database_stack.add_dependency(vpc_stack)

    # Create Valkey cache stack (depends on VPC)
    # Dev uses cost-optimized single-node deployment
    valkey_stack = ValkeyStack(
        app,
        f"{stack_prefix}-valkey",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc_construct.vpc,
        description=f"Valkey cache cluster for BixArena {environment} environment",
    )
    valkey_stack.add_dependency(vpc_stack)

    # Create ALB stack (depends on VPC)
    # Note: ALB must be created before app/gateway services can attach to it
    alb_stack = AlbStack(
        app,
        f"{stack_prefix}-alb",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc_construct.vpc,
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
        vpc=vpc_stack.vpc_construct.vpc,
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
        vpc=vpc_stack.vpc_construct.vpc,
        cluster=ecs_cluster_stack.cluster_construct.cluster,
        target_group=alb_stack.web_target_group,
        app_version=app_version,
        alb_dns_name=alb_stack.alb_construct.alb.load_balancer_dns_name,
        fqdn=fqdn if fqdn else None,
        use_https=use_https,
        openrouter_api_key=os.getenv("OPENROUTER_API_KEY", ""),
        description=f"Web client for BixArena {environment} environment",
    )
    web_stack.add_dependency(ecs_cluster_stack)
    web_stack.add_dependency(alb_stack)

    # Create API service stack (depends on database, valkey, and ECS cluster)
    # Database secret is guaranteed to exist since we use from_generated_secret()
    database_secret = database_stack.database_construct.database.secret
    if database_secret is None:
        raise ValueError(
            "Database secret must be created. "
            "Verify PostgreSQL database initialization completed successfully."
        )
    _api_service_stack = ApiServiceStack(
        app,
        f"{stack_prefix}-api-service",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc_construct.vpc,
        cluster=ecs_cluster_stack.cluster_construct.cluster,
        database=database_stack.database_construct.database,
        database_secret_arn=database_secret.secret_arn,
        valkey_endpoint=valkey_stack.valkey_construct.cluster_endpoint,
        valkey_port=valkey_stack.valkey_construct.cluster_port,
        app_version=app_version,  # Use same version tag as app
        ui_base_url=(
            f"{'https' if use_https else 'http'}://"
            f"{fqdn if fqdn else alb_stack.alb_construct.alb.load_balancer_dns_name}"
        ),
        description=f"API service for BixArena {environment} environment",
    )
    # Note: Dependencies are automatic via CloudFormation references
    # (database endpoint, etc.)
    # Don't add manual add_dependency() calls to avoid cyclic dependencies
    # with security group rules

    # Create Auth service stack (depends on database, valkey, and ECS cluster)
    _auth_service_stack = AuthServiceStack(
        app,
        f"{stack_prefix}-auth-service",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc_construct.vpc,
        cluster=ecs_cluster_stack.cluster_construct.cluster,
        database=database_stack.database_construct.database,
        database_secret_arn=database_secret.secret_arn,
        valkey_endpoint=valkey_stack.valkey_construct.cluster_endpoint,
        valkey_port=valkey_stack.valkey_construct.cluster_port,
        app_version=app_version,  # Use same version tag as app
        ui_base_url=(
            f"{'https' if use_https else 'http'}://"
            f"{fqdn if fqdn else alb_stack.alb_construct.alb.load_balancer_dns_name}"
        ),
        synapse_client_id=os.getenv("SYNAPSE_CLIENT_ID", "changeme"),
        synapse_client_secret=os.getenv("SYNAPSE_CLIENT_SECRET", "changeme"),
        description=f"Auth service for BixArena {environment} environment",
    )
    # Note: Dependencies are automatic via CloudFormation references

    # Create API Gateway stack (depends on API, Auth, Valkey, ECS cluster, and ALB)
    api_gateway_stack = ApiGatewayStack(
        app,
        f"{stack_prefix}-api-gateway",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc_construct.vpc,
        cluster=ecs_cluster_stack.cluster_construct.cluster,
        target_group=alb_stack.api_gateway_target_group,
        valkey_endpoint=valkey_stack.valkey_construct.cluster_endpoint,
        valkey_port=valkey_stack.valkey_construct.cluster_port,
        app_version=app_version,  # Use same version tag as app
        description=f"API Gateway for BixArena {environment} environment",
    )
    # Explicit dependencies: Gateway must deploy AFTER backend services
    # This ensures backend services are available when Gateway starts
    api_gateway_stack.add_dependency(_api_service_stack)
    api_gateway_stack.add_dependency(_auth_service_stack)

    # Note: Security group rules are configured within the constructs to allow
    # connections from the VPC CIDR range, avoiding cyclic dependencies

    # Create bucket stack
    BucketStack(
        app,
        stack_prefix,  # Use stack_prefix directly as stack ID
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        description=f"S3 buckets for BixArena {environment} environment",
    )

    # Create bastion stack for database access (dev environment only)
    # This creates an ECS service with 1 task for Session Manager port forwarding
    # The bastion can connect to the database since the database allows connections
    # from the VPC CIDR range. No cross-stack security group references needed.
    BastionStack(
        app,
        f"{stack_prefix}-bastion",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc_construct.vpc,
        cluster=ecs_cluster_stack.cluster_construct.cluster,
        description=f"Database bastion for BixArena {environment} environment",
    )

    app.synth()


if __name__ == "__main__":
    main()
