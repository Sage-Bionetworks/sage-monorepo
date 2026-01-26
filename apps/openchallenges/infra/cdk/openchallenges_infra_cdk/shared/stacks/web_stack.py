"""Web stack for OpenChallenges Angular web client."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from aws_cdk.aws_elasticloadbalancingv2 import IApplicationTargetGroup
from constructs import Construct

from openchallenges_infra_cdk.shared.constructs.fargate_service_construct import (
    OpenchalllengesFargateService,
)
from openchallenges_infra_cdk.shared.image_loader import load_container_image


class WebStack(cdk.Stack):
    """Stack for the OpenChallenges Angular web client Fargate service."""

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
        data_updated_on: str,
        developer_name: str | None = None,
        fqdn: str | None = None,
        use_https: bool = False,
        **kwargs,
    ) -> None:
        """
        Create web stack.

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
            data_updated_on: Date when OpenChallenges data was last updated
                (ISO 8601 format)
            developer_name: Developer name for dev environment (optional)
            fqdn: Fully qualified domain name (optional, uses ALB DNS if not provided)
            use_https: Whether to use HTTPS protocol (default: False for HTTP)
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Use custom domain if provided, otherwise use ALB DNS name
        base_url = fqdn if fqdn else alb_dns_name
        protocol = "https" if use_https else "http"

        # Container image - support local or remote images
        image = load_container_image(
            self,
            "WebImage",
            "openchallenges-app",
            f"ghcr.io/sage-bionetworks/openchallenges-app:{app_version}",
        )

        # Environment variables for the Angular SSR app container
        # The Angular app uses a config system similar to Spring Boot:
        # - Base config: application.yaml
        # - Environment-specific: application-{environment}.yaml
        # - Env vars override using pattern: SECTION_SUBSECTION_PROPERTY
        container_env = {
            # Environment configuration - determines which config file to load
            # Loads application-{environment}.yaml (dev, stage, or prod)
            "ENVIRONMENT": environment,
            # App version
            "APP_VERSION": app_version,
            # API URLs for Client-Side Rendering (browser calls via ALB)
            # Maps to api.baseUrls.csr in application.yaml
            "API_BASE_URLS_CSR": f"{protocol}://{base_url}/api/v1",
            # Maps to api.docsUrl in application.yaml
            "API_DOCS_URL": (
                "https://editor.swagger.io/?url=https://raw.githubusercontent.com/"
                "Sage-Bionetworks/sage-monorepo/refs/heads/main/libs/"
                "openchallenges/api-description/openapi/openapi.yaml"
            ),
            # API URL for Server-Side Rendering
            # (SSR calls via internal service discovery)
            # Angular SSR server calls API Gateway directly within VPC
            # Maps to api.baseUrls.ssr in application.yaml
            "API_BASE_URLS_SSR": (
                f"http://openchallenges-api-gateway.{cluster.cluster_name}.local:8082/api/v1"
            ),
            # Data update timestamp - displayed in web app footer
            # Maps to data.updatedOn in application.yaml
            "DATA_UPDATED_ON": data_updated_on,
            # Feature flags
            # Maps to features.announcement.enabled in application.yaml
            "FEATURES_ANNOUNCEMENT_ENABLED": "false",
            # Maps to features.operationFilter.enabled in application.yaml
            "FEATURES_OPERATION_FILTER_ENABLED": "false",
            # Analytics configuration
            # Maps to analytics.googleTagManager.enabled in application.yaml
            "ANALYTICS_GOOGLE_TAG_MANAGER_ENABLED": (
                "false" if environment == "dev" else "true"
            ),
            # Maps to analytics.googleTagManager.id in application.yaml
            "ANALYTICS_GOOGLE_TAG_MANAGER_ID": "GTM-NBR5XD8C",
            # Telemetry
            # Maps to telemetry.enabled in application.yaml
            "TELEMETRY_ENABLED": "false",
            # External links
            # Maps to links.privacyPolicy in application.yaml
            "LINKS_PRIVACY_POLICY": (
                "https://sagebionetworks.jira.com/wiki/spaces/OA/pages/2948530178/"
                "OpenChallenges+Privacy+Policy"
            ),
            # Maps to links.termsOfUse in application.yaml
            "LINKS_TERMS_OF_USE": (
                "https://sagebionetworks.jira.com/wiki/spaces/OA/pages/2948333575/"
                "OpenChallenges+Terms+of+Use"
            ),
        }

        # Create Fargate service for the Angular SSR app
        service_construct = OpenchalllengesFargateService(
            self,
            "WebService",
            vpc=vpc,
            cluster=cluster,
            service_name="openchallenges-web",
            container_image=image,
            container_port=4200,  # Angular SSR server port
            cpu=512,  # 0.5 vCPU - Angular SSR needs more than nginx
            memory_limit_mib=1024,  # 1 GB - Node.js SSR apps need memory
            environment=container_env,
            desired_count=1,
            target_group=target_group,
            # Health check is configured on the ALB target group to use /health
        )

        # Export service for reference
        self.service = service_construct.service

        # CloudFormation outputs
        cdk.CfnOutput(
            self,
            "ServiceName",
            value=self.service.service_name,
            description="Web client service name",
        )

        cdk.CfnOutput(
            self,
            "ServiceArn",
            value=self.service.service_arn,
            description="Web client service ARN",
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
