"""Image service stack for OpenChallenges backend API."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from constructs import Construct

from openchallenges_infra_cdk.shared.constructs.fargate_service_construct import (
    OpenchalllengesFargateService,
)
from openchallenges_infra_cdk.shared.image_loader import load_container_image


class ImageServiceStack(cdk.Stack):
    """Stack for the OpenChallenges Image Service Fargate service."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        stack_prefix: str,
        environment: str,
        vpc: ec2.IVpc,
        cluster: ecs.ICluster,
        developer_name: str | None = None,
        app_version: str = "edge",
        thumbor_host: str = "http://localhost:8000/img/",
        thumbor_security_key: str = "changeme",
        **kwargs,
    ) -> None:
        """
        Create Image service stack.

        Args:
            scope: CDK app scope
            construct_id: Stack identifier
            stack_prefix: Prefix for stack name
            environment: Environment name (dev, stage, prod)
            vpc: VPC where the service will run
            cluster: ECS cluster
            developer_name: Developer name for dev environment (optional)
            app_version: Application version (Docker image tag)
            thumbor_host: Thumbor image processing service host URL
            thumbor_security_key: Thumbor security key for signed URLs
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Container image - support local or remote images
        image = load_container_image(
            self,
            "ImageServiceImage",
            "openchallenges-image-service",
            f"ghcr.io/sage-bionetworks/openchallenges-image-service:{app_version}",
        )

        # Environment variables for the Image Service container
        # Only override values that depend on CDK infrastructure
        # Most configuration is already defined in application.yml
        container_env = {
            # Set active profile (loads application-{profile}.yml)
            "SPRING_PROFILES_ACTIVE": environment,
            # Thumbor configuration for image processing
            "APP_THUMBOR_HOST": thumbor_host,
            "APP_THUMBOR_SECURITY_KEY": thumbor_security_key,
            # Disable placeholder images (use actual images from S3)
            "APP_PLACEHOLDER_ENABLED": "false",
        }

        # Create Fargate service for the Image Service
        # Note: Image service is stateless and doesn't need database access
        service_construct = OpenchalllengesFargateService(
            self,
            "ImageService",
            vpc=vpc,
            cluster=cluster,
            service_name="openchallenges-image-service",
            container_image=image,
            container_port=8086,
            cpu=512,  # 0.5 vCPU - moderate for Spring Boot
            memory_limit_mib=1024,  # 1 GB - Spring Boot needs memory for JVM
            environment=container_env,
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
            description="Image service name",
        )

        cdk.CfnOutput(
            self,
            "ServiceArn",
            value=self.service.service_arn,
            description="Image service ARN",
        )

        cdk.CfnOutput(
            self,
            "ServiceUrl",
            value=f"http://openchallenges-image-service.{cluster.cluster_name}.local:8086",
            description="Image service internal URL (via service discovery)",
        )
