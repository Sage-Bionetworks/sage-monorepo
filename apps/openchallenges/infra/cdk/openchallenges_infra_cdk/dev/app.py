#!/usr/bin/env python3
"""CDK app for dev environment."""

import os

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2

from openchallenges_infra_cdk.shared.config import (
    get_developer_name,
    get_environment,
    get_stack_prefix,
)
from openchallenges_infra_cdk.shared.stacks.alb_stack import AlbStack
from openchallenges_infra_cdk.shared.stacks.api_gateway_stack import ApiGatewayStack
from openchallenges_infra_cdk.shared.stacks.auth_service_stack import AuthServiceStack
from openchallenges_infra_cdk.shared.stacks.bastion_stack import BastionStack
from openchallenges_infra_cdk.shared.stacks.bucket_stack import BucketStack
from openchallenges_infra_cdk.shared.stacks.challenge_service_stack import (
    ChallengeServiceStack,
)
from openchallenges_infra_cdk.shared.stacks.database_stack import DatabaseStack
from openchallenges_infra_cdk.shared.stacks.ecs_cluster_stack import EcsClusterStack
from openchallenges_infra_cdk.shared.stacks.image_service_stack import (
    ImageServiceStack,
)
from openchallenges_infra_cdk.shared.stacks.opensearch_stack import OpenSearchStack
from openchallenges_infra_cdk.shared.stacks.organization_service_stack import (
    OrganizationServiceStack,
)
from openchallenges_infra_cdk.shared.stacks.thumbor_stack import ThumborStack
from openchallenges_infra_cdk.shared.stacks.vpc_stack import VpcStack
from openchallenges_infra_cdk.shared.stacks.web_stack import WebStack


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
    # OpenSearch admin username - defaults to "opensearch-admin"
    opensearch_admin_username = os.getenv(
        "OPENSEARCH_ADMIN_USERNAME", "opensearch-admin"
    )
    # Data updated date - displayed in web app footer (REQUIRED)
    data_updated_on = os.getenv("DATA_UPDATED_ON", "")

    # OAuth credentials for auth service (required for deployment)
    # These are immediately stored in AWS Secrets Manager and never exposed
    google_client_id = os.getenv("GOOGLE_CLIENT_ID", "")
    google_client_secret = os.getenv("GOOGLE_CLIENT_SECRET", "")
    synapse_client_id = os.getenv("SYNAPSE_CLIENT_ID", "")
    synapse_client_secret = os.getenv("SYNAPSE_CLIENT_SECRET", "")

    # Validate required configuration (fail fast at deployment time)
    if not data_updated_on:
        raise ValueError(
            "DATA_UPDATED_ON environment variable is required. "
            "Please set it to the date when OpenChallenges data was last updated "
            "(format: YYYY-MM-DD, e.g., 2025-10-19)."
        )

    # Add common tags
    cdk.Tags.of(app).add("Environment", environment)
    cdk.Tags.of(app).add("Product", "OpenChallenges")
    cdk.Tags.of(app).add("ManagedBy", "CDK")

    # Add developer tag for dev environment
    if developer_name:
        cdk.Tags.of(app).add("Developer", developer_name)

    # Create VPC stack
    # Dev uses 1 NAT gateway for cost optimization (~$32/month vs ~$65/month)
    vpc_stack = VpcStack(
        app,
        f"{stack_prefix}-vpc",
        stack_prefix=stack_prefix,
        environment=environment,
        vpc_cidr=vpc_cidr,
        nat_gateways=1,  # Single NAT for cost savings in dev
        description=f"VPC for OpenChallenges {environment} environment",
    )

    # Create database stack (depends on VPC)
    # Dev uses cost-optimized single-AZ deployment
    database_stack = DatabaseStack(
        app,
        f"{stack_prefix}-database",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc,
        description=f"PostgreSQL database for OpenChallenges {environment} environment",
    )
    database_stack.add_dependency(vpc_stack)

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

    # Create ECS cluster stack (depends on VPC)
    ecs_cluster_stack = EcsClusterStack(
        app,
        f"{stack_prefix}-ecs-cluster",
        stack_prefix=stack_prefix,
        environment=environment,
        vpc=vpc_stack.vpc,
        description=f"ECS cluster for OpenChallenges {environment} environment",
    )
    ecs_cluster_stack.add_dependency(vpc_stack)

    # Create bastion stack for database access (depends on VPC and ECS cluster)
    bastion_stack = BastionStack(
        app,
        f"{stack_prefix}-bastion",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc,
        cluster=ecs_cluster_stack.cluster,
        description=(
            f"Bastion service for database access in {environment} environment"
        ),
    )
    bastion_stack.add_dependency(ecs_cluster_stack)
    bastion_stack.add_dependency(vpc_stack)

    # Create OpenSearch domain stack (depends on VPC)
    # AWS-managed OpenSearch for search functionality
    # Credentials secret with auto-generated password is created within the stack
    opensearch_stack = OpenSearchStack(
        app,
        f"{stack_prefix}-opensearch",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc,
        admin_username=opensearch_admin_username,
        description=f"OpenSearch domain for OpenChallenges {environment} environment",
    )
    opensearch_stack.add_dependency(vpc_stack)

    # Create Challenge service stack (depends on database, ECS cluster)
    # Database secret is guaranteed to exist since we use from_generated_secret()
    database_secret = database_stack.database_construct.database_secret
    if database_secret is None:
        raise ValueError(
            "Database secret must be created. "
            "Verify PostgreSQL database initialization completed successfully."
        )
    challenge_service_stack = ChallengeServiceStack(
        app,
        f"{stack_prefix}-challenge-service",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc,
        cluster=ecs_cluster_stack.cluster,
        database=database_stack.database_construct.database,
        database_secret_arn=database_secret.secret_arn,
        opensearch_endpoint=opensearch_stack.domain_endpoint,
        app_version=app_version,
        description=f"Challenge service for OpenChallenges {environment} environment",
    )
    # Note: Dependencies are automatic via CloudFormation references
    # (database endpoint, etc.). Don't add manual add_dependency() calls
    # to avoid cyclic dependencies with security group rules

    # Create Organization service stack (depends on database, ECS cluster)
    organization_service_stack = OrganizationServiceStack(
        app,
        f"{stack_prefix}-organization-service",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc,
        cluster=ecs_cluster_stack.cluster,
        database=database_stack.database_construct.database,
        database_secret_arn=database_secret.secret_arn,
        opensearch_endpoint=opensearch_stack.domain_endpoint,
        app_version=app_version,
        description=(
            f"Organization service for OpenChallenges {environment} environment"
        ),
    )

    # Configure OpenSearch security group to allow access from services
    # Create ingress rules in service stack scope to avoid cyclic dependencies
    ec2.CfnSecurityGroupIngress(
        challenge_service_stack,
        "OpenSearchIngress",
        ip_protocol="tcp",
        from_port=443,
        to_port=443,
        source_security_group_id=challenge_service_stack.security_group.security_group_id,
        group_id=opensearch_stack.opensearch_security_group.security_group_id,
        description="Allow Challenge service to access OpenSearch",
    )
    ec2.CfnSecurityGroupIngress(
        organization_service_stack,
        "OpenSearchIngress",
        ip_protocol="tcp",
        from_port=443,
        to_port=443,
        source_security_group_id=organization_service_stack.security_group.security_group_id,
        group_id=opensearch_stack.opensearch_security_group.security_group_id,
        description="Allow Organization service to access OpenSearch",
    )

    # Create Image service stack (stateless, no database)
    # Thumbor is exposed via ALB at /img/ path for public access
    protocol = "https" if (certificate_arn and certificate_arn.strip()) else "http"
    thumbor_base_url = fqdn if fqdn else alb_stack.alb.load_balancer_dns_name
    thumbor_public_url = f"{protocol}://{thumbor_base_url}/img/"

    # Image service placeholder mode - when enabled, returns placeholder image URLs
    # instead of real images from S3 (useful during initial deployment)
    placeholder_enabled = os.getenv("IMAGE_SERVICE_PLACEHOLDER_ENABLED", "true")

    image_service_stack = ImageServiceStack(
        app,
        f"{stack_prefix}-image-service",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc,
        cluster=ecs_cluster_stack.cluster,
        app_version=app_version,
        # Thumbor configuration - use public ALB URL for image URLs
        thumbor_host=thumbor_public_url,
        thumbor_security_key="changeme",
        placeholder_enabled=placeholder_enabled,
        description=f"Image service for OpenChallenges {environment} environment",
    )

    # Create Auth service stack (depends on database, ECS cluster)
    auth_service_stack = AuthServiceStack(
        app,
        f"{stack_prefix}-auth-service",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc,
        cluster=ecs_cluster_stack.cluster,
        database=database_stack.database_construct.database,
        database_secret_arn=database_secret.secret_arn,
        google_client_id=google_client_id,
        google_client_secret=google_client_secret,
        synapse_client_id=synapse_client_id,
        synapse_client_secret=synapse_client_secret,
        app_version=app_version,
        description=f"Auth service for OpenChallenges {environment} environment",
    )

    # Create API Gateway stack (depends on ALB and ECS cluster)
    # API Gateway routes traffic to Auth, Challenge, Organization, and Image services
    # Note: Dependencies on service stacks are automatic via CloudFormation references
    api_gateway_stack = ApiGatewayStack(
        app,
        f"{stack_prefix}-api-gateway",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc,
        cluster=ecs_cluster_stack.cluster,
        target_group=alb_stack.api_gateway_target_group,
        app_version=app_version,
        description=(f"API Gateway for OpenChallenges {environment} environment"),
    )
    api_gateway_stack.add_dependency(alb_stack)
    api_gateway_stack.add_dependency(ecs_cluster_stack)

    # Configure security group rules for API Gateway
    # Use CfnSecurityGroupIngress to avoid cyclic dependencies
    # Allow API Gateway to communicate with backend services
    ec2.CfnSecurityGroupIngress(
        api_gateway_stack,
        "AuthServiceIngress",
        ip_protocol="tcp",
        from_port=8087,
        to_port=8087,
        source_security_group_id=api_gateway_stack.security_group.security_group_id,
        group_id=auth_service_stack.security_group.security_group_id,
        description="Allow API Gateway to access Auth service",
    )
    ec2.CfnSecurityGroupIngress(
        api_gateway_stack,
        "ChallengeServiceIngress",
        ip_protocol="tcp",
        from_port=8085,
        to_port=8085,
        source_security_group_id=api_gateway_stack.security_group.security_group_id,
        group_id=challenge_service_stack.security_group.security_group_id,
        description="Allow API Gateway to access Challenge service",
    )
    ec2.CfnSecurityGroupIngress(
        api_gateway_stack,
        "OrganizationServiceIngress",
        ip_protocol="tcp",
        from_port=8084,
        to_port=8084,
        source_security_group_id=api_gateway_stack.security_group.security_group_id,
        group_id=organization_service_stack.security_group.security_group_id,
        description="Allow API Gateway to access Organization service",
    )
    ec2.CfnSecurityGroupIngress(
        api_gateway_stack,
        "ImageServiceIngress",
        ip_protocol="tcp",
        from_port=8086,
        to_port=8086,
        source_security_group_id=api_gateway_stack.security_group.security_group_id,
        group_id=image_service_stack.security_group.security_group_id,
        description="Allow API Gateway to access Image service",
    )
    # Allow API Gateway to receive traffic from ALB
    ec2.CfnSecurityGroupIngress(
        api_gateway_stack,
        "AlbIngress",
        ip_protocol="tcp",
        from_port=8082,
        to_port=8082,
        source_security_group_id=alb_stack.security_group.security_group_id,
        group_id=api_gateway_stack.security_group.security_group_id,
        description="Allow ALB to forward traffic to API Gateway",
    )

    # Create web stack (Angular SSR app) - depends on ECS cluster, ALB, and API Gateway
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
        target_group=alb_stack.app_target_group,
        app_version=app_version,
        alb_dns_name=alb_stack.alb.load_balancer_dns_name,
        fqdn=fqdn if fqdn else None,
        use_https=use_https,
        data_updated_on=data_updated_on,
        description=f"Web client for OpenChallenges {environment} environment",
    )
    web_stack.add_dependency(ecs_cluster_stack)
    web_stack.add_dependency(alb_stack)
    web_stack.add_dependency(api_gateway_stack)

    # Create bucket stack
    bucket_stack = BucketStack(
        app,
        f"{stack_prefix}-bucket",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        description=f"S3 buckets for OpenChallenges {environment} environment",
    )

    # Create Thumbor stack (depends on bucket, ECS cluster, and ALB)
    thumbor_stack = ThumborStack(
        app,
        f"{stack_prefix}-thumbor",
        stack_prefix=stack_prefix,
        environment=environment,
        developer_name=developer_name,
        vpc=vpc_stack.vpc,
        cluster=ecs_cluster_stack.cluster,
        image_bucket=bucket_stack.image_bucket.bucket,
        target_group=alb_stack.thumbor_target_group,
        app_version=app_version,
        security_key="changeme",  # TODO: Use secrets manager in production
        description=(
            f"Thumbor image processing service for OpenChallenges "
            f"{environment} environment"
        ),
    )
    thumbor_stack.add_dependency(bucket_stack)
    thumbor_stack.add_dependency(ecs_cluster_stack)
    thumbor_stack.add_dependency(alb_stack)

    app.synth()


if __name__ == "__main__":
    main()
