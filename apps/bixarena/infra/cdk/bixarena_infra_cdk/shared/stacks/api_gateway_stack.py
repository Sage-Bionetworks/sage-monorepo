"""API Gateway service stack for BixArena API Gateway."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from aws_cdk.aws_elasticloadbalancingv2 import IApplicationTargetGroup
from constructs import Construct

from bixarena_infra_cdk.shared.constructs.fargate_service_construct import (
    BixArenaFargateService,
)


class ApiGatewayStack(cdk.Stack):
    """Stack for the BixArena API Gateway Fargate service."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        stack_prefix: str,
        environment: str,
        vpc: ec2.IVpc,
        cluster: ecs.ICluster,
        target_group: IApplicationTargetGroup,
        valkey_endpoint: str,
        valkey_port: str,
        gateway_version: str = "edge",
        **kwargs,
    ) -> None:
        """
        Create API Gateway service stack.

        Args:
            scope: CDK app scope
            construct_id: Stack identifier
            stack_prefix: Prefix for stack name
            environment: Environment name (dev, stage, prod)
            vpc: VPC where the service will run
            cluster: ECS cluster
            target_group: ALB target group for the gateway
            valkey_endpoint: Valkey cluster endpoint
            valkey_port: Valkey cluster port
            gateway_version: Gateway version (Docker image tag)
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Container image
        image = f"ghcr.io/sage-bionetworks/bixarena-api-gateway:{gateway_version}"

        # Environment variables for the API Gateway container
        # Only override values that depend on CDK infrastructure
        # Most configuration is already defined in application.yml
        container_env = {
            # Set active profile (loads application-{profile}.yml)
            "SPRING_PROFILES_ACTIVE": environment,
            # Valkey/Redis connection (override defaults with actual endpoints)
            "SPRING_DATA_REDIS_HOST": valkey_endpoint,
            "SPRING_DATA_REDIS_PORT": valkey_port,
            # Gateway routes: override URIs to use ECS service discovery
            # Route IDs and predicates are already defined in application.yml
            "SPRING_CLOUD_GATEWAY_ROUTES_0_URI": (
                f"http://bixarena-api.{cluster.cluster_name}.local:8112"
            ),
            "SPRING_CLOUD_GATEWAY_ROUTES_1_URI": (
                f"http://bixarena-auth-service.{cluster.cluster_name}.local:8115"
            ),
        }

        # Create Fargate service for the API Gateway
        service_construct = BixArenaFargateService(
            self,
            "ApiGatewayService",
            vpc=vpc,
            cluster=cluster,
            service_name="bixarena-api-gateway",
            container_image=image,
            container_port=8113,
            cpu=512,  # 0.5 vCPU - similar to other services
            memory_limit_mib=1024,  # 1 GB - Spring Cloud Gateway needs memory
            environment=container_env,
            desired_count=1,
            target_group=target_group,  # Exposed via ALB
        )

        # Note: Security group rules are configured in the app.py
        # to avoid cyclic dependencies

        # Export service for reference
        self.service = service_construct.service
        self.security_group = service_construct.security_group

        # CloudFormation outputs
        cdk.CfnOutput(
            self,
            "ServiceName",
            value=self.service.service_name,
            description="API Gateway service name",
        )

        cdk.CfnOutput(
            self,
            "ServiceArn",
            value=self.service.service_arn,
            description="API Gateway service ARN",
        )
