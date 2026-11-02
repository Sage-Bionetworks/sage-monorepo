import logging

import pytest

from explorers_data_loader.logs import LOGGER_NAME, RunContextFilter


class RecordListHandler(logging.Handler):
    def __init__(self) -> None:
        super().__init__()
        self.records: list[logging.LogRecord] = []

    def emit(self, record: logging.LogRecord) -> None:
        self.records.append(record)


@pytest.fixture(autouse=True)
def reset_loader_logger():
    """Undo configure_logging between tests, so records reach pytest's caplog"""
    logger = logging.getLogger(LOGGER_NAME)
    yield
    logger.handlers = []
    logger.propagate = True


@pytest.fixture
def records(monkeypatch) -> list[logging.LogRecord]:
    """Capture the loader's records, with run context, instead of printing them"""
    handler = RecordListHandler()

    def capture_logging(_log_format: str, context: dict[str, str]) -> None:
        handler.addFilter(RunContextFilter(context))
        logger = logging.getLogger(LOGGER_NAME)
        logger.handlers = [handler]
        logger.setLevel(logging.DEBUG)
        logger.propagate = False

    monkeypatch.setattr(
        "explorers_data_loader.loader.configure_logging", capture_logging
    )
    return handler.records
