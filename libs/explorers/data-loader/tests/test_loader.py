from unittest import mock

import pytest

from explorers_data_loader.loader import Step, run
from explorers_data_loader.mongo import DATA_VERSION_COLLECTION_NAME
from explorers_data_loader.settings import ProductSettings

AGORA_ENV = {
    "DB_USER": "root",
    "DB_PASS": "password",
    "DB_NAME": "agora",
    "DB_HOST": "agora-mongo",
    "DB_PORT": "27017",
    "DATA_FILE": "syn13363290",
    "DATA_VERSION": "119",
}
AGORA_PRODUCT = ProductSettings(
    name="agora", insertion_ordered_collection_name="uiconfig"
)
AGORA_COLLECTIONS = [
    ("genes", "rnaseq_differential_expression.json"),
    ("uiconfig", "ui_config.json"),
]


@pytest.fixture
def working_dir(tmp_path, monkeypatch):
    for name, value in AGORA_ENV.items():
        monkeypatch.setenv(name, value)
    config_dir = tmp_path / "src" / "data"
    config_dir.mkdir(parents=True)
    (config_dir / "collections-indexes.json").write_text("[]")
    return tmp_path


def write_collections(working_dir, rows):
    (working_dir / "src" / "data" / "collections.csv").write_text(
        "".join(f"{name},{filename}\n" for name, filename in rows)
    )


@pytest.fixture
def steps():
    """Record the loader's steps in the order they run"""
    recorded = []
    with (
        mock.patch(
            "explorers_data_loader.loader.download_synapse_data",
            side_effect=lambda *_: recorded.append("download"),
        ),
        mock.patch(
            "explorers_data_loader.loader.import_collection",
            side_effect=lambda _db, _product, name, *_: recorded.append(
                f"import {name}"
            ),
        ),
        mock.patch(
            "explorers_data_loader.loader.create_collections_indexes",
            side_effect=lambda *_: recorded.append("create indexes"),
        ),
        mock.patch("explorers_data_loader.loader.MongoClient"),
    ):
        yield recorded


def test_run_imports_dataversion_after_every_collection_and_index(working_dir, steps):
    write_collections(working_dir, AGORA_COLLECTIONS)

    run(AGORA_PRODUCT, str(working_dir))

    assert steps == [
        "download",
        "import genes",
        "import uiconfig",
        "create indexes",
        f"import {DATA_VERSION_COLLECTION_NAME}",
    ]


def test_run_rejects_a_listed_dataversion_before_downloading(working_dir, steps):
    write_collections(
        working_dir, [*AGORA_COLLECTIONS, (DATA_VERSION_COLLECTION_NAME, "x.json")]
    )

    with pytest.raises(ValueError, match="must not be listed"):
        run(AGORA_PRODUCT, str(working_dir))

    assert steps == []


def test_run_does_not_import_dataversion_when_index_creation_fails(working_dir, steps):
    write_collections(working_dir, AGORA_COLLECTIONS)

    with (
        mock.patch(
            "explorers_data_loader.loader.create_collections_indexes",
            side_effect=RuntimeError("index build failed"),
        ),
        pytest.raises(RuntimeError, match="index build failed"),
    ):
        run(AGORA_PRODUCT, str(working_dir))

    assert f"import {DATA_VERSION_COLLECTION_NAME}" not in steps


def run_completed(records):
    (record,) = [r for r in records if getattr(r, "event", None) == "run_completed"]
    return record


def test_run_reports_a_succeeded_run_with_every_imported_collection(
    working_dir, steps, records
):
    write_collections(working_dir, AGORA_COLLECTIONS)

    run(AGORA_PRODUCT, str(working_dir))

    summary = run_completed(records)
    assert summary.fields["outcome"] == "succeeded"
    assert summary.fields["failed_step"] is None
    assert summary.fields["collections_imported"] == [
        "genes",
        "uiconfig",
        DATA_VERSION_COLLECTION_NAME,
    ]


def test_run_tags_every_record_from_run_started_on_with_the_release(
    working_dir, steps, records
):
    write_collections(working_dir, AGORA_COLLECTIONS)

    run(AGORA_PRODUCT, str(working_dir))

    run_ids = {record.context["run_id"] for record in records}
    assert len(run_ids) == 1
    for record in records:
        assert record.context["product"] == AGORA_PRODUCT.name
        assert record.context["data_version"] == AGORA_ENV["DATA_VERSION"]


def test_run_reports_the_step_that_failed(working_dir, steps, records):
    write_collections(working_dir, AGORA_COLLECTIONS)

    with (
        mock.patch(
            "explorers_data_loader.loader.create_collections_indexes",
            side_effect=RuntimeError("index build failed"),
        ),
        pytest.raises(RuntimeError),
    ):
        run(AGORA_PRODUCT, str(working_dir))

    summary = run_completed(records)
    assert summary.fields["outcome"] == "failed"
    assert summary.fields["failed_step"] == Step.CREATE_INDEXES
    assert summary.fields["error"] == "index build failed"
    assert summary.fields["collections_imported"] == ["genes", "uiconfig"]


def test_run_reports_missing_settings_as_a_failed_run(
    working_dir, steps, records, monkeypatch
):
    monkeypatch.delenv("DATA_VERSION")

    with pytest.raises(ValueError, match="DATA_VERSION"):
        run(AGORA_PRODUCT, str(working_dir))

    summary = run_completed(records)
    assert summary.fields["outcome"] == "failed"
    assert summary.fields["failed_step"] == Step.READ_SETTINGS
    assert steps == []
