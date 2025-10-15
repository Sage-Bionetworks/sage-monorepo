import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from constructs import Construct


class NetworkStack(cdk.Stack):
    """Network for applications."""

    def __init__(
        self, scope: Construct, construct_id: str, vpc_cidr: str, **kwargs
    ) -> None:
        super().__init__(scope, construct_id, **kwargs)

        # Create a VPC spanning up to two availability zones.
        self.vpc = ec2.Vpc(
            self,
            "Vpc",
            max_azs=2,
            ip_addresses=ec2.IpAddresses.cidr(vpc_cidr),
        )
