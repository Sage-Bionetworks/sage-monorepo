"""Valkey (Redis-compatible) ElastiCache construct for BixArena infrastructure."""

from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_elasticache as elasticache
from constructs import Construct


class ValkeyCluster(Construct):
    """Reusable Valkey ElastiCache cluster construct."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        vpc: ec2.IVpc,
        cluster_name: str = "bixarena-valkey",
        node_type: str = "cache.t4g.micro",
        num_cache_nodes: int = 1,
        engine_version: str = "8.1",
        snapshot_retention_limit: int = 1,
        **kwargs,
    ) -> None:
        """
        Create a Valkey (Redis-compatible) ElastiCache cluster.

        Args:
            scope: CDK construct scope
            construct_id: Construct identifier
            vpc: VPC to deploy the cluster in
            cluster_name: Name for the cache cluster
            node_type: ElastiCache node type (default: cache.t4g.micro)
            num_cache_nodes: Number of cache nodes (default: 1 for single-node)
            engine_version: Valkey engine version (default: 8.1)
            snapshot_retention_limit: Number of days to retain snapshots (default: 1)
            **kwargs: Additional arguments passed to parent Construct
        """
        super().__init__(scope, construct_id, **kwargs)

        # Create security group for Valkey cluster
        self.security_group = ec2.SecurityGroup(
            self,
            "ValkeySecurityGroup",
            vpc=vpc,
            description="Security group for Valkey ElastiCache cluster",
            allow_all_outbound=False,  # No outbound access needed
        )

        # Create subnet group for ElastiCache
        # Use isolated subnets for cost savings (no NAT charges)
        self.subnet_group = elasticache.CfnSubnetGroup(
            self,
            "ValkeySubnetGroup",
            description="Subnet group for Valkey ElastiCache cluster",
            subnet_ids=[subnet.subnet_id for subnet in vpc.isolated_subnets],
            cache_subnet_group_name=f"{cluster_name}-subnet-group",
        )

        # Create Valkey replication group (required for Valkey engine)
        # Even single-node deployments use replication group architecture
        self.replication_group = elasticache.CfnReplicationGroup(
            self,
            "ValkeyReplicationGroup",
            replication_group_id=cluster_name,
            replication_group_description=f"Valkey {engine_version} cluster",
            engine="valkey",
            engine_version=engine_version,
            cache_node_type=node_type,
            # For single node: set to 0 replicas, for multi-node: num_cache_nodes - 1
            num_cache_clusters=num_cache_nodes,
            security_group_ids=[self.security_group.security_group_id],
            cache_subnet_group_name=self.subnet_group.cache_subnet_group_name,
            # Enable automatic backups
            snapshot_retention_limit=snapshot_retention_limit,
            snapshot_window="03:00-05:00",  # UTC
            preferred_maintenance_window="sun:05:00-sun:07:00",  # UTC
            # Enable automatic minor version upgrades
            auto_minor_version_upgrade=True,
            # Multi-AZ configuration
            automatic_failover_enabled=num_cache_nodes > 1,
            multi_az_enabled=num_cache_nodes > 1,
            # Transit encryption disabled for simplicity (enable in prod if needed)
            transit_encryption_enabled=False,
            at_rest_encryption_enabled=True,  # Encryption at rest
        )

        # Ensure replication group is created after subnet group
        self.replication_group.add_dependency(self.subnet_group)

        # Export cluster attributes
        self.cluster_endpoint = self.replication_group.attr_primary_end_point_address
        self.cluster_port = self.replication_group.attr_primary_end_point_port
        self.cluster_id_output = self.replication_group.replication_group_id

    def allow_from(
        self, other: ec2.IConnectable, description: str = "Allow Valkey access"
    ) -> None:
        """
        Allow another resource to connect to the Valkey cluster.

        Args:
            other: The resource to allow connections from (e.g., ECS service)
            description: Description for the security group rule
        """
        self.security_group.add_ingress_rule(
            peer=other.connections.security_groups[0],
            connection=ec2.Port.tcp(6379),  # Default Redis/Valkey port
            description=description,
        )
