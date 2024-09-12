"""Hello unit test module."""

from python_app.hello import hello


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello python-app"
