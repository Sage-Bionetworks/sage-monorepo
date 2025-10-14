import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from constructs import Construct


class EcsStack(cdk.Stack):
    """ECS cluster."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        vpc: ec2.Vpc,
        namespace: str,
        **kwargs,
    ) -> None:
        super().__init__(scope, construct_id, **kwargs)

        self.cluster = ecs.Cluster(
            self,
            "Cluster",
            vpc=vpc,
            default_cloud_map_namespace=ecs.CloudMapNamespaceOptions(
                name=namespace,
                use_for_service_connect=True,
            ),
        )
