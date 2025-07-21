"""Hello unit test module."""

from sandbox_py_lib_b.hello import hello


def test_hello():
    """Test the hello function returns a greeting that starts with 'Hello from '."""
    result = hello()
    assert result.startswith("Hello sandbox-py-lib-b from ")
