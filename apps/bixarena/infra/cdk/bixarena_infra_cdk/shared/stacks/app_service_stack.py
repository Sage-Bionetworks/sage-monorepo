"""App service stack for BixArena web client."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from aws_cdk.aws_elasticloadbalancingv2 import IApplicationTargetGroup
from constructs import Construct

from bixarena_infra_cdk.shared.constructs.fargate_service_construct import (
    BixArenaFargateService,
)


class AppServiceStack(cdk.Stack):
    """Stack for the BixArena web client Fargate service."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        stack_prefix: str,
        environment: str,
        vpc: ec2.IVpc,
        cluster: ecs.ICluster,
        target_group: IApplicationTargetGroup,
        app_version: str,
        alb_dns_name: str,
        fqdn: str | None = None,
        use_https: bool = False,
        **kwargs,
    ) -> None:
        """
        Create app service stack.

        Args:
            scope: CDK app scope
            construct_id: Stack identifier
            stack_prefix: Prefix for stack name
            environment: Environment name (dev, stage, prod)
            vpc: VPC where the service will run
            cluster: ECS cluster
            target_group: ALB target group for the app
            app_version: Application version (Docker image tag)
            alb_dns_name: DNS name of the ALB
            fqdn: Fully qualified domain name (optional, uses ALB DNS if not provided)
            use_https: Whether to use HTTPS protocol (default: False for HTTP)
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Use custom domain if provided, otherwise use ALB DNS name
        base_url = fqdn if fqdn else alb_dns_name
        protocol = "https" if use_https else "http"

        # Container image - BixArena Gradio app
        image = f"ghcr.io/sage-bionetworks/bixarena-app:{app_version}"

        # Environment variables for the Gradio app container
        # The app needs access to auth and API services through the gateway
        container_env = {
            # Server configuration
            "APP_HOST": "127.0.0.1",
            "APP_PORT": "8100",
            "APP_VERSION": app_version,
            "LOG_LEVEL": "INFO",
            # Auth service URLs:
            # - SSR (server-side): Use internal API Gateway via service discovery
            # - CSR (client-side): Use public ALB (browser calls)
            "AUTH_BASE_URL_SSR": (
                f"http://bixarena-api-gateway.{cluster.cluster_name}.local:8113"
            ),
            "AUTH_BASE_URL_CSR": f"{protocol}://{base_url}",  # Client-side
            # API service URL: Use internal API Gateway via service discovery
            "API_BASE_URL": (
                f"http://bixarena-api-gateway.{cluster.cluster_name}.local:8113/api/v1"
            ),
            "APP_BRAND_URL": "https://sagebionetworks.org",
            "APP_CONTACT_URL": "https://sagebionetworks.org/contact",
        }

        # Create Fargate service for the Gradio app
        service_construct = BixArenaFargateService(
            self,
            "AppService",
            vpc=vpc,
            cluster=cluster,
            service_name="bixarena-app",
            container_image=image,
            container_port=8100,  # Gradio app default port
            cpu=512,  # 0.5 vCPU - Gradio needs more than nginx
            memory_limit_mib=1024,  # 1 GB - Gradio/Python apps need memory
            environment=container_env,
            desired_count=1,
            target_group=target_group,
            # Note: Health check path is configured on the ALB target group
        )

        # Export service for reference
        self.service = service_construct.service

        # CloudFormation outputs
        cdk.CfnOutput(
            self,
            "ServiceName",
            value=self.service.service_name,
            description="App service name",
        )

        cdk.CfnOutput(
            self,
            "ServiceArn",
            value=self.service.service_arn,
            description="App service ARN",
        )

        cdk.CfnOutput(
            self,
            "AppUrl",
            value=f"{protocol}://{base_url}",
            description="Application URL (using ALB DNS or custom domain)",
        )

        cdk.CfnOutput(
            self,
            "ApiUrl",
            value=f"{protocol}://{base_url}/api/v1",
            description="API base URL configured in the app",
        )
