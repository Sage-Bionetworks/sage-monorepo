"""Hello unit test module."""

from sandbox_py_lib_a.hello import hello


def test_hello():
    """Test the hello function."""
    result = hello()
    assert result.startswith("Hello sandbox-py-lib-b from ")
