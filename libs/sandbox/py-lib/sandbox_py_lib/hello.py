"""Sample Hello World application."""

import synapseclient

syn = synapseclient.Synapse()


def hello():
    """Return a friendly greeting."""
    return "Hello sandbox-py-lib"


def hello_synapse_user():
    return f"Hello {syn.username}"
