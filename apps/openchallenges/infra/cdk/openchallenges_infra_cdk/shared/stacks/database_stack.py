"""Database stack for OpenChallenges infrastructure."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from constructs import Construct

from openchallenges_infra_cdk.shared.constructs.rds_construct import PostgresDatabase


class DatabaseStack(cdk.Stack):
    """Stack for PostgreSQL RDS database."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        stack_prefix: str,
        environment: str,
        vpc: ec2.IVpc,
        developer_name: str | None = None,
        **kwargs,
    ) -> None:
        """
        Create a database stack.

        Args:
            scope: CDK app scope
            construct_id: Stack identifier
            stack_prefix: Prefix for resource naming
            environment: Environment name (dev, stage, prod)
            vpc: VPC to deploy the database in
            developer_name: Developer name for dev environment (optional)
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Configure database based on environment
        if environment == "dev":
            # Dev: Cost-optimized single-AZ deployment
            instance_type = ec2.InstanceType.of(
                ec2.InstanceClass.BURSTABLE4_GRAVITON, ec2.InstanceSize.SMALL
            )
            multi_az = False
            allocated_storage = 20
            backup_retention_days = 1
            deletion_protection = False
        elif environment == "stage":
            # Stage: Multi-AZ for availability testing, cost-optimized
            instance_type = ec2.InstanceType.of(
                ec2.InstanceClass.BURSTABLE4_GRAVITON, ec2.InstanceSize.SMALL
            )
            multi_az = True
            allocated_storage = 30
            backup_retention_days = 7
            deletion_protection = True
        else:  # prod
            # Prod: High availability with moderate instance size
            instance_type = ec2.InstanceType.of(
                ec2.InstanceClass.BURSTABLE4_GRAVITON, ec2.InstanceSize.MEDIUM
            )
            multi_az = True
            allocated_storage = 50
            backup_retention_days = 30
            deletion_protection = True

        # Create PostgreSQL database
        self.database_construct = PostgresDatabase(
            self,
            "PostgresDatabase",
            vpc=vpc,
            stack_prefix=stack_prefix,
            database_name="openchallenges",
            instance_type=instance_type,
            multi_az=multi_az,
            allocated_storage=allocated_storage,
            backup_retention_days=backup_retention_days,
            deletion_protection=deletion_protection,
        )

        # Export database endpoint for other stacks
        cdk.CfnOutput(
            self,
            "DatabaseEndpoint",
            value=self.database_construct.database_endpoint,
            description="PostgreSQL database endpoint",
            export_name=f"{stack_prefix}-database-endpoint",
        )

        cdk.CfnOutput(
            self,
            "DatabasePort",
            value=self.database_construct.database_port,
            description="PostgreSQL database port",
            export_name=f"{stack_prefix}-database-port",
        )

        # Database secret is guaranteed to exist since we use from_generated_secret()
        database_secret = self.database_construct.database_secret
        if database_secret is not None:
            cdk.CfnOutput(
                self,
                "DatabaseSecretArn",
                value=database_secret.secret_arn,
                description=(
                    "ARN of the Secrets Manager secret containing database credentials"
                ),
                export_name=f"{stack_prefix}-database-secret-arn",
            )

        # Expose database construct for other stacks to reference
        # Access properties through:
        #   database_stack.database_construct.database
        #   database_stack.database_construct.database_connections
        #   database_stack.database_construct.security_group
