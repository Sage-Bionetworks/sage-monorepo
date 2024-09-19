"""Hello unit test module."""

from sandbox_py_app.hello import hello, hello_lib, hello_numpy


def test_hello():
    """Test the hello function."""
    assert hello() == "Hello sandbox-py-app"


def test_hello_lib():
    assert hello_lib() == "Hello sandbox-py-lib"


def test_hello_numpy():
    assert hello_numpy() == "Hello Numpy"
