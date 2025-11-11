"""VPC stack for BixArena infrastructure."""

import aws_cdk as cdk
from constructs import Construct

from bixarena_infra_cdk.shared.constructs.vpc_construct import BixArenaVpc


class VpcStack(cdk.Stack):
    """Stack for VPC networking resources."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        stack_prefix: str,
        environment: str,
        developer_name: str | None = None,
        vpc_cidr: str = "10.0.0.0/16",
        max_azs: int = 2,
        nat_gateways: int = 1,
        **kwargs,
    ) -> None:
        """
        Create VPC stack.

        Args:
            scope: CDK app scope
            construct_id: Stack identifier
            stack_prefix: Prefix for stack name
            environment: Environment name (dev, stage, prod)
            developer_name: Developer name for dev environment (optional)
            vpc_cidr: CIDR block for the VPC (default: 10.0.0.0/16)
            max_azs: Maximum number of Availability Zones (default: 2)
            nat_gateways: Number of NAT gateways (default: 1)
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Create VPC
        self.vpc_construct = BixArenaVpc(
            self,
            "Vpc",
            cidr=vpc_cidr,
            max_azs=max_azs,
            nat_gateways=nat_gateways,
        )

        # CloudFormation outputs
        cdk.CfnOutput(
            self,
            "VpcId",
            value=self.vpc_construct.vpc.vpc_id,
            description="VPC ID",
            export_name=f"{stack_prefix}-vpc-id",
        )

        cdk.CfnOutput(
            self,
            "VpcCidr",
            value=self.vpc_construct.vpc.vpc_cidr_block,
            description="VPC CIDR block",
        )

        # Export public subnet IDs
        public_subnet_ids = [
            subnet.subnet_id for subnet in self.vpc_construct.vpc.public_subnets
        ]
        cdk.CfnOutput(
            self,
            "PublicSubnetIds",
            value=",".join(public_subnet_ids),
            description="Public subnet IDs (comma-separated)",
        )

        # Export private subnet IDs
        private_subnet_ids = [
            subnet.subnet_id for subnet in self.vpc_construct.vpc.private_subnets
        ]
        cdk.CfnOutput(
            self,
            "PrivateSubnetIds",
            value=",".join(private_subnet_ids),
            description="Private subnet IDs (comma-separated)",
        )
