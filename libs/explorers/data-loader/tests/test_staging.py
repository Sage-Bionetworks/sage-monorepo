from collections import defaultdict
import logging
from unittest import mock

import pytest
from pymongo.errors import OperationFailure

from explorers_data_loader.staging import (
    StagingVerificationError,
    SwapError,
    verify_staging_collections,
    drop_staging_leftovers,
    log_database_size,
    staging_collection_name,
    swap_staging_collections,
)

MODEL_AD_COLLECTIONS = [
    "model_overview",
    "rna_de_aggregate",
    "ui_config",
    "dataversion",
]
RENAME_ERROR = "target namespace exists"


class FakeDatabase:
    """A database whose collections are mocks, one per name"""

    def __init__(self, existing_names=(), empty_names=(), failing_renames=()):
        self.existing_names = list(existing_names)
        self.collections = defaultdict(mock.Mock)
        for name in empty_names:
            self.collections[name].find_one.return_value = None
        for name in failing_renames:
            self.collections[name].rename.side_effect = OperationFailure(RENAME_ERROR)
        self.list_collection_names = mock.Mock(return_value=self.existing_names)
        self.drop_collection = mock.Mock()
        self.command = mock.Mock()

    def get_collection(self, name):
        return self.collections[name]


def events(caplog, event):
    return [r for r in caplog.records if getattr(r, "event", None) == event]


@pytest.fixture(autouse=True)
def capture_loader_records(caplog):
    caplog.set_level(logging.DEBUG, logger="explorers_data_loader")


def test_drop_staging_leftovers_drops_only_staging_collections(caplog):
    leftover = staging_collection_name("rna_de_aggregate")
    db = FakeDatabase(existing_names=["rna_de_aggregate", leftover, "ui_config"])

    assert drop_staging_leftovers(db) == [leftover]

    db.drop_collection.assert_called_once_with(leftover)
    (record,) = events(caplog, "staging_leftovers_dropped")
    assert record.levelno == logging.WARNING
    assert record.fields["collections"] == [leftover]


def test_drop_staging_leftovers_reports_a_clean_start(caplog):
    db = FakeDatabase(existing_names=["rna_de_aggregate", "ui_config"])

    assert drop_staging_leftovers(db) == []

    db.drop_collection.assert_not_called()
    (record,) = events(caplog, "staging_leftovers_dropped")
    assert record.levelno == logging.INFO
    assert record.fields["collections"] == []


def test_verify_staging_collections_passes_when_every_collection_has_documents(
    caplog,
):
    verify_staging_collections(FakeDatabase(), MODEL_AD_COLLECTIONS)

    (record,) = events(caplog, "staging_collections_verified")
    assert record.fields["collections"] == len(MODEL_AD_COLLECTIONS)


def test_verify_staging_collections_stops_on_an_empty_collection(caplog):
    db = FakeDatabase(empty_names=[staging_collection_name("rna_de_aggregate")])

    with pytest.raises(StagingVerificationError, match="rna_de_aggregate"):
        verify_staging_collections(db, MODEL_AD_COLLECTIONS)

    (record,) = events(caplog, "staging_verification_failed")
    assert record.fields["empty_or_missing"] == ["rna_de_aggregate"]


def test_swap_staging_collections_renames_each_over_its_live_collection_in_order(
    caplog,
):
    db = FakeDatabase()

    assert swap_staging_collections(db, MODEL_AD_COLLECTIONS) == MODEL_AD_COLLECTIONS

    for name in MODEL_AD_COLLECTIONS:
        db.collections[staging_collection_name(name)].rename.assert_called_once_with(
            name, dropTarget=True
        )
    swapped_events = [
        r.fields["collection"] for r in events(caplog, "collection_swapped")
    ]
    assert swapped_events == MODEL_AD_COLLECTIONS


def test_swap_staging_collections_reports_which_collections_a_partial_swap_reached(
    caplog,
):
    db = FakeDatabase(failing_renames=[staging_collection_name("ui_config")])

    with pytest.raises(SwapError, match="Still on the previous release") as error:
        swap_staging_collections(db, MODEL_AD_COLLECTIONS)

    assert error.value.swapped == ["model_overview", "rna_de_aggregate"]
    assert error.value.not_swapped == ["ui_config", "dataversion"]
    db.collections[staging_collection_name("dataversion")].rename.assert_not_called()
    (record,) = events(caplog, "collection_swap_failed")
    assert record.fields == {"collection": "ui_config", "error": RENAME_ERROR}


def test_swap_staging_collections_failing_first_leaves_the_previous_release_live():
    db = FakeDatabase(failing_renames=[staging_collection_name("model_overview")])

    with pytest.raises(
        SwapError, match="previous release is still fully live"
    ) as error:
        swap_staging_collections(db, MODEL_AD_COLLECTIONS)

    assert error.value.swapped == []
    assert error.value.not_swapped == MODEL_AD_COLLECTIONS


def test_log_database_size_reports_sizes_in_megabytes(caplog):
    db = FakeDatabase()
    db.command.return_value = {
        "collections": 11,
        "dataSize": 1530.4,
        "storageSize": 612.8,
        "indexSize": 140.2,
    }

    log_database_size(db, "after_swap")

    (record,) = events(caplog, "database_size_measured")
    assert record.fields == {
        "stage": "after_swap",
        "collections": 11,
        "data_size_mb": 1530,
        "storage_size_mb": 613,
        "index_size_mb": 140,
    }


def test_log_database_size_warns_instead_of_failing_the_run(caplog):
    db = FakeDatabase()
    db.command.side_effect = OperationFailure("not authorized to run dbStats")

    log_database_size(db, "before_import")

    (record,) = events(caplog, "database_size_unavailable")
    assert record.levelno == logging.WARNING
    assert record.fields["stage"] == "before_import"
