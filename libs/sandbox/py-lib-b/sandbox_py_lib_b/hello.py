"""Sample Hello World application."""

from faker import Faker

fake = Faker()


def hello():
    """Return a friendly greeting."""
    name = fake.name()
    return f"Hello sandbox-py-lib-b from {name}"
