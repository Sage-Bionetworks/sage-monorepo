"""Sample Hello World application."""

import numpy as np


def hello():
    """Return a friendly greeting."""
    return "Hello sandbox-py-lib"


def hello_numpy():
    return f"Hello {np.strings.capitalize('numpy')}"
