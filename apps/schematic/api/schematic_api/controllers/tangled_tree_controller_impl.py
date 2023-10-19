"""Tangled tree controllers"""

from typing import Literal

from schematic.visualization.tangled_tree import TangledTree  # type: ignore

from schematic_api.controllers.utils import (
    handle_endpoint_status,
    download_schema_file_as_jsonld,
)


@handle_endpoint_status
def get_tangled_tree_layers(
    schema_url: str, figure_type: Literal["component", "dependency"] = "component"
) -> str:
    """Gets layers for a tangled tree visualization.

    Args:
        schema_url (str): The URL to the schema file
        figure_type (Literal["component", "dependency"]): Figure type to generate.

    Returns:
        str: A json in string form that represents the layers for a single
            tangled tree
    """
    schema_path = download_schema_file_as_jsonld(schema_url)
    tangled_tree = TangledTree(schema_path, figure_type)
    # Currently TangledTree.get_tangled_tree_layers() returns either an empty list if
    # save_file=False or a list of one string if save_file=False.
    # The API should output just the string.
    # TangledTree.get_tangled_tree_layers() will likely get changed in the future to return
    # just a string.
    layers_list: list[str] = tangled_tree.get_tangled_tree_layers(save_file=False)
    if len(layers_list) == 0:
        raise ValueError("TangledTree.get_tangled_tree_layers() returned an empty list")
    return layers_list[0]


@handle_endpoint_status
def get_tangled_tree_text(
    schema_url: str,
    figure_type: Literal["component", "dependency"] = "component",
    text_format: Literal["plain", "highlighted"] = "plain",
) -> str:
    """Gets text for a tangled tree visualization.

    Args:
        schema_url (str): The URL to the schema file
        figure_type (Literal["component", "dependency"]): Figure type to generate.
        text_format (Literal["plain", "highlighted"]):  Determines the type of text
          rendering to return

      Returns:
        str: A csv in string form
    """
    schema_path = download_schema_file_as_jsonld(schema_url)
    tangled_tree = TangledTree(schema_path, figure_type)
    return tangled_tree.get_text_for_tangled_tree(text_format, save_file=False)
