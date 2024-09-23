import sandbox_py_lib.hello

"""Sample Hello World application."""


def hello():
    """Return a friendly greeting."""
    return "Hello sandbox-py-app"


def hello_lib():
    return sandbox_py_lib.hello.hello()


def hello_numpy():
    return sandbox_py_lib.hello.hello_numpy()
