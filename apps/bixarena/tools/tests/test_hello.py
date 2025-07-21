"""Hello unit test module."""

from bixarena_tools.hello import hello


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello bixarena-tools"
