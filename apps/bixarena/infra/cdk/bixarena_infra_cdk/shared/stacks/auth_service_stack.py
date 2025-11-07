"""Auth service stack for BixArena authentication service."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from aws_cdk import aws_rds as rds
from aws_cdk import aws_secretsmanager as sm
from constructs import Construct

from bixarena_infra_cdk.shared.constructs.fargate_service_construct import (
    BixArenaFargateService,
)


class AuthServiceStack(cdk.Stack):
    """Stack for the BixArena Auth Service Fargate service."""

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
        auth_version: str = "edge",
        ui_base_url: str = "http://localhost:8100",
        synapse_client_id: str = "changeme",
        synapse_client_secret: str = "changeme",
        **kwargs,
    ) -> None:
        """
        Create Auth service stack.

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
            auth_version: Auth service version (Docker image tag)
            ui_base_url: Base URL for the UI (for redirects after auth)
            synapse_client_id: Synapse OAuth client ID
            synapse_client_secret: Synapse OAuth client secret
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Import database secret for secure credential injection
        db_secret = sm.Secret.from_secret_complete_arn(
            self, "DatabaseSecret", database_secret_arn
        )

        # Container image
        image = f"ghcr.io/sage-bionetworks/bixarena-auth-service:{auth_version}"

        # Environment variables for the Auth service container
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
            # Application-specific configuration (infrastructure-dependent)
            "APP_UI_BASE_URL": ui_base_url,
            "APP_AUTH_CLIENT_ID": synapse_client_id,
            "APP_AUTH_CLIENT_SECRET": synapse_client_secret,
            "APP_AUTH_REDIRECT_URI": "https://bixare-alb6f-ug1c5eft2tym-2057665726.us-east-1.elb.amazonaws.com/auth/callback",
            # Use empty/null domain for host-only cookies (works with ALB hostname)
            "APP_SESSION_COOKIE_DOMAIN": "",  # Empty string = null = host-only
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

        # Create Fargate service for the Auth service
        service_construct = BixArenaFargateService(
            self,
            "AuthService",
            vpc=vpc,
            cluster=cluster,
            service_name="bixarena-auth-service",
            container_image=image,
            container_port=8115,
            cpu=512,  # 0.5 vCPU - similar to API service
            memory_limit_mib=1024,  # 1 GB - Spring Boot apps need more memory
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
            description="Auth service name",
        )

        cdk.CfnOutput(
            self,
            "ServiceArn",
            value=self.service.service_arn,
            description="Auth service ARN",
        )
