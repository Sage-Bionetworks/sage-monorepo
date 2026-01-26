"""Thumbor image processing service stack for OpenChallenges."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from aws_cdk import aws_elasticloadbalancingv2 as elbv2
from aws_cdk import aws_s3 as s3
from constructs import Construct

from openchallenges_infra_cdk.shared.constructs.fargate_service_construct import (
    OpenchalllengesFargateService,
)


class ThumborStack(cdk.Stack):
    """Stack for the Thumbor image processing Fargate service."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        stack_prefix: str,
        environment: str,
        vpc: ec2.IVpc,
        cluster: ecs.ICluster,
        image_bucket: s3.IBucket,
        target_group: elbv2.IApplicationTargetGroup,
        developer_name: str | None = None,
        app_version: str = "edge",
        security_key: str = "changeme",
        **kwargs,
    ) -> None:
        """
        Create Thumbor service stack.

        Args:
            scope: CDK app scope
            construct_id: Stack identifier
            stack_prefix: Prefix for stack name
            environment: Environment name (dev, stage, prod)
            vpc: VPC where the service will run
            cluster: ECS cluster
            image_bucket: S3 bucket for storing images
            target_group: ALB target group for the service
            developer_name: Developer name for dev environment (optional)
            app_version: Application version (Docker image tag)
            security_key: Security key for signed Thumbor URLs
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Container image
        image = f"ghcr.io/sage-bionetworks/openchallenges-thumbor:{app_version}"

        # Environment variables for Thumbor configuration
        container_env = {
            "LOG_LEVEL": "info",
            "PORT": "8889",
            # AWS S3 loader configuration
            "LOADER": "thumbor_aws.loader",
            "AWS_LOADER_REGION_NAME": self.region,
            "AWS_LOADER_BUCKET_NAME": image_bucket.bucket_name,
            "AWS_LOADER_S3_ENDPOINT_URL": f"http://s3.{self.region}.amazonaws.com",
            "AWS_LOADER_ROOT_PATH": "img",
            # File storage configuration
            "STORAGE": "thumbor.storages.file_storage",
            "FILE_STORAGE_ROOT_PATH": "/data/storage",
            # Result storage configuration
            "RESULT_STORAGE": "thumbor.result_storages.file_storage",
            "RESULT_STORAGE_FILE_STORAGE_ROOT_PATH": "/data/result_storage",
            "RESULT_STORAGE_STORES_UNSAFE": "True",
            "RESULT_STORAGE_EXPIRATION_SECONDS": "2629746",  # ~1 month
            # Security and quality settings
            "SECURITY_KEY": security_key,
            "ALLOW_UNSAFE_URL": "True",
            "QUALITY": "100",
            "MAX_AGE": "86400",  # 1 day
            "AUTO_PNG_TO_JPG": "True",
            "HTTP_LOADER_VALIDATE_CERTS": "False",
        }

        # Create Fargate service for Thumbor
        service_construct = OpenchalllengesFargateService(
            self,
            "ThumborService",
            vpc=vpc,
            cluster=cluster,
            service_name="openchallenges-thumbor",
            container_image=image,
            container_port=8889,
            cpu=256,  # 0.25 vCPU - lightweight for image processing
            memory_limit_mib=512,  # 512 MB - sufficient for Thumbor
            environment=container_env,
            desired_count=1,
            target_group=target_group,  # Exposed via ALB at /img/* path
        )

        # Grant S3 read access to the image bucket
        image_bucket.grant_read(service_construct.service.task_definition.task_role)

        # Export service for reference
        self.service = service_construct.service
        self.security_group = service_construct.security_group

        # CloudFormation outputs
        cdk.CfnOutput(
            self,
            "ServiceName",
            value=self.service.service_name,
            description="Thumbor service name",
        )

        cdk.CfnOutput(
            self,
            "ServiceArn",
            value=self.service.service_arn,
            description="Thumbor service ARN",
        )

        cdk.CfnOutput(
            self,
            "ServiceUrl",
            value=f"http://openchallenges-thumbor.{cluster.cluster_name}.local:8889",
            description="Thumbor service internal URL (via service discovery)",
        )
