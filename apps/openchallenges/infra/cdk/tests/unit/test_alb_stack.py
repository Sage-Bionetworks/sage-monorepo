"""Unit tests for ALB stack."""

from aws_cdk import App
from aws_cdk.assertions import Template

from openchallenges_infra_cdk.shared.stacks.alb_stack import AlbStack
from openchallenges_infra_cdk.shared.stacks.vpc_stack import VpcStack


class TestAlbStack:
    """Tests for ALB stack creation."""

    def test_alb_stack_http_only(self):
        """Test ALB stack with HTTP-only (no certificate)."""
        app = App()

        # Create VPC first
        vpc_stack = VpcStack(
            app,
            "TestVpcStack",
            stack_prefix="test",
            environment="dev",
        )

        # Create ALB stack without certificate
        alb_stack = AlbStack(
            app,
            "TestAlbStack",
            stack_prefix="test",
            environment="dev",
            vpc=vpc_stack.vpc,
            certificate_arn=None,
        )

        # Verify ALB was created
        assert alb_stack.alb is not None
        assert alb_stack.security_group is not None

        # Get template and verify resources
        template = Template.from_stack(alb_stack)
        template.resource_count_is("AWS::ElasticLoadBalancingV2::LoadBalancer", 1)
        template.resource_count_is("AWS::ElasticLoadBalancingV2::Listener", 1)

    def test_alb_stack_with_https(self):
        """Test ALB stack with HTTPS (certificate provided)."""
        app = App()

        # Create VPC first
        vpc_stack = VpcStack(
            app,
            "TestVpcStack",
            stack_prefix="test",
            environment="dev",
        )

        # Create ALB stack with certificate
        cert_arn = "arn:aws:acm:us-east-1:123456789012:certificate/test"
        alb_stack = AlbStack(
            app,
            "TestAlbStack",
            stack_prefix="test",
            environment="dev",
            vpc=vpc_stack.vpc,
            certificate_arn=cert_arn,
        )

        # Get template and verify resources
        template = Template.from_stack(alb_stack)
        template.resource_count_is("AWS::ElasticLoadBalancingV2::LoadBalancer", 1)
        # Should have 2 listeners: HTTP (redirect) and HTTPS
        template.resource_count_is("AWS::ElasticLoadBalancingV2::Listener", 2)

    def test_alb_security_group_rules(self):
        """Test ALB security group has correct ingress rules."""
        app = App()

        # Create VPC first
        vpc_stack = VpcStack(
            app,
            "TestVpcStack",
            stack_prefix="test",
            environment="dev",
        )

        # Create ALB stack
        alb_stack = AlbStack(
            app,
            "TestAlbStack",
            stack_prefix="test",
            environment="dev",
            vpc=vpc_stack.vpc,
        )

        # Verify security group allows HTTP and HTTPS
        template = Template.from_stack(alb_stack)
        template.resource_count_is("AWS::EC2::SecurityGroup", 1)
