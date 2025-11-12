"""S3 buckets stack for BixArena infrastructure."""

import aws_cdk as cdk
from constructs import Construct

from bixarena_infra_cdk.shared.constructs.bucket_construct import BixArenaBucket


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
        # CDK auto-generates name using stack ID as prefix:
        # - Dev: bixarena-dev-{developer}-imagebucket{hash}-{random}
        # - Stage: bixarena-stage-imagebucket{hash}-{random}
        # - Prod: bixarena-prod-imagebucket{hash}-{random}
        self.image_bucket = BixArenaBucket(
            self,
            "ImageBucket",
            bucket_name=None,  # Let CDK auto-generate with stack prefix
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
