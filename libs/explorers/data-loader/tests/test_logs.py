import json
import logging
import sys

import pytest

from explorers_data_loader.logs import (
    JSON_LOG_FORMAT,
    LOGGER_NAME,
    LOG_FORMAT_ENV,
    TEXT_LOG_FORMAT,
    JsonFormatter,
    RunContextFilter,
    TextFormatter,
    configure_logging,
    event_fields,
    read_log_format,
    release_label,
)

RUN_CONTEXT = {
    "run_id": "3f2c9a0e-5b1d-4f6a-9c8e-2d7b1a4e6f30",
    "product": "model-ad",
    "data_version": "221",
}


def make_record(message, *args, extra=None, exc_info=None):
    record = logging.LogRecord(
        "explorers_data_loader.mongo",
        logging.INFO,
        "mongo.py",
        71,
        message,
        args,
        exc_info,
    )
    for name, value in (extra or {}).items():
        setattr(record, name, value)
    RunContextFilter(RUN_CONTEXT).filter(record)
    return record


def test_json_formatter_writes_the_event_its_fields_and_the_run_context():
    record = make_record(
        "Imported collection '%s'",
        "rna_de_individual",
        extra=event_fields(
            "collection_imported", collection="rna_de_individual", documents=1169360
        ),
    )

    entry = json.loads(JsonFormatter().format(record))

    assert entry["event"] == "collection_imported"
    assert entry["message"] == "Imported collection 'rna_de_individual'"
    assert entry["level"] == "INFO"
    assert entry["collection"] == "rna_de_individual"
    assert entry["documents"] == 1169360
    assert {name: entry[name] for name in RUN_CONTEXT} == RUN_CONTEXT


def test_json_formatter_marks_plain_messages_as_having_no_event():
    entry = json.loads(JsonFormatter().format(make_record("Logging in to Synapse")))

    assert entry["event"] is None
    assert entry["message"] == "Logging in to Synapse"


def test_json_formatter_includes_the_traceback_of_a_logged_exception():
    try:
        raise RuntimeError("index build failed")
    except RuntimeError:
        record = make_record("Data load failed", exc_info=sys.exc_info())

    entry = json.loads(JsonFormatter().format(record))

    assert "RuntimeError: index build failed" in entry["exception"]


def test_text_formatter_appends_the_event_and_its_fields():
    record = make_record(
        "Imported collection 'ui_config'",
        extra=event_fields("collection_imported", documents=11, duration_ms=42),
    )

    line = TextFormatter().format(record)

    assert line.endswith(
        "Imported collection 'ui_config' "
        "(event=collection_imported documents=11 duration_ms=42)"
    )


def test_read_log_format_defaults_to_json(monkeypatch):
    monkeypatch.delenv(LOG_FORMAT_ENV, raising=False)

    assert read_log_format() == JSON_LOG_FORMAT


def test_read_log_format_accepts_text(monkeypatch):
    monkeypatch.setenv(LOG_FORMAT_ENV, TEXT_LOG_FORMAT)

    assert read_log_format() == TEXT_LOG_FORMAT


def test_read_log_format_rejects_an_unknown_format(monkeypatch):
    monkeypatch.setenv(LOG_FORMAT_ENV, "yaml")

    with pytest.raises(ValueError, match="must be one of json, text"):
        read_log_format()


def test_configure_logging_writes_to_stdout_so_library_output_cannot_split_records():
    configure_logging(JSON_LOG_FORMAT, RUN_CONTEXT)

    (handler,) = logging.getLogger(LOGGER_NAME).handlers
    assert handler.stream is sys.stdout


def test_release_label_names_only_the_product_before_the_release_is_known():
    assert release_label({"product": "agora"}) == "agora"
    assert release_label({"product": "agora", "data_version": "119"}) == "agora v119"
