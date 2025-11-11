"""API Gateway service stack for BixArena API Gateway."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from aws_cdk.aws_elasticloadbalancingv2 import IApplicationTargetGroup
from constructs import Construct

from bixarena_infra_cdk.shared.constructs.fargate_service_construct import (
    BixArenaFargateService,
)
from bixarena_infra_cdk.shared.image_loader import load_container_image


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
        developer_name: str | None = None,
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
            developer_name: Developer name for dev environment (optional)
            cluster: ECS cluster
            target_group: ALB target group for the gateway
            valkey_endpoint: Valkey cluster endpoint
            valkey_port: Valkey cluster port
            gateway_version: Gateway version (Docker image tag)
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Container image - support local or remote images
        image = load_container_image(
            self,
            "ApiGatewayImage",
            "bixarena-api-gateway",
            f"ghcr.io/sage-bionetworks/bixarena-api-gateway:{gateway_version}",
        )

        # Environment variables for the API Gateway container
        # Note: Spring Boot list overrides replace the entire list item, not merge
        # So we must specify all properties (id, uri, predicates, filters)
        container_env = {
            # Set active profile (loads application-{profile}.yml)
            "SPRING_PROFILES_ACTIVE": environment,
            # Valkey/Redis connection (override defaults with actual endpoints)
            "SPRING_DATA_REDIS_HOST": valkey_endpoint,
            "SPRING_DATA_REDIS_PORT": valkey_port,
            # Auth service WebClient URL (for session validation via /userinfo)
            "APP_AUTH_SERVICE_URL": (
                f"http://bixarena-auth-service.{cluster.cluster_name}.local:8115"
            ),
            # Route 0: API Service - must specify complete route definition
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_0_ID": "bixarena-api",
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_0_URI": (
                f"http://bixarena-api.{cluster.cluster_name}.local:8112"
            ),
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_0_PREDICATES_0": (
                "Path=/api/v1/**"
            ),
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_0_FILTERS_0": ("StripPrefix=1"),
            # Route 1: Auth Service - must specify complete route definition
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_1_ID": (
                "bixarena-auth-service"
            ),
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_1_URI": (
                f"http://bixarena-auth-service.{cluster.cluster_name}.local:8115"
            ),
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_1_PREDICATES_0": (
                "Path=/auth/**,/.well-known/jwks.json,/oauth2/token,/userinfo"
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
            cpu=1024,  # 1 vCPU - Gateway needs more resources for routing
            memory_limit_mib=2048,  # 2 GB - Spring Cloud Gateway is memory-intensive
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
