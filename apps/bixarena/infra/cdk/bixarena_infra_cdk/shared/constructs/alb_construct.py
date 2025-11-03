"""ALB construct for BixArena infrastructure."""

from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_elasticloadbalancingv2 as elbv2
from constructs import Construct


class BixArenaAlb(Construct):
    """Reusable Application Load Balancer construct."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        vpc: ec2.IVpc,
        internet_facing: bool = True,
        **kwargs,
    ) -> None:
        """
        Create an Application Load Balancer.

        Args:
            scope: CDK construct scope
            construct_id: Construct identifier
            vpc: VPC to deploy the ALB in
            internet_facing: Whether the ALB is internet-facing (default: True)
            **kwargs: Additional arguments passed to parent Construct
        """
        super().__init__(scope, construct_id, **kwargs)

        # Security group for ALB
        # Allow HTTP and HTTPS from anywhere
        self.security_group = ec2.SecurityGroup(
            self,
            "SecurityGroup",
            vpc=vpc,
            description="Security group for Application Load Balancer",
            allow_all_outbound=True,
        )

        # Allow inbound HTTP (port 80)
        self.security_group.add_ingress_rule(
            peer=ec2.Peer.any_ipv4(),
            connection=ec2.Port.tcp(80),
            description="Allow HTTP traffic from anywhere",
        )

        # Allow inbound HTTPS (port 443)
        self.security_group.add_ingress_rule(
            peer=ec2.Peer.any_ipv4(),
            connection=ec2.Port.tcp(443),
            description="Allow HTTPS traffic from anywhere",
        )

        # Create Application Load Balancer
        self.alb = elbv2.ApplicationLoadBalancer(
            self,
            "Alb",
            vpc=vpc,
            internet_facing=internet_facing,
            security_group=self.security_group,
            # Enable HTTP/2
            http2_enabled=True,
            # Enable deletion protection in production (can be overridden)
            deletion_protection=False,
            # Drop invalid header fields
            drop_invalid_header_fields=True,
        )

        # Export ALB DNS name
        self.load_balancer_dns_name = self.alb.load_balancer_dns_name
        self.load_balancer_arn = self.alb.load_balancer_arn
