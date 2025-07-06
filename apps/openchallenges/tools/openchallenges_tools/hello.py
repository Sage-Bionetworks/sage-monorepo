"""Sample Hello World application."""

from openchallenges_api_client_python.hello import hello as api_hello


def hello() -> str:
    """Return a friendly greeting."""
    return "Hello openchallenges-tools"


def hello_lib() -> str:
    """Return greeting from the API client library."""
    return api_hello()


if __name__ == "__main__":
    print(hello())
    print(hello_lib())
