"""Hello unit test module."""

from platform_infra_cdk_common.hello import hello


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello platform-infra-cdk-common"
