import aws_cdk as core
import aws_cdk.assertions as assertions

from bixarena_infra_cdk.network_stack import NetworkStack


def test_vpc_created():
    app = core.App()
    vpc_cidr = "10.254.192.0/24"
    network = NetworkStack(app, "NetworkStack", vpc_cidr)
    template = assertions.Template.from_stack(network)
    template.has_resource_properties("AWS::EC2::VPC", {"CidrBlock": vpc_cidr})
