"""VPC construct for BixArena infrastructure."""

from aws_cdk import aws_ec2 as ec2
from constructs import Construct


class OpenchallengesVpc(Construct):
    """Reusable VPC construct with public and private subnets."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        cidr: str = "10.0.0.0/16",
        max_azs: int = 2,
        nat_gateways: int = 1,
        **kwargs,
    ) -> None:
        """
        Create a VPC with public and private subnets.

        Args:
            scope: CDK construct scope
            construct_id: Construct identifier
            cidr: CIDR block for the VPC (default: 10.0.0.0/16)
            max_azs: Maximum number of Availability Zones (default: 2)
            nat_gateways: Number of NAT gateways to create (default: 1)
                Use 1 for cost optimization in dev (cheaper, single point of failure)
                Use 2+ for high availability in prod (more expensive, redundant)
            **kwargs: Additional arguments passed to parent Construct
        """
        super().__init__(scope, construct_id, **kwargs)

        # Create VPC with public, private, and isolated subnets
        # Public subnets: For load balancers and NAT gateways
        # Private subnets: For application workloads (ECS tasks, etc.)
        # Isolated subnets: For databases (no internet access, no NAT costs)
        self.vpc = ec2.Vpc(
            self,
            "Vpc",
            ip_addresses=ec2.IpAddresses.cidr(cidr),
            max_azs=max_azs,
            # Enable DNS support for ECS service discovery
            enable_dns_hostnames=True,
            enable_dns_support=True,
            # Create public, private, and isolated subnets
            # Note: cidr_mask is not specified, so CDK will auto-calculate and evenly
            # divide the VPC CIDR space among all subnets (1 of each type per AZ)
            subnet_configuration=[
                ec2.SubnetConfiguration(
                    name="Public",
                    subnet_type=ec2.SubnetType.PUBLIC,
                ),
                ec2.SubnetConfiguration(
                    name="Private",
                    subnet_type=ec2.SubnetType.PRIVATE_WITH_EGRESS,
                ),
                ec2.SubnetConfiguration(
                    name="Isolated",
                    subnet_type=ec2.SubnetType.PRIVATE_ISOLATED,
                ),
            ],
            # NAT gateways for internet access from private subnets
            # 1 NAT: Cost-optimized, single point of failure
            # 2+ NATs: High availability, one per AZ for redundancy
            nat_gateways=nat_gateways,
        )

        # Create VPC endpoint for GuardDuty
        # This is required for ECS Runtime Monitoring and must be explicitly
        # managed to avoid orphaned resources during stack deletion
        self.guardduty_endpoint = ec2.InterfaceVpcEndpoint(
            self,
            "GuardDutyEndpoint",
            vpc=self.vpc,
            service=ec2.InterfaceVpcEndpointAwsService.GUARDDUTY_DATA,
            # Place endpoints in private subnets for security
            subnets=ec2.SubnetSelection(subnet_type=ec2.SubnetType.PRIVATE_WITH_EGRESS),
            # Enable private DNS to use standard GuardDuty endpoint name
            private_dns_enabled=True,
        )

        # Export VPC attributes for use in other stacks
        self.vpc_id = self.vpc.vpc_id
        self.availability_zones = self.vpc.availability_zones
        self.public_subnets = self.vpc.public_subnets
        self.private_subnets = self.vpc.private_subnets
        self.isolated_subnets = self.vpc.isolated_subnets
