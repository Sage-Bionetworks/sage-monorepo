from unittest import mock

import pytest

from explorers_data_loader.loader import LiveRelease, Step, run
from explorers_data_loader.mongo import DATA_VERSION_COLLECTION_NAME
from explorers_data_loader.settings import ProductSettings
from explorers_data_loader.staging import SwapError

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
RELEASE_COLLECTION_NAMES = ["genes", "uiconfig", DATA_VERSION_COLLECTION_NAME]


@pytest.fixture
def working_dir(tmp_path, monkeypatch):
    for name, value in AGORA_ENV.items():
        monkeypatch.setenv(name, value)
    config_dir = tmp_path / "src" / "data"
    config_dir.mkdir(parents=True)
    (config_dir / "collections-indexes.json").write_text("[]")
    (config_dir / "collections.csv").write_text(
        "".join(f"{name},{filename}\n" for name, filename in AGORA_COLLECTIONS)
    )
    return tmp_path


def record_step(recorded, step, result=None):
    def side_effect(*args):
        recorded.append(step(*args) if callable(step) else step)
        return result

    return side_effect


@pytest.fixture
def steps():
    """Record the loader's steps in the order they run"""
    recorded = []
    with (
        mock.patch(
            "explorers_data_loader.loader.download_synapse_data",
            side_effect=record_step(recorded, "download"),
        ),
        mock.patch(
            "explorers_data_loader.loader.log_database_size",
            side_effect=record_step(
                recorded, lambda _db, stage: f"measure size {stage}"
            ),
        ),
        mock.patch(
            "explorers_data_loader.loader.drop_staging_leftovers",
            side_effect=record_step(recorded, "drop staging leftovers", []),
        ),
        mock.patch(
            "explorers_data_loader.loader.import_collection",
            side_effect=record_step(
                recorded, lambda _db, _product, name, *_: f"import {name}"
            ),
        ),
        mock.patch(
            "explorers_data_loader.loader.create_collections_indexes",
            side_effect=record_step(recorded, "create indexes"),
        ),
        mock.patch(
            "explorers_data_loader.loader.verify_staging_collections",
            side_effect=record_step(recorded, lambda _db, names: f"verify {names}"),
        ),
        mock.patch(
            "explorers_data_loader.loader.swap_staging_collections",
            side_effect=lambda _db, names: recorded.append(f"swap {names}") or names,
        ),
        mock.patch("explorers_data_loader.loader.MongoClient"),
    ):
        yield recorded


def run_completed(records):
    (record,) = [r for r in records if getattr(r, "event", None) == "run_completed"]
    return record


def test_run_stages_checks_and_swaps_with_dataversion_last(working_dir, steps):
    run(AGORA_PRODUCT, str(working_dir))

    assert steps == [
        "download",
        "measure size before_import",
        "drop staging leftovers",
        "import genes",
        "import uiconfig",
        f"import {DATA_VERSION_COLLECTION_NAME}",
        "create indexes",
        f"verify {RELEASE_COLLECTION_NAMES}",
        f"swap {RELEASE_COLLECTION_NAMES}",
        "measure size after_swap",
    ]


def test_run_rejects_a_listed_dataversion_before_downloading(working_dir, steps):
    (working_dir / "src" / "data" / "collections.csv").write_text(
        "genes,rnaseq_differential_expression.json\n"
        f"{DATA_VERSION_COLLECTION_NAME},dataversion.json\n"
    )

    with pytest.raises(ValueError, match="must not be listed"):
        run(AGORA_PRODUCT, str(working_dir))

    assert steps == []


def test_run_reports_a_succeeded_run_with_the_new_release_live(
    working_dir, steps, records
):
    run(AGORA_PRODUCT, str(working_dir))

    summary = run_completed(records)
    assert summary.fields["outcome"] == "succeeded"
    assert summary.fields["failed_step"] is None
    assert summary.fields["live_release"] == LiveRelease.NEW
    assert summary.fields["collections_imported"] == RELEASE_COLLECTION_NAMES
    assert summary.fields["collections_swapped"] == RELEASE_COLLECTION_NAMES
    assert summary.fields["collections_not_swapped"] == []


def test_run_names_the_release_in_its_start_and_finish_messages(
    working_dir, steps, records
):
    run(AGORA_PRODUCT, str(working_dir))

    messages = [
        record.getMessage()
        for record in records
        if getattr(record, "event", None) in ("run_started", "run_completed")
    ]
    assert messages == [
        f"Data load started for agora v{AGORA_ENV['DATA_VERSION']}",
        f"Data load completed for agora v{AGORA_ENV['DATA_VERSION']}",
    ]


def test_run_tags_every_record_with_the_run_and_release(working_dir, steps, records):
    run(AGORA_PRODUCT, str(working_dir))

    run_ids = {record.context["run_id"] for record in records}
    assert len(run_ids) == 1
    for record in records:
        assert record.context["product"] == AGORA_PRODUCT.name
        assert record.context["data_version"] == AGORA_ENV["DATA_VERSION"]


def test_run_failing_before_the_swap_leaves_the_previous_release_live(
    working_dir, steps, records
):
    with (
        mock.patch(
            "explorers_data_loader.loader.create_collections_indexes",
            side_effect=RuntimeError("index build failed"),
        ),
        pytest.raises(RuntimeError),
    ):
        run(AGORA_PRODUCT, str(working_dir))

    assert not any(step.startswith("swap") for step in steps)
    summary = run_completed(records)
    assert summary.fields["outcome"] == "failed"
    assert summary.fields["failed_step"] == Step.CREATE_INDEXES
    assert summary.fields["error"] == "index build failed"
    assert summary.fields["live_release"] == LiveRelease.PREVIOUS
    assert summary.fields["collections_imported"] == RELEASE_COLLECTION_NAMES
    assert summary.fields["collections_swapped"] == []


def test_run_reports_a_partial_swap_as_a_mixed_release(working_dir, steps, records):
    swap_error = SwapError(
        swapped=["genes"], not_swapped=["uiconfig", DATA_VERSION_COLLECTION_NAME]
    )
    with (
        mock.patch(
            "explorers_data_loader.loader.swap_staging_collections",
            side_effect=swap_error,
        ),
        pytest.raises(SwapError),
    ):
        run(AGORA_PRODUCT, str(working_dir))

    summary = run_completed(records)
    assert summary.fields["failed_step"] == Step.SWAP
    assert summary.fields["live_release"] == LiveRelease.MIXED
    assert summary.fields["collections_swapped"] == ["genes"]
    assert summary.fields["collections_not_swapped"] == [
        "uiconfig",
        DATA_VERSION_COLLECTION_NAME,
    ]


def test_run_reports_missing_settings_as_a_failed_run(
    working_dir, steps, records, monkeypatch
):
    monkeypatch.delenv("DATA_VERSION")

    with pytest.raises(ValueError, match="DATA_VERSION"):
        run(AGORA_PRODUCT, str(working_dir))

    summary = run_completed(records)
    assert summary.fields["failed_step"] == Step.READ_SETTINGS
    assert summary.fields["live_release"] == LiveRelease.PREVIOUS
    assert steps == []
