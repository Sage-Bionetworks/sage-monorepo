"""Hello unit test module."""

from schematic_core.hello import hello


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello schematic-core"
