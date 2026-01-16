"""Organization service stack for OpenChallenges backend API."""

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


class OrganizationServiceStack(cdk.Stack):
    """Stack for the OpenChallenges Organization Service Fargate service."""

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
        developer_name: str | None = None,
        app_version: str = "edge",
        **kwargs,
    ) -> None:
        """
        Create Organization service stack.

        Args:
            scope: CDK app scope
            construct_id: Stack identifier
            stack_prefix: Prefix for stack name
            environment: Environment name (dev, stage, prod)
            vpc: VPC where the service will run
            cluster: ECS cluster
            database: RDS PostgreSQL database instance
            database_secret_arn: ARN of the database credentials secret
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
            "OrganizationServiceImage",
            "openchallenges-organization-service",
            f"ghcr.io/sage-bionetworks/openchallenges-organization-service:{app_version}",
        )

        # Environment variables for the Organization Service container
        # Only override values that depend on CDK infrastructure
        # Most configuration is already defined in application.yml
        container_env = {
            # Set active profile (loads application-{profile}.yml)
            "SPRING_PROFILES_ACTIVE": environment,
            # PostgreSQL connection: shared database with dedicated schema
            # Using PostgreSQL schema isolation (like BioArena pattern)
            "SPRING_DATASOURCE_URL": (
                f"jdbc:postgresql://{database.db_instance_endpoint_address}:"
                f"{database.db_instance_endpoint_port}/openchallenges"
            ),
            "SPRING_DATASOURCE_DRIVER_CLASS_NAME": "org.postgresql.Driver",
            # Configure Hibernate to use dedicated schema for data isolation
            "SPRING_JPA_PROPERTIES_HIBERNATE_DEFAULT_SCHEMA": "organization",
            # Configure Flyway to create and use the dedicated schema
            "SPRING_FLYWAY_SCHEMAS": "organization",
            "SPRING_FLYWAY_DEFAULT_SCHEMA": "organization",
            "SPRING_FLYWAY_CREATE_SCHEMAS": "true",
            # Challenge service URL: use ECS service discovery
            "APP_CHALLENGE_SERVICE_BASE_URL": (
                f"http://openchallenges-challenge-service.{cluster.cluster_name}.local:8085"
            ),
            # Auth service URL: use ECS service discovery
            # Note: Will need to be added when auth service is deployed
            # "APP_AUTH_SERVICE_BASE_URL": (
            #     f"http://openchallenges-auth-service.{cluster.cluster_name}.local:8087"
            # ),
            # OpenSearch/Elasticsearch configuration
            # Note: Disabled for now until OpenSearch is deployed
            "SPRING_JPA_PROPERTIES_HIBERNATE_SEARCH_ENABLED": "false",
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

        # Create Fargate service for the Organization Service
        service_construct = OpenchalllengesFargateService(
            self,
            "OrganizationService",
            vpc=vpc,
            cluster=cluster,
            service_name="openchallenges-organization-service",
            container_image=image,
            container_port=8084,
            cpu=512,  # 0.5 vCPU - moderate for Spring Boot
            memory_limit_mib=1024,  # 1 GB - Spring Boot needs memory
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
            description="Organization service name",
        )

        cdk.CfnOutput(
            self,
            "ServiceArn",
            value=self.service.service_arn,
            description="Organization service ARN",
        )

        cdk.CfnOutput(
            self,
            "ServiceUrl",
            value=f"http://openchallenges-organization-service.{cluster.cluster_name}.local:8084",
            description="Organization service internal URL (via service discovery)",
        )
