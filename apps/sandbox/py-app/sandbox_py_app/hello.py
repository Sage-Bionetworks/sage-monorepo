import sandbox_py_lib.hello

"""Sample Hello World application."""


def hello():
    """Return a friendly greeting."""
    return sandbox_py_lib.hello.hello()


def hello_synapse_user():
    return sandbox_py_lib.hello.hello_synapse_user()


print(hello_synapse_user())