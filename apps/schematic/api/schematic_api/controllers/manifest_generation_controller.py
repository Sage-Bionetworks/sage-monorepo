import connexion
import six
from typing import Dict
from typing import Tuple
from typing import Union

from schematic_api.models.basic_error import BasicError  # noqa: E501
from schematic_api.models.google_sheet_links import GoogleSheetLinks  # noqa: E501
from schematic_api import util
from schematic_api.controllers import manifest_generation_controller_impl


def generate_excel_manifest(
    schema_url,
    dataset_id,
    add_annotations=None,
    manifest_title=None,
    data_type=None,
    display_label_type=None,
    use_strict_validation=None,
    asset_view_id=None,
):  # noqa: E501
    """Generates an excel file

    Generates an excel file # noqa: E501

    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param dataset_id: The ID of a dataset.
    :type dataset_id: str
    :param add_annotations: If true, annotations are added to the manifest
    :type add_annotations: bool
    :param manifest_title: If making one manifest, the title of the manifest. If making multiple manifests, the prefix of the title of the manifests.
    :type manifest_title: str
    :param data_type: A data type
    :type data_type: str
    :param display_label_type: The type of label to display
    :type display_label_type: str
    :param use_strict_validation: If true, users are blocked from entering incorrect values. If false, users will get a warning when using incorrect values.
    :type use_strict_validation: bool
    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str

    :rtype: Union[file, Tuple[file, int], Tuple[file, int, Dict[str, str]]
    """
    return manifest_generation_controller_impl.generate_excel_manifest(
        schema_url,
        dataset_id,
        add_annotations,
        manifest_title,
        data_type,
        display_label_type,
        use_strict_validation,
        asset_view_id,
    )


def generate_google_sheet_manifests(
    schema_url,
    add_annotations=None,
    dataset_id_array=None,
    manifest_title=None,
    data_type_array=None,
    display_label_type=None,
    use_strict_validation=None,
    asset_view_id=None,
    generate_all_manifests=None,
):  # noqa: E501
    """Generates a list of google sheet links

    Generates a list of google sheet links # noqa: E501

    :param schema_url: The URL of a schema in jsonld or csv form
    :type schema_url: str
    :param add_annotations: If true, annotations are added to the manifest
    :type add_annotations: bool
    :param dataset_id_array: An array of dataset ids
    :type dataset_id_array: List[str]
    :param manifest_title: If making one manifest, the title of the manifest. If making multiple manifests, the prefix of the title of the manifests.
    :type manifest_title: str
    :param data_type_array: An array of data types
    :type data_type_array: List[str]
    :param display_label_type: The type of label to display
    :type display_label_type: str
    :param use_strict_validation: If true, users are blocked from entering incorrect values. If false, users will get a warning when using incorrect values.
    :type use_strict_validation: bool
    :param asset_view_id: ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
    :type asset_view_id: str
    :param generate_all_manifests: If true, a manifest for all components will be generated, datasetIds will be ignored. If false, manifests for each id in datasetIds will be generated.
    :type generate_all_manifests: bool

    :rtype: Union[GoogleSheetLinks, Tuple[GoogleSheetLinks, int], Tuple[GoogleSheetLinks, int, Dict[str, str]]
    """
    return manifest_generation_controller_impl.generate_google_sheet_manifests(
        schema_url,
        add_annotations,
        dataset_id_array,
        manifest_title,
        data_type_array,
        display_label_type,
        use_strict_validation,
        asset_view_id,
        generate_all_manifests,
    )
