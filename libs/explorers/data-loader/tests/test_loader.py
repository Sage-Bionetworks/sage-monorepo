from unittest import mock

import json

import pytest

from explorers_data_loader.loader import LiveRelease, Step, run
from explorers_data_loader.collection_config import DATA_VERSION_COLLECTION_NAME
from explorers_data_loader.settings import ProductSettings
from explorers_data_loader.staging import OrphanCleanup, SwapError
from explorers_data_loader.team_images import TEAM_IMAGES_COLLECTION_NAMES

AGORA_ENV = {
    "DB_USER": "root",
    "DB_PASS": "password",
    "DB_NAME": "agora",
    "DB_HOST": "agora-mongo",
    "DB_PORT": "27017",
}
AGORA_LOCAL_RELEASE = {
    "dataFile": "syn13363290",
    "dataVersion": "117",
    "teamImagesId": "syn12861877",
}
RELEASE_ENV_NAMES = ("DATA_RELEASE", "DATA_FILE", "DATA_VERSION", "TEAM_IMAGES_ID")
AGORA_PRODUCT = ProductSettings(name="agora")
AGORA_COLLECTIONS = [
    {"name": "genes", "file": "rnaseq_differential_expression.json"},
    {"name": "uiconfig", "file": "ui_config.json", "maintainInsertionOrder": True},
]
RELEASE_COLLECTION_NAMES = ["genes", "uiconfig", DATA_VERSION_COLLECTION_NAME]
TEAM_IMAGE_PATHS = (
    "/app/local/data/team_images/jane_doe.jpg",
    "/app/local/data/team_images/john_roe.png",
)
AGORA_WITH_TEAM_IMAGES = ProductSettings(name="agora", loads_team_images=True)
RELEASE_WITH_TEAM_IMAGES = [
    "genes",
    "uiconfig",
    *TEAM_IMAGES_COLLECTION_NAMES,
    DATA_VERSION_COLLECTION_NAME,
]


@pytest.fixture
def working_dir(tmp_path, monkeypatch):
    for name, value in AGORA_ENV.items():
        monkeypatch.setenv(name, value)
    for name in RELEASE_ENV_NAMES:
        monkeypatch.delenv(name, raising=False)
    config_dir = tmp_path / "src" / "data"
    config_dir.mkdir(parents=True)
    (config_dir / "collections.json").write_text(json.dumps(AGORA_COLLECTIONS))
    (config_dir / "data-releases.json").write_text(
        json.dumps({"local": AGORA_LOCAL_RELEASE})
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
                recorded, lambda _db, collection, _dir: f"import {collection.name}"
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
        mock.patch(
            "explorers_data_loader.loader.drop_orphaned_collections",
            side_effect=lambda _db, names: (
                recorded.append(f"drop orphans {names}") or OrphanCleanup()
            ),
        ),
        mock.patch(
            "explorers_data_loader.loader.login_to_synapse",
            side_effect=record_step(recorded, "log in to synapse"),
        ),
        mock.patch(
            "explorers_data_loader.loader.download_team_images",
            side_effect=record_step(
                recorded, "download team images", list(TEAM_IMAGE_PATHS)
            ),
        ),
        mock.patch(
            "explorers_data_loader.loader.upload_team_images_to_staging",
            side_effect=record_step(
                recorded, lambda _db, paths: f"upload team images {paths}"
            ),
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
        "log in to synapse",
        "download",
        "measure size before_import",
        "drop staging leftovers",
        "import genes",
        "import uiconfig",
        f"import {DATA_VERSION_COLLECTION_NAME}",
        "create indexes",
        f"verify {RELEASE_COLLECTION_NAMES}",
        f"swap {RELEASE_COLLECTION_NAMES}",
        f"drop orphans {RELEASE_COLLECTION_NAMES}",
        "measure size after_load",
    ]


def test_run_rejects_an_invalid_collection_list_before_downloading(working_dir, steps):
    (working_dir / "src" / "data" / "collections.json").write_text(
        json.dumps(
            [
                *AGORA_COLLECTIONS,
                {"name": DATA_VERSION_COLLECTION_NAME, "file": "dataversion.json"},
            ]
        )
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
    assert summary.fields["orphans_dropped"] == []
    assert summary.fields["orphans_failed"] == []


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
        f"Data load started for agora v{AGORA_LOCAL_RELEASE['dataVersion']}",
        f"Data load completed for agora v{AGORA_LOCAL_RELEASE['dataVersion']}",
    ]


def test_run_tags_every_record_with_the_run_and_release(working_dir, steps, records):
    run(AGORA_PRODUCT, str(working_dir))

    run_ids = {record.context["run_id"] for record in records}
    assert len(run_ids) == 1
    for record in records:
        assert record.context["product"] == AGORA_PRODUCT.name
        assert record.context["data_release"] == "local"
        assert record.context["data_version"] == AGORA_LOCAL_RELEASE["dataVersion"]


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
    monkeypatch.delenv("DB_PASS")

    with pytest.raises(ValueError, match="DB_PASS"):
        run(AGORA_PRODUCT, str(working_dir))

    summary = run_completed(records)
    assert summary.fields["failed_step"] == Step.READ_SETTINGS
    assert summary.fields["live_release"] == LiveRelease.PREVIOUS
    assert steps == []


def test_run_reports_dropped_orphans(working_dir, steps, records):
    orphan_cleanup = OrphanCleanup(
        dropped=["genes_legacy"], failed=["proteomics_legacy"]
    )
    with mock.patch(
        "explorers_data_loader.loader.drop_orphaned_collections",
        return_value=orphan_cleanup,
    ):
        run(AGORA_PRODUCT, str(working_dir))

    summary = run_completed(records)
    assert summary.fields["outcome"] == "succeeded"
    assert summary.fields["orphans_dropped"] == ["genes_legacy"]
    assert summary.fields["orphans_failed"] == ["proteomics_legacy"]


def test_run_loads_team_images_after_the_indexes_and_swaps_them_before_dataversion(
    working_dir, steps, monkeypatch
):
    run(AGORA_WITH_TEAM_IMAGES, str(working_dir))

    assert steps[steps.index("download") + 1] == "download team images"
    assert steps[steps.index("create indexes") + 1] == (
        f"upload team images {list(TEAM_IMAGE_PATHS)}"
    )
    assert f"verify {RELEASE_WITH_TEAM_IMAGES}" in steps
    assert f"swap {RELEASE_WITH_TEAM_IMAGES}" in steps
    assert f"drop orphans {RELEASE_WITH_TEAM_IMAGES}" in steps


def test_run_with_team_images_off_keeps_the_loaded_photos(
    working_dir, steps, records, monkeypatch
):
    monkeypatch.setenv("LOAD_TEAM_IMAGES", "false")

    run(AGORA_WITH_TEAM_IMAGES, str(working_dir))

    assert "download team images" not in steps
    assert not any(step.startswith("upload team images") for step in steps)
    assert f"swap {RELEASE_COLLECTION_NAMES}" in steps
    assert f"drop orphans {RELEASE_WITH_TEAM_IMAGES}" in steps
    skipped = [r for r in records if getattr(r, "event", None) == "team_images_skipped"]
    assert len(skipped) == 1


def test_run_warns_about_retired_release_variables_and_ignores_them(
    working_dir, steps, records, monkeypatch
):
    monkeypatch.setenv("DATA_VERSION", "119")

    run(AGORA_PRODUCT, str(working_dir))

    (ignored,) = [
        r for r in records if getattr(r, "event", None) == "release_env_ignored"
    ]
    assert ignored.fields["variables"] == ["DATA_VERSION"]
    assert (
        run_completed(records).context["data_version"]
        == (AGORA_LOCAL_RELEASE["dataVersion"])
    )
