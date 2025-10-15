from collections.abc import Sequence
from dataclasses import dataclass

from aws_cdk import aws_ecs as ecs

CONTAINER_LOCATION_PATH_ID = "path://"


@dataclass
class ServiceSecret:
    """Configuration for a secret used in the container."""

    secret_name: str
    environment_key: str


@dataclass
class ContainerVolume:
    """Configuration for a volume used by the container."""

    path: str
    size: int = 15
    read_only: bool = False


class ServiceProps:
    """ECS service properties container."""

    def __init__(
        self,
        container_name: str,
        container_location: str,
        container_port: int,
        container_env_vars: dict | None = None,
        container_secrets: list[ServiceSecret] | None = None,
        container_volumes: list[ContainerVolume] | None = None,
        ecs_task_cpu: int = 256,
        ecs_task_memory: int = 512,
        auto_scale_min_capacity: int = 1,
        auto_scale_max_capacity: int = 1,
        container_command: Sequence[str] | None = None,
        container_healthcheck: ecs.HealthCheck | None = None,
    ) -> None:
        self.container_name = container_name
        self.container_port = container_port
        self.ecs_task_cpu = ecs_task_cpu
        self.ecs_task_memory = ecs_task_memory
        if CONTAINER_LOCATION_PATH_ID in container_location:
            container_location = container_location.removeprefix(
                CONTAINER_LOCATION_PATH_ID
            )
        self.container_location = container_location

        self.container_env_vars = container_env_vars or {}
        self.container_secrets = container_secrets or []
        self.container_volumes = container_volumes or []

        self.auto_scale_min_capacity = auto_scale_min_capacity
        self.auto_scale_max_capacity = auto_scale_max_capacity
        self.container_command = container_command
        self.container_healthcheck = container_healthcheck
