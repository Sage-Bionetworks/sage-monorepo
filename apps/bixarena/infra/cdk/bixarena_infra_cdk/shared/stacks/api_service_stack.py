"""API service stack for BixArena backend API."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from aws_cdk import aws_rds as rds
from constructs import Construct

from bixarena_infra_cdk.shared.constructs.fargate_service_construct import (
    BixArenaFargateService,
)


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
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Import database secret
        database_secret = cdk.SecretValue.secrets_manager(database_secret_arn)

        # Container image
        image = f"ghcr.io/sage-bionetworks/bixarena-api:{api_version}"

        # Environment variables for the API container
        # Based on application.yml configuration
        container_env = {
            "SPRING_PROFILES_ACTIVE": environment,
            "SPRING_APPLICATION_NAME": "bixarena-api",
            "SERVER_PORT": "8112",
            # PostgreSQL configuration
            "SPRING_DATASOURCE_URL": (
                f"jdbc:postgresql://{database.db_instance_endpoint_address}:"
                f"{database.db_instance_endpoint_port}/bixarena"
            ),
            # Will use secrets manager
            "SPRING_DATASOURCE_USERNAME": database_secret.unsafe_unwrap(),
            "SPRING_DATASOURCE_DRIVER_CLASS_NAME": "org.postgresql.Driver",
            # Valkey/Redis configuration (database 0 for API caching)
            "SPRING_DATA_REDIS_HOST": valkey_endpoint,
            "SPRING_DATA_REDIS_PORT": valkey_port,
            "SPRING_DATA_REDIS_DATABASE": "0",
            "SPRING_DATA_REDIS_TIMEOUT": "2000ms",
            # Flyway configuration
            "SPRING_FLYWAY_ENABLED": "true",
            "SPRING_FLYWAY_SCHEMAS": "api",
            "SPRING_FLYWAY_DEFAULT_SCHEMA": "api",
            # JPA/Hibernate configuration
            "SPRING_JPA_HIBERNATE_DDL_AUTO": "validate",
            "SPRING_JPA_PROPERTIES_HIBERNATE_DEFAULT_SCHEMA": "api",
            # Auth service URL (will be internal ECS service discovery later)
            "AUTH_SERVICE_BASE_URL": f"http://bixarena-auth-service.{cluster.cluster_name}.local:8115",
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
            cpu=512,  # 0.5 vCPU - more than app since this is backend API
            memory_limit_mib=1024,  # 1 GB - Spring Boot apps need more memory
            environment=container_env,
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
