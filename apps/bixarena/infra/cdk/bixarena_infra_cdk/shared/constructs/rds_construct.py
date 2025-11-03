"""PostgreSQL RDS construct for BixArena infrastructure."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_rds as rds
from constructs import Construct


class PostgresDatabase(Construct):
    """Reusable PostgreSQL RDS database construct."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        vpc: ec2.IVpc,
        database_name: str = "bixarena",
        instance_type: ec2.InstanceType | None = None,
        multi_az: bool = False,
        allocated_storage: int = 20,
        backup_retention_days: int = 7,
        deletion_protection: bool = False,
        **kwargs,
    ) -> None:
        """
        Create a PostgreSQL RDS database instance.

        Args:
            scope: CDK construct scope
            construct_id: Construct identifier
            vpc: VPC to deploy the database in
            database_name: Name of the default database (default: bixarena)
            instance_type: EC2 instance type (default: t4g.small)
            multi_az: Enable Multi-AZ deployment for high availability (default: False)
            allocated_storage: Storage size in GB (default: 20)
            backup_retention_days: Number of days to retain backups (default: 7)
            deletion_protection: Enable deletion protection (default: False)
            **kwargs: Additional arguments passed to parent Construct
        """
        super().__init__(scope, construct_id, **kwargs)

        # Default to t4g.small if not specified
        if instance_type is None:
            instance_type = ec2.InstanceType.of(
                ec2.InstanceClass.BURSTABLE4_GRAVITON, ec2.InstanceSize.SMALL
            )

        # Create security group for database
        self.security_group = ec2.SecurityGroup(
            self,
            "DatabaseSecurityGroup",
            vpc=vpc,
            description="Security group for PostgreSQL RDS database",
            allow_all_outbound=False,  # No outbound access needed
        )

        # Create database instance
        self.database = rds.DatabaseInstance(
            self,
            "Database",
            engine=rds.DatabaseInstanceEngine.postgres(
                version=rds.PostgresEngineVersion.VER_16_9
            ),
            instance_type=instance_type,
            vpc=vpc,
            # Deploy to isolated subnets (no internet access, no NAT costs)
            vpc_subnets=ec2.SubnetSelection(
                subnet_type=ec2.SubnetType.PRIVATE_ISOLATED
            ),
            security_groups=[self.security_group],
            # Auto-generate credentials and store in Secrets Manager
            credentials=rds.Credentials.from_generated_secret("postgres"),
            multi_az=multi_az,
            allocated_storage=allocated_storage,
            # Use gp3 for better performance and lower cost
            storage_type=rds.StorageType.GP3,
            storage_encrypted=True,
            # Enable automatic storage scaling
            max_allocated_storage=allocated_storage * 2,
            backup_retention=cdk.Duration.days(backup_retention_days),
            # Enable automated backups with preferred window
            preferred_backup_window="03:00-04:00",  # UTC
            preferred_maintenance_window="sun:04:00-sun:05:00",  # UTC
            deletion_protection=deletion_protection,
            # Enable auto minor version upgrades during maintenance window
            auto_minor_version_upgrade=True,
            database_name=database_name,
            # Enable CloudWatch monitoring
            monitoring_interval=cdk.Duration.seconds(60),
            # Enable performance insights for query analysis
            enable_performance_insights=True,
            performance_insight_retention=rds.PerformanceInsightRetention.DEFAULT,
        )

        # Export database attributes
        self.database_endpoint = self.database.db_instance_endpoint_address
        self.database_port = self.database.db_instance_endpoint_port
        self.database_secret = self.database.secret
        self.database_connections = self.database.connections

    def allow_from(
        self, other: ec2.IConnectable, description: str = "Allow database access"
    ) -> None:
        """
        Allow another resource to connect to the database.

        Args:
            other: The resource to allow connections from (e.g., ECS service)
            description: Description for the security group rule
        """
        self.database.connections.allow_from(
            other, ec2.Port.tcp(5432), description=description
        )
