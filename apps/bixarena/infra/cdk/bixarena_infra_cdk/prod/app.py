#!/usr/bin/env python3
"""BixArena CDK Application - Production Environment.

This module defines the complete infrastructure stack for the BixArena platform
in the production environment.

Architecture Overview:
    The application consists of several interconnected services organized in layers:

    1. Foundation Layer:
       - VPC with public/private subnets across multiple availability zones
       - Application Load Balancer (ALB) for HTTPS traffic routing
       - S3 bucket for application data storage

    2. Data Layer:
       - PostgreSQL database (RDS) with Multi-AZ deployment for high availability
       - Valkey cache cluster with multi-node configuration

    3. Service Layer:
       - Auth Service: OAuth2/OIDC authentication with Synapse integration
       - API Service: Backend business logic and data access
       - API Gateway: Request routing, session validation, and service aggregation
       - Web Client: Gradio-based Python frontend application

    4. Management Layer:
       - Bastion host: ECS task for secure database access via Session Manager
       - ECS Cluster: Container orchestration for all services

Service Discovery:
    Services communicate internally using AWS Cloud Map service discovery:
    - bixarena-auth-service.{cluster_name}.local:8115 (Auth Service)
    - bixarena-api.{cluster_name}.local:8112 (API Service)
    - bixarena-api-gateway.{cluster_name}.local:8113 (API Gateway)
    - bixarena-app.{cluster_name}.local:8100 (Web Client)

    External traffic flows through the ALB:
    - /health → Fixed ALB response (health check)
    - /api/*, /auth/*, /userinfo, /.well-known/jwks.json, /oauth2/token → API Gateway
    - /* (default) → Web Client

Stack Dependencies:
    Stacks have explicit and implicit dependencies that determine deployment order:

    1. VPC Stack (no dependencies)
       - Provides networking foundation for all resources

    2. Database Stack, Valkey Stack, ALB Stack (depends on: VPC)
       - Database and Valkey deployed in private subnets with Multi-AZ
       - ALB deployed in public subnets

    3. ECS Cluster Stack (depends on: VPC)
       - Container orchestration platform

    4. Auth Service Stack, API Service Stack
       (depends on: VPC, ECS Cluster, Database, Valkey)
       - Backend services with database and cache access
       - Use Cloud Map for service discovery

    5. API Gateway Stack
       (depends on: VPC, ECS Cluster, Valkey, Auth Service, API Service)
       - Routes traffic to backend services
       - Validates sessions using Auth Service
       - Connected to ALB target group

    6. Web Stack (depends on: VPC, ECS Cluster, ALB)
       - Frontend service connected to ALB
       - Communicates with backend via API Gateway

    7. Bastion Stack, Bucket Stack (depends on: VPC, ECS Cluster)
       - Support infrastructure

    Note: Some dependencies are implicit via CloudFormation references (e.g., security
    group rules, database endpoints) and don't require explicit add_dependency() calls.

Environment Configuration:
    Production environment uses high-availability, production-grade settings:

    - VPC: One NAT Gateway per AZ for high availability
    - Database: t4g.large, Multi-AZ, 100GB storage, 30-day backup retention
    - Valkey: Multi-node deployment for high availability
    - Services: Production-grade CPU/memory allocation
    - Bastion: Enabled for secure database access via Session Manager
    - HTTPS: Required via CERTIFICATE_ARN environment variable
    - Multi-AZ: Enabled for all critical services (2 AZs by default)
    - Deletion Protection: Enabled for database

    Configuration via environment variables:
    - ENVIRONMENT: Must be "prod"
    - STACK_PREFIX: Unique identifier for this deployment
      (default: bixarena-prod)
    - VPC_CIDR: VPC CIDR block (default: 10.0.0.0/16)
    - MAX_AZS: Number of Availability Zones (default: 2)
    - APP_VERSION: Docker image tag for all services (default: "latest")
    - CERTIFICATE_ARN: ACM certificate ARN for HTTPS (REQUIRED for production)
    - FQDN: Custom domain name (optional, uses ALB DNS if not provided)
    - SYNAPSE_CLIENT_ID: Synapse OAuth client ID (REQUIRED)
    - SYNAPSE_CLIENT_SECRET: Synapse OAuth client secret (REQUIRED)
    - OPENROUTER_API_KEY: API key for LLM access (optional)

Docker Images:
    Images are loaded from remote registry:
    - Remote: Uses ghcr.io/sage-bionetworks/{service}:{APP_VERSION}
    - Default version: "latest" (stable releases only)
    - Never use "edge" or development tags in production

Security:
    - All services run in private subnets (no direct internet access)
    - Sensitive credentials stored in AWS Secrets Manager:
      * Database credentials (username/password)
      * Synapse OAuth credentials (client ID/secret)
      * OpenRouter API key
    - Credentials injected securely at runtime via ECS Secrets
    - Never exposed in CloudFormation templates or logs
    - Security groups restrict traffic to VPC CIDR range
    - ALB provides single entry point for external traffic
    - Sessions managed via Valkey with secure cookie configuration
    - Multi-AZ deployment for high availability and fault tolerance
    - Deletion protection enabled for critical resources
    - HTTPS enforced for all traffic (HTTP redirects to HTTPS)
"""

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
        vpc=vpc_stack.vpc_construct.vpc,
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
        vpc=vpc_stack.vpc_construct.vpc,
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

    # Get Synapse OAuth credentials (REQUIRED for Auth Service)
    # Credentials are securely stored in AWS Secrets Manager by AuthServiceStack
    synapse_client_id = os.getenv("SYNAPSE_CLIENT_ID", "")
    synapse_client_secret = os.getenv("SYNAPSE_CLIENT_SECRET", "")

    # Validate credentials are provided (fail fast at deployment time)
    if not synapse_client_id or not synapse_client_secret:
        raise ValueError(
            "SYNAPSE_CLIENT_ID and SYNAPSE_CLIENT_SECRET environment variables "
            "are required for deployment. These OAuth credentials are securely "
            "stored in AWS Secrets Manager and injected into the Auth Service "
            "container at runtime. Never exposed in CloudFormation or logs."
        )

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
        app_version=app_version,
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
        app_version=app_version,
        ui_base_url=(
            f"{'https' if use_https else 'http'}://"
            f"{fqdn if fqdn else alb_stack.alb_construct.alb.load_balancer_dns_name}"
        ),
        synapse_client_id=synapse_client_id,
        synapse_client_secret=synapse_client_secret,
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
        app_version=app_version,
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
        f"{stack_prefix}-bucket",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        description=f"S3 buckets for BixArena {environment} environment",
    )

    # Create bastion stack for database access
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
