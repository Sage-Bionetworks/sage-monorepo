"""Auth service stack for OpenChallenges authentication service."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from aws_cdk import aws_rds as rds
from aws_cdk import aws_secretsmanager as sm
from constructs import Construct

from openchallenges_infra_cdk.shared.constructs.fargate_service_construct import (
    OpenchalllengesFargateService,
)
from openchallenges_infra_cdk.shared.image_loader import load_container_image


class AuthServiceStack(cdk.Stack):
    """Stack for the OpenChallenges Auth Service Fargate service."""

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
        google_client_id: str,
        google_client_secret: str,
        synapse_client_id: str,
        synapse_client_secret: str,
        developer_name: str | None = None,
        app_version: str = "edge",
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
            google_client_id: Google OAuth client ID (REQUIRED)
            google_client_secret: Google OAuth client secret (REQUIRED)
            synapse_client_id: Synapse OAuth client ID (REQUIRED)
            synapse_client_secret: Synapse OAuth client secret (REQUIRED)
            developer_name: Developer name for dev environment (optional)
            app_version: Application version (Docker image tag)
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
            "AuthServiceImage",
            "openchallenges-auth-service",
            f"ghcr.io/sage-bionetworks/openchallenges-auth-service:{app_version}",
        )

        # Environment variables for the Auth Service container
        # Only override values that depend on CDK infrastructure
        # Most configuration is already defined in application.yml
        container_env = {
            # Set active profile (loads application-{profile}.yml)
            "SPRING_PROFILES_ACTIVE": environment,
            # PostgreSQL connection: shared database with dedicated schema
            # Using PostgreSQL schema isolation (like other services)
            "SPRING_DATASOURCE_URL": (
                f"jdbc:postgresql://{database.db_instance_endpoint_address}:"
                f"{database.db_instance_endpoint_port}/openchallenges"
            ),
            "SPRING_DATASOURCE_DRIVER_CLASS_NAME": "org.postgresql.Driver",
            # Configure Hibernate to use dedicated schema for data isolation
            "SPRING_JPA_PROPERTIES_HIBERNATE_DEFAULT_SCHEMA": "auth",
            # Configure Flyway to create and use the dedicated schema
            "SPRING_FLYWAY_SCHEMAS": "auth",
            "SPRING_FLYWAY_DEFAULT_SCHEMA": "auth",
            "SPRING_FLYWAY_CREATE_SCHEMAS": "true",
        }

        # Create Google OAuth credentials secret in Secrets Manager
        # This stores credentials securely and never exposes them in CloudFormation
        google_oauth_secret = sm.Secret(
            self,
            "GoogleOAuthSecret",
            secret_name=f"{stack_prefix}-google-oauth",
            description=(
                "Google OAuth client credentials for OpenChallenges Auth Service"
            ),
            secret_object_value={
                "client_id": cdk.SecretValue.unsafe_plain_text(google_client_id),
                "client_secret": cdk.SecretValue.unsafe_plain_text(
                    google_client_secret
                ),
            },
        )

        # Create Synapse OAuth credentials secret in Secrets Manager
        synapse_oauth_secret = sm.Secret(
            self,
            "SynapseOAuthSecret",
            secret_name=f"{stack_prefix}-synapse-oauth",
            description=(
                "Synapse OAuth client credentials for OpenChallenges Auth Service"
            ),
            secret_object_value={
                "client_id": cdk.SecretValue.unsafe_plain_text(synapse_client_id),
                "client_secret": cdk.SecretValue.unsafe_plain_text(
                    synapse_client_secret
                ),
            },
        )

        # Secrets from AWS Secrets Manager (injected securely at runtime)
        # These are never exposed in CloudFormation templates or ECS task definitions
        container_secrets = {
            # Database credentials
            "SPRING_DATASOURCE_USERNAME": ecs.Secret.from_secrets_manager(
                db_secret, field="username"
            ),
            "SPRING_DATASOURCE_PASSWORD": ecs.Secret.from_secrets_manager(
                db_secret, field="password"
            ),
            # Google OAuth credentials
            "APP_OAUTH2_GOOGLE_CLIENT_ID": ecs.Secret.from_secrets_manager(
                google_oauth_secret, field="client_id"
            ),
            "APP_OAUTH2_GOOGLE_CLIENT_SECRET": ecs.Secret.from_secrets_manager(
                google_oauth_secret, field="client_secret"
            ),
            # Synapse OAuth credentials
            "APP_OAUTH2_SYNAPSE_CLIENT_ID": ecs.Secret.from_secrets_manager(
                synapse_oauth_secret, field="client_id"
            ),
            "APP_OAUTH2_SYNAPSE_CLIENT_SECRET": ecs.Secret.from_secrets_manager(
                synapse_oauth_secret, field="client_secret"
            ),
        }

        # Create Fargate service for the Auth Service
        service_construct = OpenchalllengesFargateService(
            self,
            "AuthService",
            vpc=vpc,
            cluster=cluster,
            service_name="openchallenges-auth-service",
            container_image=image,
            container_port=8087,
            cpu=1024,  # 1 vCPU - Spring Boot + OAuth
            memory_limit_mib=2048,  # 2 GB - Spring Boot needs memory
            environment=container_env,
            secrets=container_secrets,  # Secure credential injection
            desired_count=1,
            target_group=None,  # Not directly exposed to ALB initially
        )

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

        cdk.CfnOutput(
            self,
            "ServiceUrl",
            value=(
                f"http://openchallenges-auth-service.{cluster.cluster_name}.local:8087"
            ),
            description="Auth service internal URL (via service discovery)",
        )
