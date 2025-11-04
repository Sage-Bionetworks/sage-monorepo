"""ECS cluster stack for BixArena infrastructure."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from constructs import Construct

from bixarena_infra_cdk.shared.constructs.ecs_cluster_construct import (
    BixArenaEcsCluster,
)


class EcsClusterStack(cdk.Stack):
    """Stack for ECS cluster resources."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        stack_prefix: str,
        environment: str,
        vpc: ec2.IVpc,
        **kwargs,
    ) -> None:
        """
        Create ECS cluster stack.

        Args:
            scope: CDK app scope
            construct_id: Stack identifier
            stack_prefix: Prefix for stack name
            environment: Environment name (dev, stage, prod)
            vpc: VPC where the cluster will be created
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Create ECS cluster with service discovery
        cluster_construct = BixArenaEcsCluster(
            self,
            "Cluster",
            vpc=vpc,
            cluster_name=stack_prefix,  # Use stack prefix as cluster name
        )

        # Export cluster for use in service stacks
        self.cluster = cluster_construct.cluster

        # CloudFormation outputs
        cdk.CfnOutput(
            self,
            "ClusterName",
            value=self.cluster.cluster_name,
            description="ECS cluster name",
            export_name=f"{stack_prefix}-cluster-name",
        )

        cdk.CfnOutput(
            self,
            "ClusterArn",
            value=self.cluster.cluster_arn,
            description="ECS cluster ARN",
            export_name=f"{stack_prefix}-cluster-arn",
        )
