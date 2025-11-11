"""API service stack for BixArena backend API."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from aws_cdk import aws_rds as rds
from aws_cdk import aws_secretsmanager as sm
from constructs import Construct

from bixarena_infra_cdk.shared.constructs.fargate_service_construct import (
    BixArenaFargateService,
)
from bixarena_infra_cdk.shared.utils import load_container_image


class ApiServiceStack(cdk.Stack):
    """Stack for the BixArena API Fargate service."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        stack_prefix: str,
        environment: str,
        vpc: ec2.IVpc,
        cluster: ecs.ICluster,
        database: rds.IDatabaseInstance,
        database_secret_arn: str,
        valkey_endpoint: str,
        valkey_port: str,
        api_version: str = "edge",
        ui_base_url: str = "http://localhost:8100",
        **kwargs,
    ) -> None:
        """
        Create API service stack.

        Args:
            scope: CDK app scope
            construct_id: Stack identifier
            stack_prefix: Prefix for stack name
            environment: Environment name (dev, stage, prod)
            vpc: VPC where the service will run
            cluster: ECS cluster
            database: RDS PostgreSQL database instance
            database_secret_arn: ARN of the database credentials secret
            valkey_endpoint: Valkey cluster endpoint
            valkey_port: Valkey cluster port
            api_version: API version (Docker image tag)
            ui_base_url: Base URL for the UI (for CORS configuration)
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Import database secret for secure credential injection
        db_secret = sm.Secret.from_secret_complete_arn(
            self, "DatabaseSecret", database_secret_arn
        )

        # Container image - support local or remote images
        image = load_container_image(
            self,
            "ApiServiceImage",
            "bixarena-api",
            f"ghcr.io/sage-bionetworks/bixarena-api:{api_version}",
        )

        # Environment variables for the API container
        # Only override values that depend on CDK infrastructure
        # Most configuration is already defined in application.yml
        container_env = {
            # Set active profile (loads application-{profile}.yml)
            "SPRING_PROFILES_ACTIVE": environment,
            # PostgreSQL connection (override defaults with actual endpoints)
            "SPRING_DATASOURCE_URL": (
                f"jdbc:postgresql://{database.db_instance_endpoint_address}:"
                f"{database.db_instance_endpoint_port}/bixarena"
            ),
            # Valkey/Redis connection (override defaults with actual endpoints)
            "SPRING_DATA_REDIS_HOST": valkey_endpoint,
            "SPRING_DATA_REDIS_PORT": valkey_port,
            # Auth service URL: override to use ECS service discovery
            "AUTH_SERVICE_BASE_URL": (
                f"http://bixarena-auth-service.{cluster.cluster_name}.local:8115"
            ),
            # CORS configuration: only allow requests from deployment URL
            # This overrides the default list in application.yml
            "APP_CORS_ALLOWED_ORIGINS": ui_base_url,
        }

        # Secrets from AWS Secrets Manager (injected securely at runtime)
        container_secrets = {
            "SPRING_DATASOURCE_USERNAME": ecs.Secret.from_secrets_manager(
                db_secret, field="username"
            ),
            "SPRING_DATASOURCE_PASSWORD": ecs.Secret.from_secrets_manager(
                db_secret, field="password"
            ),
        }

        # Create Fargate service for the API
        service_construct = BixArenaFargateService(
            self,
            "ApiService",
            vpc=vpc,
            cluster=cluster,
            service_name="bixarena-api",
            container_image=image,
            container_port=8112,
            cpu=1024,  # 1 vCPU - aligned with gateway
            memory_limit_mib=2048,  # 2 GB - Spring Boot apps need more memory
            environment=container_env,
            secrets=container_secrets,  # Secure credential injection
            desired_count=1,
            target_group=None,  # Not directly exposed to ALB, accessed via API Gateway
        )

        # Note: Security group rules are configured in the app.py
        # to avoid cyclic dependencies

        # Export service for reference
        self.service = service_construct.service
        self.security_group = service_construct.security_group

        # CloudFormation outputs
        cdk.CfnOutput(
            self,
            "ServiceName",
            value=self.service.service_name,
            description="API service name",
        )

        cdk.CfnOutput(
            self,
            "ServiceArn",
            value=self.service.service_arn,
            description="API service ARN",
        )
