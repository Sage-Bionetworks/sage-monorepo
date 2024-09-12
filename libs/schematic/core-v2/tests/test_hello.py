"""Hello unit test module."""

from schematiccorev2.hello import hello


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello schematic-core-v2"
