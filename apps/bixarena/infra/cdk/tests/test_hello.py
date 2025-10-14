"""Hello unit test module."""

from bixarena_infra_cdk.hello import hello


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello bixarena-infra-cdk"
