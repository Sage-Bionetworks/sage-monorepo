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
        # Based on application.yml configuration
        container_env = {
            "SPRING_PROFILES_ACTIVE": environment,
            "SPRING_APPLICATION_NAME": "bixarena-api-gateway",
            "SERVER_PORT": "8113",
            # Valkey/Redis configuration (database 1 for rate limiting)
            "SPRING_DATA_REDIS_HOST": valkey_endpoint,
            "SPRING_DATA_REDIS_PORT": valkey_port,
            "SPRING_DATA_REDIS_DATABASE": "1",
            "SPRING_DATA_REDIS_TIMEOUT": "2000ms",
            "SPRING_DATA_REDIS_CONNECT_TIMEOUT": "5000ms",
            # Rate limiting configuration
            "APP_RATE_LIMIT_ENABLED": "true",
            "APP_RATE_LIMIT_FAIL_OPEN": "true",
            "APP_RATE_LIMIT_DEFAULT_REQUESTS_PER_MINUTE": "100",
            "APP_RATE_LIMIT_KEY_PREFIX": "bixarena:gateway:ratelimit",
            # Gateway routes (using ECS service discovery)
            "SPRING_CLOUD_GATEWAY_ROUTES_0_ID": "bixarena-api",
            "SPRING_CLOUD_GATEWAY_ROUTES_0_URI": f"http://bixarena-api.{cluster.cluster_name}.local:8112",
            "SPRING_CLOUD_GATEWAY_ROUTES_0_PREDICATES_0": "Path=/api/v1/**",
            "SPRING_CLOUD_GATEWAY_ROUTES_1_ID": "bixarena-auth-service",
            "SPRING_CLOUD_GATEWAY_ROUTES_1_URI": (
                f"http://bixarena-auth-service.{cluster.cluster_name}.local:8115"
            ),
            "SPRING_CLOUD_GATEWAY_ROUTES_1_PREDICATES_0": (
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
