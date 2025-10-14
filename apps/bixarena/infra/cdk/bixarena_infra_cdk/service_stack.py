import aws_cdk as cdk
from aws_cdk import Duration as duration
from aws_cdk import Size as size
from aws_cdk import aws_certificatemanager as acm
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_ecs as ecs
from aws_cdk import aws_elasticloadbalancingv2 as elbv2
from aws_cdk import aws_iam as iam
from aws_cdk import aws_logs as logs
from aws_cdk import aws_secretsmanager as sm
from constructs import Construct

from .service_props import ServiceProps

ALB_HTTP_LISTENER_PORT = 80
ALB_HTTPS_LISTENER_PORT = 443


class ServiceStack(cdk.Stack):
    """ECS Service stack."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        vpc: ec2.Vpc,
        cluster: ecs.Cluster,
        props: ServiceProps,
        **kwargs,
    ) -> None:
        super().__init__(scope, construct_id, **kwargs)

        # allow containers default task access and s3 bucket access
        task_role = iam.Role(
            self,
            "TaskRole",
            assumed_by=iam.ServicePrincipal("ecs-tasks.amazonaws.com"),
            managed_policies=[
                iam.ManagedPolicy.from_aws_managed_policy_name("AmazonS3FullAccess"),
            ],
        )
        task_role.add_to_policy(
            iam.PolicyStatement(
                actions=[
                    "logs:CreateLogStream",
                    "logs:DescribeLogGroups",
                    "logs:DescribeLogStreams",
                    "logs:PutLogEvents",
                    "ssmmessages:CreateControlChannel",
                    "ssmmessages:CreateDataChannel",
                    "ssmmessages:OpenControlChannel",
                    "ssmmessages:OpenDataChannel",
                ],
                resources=["*"],
                effect=iam.Effect.ALLOW,
            )
        )

        # default ECS execution policy
        execution_role = iam.Role(
            self,
            "ExecutionRole",
            assumed_by=iam.ServicePrincipal("ecs-tasks.amazonaws.com"),
            managed_policies=[
                iam.ManagedPolicy.from_aws_managed_policy_name(
                    "service-role/AmazonECSTaskExecutionRolePolicy"
                ),
            ],
        )
        execution_role.add_to_policy(
            iam.PolicyStatement(
                actions=[
                    "logs:CreateLogStream",
                    "logs:PutLogEvents",
                ],
                resources=["*"],
                effect=iam.Effect.ALLOW,
            )
        )

        # ECS task with fargate
        self.task_definition = ecs.FargateTaskDefinition(
            self,
            "TaskDef",
            cpu=props.ecs_task_cpu,
            memory_limit_mib=props.ecs_task_memory,
            task_role=task_role,
            execution_role=execution_role,
        )

        image = ecs.ContainerImage.from_registry(props.container_location)
        if "path://" in props.container_location:  # build container from source
            location = props.container_location.removeprefix("path://")
            image = ecs.ContainerImage.from_asset(location)

        def _get_secret(scope: Construct, id: str, name: str) -> sm.Secret:
            """Get a secret from the AWS secrets manager."""
            isecret = sm.Secret.from_secret_name_v2(scope, id, name)
            return ecs.Secret.from_secrets_manager(isecret)

        secrets: dict[str, ecs.Secret] = {}
        for secret in props.container_secrets:
            secrets[secret.environment_key] = _get_secret(
                self, f"sm-secrets-{secret.environment_key}", secret.secret_name
            )

        self.container = self.task_definition.add_container(
            props.container_name,
            image=image,
            environment=props.container_env_vars,
            secrets=secrets,
            port_mappings=[
                ecs.PortMapping(
                    name=props.container_name,
                    container_port=props.container_port,
                    protocol=ecs.Protocol.TCP,
                )
            ],
            logging=ecs.LogDrivers.aws_logs(
                stream_prefix=f"{construct_id}",
                log_retention=logs.RetentionDays.FOUR_MONTHS,
            ),
            command=props.container_command,
            health_check=props.container_healthcheck,
        )

        self.security_group = ec2.SecurityGroup(self, "SecurityGroup", vpc=vpc)
        self.security_group.add_ingress_rule(
            peer=ec2.Peer.ipv4("0.0.0.0/0"),
            connection=ec2.Port.tcp(props.container_port),
        )

        # attach ECS task to ECS cluster
        self.service = ecs.FargateService(
            self,
            "Service",
            cluster=cluster,
            task_definition=self.task_definition,
            enable_execute_command=True,
            circuit_breaker=ecs.DeploymentCircuitBreaker(enable=True, rollback=True),
            security_groups=([self.security_group]),
            service_connect_configuration=ecs.ServiceConnectProps(
                log_driver=ecs.LogDrivers.aws_logs(stream_prefix=f"{construct_id}"),
                services=[
                    ecs.ServiceConnectService(
                        port_mapping_name=props.container_name,
                        port=props.container_port,
                        dns_name=props.container_name,
                    )
                ],
            ),
            capacity_provider_strategies=[
                ecs.CapacityProviderStrategy(
                    capacity_provider="FARGATE",
                    base=1,
                ),
                ecs.CapacityProviderStrategy(
                    capacity_provider="FARGATE_SPOT",
                    weight=1,
                ),
            ],
        )

        scaling = self.service.auto_scale_task_count(
            min_capacity=props.auto_scale_min_capacity,
            max_capacity=props.auto_scale_max_capacity,
        )
        scaling.scale_on_cpu_utilization(
            "CpuScaling",
            target_utilization_percent=50,
        )
        scaling.scale_on_memory_utilization(
            "MemoryScaling",
            target_utilization_percent=50,
        )

        for container_volume in props.container_volumes:
            service_volume = ecs.ServiceManagedVolume(
                self,
                "ContainerVolume",
                name=props.container_name,
                managed_ebs_volume=ecs.ServiceManagedEBSVolumeConfiguration(
                    size=size.gibibytes(container_volume.size),
                    volume_type=ec2.EbsDeviceVolumeType.GP3,
                ),
            )

            self.task_definition.add_volume(
                name=props.container_name, configured_at_launch=True
            )
            self.service.add_volume(service_volume)

            service_volume.mount_in(
                self.container,
                container_path=container_volume.path,
                read_only=container_volume.read_only,
            )


class LoadBalancedServiceStack(ServiceStack):
    """Service stack fronted by a load balancer."""

    def __init__(
        self,
        scope: Construct,
        construct_id: str,
        vpc: ec2.Vpc,
        cluster: ecs.Cluster,
        props: ServiceProps,
        load_balancer: elbv2.ApplicationLoadBalancer,
        certificate_arn: str,
        health_check_path: str = "/",
        health_check_interval: int = 1,
        **kwargs,
    ) -> None:
        super().__init__(scope, construct_id, vpc, cluster, props, **kwargs)

        # ACM Certificate for HTTPS
        self.cert = acm.Certificate.from_certificate_arn(
            self, "Cert", certificate_arn=certificate_arn
        )

        https_listener = elbv2.ApplicationListener(
            self,
            "HttpsListener",
            load_balancer=load_balancer,
            port=ALB_HTTPS_LISTENER_PORT,
            open=True,
            protocol=elbv2.ApplicationProtocol.HTTPS,
            certificates=[self.cert],
        )

        https_listener.add_targets(
            "HttpsTarget",
            port=props.container_port,
            protocol=elbv2.ApplicationProtocol.HTTP,
            targets=[self.service],
            health_check=elbv2.HealthCheck(
                path=health_check_path, interval=duration.minutes(health_check_interval)
            ),
        )

        http_listener = elbv2.ApplicationListener(
            self,
            "HttpListener",
            load_balancer=load_balancer,
            port=ALB_HTTP_LISTENER_PORT,
            open=True,
            protocol=elbv2.ApplicationProtocol.HTTP,
        )

        http_listener.add_action(
            "HttpRedirect",
            action=elbv2.ListenerAction.redirect(
                port=str(ALB_HTTPS_LISTENER_PORT),
                protocol=(elbv2.ApplicationProtocol.HTTPS).value,
                permanent=True,
            ),
        )
