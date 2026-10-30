import json

import pytest

from explorers_data_loader.collection_config import (
    DATA_VERSION_COLLECTION_NAME,
    CollectionConfig,
    CollectionConfigError,
    read_collection_configs,
)

MODEL_AD_UI_CONFIG = {
    "name": "ui_config",
    "file": "ui_config.json",
    "indexes": [{"page": 1}],
    "maintainInsertionOrder": True,
}
MODEL_AD_RNA_DE_AGGREGATE = {
    "name": "rna_de_aggregate",
    "file": "rna_de_aggregate.json",
    "indexes": [{"ensembl_gene_id": 1}],
    "collatedIndexes": [
        {"tissue": 1, "ensembl_gene_id": 1, "name.link_text": 1, "sex": 1}
    ],
}


def write_collections(tmp_path, entries):
    collections_filepath = tmp_path / "collections.json"
    collections_filepath.write_text(json.dumps(entries))
    return str(collections_filepath)


def read_problems(tmp_path, entries):
    with pytest.raises(CollectionConfigError) as error:
        read_collection_configs(write_collections(tmp_path, entries))
    return str(error.value)


def test_read_collection_configs_keeps_the_file_order_and_every_setting(tmp_path):
    configs = read_collection_configs(
        write_collections(tmp_path, [MODEL_AD_RNA_DE_AGGREGATE, MODEL_AD_UI_CONFIG])
    )

    assert configs == [
        CollectionConfig(
            name="rna_de_aggregate",
            file="rna_de_aggregate.json",
            indexes=({"ensembl_gene_id": 1},),
            collated_indexes=(
                {"tissue": 1, "ensembl_gene_id": 1, "name.link_text": 1, "sex": 1},
            ),
        ),
        CollectionConfig(
            name="ui_config",
            file="ui_config.json",
            indexes=({"page": 1},),
            maintain_insertion_order=True,
        ),
    ]


def test_read_collection_configs_keeps_compound_index_field_order(tmp_path):
    (config,) = read_collection_configs(
        write_collections(tmp_path, [MODEL_AD_RNA_DE_AGGREGATE])
    )

    assert list(config.collated_indexes[0]) == [
        "tissue",
        "ensembl_gene_id",
        "name.link_text",
        "sex",
    ]


def test_read_collection_configs_rejects_a_listed_dataversion(tmp_path):
    problems = read_problems(
        tmp_path,
        [
            MODEL_AD_UI_CONFIG,
            {"name": DATA_VERSION_COLLECTION_NAME, "file": "dataversion.json"},
        ],
    )

    assert f"'{DATA_VERSION_COLLECTION_NAME}' must not be listed" in problems


def test_read_collection_configs_reports_every_problem_at_once(tmp_path):
    problems = read_problems(
        tmp_path,
        [
            {"name": "ui_config", "file": ""},
            {"name": "ui_config", "file": "ui_config.json", "collatedIndex": []},
            {"name": "genes", "file": "genes.json", "indexes": [{"name": True}]},
            {
                "name": "model_details",
                "file": "model_details.json",
                "maintainInsertionOrder": "yes",
            },
        ],
    )

    assert "entry 1 (ui_config): 'file' must be a non-empty string" in problems
    assert "entry 2 (ui_config): unknown key 'collatedIndex'" in problems
    assert "entry 3 (genes): 'indexes' must be a list of indexes" in problems
    assert (
        "entry 4 (model_details): 'maintainInsertionOrder' must be true or false"
        in problems
    )
    assert "'ui_config' is listed more than once" in problems


def test_read_collection_configs_requires_an_array(tmp_path):
    with pytest.raises(CollectionConfigError, match="must hold a JSON array"):
        read_collection_configs(write_collections(tmp_path, MODEL_AD_UI_CONFIG))


def test_read_collection_configs_accepts_an_empty_release(tmp_path):
    assert read_collection_configs(write_collections(tmp_path, [])) == []


def test_read_collection_configs_reports_a_name_that_is_not_a_string(tmp_path):
    problems = read_problems(tmp_path, [{"name": ["ui_config"], "file": "ui.json"}])

    assert "entry 1 (unnamed): 'name' must be a non-empty string" in problems
