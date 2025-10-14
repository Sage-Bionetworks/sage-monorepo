"""CDK application entrypoint for BixArena infrastructure."""

from os import environ

import aws_cdk as cdk

from .ecs_stack import EcsStack
from .load_balancer_stack import LoadBalancerStack
from .network_stack import NetworkStack
from .service_props import ServiceProps
from .service_stack import LoadBalancedServiceStack

VALID_ENVIRONMENTS = ["dev", "stage", "prod"]


def build_app() -> cdk.App:
    """Build and synthesize the CDK application.

    Reads the ENV environment variable and deploys the appropriate stacks.
    """

    environment = environ.get("ENV")
    match environment:
        case "prod":
            environment_variables = {
                "VPC_CIDR": "10.254.174.0/24",
                "FQDN": "prod.mydomain.io",
                "CERTIFICATE_ARN": (
                    "arn:aws:acm:us-east-1:XXXXXXXXX:certificate/"
                    "69b3ba97-b382-4648-8f94-a250b77b4994"
                ),
                "TAGS": {"CostCenter": "NO PROGRAM / 000000"},
            }
        case "stage":
            environment_variables = {
                "VPC_CIDR": "10.254.173.0/24",
                "FQDN": "stage.mydomain.io",
                "CERTIFICATE_ARN": (
                    "arn:aws:acm:us-east-1:XXXXXXXXXX:certificate/"
                    "69b3ba97-b382-4648-8f94-a250b77b4994"
                ),
                "TAGS": {"CostCenter": "NO PROGRAM / 000000"},
            }
        case "dev":
            environment_variables = {
                "VPC_CIDR": "10.254.172.0/24",
                "FQDN": "dev.mydomain.io",
                "CERTIFICATE_ARN": (
                    "arn:aws:acm:us-east-1:607346494281:certificate/"
                    "e8093404-7db1-4042-90d0-01eb5bde1ffc"
                ),
                "TAGS": {"CostCenter": "NO PROGRAM / 000000"},
            }
        case _:
            valid_envs_str = ",".join(VALID_ENVIRONMENTS)
            raise SystemExit(
                "Must set environment variable `ENV` to one of "
                f"{valid_envs_str}. Currently set to {environment}."
            )

    stack_name_prefix = f"app-{environment}"
    fully_qualified_domain_name = environment_variables["FQDN"]
    environment_tags = environment_variables["TAGS"]
    app_version = "edge"

    cdk_app = cdk.App()

    if environment_tags:
        for key, value in environment_tags.items():
            cdk.Tags.of(cdk_app).add(key, value)

    network_stack = NetworkStack(
        scope=cdk_app,
        construct_id=f"{stack_name_prefix}-network",
        vpc_cidr=environment_variables["VPC_CIDR"],
    )

    ecs_stack = EcsStack(
        scope=cdk_app,
        construct_id=f"{stack_name_prefix}-ecs",
        vpc=network_stack.vpc,
        namespace=fully_qualified_domain_name,
    )

    load_balancer_stack = LoadBalancerStack(
        scope=cdk_app,
        construct_id=f"{stack_name_prefix}-load-balancer",
        vpc=network_stack.vpc,
    )

    app_props = ServiceProps(
        ecs_task_cpu=256,
        ecs_task_memory=512,
        container_name="my-app",
        container_location=f"ghcr.io/sage-bionetworks/my-app:{app_version}",
        container_port=80,
        container_env_vars={
            "APP_VERSION": f"{app_version}",
        },
    )
    app_stack = LoadBalancedServiceStack(
        scope=cdk_app,
        construct_id=f"{stack_name_prefix}-app",
        vpc=network_stack.vpc,
        cluster=ecs_stack.cluster,
        props=app_props,
        load_balancer=load_balancer_stack.alb,
        certificate_arn=environment_variables["CERTIFICATE_ARN"],
        health_check_path="/health",
    )
    app_stack.add_dependency(app_stack)

    return cdk_app


def main() -> None:  # pragma: no cover - thin wrapper
    """CLI entry point."""
    build_app().synth()


if __name__ == "__main__":  # pragma: no cover
    main()
