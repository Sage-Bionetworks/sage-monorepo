from datetime import UTC, datetime
import json
import logging
from os import getenv
import sys
from time import monotonic
from typing import Any

LOGGER_NAME = "explorers_data_loader"

LOG_FORMAT_ENV = "LOG_FORMAT"
JSON_LOG_FORMAT = "json"
TEXT_LOG_FORMAT = "text"
LOG_FORMATS = (JSON_LOG_FORMAT, TEXT_LOG_FORMAT)

BYTES_PER_MEGABYTE = 1024 * 1024

TEXT_LINE_FORMAT = "%(asctime)s [%(module)s:%(lineno)d - %(levelname)s]: %(message)s"


def release_label(context: dict[str, str]) -> str:
    """Name the product and release a run loads, e.g. 'model-ad v221'"""
    label = context.get("product", "")
    if "data_version" in context:
        label = f"{label} v{context['data_version']}"
    return label.strip()


def elapsed_ms(started: float) -> int:
    """Milliseconds since a time.monotonic() reading"""
    return round((monotonic() - started) * 1000)


def event_fields(event: str, **fields: Any) -> dict[str, Any]:
    """Build the `extra` argument that makes a log call a structured event.

    Event names and field names are a contract with log queries and dashboards:
    add new ones freely, but rename or remove one only together with its queries.
    """
    return {"event": event, "fields": fields}


class RunContextFilter(logging.Filter):
    """Adds the fields that identify the current run to every record"""

    def __init__(self, context: dict[str, str]):
        super().__init__()
        self.context = context

    def filter(self, record: logging.LogRecord) -> bool:
        record.context = dict(self.context)
        return True


class JsonFormatter(logging.Formatter):
    """Writes each record as one JSON object per line"""

    def format(self, record: logging.LogRecord) -> str:
        entry = {
            "time": datetime.fromtimestamp(record.created, UTC).isoformat(
                timespec="milliseconds"
            ),
            "level": record.levelname,
            "event": getattr(record, "event", None),
            "message": record.getMessage(),
            **getattr(record, "context", {}),
            **getattr(record, "fields", {}),
        }
        if record.exc_info:
            entry["exception"] = self.formatException(record.exc_info)
        return json.dumps(entry, default=str)


class TextFormatter(logging.Formatter):
    """Writes readable lines, with an event's fields appended to its message"""

    def __init__(self) -> None:
        super().__init__(TEXT_LINE_FORMAT)

    def formatMessage(self, record: logging.LogRecord) -> str:
        line = super().formatMessage(record)
        event = getattr(record, "event", None)
        if event is None:
            return line
        details = "".join(
            f" {name}={value}" for name, value in getattr(record, "fields", {}).items()
        )
        return f"{line} (event={event}{details})"


def read_log_format() -> str:
    log_format = getenv(LOG_FORMAT_ENV) or JSON_LOG_FORMAT
    if log_format not in LOG_FORMATS:
        raise ValueError(
            f"{LOG_FORMAT_ENV} must be one of {', '.join(LOG_FORMATS)}, "
            f"not '{log_format}'"
        )
    return log_format


def configure_logging(log_format: str, context: dict[str, str]) -> None:
    """Send the loader's logs to stdout, tagged with the run's context.

    The context dict is read on every record, so fields added to it later, once
    they are known, appear on every record from then on. Libraries such as
    the Synapse client write to stderr, so keeping the loader's records on
    stdout stops their output from splitting a record's line.
    """
    handler = logging.StreamHandler(sys.stdout)
    handler.setFormatter(
        JsonFormatter() if log_format == JSON_LOG_FORMAT else TextFormatter()
    )
    handler.addFilter(RunContextFilter(context))

    logger = logging.getLogger(LOGGER_NAME)
    logger.handlers = [handler]
    logger.setLevel(logging.DEBUG)
    logger.propagate = False
