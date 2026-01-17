"""OpenSearch domain stack for OpenChallenges search functionality."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_opensearchservice as opensearch
from constructs import Construct


class OpenSearchStack(cdk.Stack):
    """Stack for AWS-managed OpenSearch domain."""

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
        Create OpenSearch domain stack.

        Args:
            scope: CDK app scope
            construct_id: Stack identifier
            stack_prefix: Prefix for stack name
            environment: Environment name (dev, stage, prod)
            vpc: VPC where OpenSearch will be deployed
            developer_name: Developer name for dev environment (optional)
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Determine removal policy and configuration based on environment
        # Dev: DESTROY for easier cleanup, minimal resources for cost savings
        # Stage/Prod: RETAIN for data safety, multi-AZ for HA
        removal_policy = (
            cdk.RemovalPolicy.DESTROY
            if environment == "dev"
            else cdk.RemovalPolicy.RETAIN
        )
        zone_awareness_enabled = environment != "dev"
        instance_count = 1 if environment == "dev" else 2
        ebs_volume_size = 10 if environment == "dev" else 20

        # Create security group for OpenSearch domain
        # Allow access from ECS services on port 9200 (REST API)
        self.opensearch_security_group = ec2.SecurityGroup(
            self,
            "OpenSearchSecurityGroup",
            vpc=vpc,
            description="Security group for OpenSearch domain",
            allow_all_outbound=False,
        )

        # Allow HTTPS (443) for OpenSearch REST API
        # Note: AWS OpenSearch uses HTTPS on 443, not HTTP on 9200
        self.opensearch_security_group.add_ingress_rule(
            peer=ec2.Peer.ipv4(vpc.vpc_cidr_block),
            connection=ec2.Port.tcp(443),
            description="Allow HTTPS access from VPC for OpenSearch API",
        )

        # Create OpenSearch domain with fine-grained access control
        # Using OpenSearch 2.19 (closest to 2.19.1)
        self.domain = opensearch.Domain(
            self,
            "Domain",
            # OpenSearch version 2.19 (latest available in CDK)
            version=opensearch.EngineVersion.OPENSEARCH_2_19,
            # Domain name (auto-generated with stack prefix)
            domain_name=None,  # Auto-generate based on construct ID
            removal_policy=removal_policy,
            # Capacity configuration
            capacity=opensearch.CapacityConfig(
                # Dev: t3.small.search (1 vCPU, 2 GB RAM) - cost-effective
                # Stage/Prod: t3.medium.search (2 vCPU, 4 GB RAM)
                data_node_instance_type=(
                    "t3.small.search" if environment == "dev" else "t3.medium.search"
                ),
                data_nodes=instance_count,
                # Multi-AZ for stage/prod, single-AZ for dev
                multi_az_with_standby_enabled=False,  # Disable standby for cost
            ),
            # EBS storage configuration
            ebs=opensearch.EbsOptions(
                enabled=True,
                volume_size=ebs_volume_size,  # GB
                volume_type=ec2.EbsDeviceVolumeType.GP3,  # General Purpose SSD
            ),
            # Zone awareness (multi-AZ) for stage/prod
            zone_awareness=opensearch.ZoneAwarenessConfig(
                enabled=zone_awareness_enabled,
                availability_zone_count=2 if zone_awareness_enabled else None,
            ),
            # VPC configuration - deploy in private subnets
            # For single-node (dev), select only one subnet
            # For multi-node (stage/prod), distribute across AZs
            vpc=vpc,
            vpc_subnets=[
                ec2.SubnetSelection(
                    subnet_type=ec2.SubnetType.PRIVATE_WITH_EGRESS,
                    availability_zones=[vpc.availability_zones[0]]
                    if environment == "dev"
                    else None,
                )
            ],
            security_groups=[self.opensearch_security_group],
            # Encryption configuration
            encryption_at_rest=opensearch.EncryptionAtRestOptions(
                enabled=True,
            ),
            node_to_node_encryption=True,
            enforce_https=True,
            # Fine-grained access control
            # Use master user with username/password (simpler than IAM for dev)
            fine_grained_access_control=opensearch.AdvancedSecurityOptions(
                master_user_name="admin",
                # Master user password must be at least 8 chars with special chars
                # In production, use Secrets Manager instead
                master_user_password=cdk.SecretValue.unsafe_plain_text(
                    "Admin123!"  # TODO: Use Secrets Manager in production
                ),
            ),
            # Logging configuration (CloudWatch logs)
            logging=opensearch.LoggingOptions(
                slow_search_log_enabled=True,
                app_log_enabled=True,
                slow_index_log_enabled=True,
            ),
            # Automated snapshot configuration
            automated_snapshot_start_hour=2,  # 2 AM UTC
            # Use unsigned basic auth for simplicity in dev
            # Services will use username/password for authentication
            use_unsigned_basic_auth=True,
        )

        # Tag the domain
        cdk.Tags.of(self.domain).add("Name", f"{stack_prefix}-opensearch")

        # Export domain endpoint and ARN
        self.domain_endpoint = self.domain.domain_endpoint
        self.domain_arn = self.domain.domain_arn

        # CloudFormation outputs
        cdk.CfnOutput(
            self,
            "DomainEndpoint",
            value=self.domain_endpoint,
            description="OpenSearch domain endpoint (HTTPS)",
            export_name=f"{stack_prefix}-opensearch-endpoint",
        )

        cdk.CfnOutput(
            self,
            "DomainArn",
            value=self.domain_arn,
            description="OpenSearch domain ARN",
            export_name=f"{stack_prefix}-opensearch-arn",
        )

        cdk.CfnOutput(
            self,
            "DashboardsUrl",
            value=f"https://{self.domain_endpoint}/_dashboards",
            description="OpenSearch Dashboards URL",
        )

        cdk.CfnOutput(
            self,
            "MasterUsername",
            value="admin",
            description="OpenSearch master username (use with master password)",
        )
