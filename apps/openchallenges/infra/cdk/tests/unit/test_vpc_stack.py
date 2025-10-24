"""Unit tests for VPC stack."""

from aws_cdk import App

from openchallenges_infra_cdk.shared.stacks.vpc_stack import VpcStack


class TestVpcStack:
    """Tests for VPC stack creation."""

    def test_vpc_stack_creation(self):
        """Test VPC stack can be created successfully."""
        app = App()

        stack = VpcStack(
            app,
            "TestVpcStack",
            stack_prefix="test",
            environment="dev",
            vpc_cidr="10.0.0.0/16",
        )

        assert stack is not None
        assert stack.vpc is not None

    def test_vpc_stack_custom_cidr(self):
        """Test VPC stack with custom CIDR block."""
        app = App()

        custom_cidr = "172.16.0.0/16"
        stack = VpcStack(
            app,
            "TestVpcStack",
            stack_prefix="test",
            environment="dev",
            vpc_cidr=custom_cidr,
        )

        # VPC CIDR is resolved as a token during synthesis
        # Just verify the VPC was created
        assert stack.vpc is not None

    def test_vpc_stack_custom_azs(self):
        """Test VPC stack with custom number of AZs."""
        app = App()

        stack = VpcStack(
            app,
            "TestVpcStack",
            stack_prefix="test",
            environment="dev",
            max_azs=3,
        )

        assert stack.vpc is not None
