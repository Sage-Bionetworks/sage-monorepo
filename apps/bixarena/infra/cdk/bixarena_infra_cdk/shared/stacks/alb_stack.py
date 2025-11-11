"""ALB stack for BixArena infrastructure."""

import aws_cdk as cdk
from aws_cdk import aws_certificatemanager as acm
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_elasticloadbalancingv2 as elbv2
from constructs import Construct

from bixarena_infra_cdk.shared.constructs.alb_construct import BixArenaAlb

# ALB listener configuration constants
# Path patterns for health check endpoint
HEALTH_CHECK_PATH_PATTERNS = ["/health"]

# Path patterns for API Gateway routing (API, Auth, OAuth/OIDC endpoints)
API_GATEWAY_PATH_PATTERNS = [
    "/api/*",
    "/auth/*",
    "/userinfo",
    "/.well-known/jwks.json",
    "/oauth2/token",
]

# Health check response body
HEALTH_CHECK_RESPONSE_BODY = '{"status":"healthy","service":"bixarena-alb"}'


class AlbStack(cdk.Stack):
    """Stack for Application Load Balancer."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        stack_prefix: str,
        environment: str,
        vpc: ec2.IVpc,
        developer_name: str | None = None,
        certificate_arn: str | None = None,
        **kwargs,
    ) -> None:
        """
        Create ALB stack with HTTP/HTTPS listeners and /health endpoint.

        Args:
            scope: CDK app scope
            construct_id: Stack identifier
            stack_prefix: Prefix for stack name
            environment: Environment name (dev, stage, prod)
            vpc: VPC to deploy the ALB in
            developer_name: Developer name for dev environment (optional)
            certificate_arn: ARN of ACM certificate for HTTPS (optional for HTTP-only)
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Create ALB
        self.alb_construct = BixArenaAlb(
            self,
            "Alb",
            vpc=vpc,
            internet_facing=True,
        )

        # Create target group for the web client (Gradio app)
        # This will be used by the Fargate service
        self.web_target_group = elbv2.ApplicationTargetGroup(
            self,
            "WebTargetGroup",
            vpc=vpc,
            port=8100,  # Gradio app port
            protocol=elbv2.ApplicationProtocol.HTTP,
            target_type=elbv2.TargetType.IP,
            health_check=elbv2.HealthCheck(
                path="/health",  # Gradio app health endpoint
                interval=cdk.Duration.seconds(30),
                timeout=cdk.Duration.seconds(5),
                healthy_threshold_count=2,
                unhealthy_threshold_count=3,
            ),
            deregistration_delay=cdk.Duration.seconds(30),
        )

        # Create target group for the API Gateway service
        # API Gateway routes traffic to API and Auth services
        self.api_gateway_target_group = elbv2.ApplicationTargetGroup(
            self,
            "ApiGatewayTargetGroup",
            vpc=vpc,
            port=8113,  # API Gateway port
            protocol=elbv2.ApplicationProtocol.HTTP,
            target_type=elbv2.TargetType.IP,
            health_check=elbv2.HealthCheck(
                path="/actuator/health",  # Spring Boot Actuator health endpoint
                interval=cdk.Duration.seconds(30),
                timeout=cdk.Duration.seconds(5),
                healthy_threshold_count=2,
                unhealthy_threshold_count=3,
            ),
            deregistration_delay=cdk.Duration.seconds(30),
        )

        # Determine if we should create HTTPS listener
        use_https = certificate_arn is not None and certificate_arn.strip() != ""

        if use_https:
            # Import ACM certificate
            certificate = acm.Certificate.from_certificate_arn(
                self,
                "Certificate",
                certificate_arn=certificate_arn,  # type: ignore
            )

            # Create HTTPS listener (port 443)
            https_listener = elbv2.ApplicationListener(
                self,
                "HttpsListener",
                load_balancer=self.alb_construct.alb,
                port=443,
                protocol=elbv2.ApplicationProtocol.HTTPS,
                certificates=[certificate],
                open=True,
            )

            # Add /health endpoint as fixed response with highest priority
            https_listener.add_action(
                "HealthCheck",
                priority=1,
                conditions=[
                    elbv2.ListenerCondition.path_patterns(HEALTH_CHECK_PATH_PATTERNS)
                ],
                action=elbv2.ListenerAction.fixed_response(
                    status_code=200,
                    content_type="application/json",
                    message_body=HEALTH_CHECK_RESPONSE_BODY,
                ),
            )

            # Route /api/* and /auth/* to API Gateway (priority 2)
            # Also route OAuth/OIDC endpoints
            https_listener.add_action(
                "ApiGatewayHttps",
                priority=2,
                conditions=[
                    elbv2.ListenerCondition.path_patterns(API_GATEWAY_PATH_PATTERNS)
                ],
                action=elbv2.ListenerAction.forward([self.api_gateway_target_group]),
            )

            # Default action for HTTPS: forward to web client
            https_listener.add_action(
                "DefaultHttps",
                action=elbv2.ListenerAction.forward([self.web_target_group]),
            )

            # Create HTTP listener (port 80) that redirects to HTTPS
            http_listener = elbv2.ApplicationListener(
                self,
                "HttpListener",
                load_balancer=self.alb_construct.alb,
                port=80,
                protocol=elbv2.ApplicationProtocol.HTTP,
                open=True,
            )

            # Redirect all HTTP traffic to HTTPS
            http_listener.add_action(
                "HttpsRedirect",
                action=elbv2.ListenerAction.redirect(
                    protocol="HTTPS",
                    port="443",
                    permanent=True,
                ),
            )

        else:
            # HTTP-only mode (for dev/testing without certificate)
            http_listener = elbv2.ApplicationListener(
                self,
                "HttpListener",
                load_balancer=self.alb_construct.alb,
                port=80,
                protocol=elbv2.ApplicationProtocol.HTTP,
                open=True,
            )

            # Add /health endpoint as fixed response with highest priority
            http_listener.add_action(
                "HealthCheck",
                priority=1,
                conditions=[
                    elbv2.ListenerCondition.path_patterns(HEALTH_CHECK_PATH_PATTERNS)
                ],
                action=elbv2.ListenerAction.fixed_response(
                    status_code=200,
                    content_type="application/json",
                    message_body=HEALTH_CHECK_RESPONSE_BODY,
                ),
            )

            # Route /api/* and /auth/* to API Gateway (priority 2)
            # Also route OAuth/OIDC endpoints
            http_listener.add_action(
                "ApiGatewayHttp",
                priority=2,
                conditions=[
                    elbv2.ListenerCondition.path_patterns(API_GATEWAY_PATH_PATTERNS)
                ],
                action=elbv2.ListenerAction.forward([self.api_gateway_target_group]),
            )

            # Default action for HTTP: forward to web client
            http_listener.add_action(
                "DefaultHttp",
                action=elbv2.ListenerAction.forward([self.web_target_group]),
            )

        # CloudFormation outputs
        cdk.CfnOutput(
            self,
            "LoadBalancerDnsName",
            value=self.alb_construct.alb.load_balancer_dns_name,
            description="ALB DNS name",
            export_name=f"{stack_prefix}-alb-dns-name",
        )

        cdk.CfnOutput(
            self,
            "LoadBalancerArn",
            value=self.alb_construct.alb.load_balancer_arn,
            description="ALB ARN",
        )

        # Output health check URL
        protocol = "https" if use_https else "http"
        health_url = (
            f"{protocol}://{self.alb_construct.alb.load_balancer_dns_name}/health"
        )
        cdk.CfnOutput(
            self,
            "HealthCheckUrl",
            value=health_url,
            description="Health check endpoint URL",
        )
