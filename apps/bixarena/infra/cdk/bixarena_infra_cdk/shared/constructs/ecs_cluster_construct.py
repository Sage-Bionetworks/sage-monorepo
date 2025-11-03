"""ECS cluster construct for BixArena infrastructure."""

from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from constructs import Construct


class BixArenaEcsCluster(Construct):
    """Reusable ECS cluster construct with CloudWatch Container Insights."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        vpc: ec2.IVpc,
        **kwargs,
    ) -> None:
        """
        Create an ECS cluster.

        Args:
            scope: CDK construct scope
            construct_id: Construct identifier
            vpc: VPC where the cluster will be created
            **kwargs: Additional arguments passed to parent Construct
        """
        super().__init__(scope, construct_id, **kwargs)

        # Create ECS cluster with Container Insights V2 enabled
        self.cluster = ecs.Cluster(
            self,
            "Cluster",
            vpc=vpc,
            # Enable CloudWatch Container Insights with enhanced observability
            container_insights_v2=ecs.ContainerInsights.ENHANCED,
        )

        # Export cluster attributes for use in service stacks
        self.cluster_name = self.cluster.cluster_name
        self.cluster_arn = self.cluster.cluster_arn
