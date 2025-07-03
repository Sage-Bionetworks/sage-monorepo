"""
Core functionality for the DataModelToolkit.

This module contains the main functions and classes for the toolkit.
"""

from typing import Optional


def hello_world(name: Optional[str] = None) -> str:
    """
    A simple hello world function.

    Args:
        name: Optional name to include in the greeting. If None, uses "World".

    Returns:
        A greeting message string.

    Examples:
        >>> hello_world()
        'Hello, World! Welcome to DataModelToolkit!'

        >>> hello_world("Alice")
        'Hello, Alice! Welcome to DataModelToolkit!'
    """
    if name is None:
        name = "World"
    return f"Hello, {name}! Welcome to DataModelToolkit!"


def get_version() -> str:
    """
    Get the version of the DataModelToolkit.

    Returns:
        The version string.
    """
    from . import __version__

    return __version__
