"""S3 Bucket construct for OpenChallenges infrastructure."""

import aws_cdk as cdk
from aws_cdk import aws_s3 as s3
from constructs import Construct


class OpenchallengesBucket(Construct):
    """Reusable S3 bucket construct for OpenChallenges."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        bucket_name: str,
        versioned: bool = False,
        lifecycle_rules: list[s3.LifecycleRule] | None = None,
        removal_policy: cdk.RemovalPolicy = cdk.RemovalPolicy.RETAIN,
        auto_delete_objects: bool = False,
        **kwargs,
    ) -> None:
        """
        Create an S3 bucket with OpenChallenges standard configuration.

        Args:
            scope: CDK construct scope
            construct_id: Construct identifier
            bucket_name: Base name for the bucket (suffix will be added for uniqueness)
            versioned: Enable versioning (default: False)
            lifecycle_rules: Optional lifecycle rules
            removal_policy: Policy for bucket removal (default: RETAIN)
            auto_delete_objects: Auto-delete objects when stack is deleted
                (default: False)
            **kwargs: Additional arguments passed to parent Construct
        """
        super().__init__(scope, construct_id, **kwargs)

        # Create bucket with best practice settings
        self.bucket = s3.Bucket(
            self,
            "Bucket",
            bucket_name=bucket_name,
            # Security: Enable encryption at rest
            encryption=s3.BucketEncryption.S3_MANAGED,
            # Security: Block all public access
            block_public_access=s3.BlockPublicAccess.BLOCK_ALL,
            # Security: Enforce bucket owner for all objects
            object_ownership=s3.ObjectOwnership.BUCKET_OWNER_ENFORCED,
            # Versioning
            versioned=versioned,
            # Lifecycle rules
            lifecycle_rules=lifecycle_rules or [],
            # Auto-delete objects when stack is deleted
            auto_delete_objects=auto_delete_objects,
            # Removal policy
            removal_policy=removal_policy,
            # Enable event bridge notifications
            event_bridge_enabled=True,
        )

    @property
    def bucket_name(self) -> str:
        """Get the bucket name."""
        return self.bucket.bucket_name

    @property
    def bucket_arn(self) -> str:
        """Get the bucket ARN."""
        return self.bucket.bucket_arn
