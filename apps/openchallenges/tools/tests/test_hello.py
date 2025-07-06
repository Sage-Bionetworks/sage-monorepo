"""Hello unit test module."""

from openchallenges_tools.hello import hello


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello openchallenges-tools"
