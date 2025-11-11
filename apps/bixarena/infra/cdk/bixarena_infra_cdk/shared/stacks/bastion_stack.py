"""Bastion stack for database access via AWS Systems Manager Session Manager."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from aws_cdk import aws_iam as iam
from aws_cdk import aws_logs as logs
from constructs import Construct


class BastionStack(cdk.Stack):
    """Stack for Systems Manager bastion ECS service for database port forwarding."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        stack_prefix: str,
        environment: str,
        vpc: ec2.IVpc,
        cluster: ecs.ICluster,
        database_security_group: ec2.ISecurityGroup,
        **kwargs,
    ) -> None:
        """
        Create bastion stack with Session Manager support.

        The bastion service runs a single task that keeps a simple container alive.
        Access is via AWS Systems Manager port forwarding (no SSH, no public IP).

        Args:
            scope: CDK app scope
            construct_id: Stack identifier
            stack_prefix: Prefix for stack name
            environment: Environment name (dev, stage, prod)
            vpc: VPC where the bastion will run
            cluster: ECS cluster
            database_security_group: Security group of the database
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Create security group for bastion
        bastion_sg = ec2.SecurityGroup(
            self,
            "BastionSecurityGroup",
            vpc=vpc,
            description="Security group for database bastion",
            allow_all_outbound=True,
        )

        # Allow bastion to connect to database
        database_security_group.add_ingress_rule(
            peer=bastion_sg,
            connection=ec2.Port.tcp(5432),
            description="Allow bastion to connect to PostgreSQL",
        )

        # Create task execution role with SSM permissions
        task_role = iam.Role(
            self,
            "BastionTaskRole",
            assumed_by=iam.ServicePrincipal("ecs-tasks.amazonaws.com"),
            description="Task role for bastion with SSM access",
        )

        # Add SSM permissions for Session Manager
        task_role.add_managed_policy(
            iam.ManagedPolicy.from_aws_managed_policy_name(
                "AmazonSSMManagedInstanceCore"
            )
        )

        # Create task definition
        task_definition = ecs.FargateTaskDefinition(
            self,
            "BastionTaskDefinition",
            cpu=256,  # 0.25 vCPU - minimal for port forwarding
            memory_limit_mib=512,  # 512 MB - minimal
            task_role=task_role,
            runtime_platform=ecs.RuntimePlatform(
                cpu_architecture=ecs.CpuArchitecture.ARM64,  # Use ARM for cost savings
                operating_system_family=ecs.OperatingSystemFamily.LINUX,
            ),
        )

        # Create log group
        log_group = logs.LogGroup(
            self,
            "BastionLogGroup",
            retention=logs.RetentionDays.ONE_WEEK,
            removal_policy=cdk.RemovalPolicy.DESTROY,
        )

        # Add container - lightweight container for ECS Exec port forwarding
        # ECS Exec is handled by the ECS agent, not by installing SSM agent
        task_definition.add_container(
            "bastion",
            # Use Alpine - minimal and lightweight
            image=ecs.ContainerImage.from_registry(
                "public.ecr.aws/docker/library/alpine:latest"
            ),
            logging=ecs.LogDrivers.aws_logs(
                stream_prefix="bastion",
                log_group=log_group,
            ),
            # Keep container alive - ECS Exec handles Session Manager
            command=["/bin/sh", "-c", "while true; do sleep 3600; done"],
        )

        # Create Fargate service with minimal resources
        # This keeps 1 task always running for database access
        service = ecs.FargateService(
            self,
            "BastionService",
            cluster=cluster,
            task_definition=task_definition,
            desired_count=1,  # Single task is enough
            security_groups=[bastion_sg],
            vpc_subnets=ec2.SubnetSelection(
                subnet_type=ec2.SubnetType.PRIVATE_WITH_EGRESS
            ),
            # Enable ECS Exec for Session Manager access
            enable_execute_command=True,
            # Circuit breaker for automatic rollback on failure
            circuit_breaker=ecs.DeploymentCircuitBreaker(rollback=True),
            # Health check grace period
            health_check_grace_period=cdk.Duration.seconds(60),
            # Deployment configuration for single-task service
            # AZ rebalancing requires max > 100% to temporarily run 2 tasks
            min_healthy_percent=0,
            max_healthy_percent=200,
        )

        # Store for reference
        self.service = service
        self.task_definition = task_definition
        self.security_group = bastion_sg
        self.cluster = cluster
        self.task_role = task_role

        # CloudFormation outputs
        cdk.CfnOutput(
            self,
            "BastionServiceName",
            value=service.service_name,
            description="Bastion service name",
        )

        cdk.CfnOutput(
            self,
            "BastionServiceArn",
            value=service.service_arn,
            description="Bastion service ARN",
        )

        cdk.CfnOutput(
            self,
            "BastionSecurityGroupId",
            value=bastion_sg.security_group_id,
            description="Bastion security group ID",
        )
