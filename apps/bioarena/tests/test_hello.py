"""Hello unit test module."""

from fastchat.hello import hello


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello fastchat"
