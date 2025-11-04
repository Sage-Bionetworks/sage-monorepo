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
        cluster_name: str,
        **kwargs,
    ) -> None:
        """
        Create an ECS cluster.

        Args:
            scope: CDK construct scope
            construct_id: Construct identifier
            vpc: VPC where the cluster will be created
            cluster_name: Name for the cluster and service discovery namespace
            **kwargs: Additional arguments passed to parent Construct
        """
        super().__init__(scope, construct_id, **kwargs)

        # Create ECS cluster with Container Insights V2 and Service Discovery
        self.cluster = ecs.Cluster(
            self,
            "Cluster",
            vpc=vpc,
            # Set cluster name for service discovery
            cluster_name=cluster_name,
            # Enable CloudWatch Container Insights with enhanced observability
            container_insights_v2=ecs.ContainerInsights.ENHANCED,
            # Enable service discovery with Cloud Map private DNS namespace
            default_cloud_map_namespace=ecs.CloudMapNamespaceOptions(
                name=f"{cluster_name}.local",
                vpc=vpc,
            ),
        )

        # Export cluster attributes for use in service stacks
        self.cluster_name = self.cluster.cluster_name
        self.cluster_arn = self.cluster.cluster_arn
