"""Integration tests for CDK stack synthesis."""

import aws_cdk as cdk
from aws_cdk.assertions import Template

from openchallenges_infra_cdk.shared.config import get_stack_prefix


class TestSynthesis:
    """Tests for CDK stack synthesis."""

    def test_dev_synth(self, monkeypatch):
        """Test dev environment synthesis."""
        # Set environment variables
        monkeypatch.setenv("ENV", "dev")
        monkeypatch.setenv("DEVELOPER_NAME", "testuser")

        # Create app and synthesize
        app = cdk.App()
        monkeypatch.setattr("aws_cdk.App", lambda: app)

        # Import and create stack manually
        from openchallenges_infra_cdk.shared.config import (
            get_developer_name,
            get_environment,
        )
        from openchallenges_infra_cdk.shared.stacks.bucket_stack import BucketStack

        environment = get_environment()
        developer_name = get_developer_name()
        stack_prefix = get_stack_prefix()

        cdk.Tags.of(app).add("Environment", environment)
        cdk.Tags.of(app).add("Product", "OpenChallenges")
        cdk.Tags.of(app).add("ManagedBy", "CDK")
        if developer_name:
            cdk.Tags.of(app).add("Developer", developer_name)

        stack = BucketStack(
            app,
            stack_prefix,  # Use stack_prefix directly as stack ID
            stack_prefix=stack_prefix,
            environment=environment,
            developer_name=developer_name,
            description=f"S3 buckets for OpenChallenges {environment} environment",
        )

        # Get template
        template = Template.from_stack(stack)

        # Verify stack has S3 bucket
        template.resource_count_is("AWS::S3::Bucket", 1)

        # All environments use auto-generated bucket names with stack prefix
        # Dev: openchallenges-dev-testuser-imagebucket{hash}-{random}
        # This prevents naming conflicts on stack updates
        template.has_resource_properties(
            "AWS::S3::Bucket",
            {
                "BucketEncryption": {
                    "ServerSideEncryptionConfiguration": [
                        {"ServerSideEncryptionByDefault": {"SSEAlgorithm": "AES256"}}
                    ]
                },
                "PublicAccessBlockConfiguration": {
                    "BlockPublicAcls": True,
                    "BlockPublicPolicy": True,
                    "IgnorePublicAcls": True,
                    "RestrictPublicBuckets": True,
                },
            },
        )

    def test_stage_synth(self, monkeypatch):
        """Test stage environment synthesis."""
        # Set environment variables
        monkeypatch.setenv("ENV", "stage")

        # Create app and synthesize
        app = cdk.App()

        from openchallenges_infra_cdk.shared.config import (
            get_developer_name,
            get_environment,
        )
        from openchallenges_infra_cdk.shared.stacks.bucket_stack import BucketStack

        environment = get_environment()
        developer_name = get_developer_name()
        stack_prefix = get_stack_prefix()

        cdk.Tags.of(app).add("Environment", environment)
        cdk.Tags.of(app).add("Product", "OpenChallenges")
        cdk.Tags.of(app).add("ManagedBy", "CDK")

        stack = BucketStack(
            app,
            stack_prefix,  # Use stack_prefix directly as stack ID
            stack_prefix=stack_prefix,
            environment=environment,
            developer_name=developer_name,
            description=f"S3 buckets for OpenChallenges {environment} environment",
        )

        # Get template
        template = Template.from_stack(stack)

        # Verify stack has S3 bucket
        template.resource_count_is("AWS::S3::Bucket", 1)

        # Stage uses auto-generated bucket names (no explicit BucketName property)
        # This prevents naming conflicts on stack updates
        # Verify other bucket properties
        template.has_resource_properties(
            "AWS::S3::Bucket",
            {
                "BucketEncryption": {
                    "ServerSideEncryptionConfiguration": [
                        {"ServerSideEncryptionByDefault": {"SSEAlgorithm": "AES256"}}
                    ]
                },
                "PublicAccessBlockConfiguration": {
                    "BlockPublicAcls": True,
                    "BlockPublicPolicy": True,
                    "IgnorePublicAcls": True,
                    "RestrictPublicBuckets": True,
                },
            },
        )

    def test_prod_synth(self, monkeypatch):
        """Test prod environment synthesis."""
        # Set environment variables
        monkeypatch.setenv("ENV", "prod")

        # Create app and synthesize
        app = cdk.App()

        from openchallenges_infra_cdk.shared.config import (
            get_developer_name,
            get_environment,
        )
        from openchallenges_infra_cdk.shared.stacks.bucket_stack import BucketStack

        environment = get_environment()
        developer_name = get_developer_name()
        stack_prefix = get_stack_prefix()

        cdk.Tags.of(app).add("Environment", environment)
        cdk.Tags.of(app).add("Product", "OpenChallenges")
        cdk.Tags.of(app).add("ManagedBy", "CDK")

        stack = BucketStack(
            app,
            stack_prefix,  # Use stack_prefix directly as stack ID
            stack_prefix=stack_prefix,
            environment=environment,
            developer_name=developer_name,
            description=f"S3 buckets for OpenChallenges {environment} environment",
        )

        # Get template
        template = Template.from_stack(stack)

        # Verify stack has S3 bucket
        template.resource_count_is("AWS::S3::Bucket", 1)

        # Prod uses auto-generated bucket names (no explicit BucketName property)
        # This prevents naming conflicts on stack updates
        # Verify other bucket properties
        template.has_resource_properties(
            "AWS::S3::Bucket",
            {
                "BucketEncryption": {
                    "ServerSideEncryptionConfiguration": [
                        {"ServerSideEncryptionByDefault": {"SSEAlgorithm": "AES256"}}
                    ]
                },
                "PublicAccessBlockConfiguration": {
                    "BlockPublicAcls": True,
                    "BlockPublicPolicy": True,
                    "IgnorePublicAcls": True,
                    "RestrictPublicBuckets": True,
                },
            },
        )
