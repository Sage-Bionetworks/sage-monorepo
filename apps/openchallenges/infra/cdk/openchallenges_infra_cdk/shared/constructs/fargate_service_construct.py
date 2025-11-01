"""Fargate service construct for OpenChallenges infrastructure."""

from aws_cdk import Duration
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from aws_cdk import aws_iam as iam
from aws_cdk import aws_logs as logs
from aws_cdk.aws_elasticloadbalancingv2 import IApplicationTargetGroup
from constructs import Construct


class OpenchalllengesFargateService(Construct):
    """Reusable Fargate service construct for deploying containers."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        vpc: ec2.IVpc,
        cluster: ecs.ICluster,
        service_name: str,
        container_image: str,
        container_port: int,
        cpu: int = 256,
        memory_limit_mib: int = 512,
        environment: dict[str, str] | None = None,
        desired_count: int = 1,
        target_group: IApplicationTargetGroup | None = None,
        **kwargs,
    ) -> None:
        """
        Create a Fargate service.

        Args:
            scope: CDK construct scope
            construct_id: Construct identifier
            vpc: VPC where the service will run
            cluster: ECS cluster
            service_name: Name of the service
            container_image: Docker image URI
            container_port: Port the container listens on
            cpu: CPU units (256 = 0.25 vCPU, 512 = 0.5 vCPU, etc.)
            memory_limit_mib: Memory limit in MiB
            environment: Environment variables for the container
            desired_count: Number of tasks to run
            target_group: Optional ALB target group to attach to
            **kwargs: Additional arguments passed to parent Construct
        """
        super().__init__(scope, construct_id, **kwargs)

        # Create security group for the service
        self.security_group = ec2.SecurityGroup(
            self,
            "ServiceSecurityGroup",
            vpc=vpc,
            description=f"Security group for {service_name}",
            allow_all_outbound=True,
        )

        # Allow inbound traffic on the container port from the VPC
        self.security_group.add_ingress_rule(
            peer=ec2.Peer.ipv4(vpc.vpc_cidr_block),
            connection=ec2.Port.tcp(container_port),
            description=f"Allow {service_name} traffic from VPC",
        )

        # Create task definition
        task_definition = ecs.FargateTaskDefinition(
            self,
            "TaskDefinition",
            cpu=cpu,
            memory_limit_mib=memory_limit_mib,
        )

        # Add IAM permissions for GuardDuty agent sidecar container
        # GuardDuty automatically injects a sidecar container for runtime monitoring
        # This requires permission to pull the agent image from AWS ECR
        task_definition.add_to_execution_role_policy(
            iam.PolicyStatement(
                effect=iam.Effect.ALLOW,
                actions=[
                    "ecr:GetAuthorizationToken",
                    "ecr:BatchCheckLayerAvailability",
                    "ecr:GetDownloadUrlForLayer",
                    "ecr:BatchGetImage",
                ],
                resources=["*"],  # GuardDuty agent is in AWS-managed ECR
            )
        )

        # Add container to task definition
        container = task_definition.add_container(
            "Container",
            image=ecs.ContainerImage.from_registry(container_image),
            environment=environment or {},
            logging=ecs.LogDrivers.aws_logs(
                stream_prefix=service_name,
                log_retention=logs.RetentionDays.ONE_WEEK,
            ),
        )

        # Add port mapping
        container.add_port_mappings(
            ecs.PortMapping(
                container_port=container_port,
                protocol=ecs.Protocol.TCP,
            )
        )

        # Create Fargate service
        self.service = ecs.FargateService(
            self,
            "Service",
            cluster=cluster,
            task_definition=task_definition,
            desired_count=desired_count,
            security_groups=[self.security_group],
            vpc_subnets=ec2.SubnetSelection(
                subnet_type=ec2.SubnetType.PRIVATE_WITH_EGRESS
            ),
            # Health check grace period for ALB health checks
            health_check_grace_period=(Duration.seconds(60) if target_group else None),
        )

        # Attach to target group if provided
        if target_group:
            self.service.attach_to_application_target_group(target_group)

        # Export service attributes
        self.service_name = self.service.service_name
        self.service_arn = self.service.service_arn
