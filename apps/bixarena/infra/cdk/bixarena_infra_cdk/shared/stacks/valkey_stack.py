"""Valkey cache stack for BixArena infrastructure."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from constructs import Construct

from bixarena_infra_cdk.shared.constructs.valkey_construct import ValkeyCluster


class ValkeyStack(cdk.Stack):
    """Stack for Valkey (Redis-compatible) ElastiCache cluster."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        stack_prefix: str,
        environment: str,
        vpc: ec2.IVpc,
        **kwargs,
    ) -> None:
        """
        Create a Valkey cache stack.

        Args:
            scope: CDK app scope
            construct_id: Stack identifier
            stack_prefix: Prefix for resource naming
            environment: Environment name (dev, stage, prod)
            vpc: VPC to deploy the cache in
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Configure Valkey cluster based on environment
        if environment == "dev":
            # Dev: Cost-optimized single-node deployment
            node_type = "cache.t4g.micro"
            num_cache_nodes = 1
            snapshot_retention_limit = 1
        elif environment == "stage":
            # Stage: Multi-node for availability testing
            node_type = "cache.t4g.small"
            num_cache_nodes = 2  # Primary + 1 replica
            snapshot_retention_limit = 7
        else:  # prod
            # Prod: Multi-node for high availability
            node_type = "cache.t4g.small"
            num_cache_nodes = 2  # Primary + 1 replica
            snapshot_retention_limit = 30

        # Create Valkey cluster
        self.valkey_construct = ValkeyCluster(
            self,
            "ValkeyCluster",
            vpc=vpc,
            cluster_name=f"{stack_prefix}-valkey",
            node_type=node_type,
            num_cache_nodes=num_cache_nodes,
            engine_version="8.1",  # Valkey 8.1 (matches Docker Compose version)
            snapshot_retention_limit=snapshot_retention_limit,
        )

        # Export cluster endpoint for other stacks
        cdk.CfnOutput(
            self,
            "ValkeyEndpoint",
            value=self.valkey_construct.cluster_endpoint,
            description="Valkey cluster endpoint",
            export_name=f"{stack_prefix}-valkey-endpoint",
        )

        cdk.CfnOutput(
            self,
            "ValkeyPort",
            value=self.valkey_construct.cluster_port,
            description="Valkey cluster port",
            export_name=f"{stack_prefix}-valkey-port",
        )

        cdk.CfnOutput(
            self,
            "ValkeyClusterId",
            value=self.valkey_construct.cluster_id_output,
            description="Valkey cluster ID",
            export_name=f"{stack_prefix}-valkey-cluster-id",
        )

        # Expose cluster for other stacks to reference
        self.replication_group = self.valkey_construct.replication_group
        self.security_group = self.valkey_construct.security_group
