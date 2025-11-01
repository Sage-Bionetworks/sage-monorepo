"""Unit tests for bucket stack."""

import os
from unittest.mock import patch

import aws_cdk as cdk
from aws_cdk.assertions import Template

from bixarena_infra_cdk.shared.stacks.bucket_stack import BucketStack


class TestBucketStack:
    """Tests for BucketStack."""

    def test_bucket_stack_creates_bucket(self):
        """Test that bucket stack creates S3 bucket."""
        with patch.dict(os.environ, {"ENV": "dev", "DEVELOPER_NAME": "jsmith"}):
            app = cdk.App()
            stack = BucketStack(
                app,
                "test-stack",
                stack_prefix="bixarena-dev-jsmith",
                environment="dev",
                developer_name="jsmith",
            )
            template = Template.from_stack(stack)

            # Assert S3 bucket is created
            template.resource_count_is("AWS::S3::Bucket", 1)

    def test_bucket_stack_outputs(self):
        """Test that bucket stack exports outputs."""
        with patch.dict(os.environ, {"ENV": "dev", "DEVELOPER_NAME": "jsmith"}):
            app = cdk.App()
            stack = BucketStack(
                app,
                "test-stack",
                stack_prefix="bixarena-dev-jsmith",
                environment="dev",
                developer_name="jsmith",
            )
            template = Template.from_stack(stack)

            # Assert outputs are defined
            template.has_output(
                "ImageBucketName",
                {
                    "Description": "Name of the S3 bucket for images",
                    "Export": {"Name": "bixarena-dev-jsmith-image-bucket-name"},
                },
            )

            template.has_output(
                "ImageBucketArn",
                {
                    "Description": "ARN of the S3 bucket for images",
                    "Export": {"Name": "bixarena-dev-jsmith-image-bucket-arn"},
                },
            )

    def test_bucket_encryption(self):
        """Test that bucket has encryption enabled."""
        with patch.dict(os.environ, {"ENV": "dev", "DEVELOPER_NAME": "jsmith"}):
            app = cdk.App()
            stack = BucketStack(
                app,
                "test-stack",
                stack_prefix="bixarena-dev-jsmith",
                environment="dev",
                developer_name="jsmith",
            )
            template = Template.from_stack(stack)

            # Assert bucket has encryption configuration
            template.has_resource_properties(
                "AWS::S3::Bucket",
                {
                    "BucketEncryption": {
                        "ServerSideEncryptionConfiguration": [
                            {
                                "ServerSideEncryptionByDefault": {
                                    "SSEAlgorithm": "AES256"
                                }
                            }
                        ]
                    }
                },
            )

    def test_bucket_public_access_blocked(self):
        """Test that bucket blocks all public access."""
        with patch.dict(os.environ, {"ENV": "dev", "DEVELOPER_NAME": "jsmith"}):
            app = cdk.App()
            stack = BucketStack(
                app,
                "test-stack",
                stack_prefix="bixarena-dev-jsmith",
                environment="dev",
                developer_name="jsmith",
            )
            template = Template.from_stack(stack)

            # Assert public access is blocked
            template.has_resource_properties(
                "AWS::S3::Bucket",
                {
                    "PublicAccessBlockConfiguration": {
                        "BlockPublicAcls": True,
                        "BlockPublicPolicy": True,
                        "IgnorePublicAcls": True,
                        "RestrictPublicBuckets": True,
                    }
                },
            )

    def test_bucket_versioning_disabled(self):
        """Test that bucket has versioning disabled by default."""
        with patch.dict(os.environ, {"ENV": "dev", "DEVELOPER_NAME": "jsmith"}):
            app = cdk.App()
            stack = BucketStack(
                app,
                "test-stack",
                stack_prefix="bixarena-dev-jsmith",
                environment="dev",
                developer_name="jsmith",
            )
            template = Template.from_stack(stack)

            # Assert versioning is not enabled (VersioningConfiguration not present)
            # We can check that the property doesn't exist or is not Enabled
            bucket_template = template.to_json()["Resources"]
            bucket_resources = [
                r for r in bucket_template.values() if r["Type"] == "AWS::S3::Bucket"
            ]
            assert len(bucket_resources) == 1
            bucket_props = bucket_resources[0].get("Properties", {})

            # Either no VersioningConfiguration or Status is not "Enabled"
            versioning = bucket_props.get("VersioningConfiguration")
            if versioning:
                assert versioning.get("Status") != "Enabled"
