"""Hello unit test module."""

from openchallenges_api_client_python.hello import hello


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello openchallenges-api-client-python"
