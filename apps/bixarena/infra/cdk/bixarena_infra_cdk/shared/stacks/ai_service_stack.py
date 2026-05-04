"""AI service stack for BixArena AI validation service."""

import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from aws_cdk import aws_secretsmanager as sm
from constructs import Construct

from bixarena_infra_cdk.shared.constructs.fargate_service_construct import (
    BixArenaFargateService,
)
from bixarena_infra_cdk.shared.image_loader import load_container_image


class AiServiceStack(cdk.Stack):
    """Stack for the BixArena AI Service Fargate service."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        stack_prefix: str,
        environment: str,
        vpc: ec2.IVpc,
        cluster: ecs.ICluster,
        valkey_endpoint: str,
        valkey_port: str,
        developer_name: str | None = None,
        openrouter_api_key: str = "",
        app_version: str = "edge",
        **kwargs,
    ) -> None:
        """
        Create AI service stack.

        Args:
            scope: CDK app scope
            construct_id: Stack identifier
            stack_prefix: Prefix for stack name
            environment: Environment name (dev, stage, prod)
            vpc: VPC where the service will run
            cluster: ECS cluster
            valkey_endpoint: Valkey cluster endpoint
            valkey_port: Valkey cluster port
            developer_name: Developer name for dev environment (optional)
            openrouter_api_key: OpenRouter API key for LLM access
            app_version: Application version (Docker image tag)
            **kwargs: Additional arguments passed to parent Stack
        """
        super().__init__(scope, construct_id, **kwargs)

        # Container image - support local or remote images
        image = load_container_image(
            self,
            "AiServiceImage",
            "bixarena-ai-service",
            f"ghcr.io/sage-bionetworks/bixarena-ai-service:{app_version}",
        )

        # Environment variables for the AI service container
        container_env = {
            # Auth service URL via Cloud Map service discovery
            "BIXARENA_AI_AUTH_SERVICE_URL": (
                f"http://bixarena-auth-service.{cluster.cluster_name}.local:8115"
            ),
            # JWT validation configuration
            "BIXARENA_AI_JWT_EXPECTED_ISSUER": "urn:bixarena:auth",
            "BIXARENA_AI_JWT_EXPECTED_AUDIENCE": "urn:bixarena:ai",
            # Validation configuration
            "BIXARENA_AI_PROMPT_VALIDATION_CONFIDENCE_THRESHOLD": "0.5",
            "BIXARENA_AI_BATTLE_VALIDATION_CONFIDENCE_THRESHOLD": "0.2",
            "BIXARENA_AI_PROMPT_MAX_LENGTH": "10000",
            # OpenRouter / LLM configuration
            "BIXARENA_AI_OPENROUTER_BASE_URL": "https://openrouter.ai/api/v1",
            "BIXARENA_AI_OPENROUTER_MODEL": "anthropic/claude-haiku-4.5",
            "BIXARENA_AI_OPENROUTER_TIMEOUT": "30.0",
            "BIXARENA_AI_OPENROUTER_MAX_RETRIES": "2",
            # Validation/categorization method IDs (bump when changing prompt or model)
            "BIXARENA_AI_PROMPT_VALIDATION_METHOD": "openrouter-haiku-v1",
            "BIXARENA_AI_BATTLE_VALIDATION_METHOD": "openrouter-haiku-v1",
            "BIXARENA_AI_PROMPT_CATEGORIZATION_METHOD": "openrouter-haiku-v1",
            "BIXARENA_AI_BATTLE_CATEGORIZATION_METHOD": "openrouter-haiku-v1",
            # Valkey cache connection
            "BIXARENA_AI_VALKEY_HOST": valkey_endpoint,
            "BIXARENA_AI_VALKEY_PORT": valkey_port,
            "BIXARENA_AI_VALKEY_DB": "3",
            "BIXARENA_AI_VALKEY_CACHE_TTL": "2592000",
        }

        # Create OpenRouter API key secret in Secrets Manager
        container_secrets = {}
        if openrouter_api_key:
            openrouter_secret = sm.Secret(
                self,
                "OpenRouterApiKeySecret",
                secret_name=f"{stack_prefix}-ai-openrouter-api-key",
                description="OpenRouter API key for BixArena AI service",
                secret_string_value=cdk.SecretValue.unsafe_plain_text(
                    openrouter_api_key
                ),
            )
            container_secrets["BIXARENA_AI_OPENROUTER_API_KEY"] = (
                ecs.Secret.from_secrets_manager(openrouter_secret)
            )

        # Create Fargate service for the AI service
        service_construct = BixArenaFargateService(
            self,
            "AiService",
            vpc=vpc,
            cluster=cluster,
            service_name="bixarena-ai-service",
            container_image=image,
            container_port=8114,
            cpu=512,  # 0.5 vCPU - Python FastAPI with LLM API calls
            memory_limit_mib=1024,  # 1 GB - aligned with web_stack
            environment=container_env,
            secrets=container_secrets,  # Secure secret injection
            desired_count=1,
            target_group=None,  # Internal only, accessed via API Gateway
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
            description="AI service name",
        )

        cdk.CfnOutput(
            self,
            "ServiceArn",
            value=self.service.service_arn,
            description="AI service ARN",
        )
