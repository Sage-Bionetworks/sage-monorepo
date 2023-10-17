"""Tangled tree controllers"""

from schematic.visualization.tangled_tree import TangledTree  # type: ignore

from schematic_api.controllers.utils import (
    handle_exceptions,
    download_schema_file_as_jsonld,
)


@handle_exceptions
def get_tangled_tree_layers(schema_url: str, figure_type: str) -> str:
    """Gets layers for a tangled tree visualization.

    Args:
        schema_url (str): The URL to the schema file
        figure_type (str): Figure type to generate.

    Returns:
        str: A json in string form that represents the layers for a single tangled tree
    """
    schema_path = download_schema_file_as_jsonld(schema_url)
    tangled_tree = TangledTree(schema_path, figure_type)
    layers = tangled_tree.get_tangled_tree_layers(save_file=False)
    if len(layers) == 0:
        raise ValueError("TangledTree.get_tangled_tree_layers() returned an empty list")
    return layers[0]


@handle_exceptions
def get_tangled_tree_text(schema_url: str, figure_type: str, text_format: str) -> str:
    """Gets text for a tangled tree visualization.

    Args:
        schema_url (str): The URL to the schema file
        figure_type (str): Figure type to generate.
        text_format (str):  Determines the type of text rendering to return

    Returns:
        str: A csv in string form
    """
    schema_path = download_schema_file_as_jsonld(schema_url)
    tangled_tree = TangledTree(schema_path, figure_type)
    csv_string = tangled_tree.get_text_for_tangled_tree(text_format, save_file=False)
    return csv_string
