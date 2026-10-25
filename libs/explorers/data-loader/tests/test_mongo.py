import logging
import os
import stat
from unittest import mock

import pytest

from explorers_data_loader.collection_config import CollectionConfig
from explorers_data_loader.mongo import (
    CASE_INSENSITIVE_COLLATION,
    create_collections_indexes,
    import_collection,
    read_imported_document_count,
)
from explorers_data_loader.settings import DatabaseSettings
from explorers_data_loader.staging import staging_collection_name

AGORA_DB_SETTINGS = DatabaseSettings(
    user="root", password="password", name="agora", host="agora-mongo", port="27017"
)
GENES = CollectionConfig(name="genes", file="rnaseq_differential_expression.json")
UI_CONFIG = CollectionConfig(
    name="uiconfig", file="ui_config.json", maintain_insertion_order=True
)
RNA_DE_INDIVIDUAL = CollectionConfig(
    name="rna_de_individual", file="rna_de_individual.json"
)
DOWNLOAD_DIR = "/app/local/data"
RNA_DE_INDIVIDUAL_DOCUMENTS = 1169360
MONGOIMPORT_SUMMARY = (
    "2026-10-25T06:34:16.000+0000\tconnected to: mongodb://agora-mongo:27017\n"
    f"2026-10-25T06:35:21.000+0000\t{RNA_DE_INDIVIDUAL_DOCUMENTS} document(s) "
    "imported successfully. 0 document(s) failed to import.\n"
)
MONGOIMPORT_ERROR = "error connecting to host: connection refused"


def run_import(collection, returncode=0, stderr=MONGOIMPORT_SUMMARY):
    with mock.patch(
        "explorers_data_loader.mongo.subprocess.run",
        return_value=mock.Mock(returncode=returncode, stderr=stderr),
    ) as run:
        import_collection(AGORA_DB_SETTINGS, collection, DOWNLOAD_DIR)
    return run.call_args.args[0]


def test_import_collection_keeps_insertion_order_when_the_collection_asks_for_it():
    command = run_import(UI_CONFIG)

    assert "--maintainInsertionOrder" in command
    assert "--numInsertionWorkers=4" not in command


def test_import_collection_uses_parallel_workers_for_other_collections():
    command = run_import(GENES)

    assert "--numInsertionWorkers=4" in command
    assert "--maintainInsertionOrder" not in command
    assert f"--file={DOWNLOAD_DIR}/{GENES.file}" in command


def test_import_collection_writes_only_to_the_staging_collection():
    command = run_import(GENES)

    assert f"--collection={staging_collection_name(GENES.name)}" in command
    assert f"--collection={GENES.name}" not in command


def test_import_collection_raises_when_mongoimport_fails():
    with pytest.raises(Exception, match="Failed to import genes"):
        run_import(GENES, returncode=1, stderr=MONGOIMPORT_ERROR)


def test_create_collections_indexes_creates_plain_and_collated_indexes():
    geneinfo = CollectionConfig(
        name="geneinfo",
        file="gene_info.json",
        indexes=({"hgnc_symbol": 1},),
        collated_indexes=({"hgnc_symbol": 1, "ensembl_gene_id": 1},),
    )
    druginfo = CollectionConfig(
        name="druginfo", file="drug_info.json", collated_indexes=({"common_name": 1},)
    )
    geneinfo_staging, druginfo_staging = mock.Mock(), mock.Mock()
    staging_collections = {
        staging_collection_name("geneinfo"): geneinfo_staging,
        staging_collection_name("druginfo"): druginfo_staging,
    }
    db = mock.Mock(get_collection=staging_collections.__getitem__)

    # GENES has no indexes, so it isn't looked up at all
    create_collections_indexes(db, [geneinfo, druginfo, GENES])

    assert geneinfo_staging.create_index.call_args_list == [
        mock.call([("hgnc_symbol", 1)]),
        mock.call(
            [("hgnc_symbol", 1), ("ensembl_gene_id", 1)],
            collation=CASE_INSENSITIVE_COLLATION,
        ),
    ]
    assert druginfo_staging.create_index.call_args_list == [
        mock.call([("common_name", 1)], collation=CASE_INSENSITIVE_COLLATION),
    ]


def test_import_collection_logs_the_imported_document_count(caplog):
    with caplog.at_level(logging.INFO, logger="explorers_data_loader"):
        run_import(RNA_DE_INDIVIDUAL)

    (record,) = [r for r in caplog.records if r.event == "collection_imported"]
    assert record.fields["collection"] == RNA_DE_INDIVIDUAL.name
    assert record.fields["staging_collection"] == staging_collection_name(
        RNA_DE_INDIVIDUAL.name
    )
    assert record.fields["file"] == RNA_DE_INDIVIDUAL.file
    assert record.fields["documents"] == RNA_DE_INDIVIDUAL_DOCUMENTS
    assert isinstance(record.fields["duration_ms"], int)


def test_import_collection_logs_mongoimport_errors_as_a_failed_import(caplog):
    with (
        caplog.at_level(logging.INFO, logger="explorers_data_loader"),
        pytest.raises(Exception, match="Failed to import genes"),
    ):
        run_import(GENES, returncode=1, stderr=MONGOIMPORT_ERROR)

    (record,) = [
        r
        for r in caplog.records
        if getattr(r, "event", None) == "collection_import_failed"
    ]
    assert record.levelno == logging.ERROR
    assert record.fields["collection"] == GENES.name
    assert record.fields["error"] == MONGOIMPORT_ERROR


def test_read_imported_document_count_is_none_without_a_summary_line():
    assert read_imported_document_count(MONGOIMPORT_ERROR) is None


def test_import_collection_keeps_the_password_off_the_command_line():
    command = run_import(GENES)

    assert not any(AGORA_DB_SETTINGS.password in argument for argument in command)
    assert any(argument.startswith("--config=") for argument in command)


def test_import_collection_reads_the_uri_from_a_private_config_file_it_removes():
    seen = {}

    def inspect_config_file(command, **_):
        config_path = next(
            argument.removeprefix("--config=")
            for argument in command
            if argument.startswith("--config=")
        )
        seen["path"] = config_path
        seen["mode"] = stat.S_IMODE(os.stat(config_path).st_mode)
        with open(config_path) as config_file:
            seen["contents"] = config_file.read()
        return mock.Mock(returncode=0, stderr=MONGOIMPORT_SUMMARY)

    with mock.patch(
        "explorers_data_loader.mongo.subprocess.run", side_effect=inspect_config_file
    ):
        import_collection(AGORA_DB_SETTINGS, GENES, DOWNLOAD_DIR)

    assert seen["mode"] == 0o600
    assert seen["contents"] == f'uri: "{AGORA_DB_SETTINGS.import_uri}"\n'
    assert not os.path.exists(seen["path"])


def test_import_collection_removes_the_config_file_when_mongoimport_cannot_start():
    seen = {}

    def fail_to_start(command, **_):
        seen["path"] = next(
            argument.removeprefix("--config=")
            for argument in command
            if argument.startswith("--config=")
        )
        raise FileNotFoundError("mongoimport")

    with (
        mock.patch(
            "explorers_data_loader.mongo.subprocess.run", side_effect=fail_to_start
        ),
        pytest.raises(FileNotFoundError),
    ):
        import_collection(AGORA_DB_SETTINGS, GENES, DOWNLOAD_DIR)

    assert not os.path.exists(seen["path"])
