"""App service stack for BixArena web client."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from aws_cdk.aws_elasticloadbalancingv2 import IApplicationTargetGroup
from constructs import Construct

from bixarena_infra_cdk.shared.constructs.fargate_service_construct import (
    OpenchalllengesFargateService,
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

        # Container image
        # TEMPORARY: Using nginx for testing - replace with app when ready
        # image = f"ghcr.io/sage-bionetworks/bixarena-app:{app_version}"
        image = "nginx:alpine"

        # Environment variables for the app container
        # Based on the original CDK app configuration
        container_env = {
            "API_DOCS_URL": f"{protocol}://{base_url}/api-docs",
            "APP_VERSION": app_version,
            "CSR_API_URL": f"{protocol}://{base_url}/api/v1",
            "DATA_UPDATED_ON": "2025-06-16",
            "ENVIRONMENT": environment,
            "GOOGLE_TAG_MANAGER_ID": "GTM-NBR5XD8C",
            # SSR_API_URL will be updated later when API Gateway is deployed
            "SSR_API_URL": f"{protocol}://{base_url}/api/v1",
            "ENABLE_OPERATION_FILTER": "false",
            "SHOW_ANNOUNCEMENT": "false",
            "TELEMETRY_ENABLED": "false",
        }

        # Create Fargate service for the app
        service_construct = OpenchalllengesFargateService(
            self,
            "AppService",
            vpc=vpc,
            cluster=cluster,
            service_name="bixarena-app",
            container_image=image,
            container_port=80,  # nginx default port (change to 4200 for app)
            cpu=256,  # 0.25 vCPU
            memory_limit_mib=512,  # 512 MiB
            environment=container_env,
            desired_count=1,
            target_group=target_group,
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
