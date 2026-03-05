"""Lambda stack for BixArena infrastructure."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_events as events
from aws_cdk import aws_events_targets as targets
from aws_cdk import aws_lambda as lambda_
from aws_cdk import aws_rds as rds
from aws_cdk import aws_secretsmanager as sm
from constructs import Construct

from bixarena_infra_cdk.shared.image_loader import load_container_image


class LambdaStack(cdk.Stack):
    """Stack for the BixArena Lambda functions."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        stack_prefix: str,
        environment: str,
        vpc: ec2.IVpc,
        database: rds.IDatabaseInstance,
        database_secret_arn: str,
        app_version: str = "edge",
        developer_name: str | None = None,
        **kwargs,
    ) -> None:
        """
        Create the Lambda stack.

        Args:
            scope: CDK app scope
            construct_id: Stack identifier
            stack_prefix: Prefix for resource naming
            environment: Environment name (dev, stage, prod)
            vpc: VPC to deploy the Lambda in
            database: RDS database instance (for security group access)
            database_secret_arn: ARN of the database credentials secret
            app_version: Docker image tag for the Lambda container
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
            "LambdaImage",
            "bixarena-lambda",
            f"ghcr.io/sage-bionetworks/bixarena-lambda:{app_version}",
        )

        # Security group for the Lambda function
        security_group = ec2.SecurityGroup(
            self,
            "LambdaSecurityGroup",
            vpc=vpc,
            description="Security group for BixArena Lambda",
            allow_all_outbound=True,
        )

        # Allow Lambda to connect to the database
        database.connections.allow_from(
            security_group,
            ec2.Port.tcp(5432),
            description="Allow BixArena Lambda to connect to RDS",
        )

        container_env = {
            "LEADERBOARD_SLUG": "overall",
            "NUM_BOOTSTRAP": "1000",
            "MIN_EVALS": "10",
            "SIGNIFICANT": "false",
            "POSTGRES_HOST": database.db_instance_endpoint_address,
            "POSTGRES_PORT": database.db_instance_endpoint_port,
            "POSTGRES_DB": "bixarena",
            "DATABASE_SECRET_ARN": database_secret_arn,
        }

        # Lambda function
        fn = lambda_.DockerImageFunction(
            self,
            "LeaderboardSnapshotFunction",
            function_name=f"{stack_prefix}-leaderboard-snapshot",
            code=lambda_.DockerImageCode.from_image_name(image),
            vpc=vpc,
            vpc_subnets=ec2.SubnetSelection(
                subnet_type=ec2.SubnetType.PRIVATE_WITH_EGRESS
            ),
            security_groups=[security_group],
            memory_size=2048,
            timeout=cdk.Duration.minutes(15),
            environment=container_env,
        )

        # Grant Lambda IAM permission to read DB credentials at runtime
        db_secret.grant_read(fn)

        # TODO: restore to daily cron(hour="10", minute="0") before merging to prod
        # Temporary: every 5 minutes for dev testing
        rule = events.Rule(
            self,
            "DailyScheduleRule",
            rule_name=f"{stack_prefix}-leaderboard-snapshot-schedule",
            description="Trigger leaderboard snapshot daily at 10:00 UTC (2 AM PST)",
            schedule=events.Schedule.rate(cdk.Duration.minutes(5)),
        )
        rule.add_target(
            targets.LambdaFunction(
                fn,
                retry_attempts=2,
                max_event_age=cdk.Duration.hours(1),
            )
        )

        # CloudFormation outputs
        cdk.CfnOutput(
            self,
            "FunctionName",
            value=fn.function_name,
            description="Lambda function name",
        )
        cdk.CfnOutput(
            self,
            "FunctionArn",
            value=fn.function_arn,
            description="Lambda function ARN",
        )
