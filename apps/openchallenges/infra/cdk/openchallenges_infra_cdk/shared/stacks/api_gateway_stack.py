"""API Gateway service stack for OpenChallenges API Gateway."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from aws_cdk.aws_elasticloadbalancingv2 import IApplicationTargetGroup
from constructs import Construct

from openchallenges_infra_cdk.shared.constructs.fargate_service_construct import (
    OpenchalllengesFargateService,
)
from openchallenges_infra_cdk.shared.image_loader import load_container_image


class ApiGatewayStack(cdk.Stack):
    """Stack for the OpenChallenges API Gateway Fargate service."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        stack_prefix: str,
        environment: str,
        vpc: ec2.IVpc,
        cluster: ecs.ICluster,
        target_group: IApplicationTargetGroup,
        developer_name: str | None = None,
        app_version: str = "edge",
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
            developer_name: Developer name for dev environment (optional)
            app_version: Application version (Docker image tag)
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Container image - support local or remote images
        image = load_container_image(
            self,
            "ApiGatewayImage",
            "openchallenges-api-gateway",
            f"ghcr.io/sage-bionetworks/openchallenges-api-gateway:{app_version}",
        )

        # Build service discovery URLs for backend services
        # Format: http://{service-name}.{cluster-name}.local:{port}
        auth_service_url = (
            f"http://openchallenges-auth-service.{cluster.cluster_name}.local:8087"
        )
        org_service_url = (
            f"http://openchallenges-organization-service."
            f"{cluster.cluster_name}.local:8084"
        )
        challenge_service_url = (
            f"http://openchallenges-challenge-service.{cluster.cluster_name}.local:8085"
        )
        image_service_url = (
            f"http://openchallenges-image-service.{cluster.cluster_name}.local:8086"
        )

        # Environment variables for the API Gateway container
        # Note: Spring Boot list overrides replace the entire list item, not merge
        # So we must specify all route properties (id, uri, predicates, filters)
        container_env = {
            # Set active profile (loads application-{profile}.yml)
            "SPRING_PROFILES_ACTIVE": environment,
            # Auth service URL for JWT validation/introspection
            # Maps to app.auth.service-url property
            "APP_AUTH_SERVICE_URL": f"{auth_service_url}/v1",
            # Route 0: Auth Service OAuth2 endpoints (no StripPrefix)
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_0_ID": (
                "openchallenges-auth-service-oauth2"
            ),
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_0_URI": auth_service_url,
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_0_PREDICATES_0": (
                "Path=/oauth2/**,/.well-known/**"
            ),
            # Route 1: Auth Service API
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_1_ID": (
                "openchallenges-auth-service"
            ),
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_1_URI": auth_service_url,
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_1_PREDICATES_0": (
                "Path=/api/v1/auth/**"
            ),
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_1_FILTERS_0": "StripPrefix=1",
            # Route 2: Organization Service
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_2_ID": (
                "openchallenges-organization-service"
            ),
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_2_URI": org_service_url,
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_2_PREDICATES_0": (
                "Path=/api/v1/organizations/**"
            ),
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_2_FILTERS_0": "StripPrefix=1",
            # Route 3: Challenge Service
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_3_ID": (
                "openchallenges-challenge-service"
            ),
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_3_URI": challenge_service_url,
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_3_PREDICATES_0": (
                "Path=/api/v1/challenges/**,/api/v1/challenge-analytics/**,"
                "/api/v1/challenge-platforms/**,/api/v1/edam-concepts/**"
            ),
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_3_FILTERS_0": "StripPrefix=1",
            # Route 4: Image Service
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_4_ID": (
                "openchallenges-image-service"
            ),
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_4_URI": image_service_url,
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_4_PREDICATES_0": (
                "Path=/api/v1/images/**"
            ),
            "SPRING_CLOUD_GATEWAY_SERVER_WEBFLUX_ROUTES_4_FILTERS_0": "StripPrefix=1",
        }

        # Create Fargate service for the API Gateway
        service_construct = OpenchalllengesFargateService(
            self,
            "ApiGatewayService",
            vpc=vpc,
            cluster=cluster,
            service_name="openchallenges-api-gateway",
            container_image=image,
            container_port=8082,
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
