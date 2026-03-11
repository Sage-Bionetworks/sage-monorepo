"""Worker stack for BixArena infrastructure."""

import aws_cdk as cdk
from aws_cdk import aws_applicationautoscaling as appscaling
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from aws_cdk import aws_ecs_patterns as ecs_patterns
from aws_cdk import aws_iam as iam
from aws_cdk import aws_logs as logs
from aws_cdk import aws_rds as rds
from aws_cdk import aws_secretsmanager as sm
from constructs import Construct

from bixarena_infra_cdk.shared.image_loader import load_container_image


class WorkerStack(cdk.Stack):
    """Stack for BixArena scheduled worker tasks."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        stack_prefix: str,
        environment: str,
        vpc: ec2.IVpc,
        cluster: ecs.ICluster,
        database: rds.IDatabaseInstance,
        database_secret_arn: str,
        app_version: str = "edge",
        developer_name: str | None = None,
        **kwargs,
    ) -> None:
        """
        Create the worker stack.

        Args:
            scope: CDK app scope
            construct_id: Stack identifier
            stack_prefix: Prefix for resource naming
            environment: Environment name (dev, stage, prod)
            vpc: VPC to deploy the task in
            cluster: ECS cluster to run the scheduled task on
            database: RDS database instance (for security group access)
            database_secret_arn: ARN of the database credentials secret
            app_version: Docker image tag for the container
            developer_name: Developer name for dev environment (optional)
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Import database secret for secure credential injection
        db_secret = sm.Secret.from_secret_complete_arn(
            self, "DatabaseSecret", database_secret_arn
        )

        # Container image - support local or remote images
        image = load_container_image(
            self,
            "WorkerImage",
            "bixarena-worker",
            f"ghcr.io/sage-bionetworks/bixarena-worker:{app_version}",
        )

        # Environment variables for the leaderboard snapshot container
        container_env = {
            "LEADERBOARD_SLUG": "overall",
            "NUM_BOOTSTRAP": "1000",
            "MIN_EVALS": "10",
            "SIGNIFICANT": "false",
            "POSTGRES_HOST": database.db_instance_endpoint_address,
            "POSTGRES_PORT": database.db_instance_endpoint_port,
            "POSTGRES_DB": "bixarena",
        }

        # Secrets from AWS Secrets Manager (injected securely at runtime)
        container_secrets = {
            "POSTGRES_USER": ecs.Secret.from_secrets_manager(
                db_secret, field="username"
            ),
            "POSTGRES_PASSWORD": ecs.Secret.from_secrets_manager(
                db_secret, field="password"
            ),
        }

        log_group = logs.LogGroup(
            self,
            "WorkerLogGroup",
            log_group_name=f"/aws/ecs/{stack_prefix}-worker",
            retention=logs.RetentionDays.THREE_MONTHS,
            removal_policy=cdk.RemovalPolicy.RETAIN,
        )

        # Daily at 10:00 AM UTC
        scheduled_task = ecs_patterns.ScheduledFargateTask(
            self,
            "LeaderboardSnapshotTask",
            cluster=cluster,
            rule_name=f"{stack_prefix}-leaderboard-snapshot-schedule",
            schedule=appscaling.Schedule.cron(hour="10", minute="0"),
            subnet_selection=ec2.SubnetSelection(
                subnet_type=ec2.SubnetType.PRIVATE_WITH_EGRESS
            ),
            scheduled_fargate_task_image_options=ecs_patterns.ScheduledFargateTaskImageOptions(
                image=ecs.ContainerImage.from_registry(image),
                cpu=1024,
                memory_limit_mib=2048,
                environment=container_env,
                secrets=container_secrets,
                log_driver=ecs.LogDrivers.aws_logs(
                    stream_prefix="worker",
                    log_group=log_group,
                ),
            ),
        )

        # Add IAM permissions for GuardDuty agent sidecar container
        # GuardDuty automatically injects a sidecar container for runtime monitoring
        # This requires permission to pull the agent image from AWS ECR
        scheduled_task.task_definition.add_to_execution_role_policy(
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
        # Note: no explicit allow_from needed — RDS security group already allows
        # all inbound on port 5432 from the VPC CIDR (set in rds_construct.py)
