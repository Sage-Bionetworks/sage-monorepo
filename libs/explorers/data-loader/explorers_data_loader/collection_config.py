"""Read a product's collections.json: which collections a release imports, from
which files, with which indexes, in import order."""

from dataclasses import dataclass
from json import load
from typing import Any

DATA_VERSION_COLLECTION_NAME = "dataversion"
DATA_VERSION_COLLECTION_FILENAME = "dataversion.json"

INDEX_DIRECTIONS = (1, -1)
REQUIRED_KEYS = ("name", "file")
INDEX_KEYS = ("indexes", "collatedIndexes")
OPTIONAL_KEYS = (*INDEX_KEYS, "maintainInsertionOrder")


@dataclass(frozen=True)
class CollectionConfig:
    name: str
    file: str
    indexes: tuple[dict[str, int], ...] = ()
    collated_indexes: tuple[dict[str, int], ...] = ()
    # The UI shows this collection's documents in the order they are stored
    maintain_insertion_order: bool = False


DATA_VERSION_COLLECTION = CollectionConfig(
    name=DATA_VERSION_COLLECTION_NAME, file=DATA_VERSION_COLLECTION_FILENAME
)


class CollectionConfigError(ValueError):
    pass


def read_collection_configs(collections_filepath: str) -> list[CollectionConfig]:
    """Read and check the whole file, reporting every problem in one error"""
    with open(collections_filepath) as collections_file:
        entries = load(collections_file)
    if not isinstance(entries, list):
        raise CollectionConfigError(f"{collections_filepath} must hold a JSON array")

    problems = [
        f"entry {number} ({entry_label(entry)}): {problem}"
        for number, entry in enumerate(entries, start=1)
        for problem in entry_problems(entry)
    ]
    # A name that isn't a string is already reported, and can't be counted
    names = [
        entry["name"]
        for entry in entries
        if isinstance(entry, dict) and isinstance(entry.get("name"), str)
    ]
    problems += [
        f"'{name}' is listed more than once"
        for name in sorted({name for name in names if names.count(name) > 1})
    ]
    if DATA_VERSION_COLLECTION_NAME in names:
        problems.append(
            f"'{DATA_VERSION_COLLECTION_NAME}' must not be listed: it is imported "
            "last, once every other collection is loaded and indexed"
        )
    if problems:
        raise CollectionConfigError(
            f"{collections_filepath} is invalid:\n- " + "\n- ".join(problems)
        )

    return [
        CollectionConfig(
            name=entry["name"],
            file=entry["file"],
            indexes=tuple(entry.get("indexes", [])),
            collated_indexes=tuple(entry.get("collatedIndexes", [])),
            maintain_insertion_order=entry.get("maintainInsertionOrder", False),
        )
        for entry in entries
    ]


def entry_label(entry: Any) -> str:
    if isinstance(entry, dict) and isinstance(entry.get("name"), str):
        return entry["name"]
    return "unnamed"


def entry_problems(entry: Any) -> list[str]:
    if not isinstance(entry, dict):
        return ["must be a JSON object"]
    problems = [
        f"'{key}' must be a non-empty string"
        for key in REQUIRED_KEYS
        if not isinstance(entry.get(key), str) or not entry[key].strip()
    ]
    problems += [
        f"unknown key '{key}', expected one of "
        f"{', '.join(REQUIRED_KEYS + OPTIONAL_KEYS)}"
        for key in entry
        if key not in REQUIRED_KEYS + OPTIONAL_KEYS
    ]
    for key in INDEX_KEYS:
        if key in entry and not is_index_list(entry[key]):
            problems.append(
                f"'{key}' must be a list of indexes like "
                '{"field": 1}, with each field 1 or -1'
            )
    if "maintainInsertionOrder" in entry and not isinstance(
        entry["maintainInsertionOrder"], bool
    ):
        problems.append("'maintainInsertionOrder' must be true or false")
    return problems


def is_index_list(indexes: Any) -> bool:
    return isinstance(indexes, list) and all(
        isinstance(index, dict)
        and index
        and all(
            # bool is an int in Python, so true would otherwise pass as 1
            isinstance(field, str)
            and not isinstance(direction, bool)
            and direction in INDEX_DIRECTIONS
            for field, direction in index.items()
        )
        for index in indexes
    )
