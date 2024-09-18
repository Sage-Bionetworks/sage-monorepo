"""Hello unit test module."""

from sandbox_py_app.hello import hello


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello sandbox-py-app"
