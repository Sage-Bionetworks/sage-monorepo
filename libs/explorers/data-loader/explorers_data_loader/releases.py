"""Read the release a run loads from the data project's data-releases.json.

The file maps a release name (local, develop, staging, prod) to a Synapse data
release, and DATA_RELEASE picks the entry, local by default. To load another
release locally, edit the local entry without committing it, and rebuild the
data image.
"""

from dataclasses import dataclass
from json import load
from os import getenv
from typing import Any

DATA_RELEASE_ENV = "DATA_RELEASE"
DEFAULT_RELEASE_NAME = "local"

# Keys starting with this, such as "$comment", annotate the file and name no release
ANNOTATION_KEY_PREFIX = "$"

REQUIRED_KEYS = ("dataFile", "dataVersion")
OPTIONAL_KEYS = ("teamImagesId",)

# The variables .env files set before data-releases.json, which runs now ignore
RETIRED_RELEASE_ENV_NAMES = ("DATA_FILE", "DATA_VERSION", "TEAM_IMAGES_ID")


@dataclass(frozen=True)
class ReleaseSettings:
    name: str
    data_file: str
    data_version: str
    team_images_id: str | None = None


class ReleaseConfigError(ValueError):
    pass


def read_release(releases_filepath: str) -> ReleaseSettings:
    """The release DATA_RELEASE names"""
    release_name = getenv(DATA_RELEASE_ENV) or DEFAULT_RELEASE_NAME
    with open(releases_filepath) as releases_file:
        releases = load(releases_file)
    if not isinstance(releases, dict):
        raise ReleaseConfigError(f"{releases_filepath} must hold a JSON object")

    release_names = [
        name for name in releases if not name.startswith(ANNOTATION_KEY_PREFIX)
    ]
    if release_name not in release_names:
        raise ReleaseConfigError(
            f"{DATA_RELEASE_ENV} is '{release_name}', but {releases_filepath} only "
            f"has: {', '.join(release_names)}"
        )
    entry = releases[release_name]
    problems = entry_problems(entry)
    if problems:
        raise ReleaseConfigError(
            f"Release '{release_name}' in {releases_filepath} is invalid: "
            + "; ".join(problems)
        )
    return ReleaseSettings(
        name=release_name,
        data_file=entry["dataFile"],
        data_version=entry["dataVersion"],
        team_images_id=entry.get("teamImagesId"),
    )


def retired_release_env_names() -> list[str]:
    """The retired release variables still set, e.g. by an older .env file"""
    return [name for name in RETIRED_RELEASE_ENV_NAMES if getenv(name)]


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
    if "teamImagesId" in entry and not isinstance(entry["teamImagesId"], str):
        problems.append("'teamImagesId' must be a string")
    return problems
