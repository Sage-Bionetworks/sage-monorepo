"""Sample Hello World application."""

import synapseclient
import numpy as np

syn = synapseclient.Synapse()


def hello():
    """Return a friendly greeting."""
    return "Hello sandbox-py-lib"


def hello_synapse_user():
    return f"Hello {syn.username}"


def hello_numpy():
    return f"Hello {np.strings.capitalize('numpy')}"
