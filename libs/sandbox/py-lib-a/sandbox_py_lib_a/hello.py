"""Sample Hello World application."""

from sandbox_py_lib_b.hello import hello as lib_b_hello


def hello():
    """Return a friendly greeting."""
    return lib_b_hello()


if __name__ == "__main__":
    print(hello())
