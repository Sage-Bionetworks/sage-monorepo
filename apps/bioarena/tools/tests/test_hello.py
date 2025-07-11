"""Hello unit test module."""

from bioarena_tools.hello import hello


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello bioarena-tools"
