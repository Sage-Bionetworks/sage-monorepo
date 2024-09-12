"""Hello unit test module."""

from schematic_api_next.hello import hello


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello schematic-api-next"
