"""Hello unit test module."""

from schematicpy.hello import hello


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello schematicpy"
