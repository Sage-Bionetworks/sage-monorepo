"""S3 buckets stack for OpenChallenges infrastructure."""

import aws_cdk as cdk
from constructs import Construct

from openchallenges_infra_cdk.shared.constructs.bucket_construct import (
    OpenchallengesBucket,
)
from openchallenges_infra_cdk.shared.naming import generate_resource_name


class BucketStack(cdk.Stack):
    """Stack for S3 buckets."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        stack_prefix: str,
        environment: str,
        developer_name: str | None = None,
        **kwargs,
    ) -> None:
        """
        Create S3 buckets stack.

        Args:
            scope: CDK app scope
            construct_id: Stack identifier
            stack_prefix: Prefix for stack name
            environment: Environment name (dev, stage, prod)
            developer_name: Developer name for dev environment
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Generate bucket name
        bucket_base_name = generate_resource_name(
            resource_type="img",
            environment=environment,
            developer_name=developer_name,
        )

        # Determine removal policy based on environment
        # Dev: DESTROY for easier cleanup and re-deployment
        # Stage/Prod: RETAIN for data safety
        removal_policy = (
            cdk.RemovalPolicy.DESTROY
            if environment == "dev"
            else cdk.RemovalPolicy.RETAIN
        )
        auto_delete = environment == "dev"

        # Create image bucket for Thumbor service
        self.image_bucket = OpenchallengesBucket(
            self,
            "ImageBucket",
            bucket_name=bucket_base_name,
            versioned=False,  # Images are immutable, no need for versioning
            removal_policy=removal_policy,
            auto_delete_objects=auto_delete,
        )

        # CloudFormation outputs
        cdk.CfnOutput(
            self,
            "ImageBucketName",
            value=self.image_bucket.bucket_name,
            description="Name of the S3 bucket for images",
            export_name=f"{stack_prefix}-image-bucket-name",
        )

        cdk.CfnOutput(
            self,
            "ImageBucketArn",
            value=self.image_bucket.bucket_arn,
            description="ARN of the S3 bucket for images",
            export_name=f"{stack_prefix}-image-bucket-arn",
        )
