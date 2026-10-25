import json
from unittest import mock

import pytest

from explorers_data_loader.mongo import (
    CASE_INSENSITIVE_COLLATION,
    DATA_VERSION_COLLECTION_NAME,
    create_collections_indexes,
    import_collection,
    read_collections,
)
from explorers_data_loader.settings import DatabaseSettings, ProductSettings

AGORA_DB_SETTINGS = DatabaseSettings(
    user="root", password="changeme", name="agora", host="agora-mongo", port="27017"
)
AGORA_PRODUCT = ProductSettings(insertion_ordered_collection_name="uiconfig")
DOWNLOAD_DIR = "/app/local/data"


def write_collections_csv(tmp_path, rows):
    collections_filepath = tmp_path / "collections.csv"
    collections_filepath.write_text("".join(f"{name},{file}\n" for name, file in rows))
    return str(collections_filepath)


def run_import(collection_name, collection_filename, returncode=0):
    with mock.patch(
        "explorers_data_loader.mongo.subprocess.run",
        return_value=mock.Mock(returncode=returncode, stderr="connection refused"),
    ) as run:
        import_collection(
            AGORA_DB_SETTINGS,
            AGORA_PRODUCT,
            collection_name,
            collection_filename,
            DOWNLOAD_DIR,
        )
    return run.call_args.args[0]


def test_read_collections_returns_name_and_file_pairs(tmp_path):
    rows = [
        ("genes", "rnaseq_differential_expression.json"),
        ("uiconfig", "ui_config.json"),
    ]

    assert read_collections(write_collections_csv(tmp_path, rows)) == rows


def test_read_collections_rejects_a_listed_dataversion(tmp_path):
    rows = [
        ("genes", "rnaseq_differential_expression.json"),
        (DATA_VERSION_COLLECTION_NAME, "dataversion.json"),
    ]

    with pytest.raises(ValueError, match="must not be listed"):
        read_collections(write_collections_csv(tmp_path, rows))


def test_import_collection_keeps_insertion_order_for_the_configured_collection():
    command = run_import("uiconfig", "ui_config.json")

    assert "--maintainInsertionOrder" in command
    assert "--numInsertionWorkers=4" not in command


def test_import_collection_uses_parallel_workers_for_other_collections():
    command = run_import("genes", "rnaseq_differential_expression.json")

    assert "--numInsertionWorkers=4" in command
    assert "--maintainInsertionOrder" not in command
    assert f"--uri={AGORA_DB_SETTINGS.import_uri}" in command
    assert f"--file={DOWNLOAD_DIR}/rnaseq_differential_expression.json" in command


def test_import_collection_raises_when_mongoimport_fails():
    with pytest.raises(Exception, match="Failed to import genes"):
        run_import("genes", "rnaseq_differential_expression.json", returncode=1)


def test_create_collections_indexes_creates_plain_and_collated_indexes(tmp_path):
    indexes_filepath = tmp_path / "collections-indexes.json"
    indexes_filepath.write_text(
        json.dumps(
            [
                {
                    "name": "geneinfo",
                    "indexes": [{"hgnc_symbol": 1}],
                    "collatedIndexes": [{"hgnc_symbol": 1, "ensembl_gene_id": 1}],
                },
                {"name": "druginfo", "collatedIndexes": [{"common_name": 1}]},
            ]
        )
    )
    collections = {"geneinfo": mock.Mock(), "druginfo": mock.Mock()}
    db = mock.Mock(get_collection=collections.__getitem__)

    create_collections_indexes(db, str(indexes_filepath))

    assert collections["geneinfo"].create_index.call_args_list == [
        mock.call([("hgnc_symbol", 1)]),
        mock.call(
            [("hgnc_symbol", 1), ("ensembl_gene_id", 1)],
            collation=CASE_INSENSITIVE_COLLATION,
        ),
    ]
    assert collections["druginfo"].create_index.call_args_list == [
        mock.call([("common_name", 1)], collation=CASE_INSENSITIVE_COLLATION),
    ]
