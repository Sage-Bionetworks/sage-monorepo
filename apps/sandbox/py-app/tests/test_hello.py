"""Hello unit test module."""

from sandbox_py_app.hello import hello, hello_synapse_user


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello sandbox-py-lib"


def test_hello_synapse_user():
    assert hello_synapse_user() == "Hello None"
