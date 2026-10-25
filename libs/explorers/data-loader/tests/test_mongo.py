import json
import logging
from unittest import mock

import pytest

from explorers_data_loader.mongo import (
    CASE_INSENSITIVE_COLLATION,
    DATA_VERSION_COLLECTION_NAME,
    create_collections_indexes,
    import_collection,
    read_collections,
    read_imported_document_count,
)
from explorers_data_loader.settings import DatabaseSettings, ProductSettings
from explorers_data_loader.staging import staging_collection_name

AGORA_DB_SETTINGS = DatabaseSettings(
    user="root", password="password", name="agora", host="agora-mongo", port="27017"
)
AGORA_PRODUCT = ProductSettings(
    name="agora", insertion_ordered_collection_name="uiconfig"
)
DOWNLOAD_DIR = "/app/local/data"
RNA_DE_INDIVIDUAL_DOCUMENTS = 1169360
MONGOIMPORT_SUMMARY = (
    "2026-10-25T06:34:16.000+0000\tconnected to: mongodb://agora-mongo:27017\n"
    f"2026-10-25T06:35:21.000+0000\t{RNA_DE_INDIVIDUAL_DOCUMENTS} document(s) "
    "imported successfully. 0 document(s) failed to import.\n"
)
MONGOIMPORT_ERROR = "error connecting to host: connection refused"


def write_collections_csv(tmp_path, rows):
    collections_filepath = tmp_path / "collections.csv"
    collections_filepath.write_text("".join(f"{name},{file}\n" for name, file in rows))
    return str(collections_filepath)


def run_import(
    collection_name, collection_filename, returncode=0, stderr=MONGOIMPORT_SUMMARY
):
    with mock.patch(
        "explorers_data_loader.mongo.subprocess.run",
        return_value=mock.Mock(returncode=returncode, stderr=stderr),
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


def test_import_collection_writes_only_to_the_staging_collection():
    command = run_import("genes", "rnaseq_differential_expression.json")

    assert f"--collection={staging_collection_name('genes')}" in command
    assert "--collection=genes" not in command


def test_import_collection_raises_when_mongoimport_fails():
    with pytest.raises(Exception, match="Failed to import genes"):
        run_import(
            "genes",
            "rnaseq_differential_expression.json",
            returncode=1,
            stderr=MONGOIMPORT_ERROR,
        )


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
    geneinfo, druginfo = mock.Mock(), mock.Mock()
    collections = {
        staging_collection_name("geneinfo"): geneinfo,
        staging_collection_name("druginfo"): druginfo,
    }
    db = mock.Mock(get_collection=collections.__getitem__)

    create_collections_indexes(db, str(indexes_filepath))

    assert geneinfo.create_index.call_args_list == [
        mock.call([("hgnc_symbol", 1)]),
        mock.call(
            [("hgnc_symbol", 1), ("ensembl_gene_id", 1)],
            collation=CASE_INSENSITIVE_COLLATION,
        ),
    ]
    assert druginfo.create_index.call_args_list == [
        mock.call([("common_name", 1)], collation=CASE_INSENSITIVE_COLLATION),
    ]


def test_import_collection_logs_the_imported_document_count(caplog):
    with caplog.at_level(logging.INFO, logger="explorers_data_loader"):
        run_import("rna_de_individual", "rna_de_individual.json")

    (record,) = [r for r in caplog.records if r.event == "collection_imported"]
    assert record.fields["collection"] == "rna_de_individual"
    assert record.fields["staging_collection"] == staging_collection_name(
        "rna_de_individual"
    )
    assert record.fields["file"] == "rna_de_individual.json"
    assert record.fields["documents"] == RNA_DE_INDIVIDUAL_DOCUMENTS
    assert isinstance(record.fields["duration_ms"], int)


def test_import_collection_logs_mongoimport_errors_as_a_failed_import(caplog):
    with (
        caplog.at_level(logging.INFO, logger="explorers_data_loader"),
        pytest.raises(Exception, match="Failed to import genes"),
    ):
        run_import(
            "genes",
            "rnaseq_differential_expression.json",
            returncode=1,
            stderr=MONGOIMPORT_ERROR,
        )

    (record,) = [
        r
        for r in caplog.records
        if getattr(r, "event", None) == "collection_import_failed"
    ]
    assert record.levelno == logging.ERROR
    assert record.fields["collection"] == "genes"
    assert record.fields["error"] == MONGOIMPORT_ERROR


def test_read_imported_document_count_is_none_without_a_summary_line():
    assert read_imported_document_count(MONGOIMPORT_ERROR) is None
