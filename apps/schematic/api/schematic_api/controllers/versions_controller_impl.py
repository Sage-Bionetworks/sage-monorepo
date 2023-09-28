"Version endpoint controllers"

import importlib.metadata

from schematic_api.controllers.utils import handle_exceptions


@handle_exceptions
def get_schematic_version() -> str:
    """Gets the current schematic version

    Returns:
        str: The current schematic version
    """
    return importlib.metadata.version("schematicpy")
