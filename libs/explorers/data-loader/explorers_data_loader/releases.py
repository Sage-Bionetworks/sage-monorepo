"""Read the release a run loads from the data project's data-release.json.

Every environment loads the same release: the import is baked into the data
image, so the release follows the image tag as it promotes from dev to stage
to prod. To load another release locally, edit the file without committing it,
and rebuild the data image.
"""

from dataclasses import dataclass
from json import load
from os import getenv
from typing import Any

# Keys starting with this, such as "$comment", annotate the file and are not
# part of the release
ANNOTATION_KEY_PREFIX = "$"

REQUIRED_KEYS = ("dataFile", "dataVersion")
OPTIONAL_KEYS = ("teamImagesId",)

# The variables .env files set before data-release.json, which runs now ignore
RETIRED_RELEASE_ENV_NAMES = ("DATA_FILE", "DATA_VERSION", "TEAM_IMAGES_ID")


@dataclass(frozen=True)
class ReleaseSettings:
    data_file: str
    data_version: str
    team_images_id: str | None = None


class ReleaseConfigError(ValueError):
    pass


def read_release(release_filepath: str) -> ReleaseSettings:
    """The release every environment loads"""
    with open(release_filepath) as release_file:
        release = load(release_file)
    problems = release_problems(release)
    if problems:
        raise ReleaseConfigError(
            f"{release_filepath} is invalid: " + "; ".join(problems)
        )
    return ReleaseSettings(
        data_file=release["dataFile"],
        data_version=release["dataVersion"],
        team_images_id=release.get("teamImagesId"),
    )


def retired_release_env_names() -> list[str]:
    """The retired release variables still set, e.g. by an older .env file"""
    return [name for name in RETIRED_RELEASE_ENV_NAMES if getenv(name)]


def release_problems(release: Any) -> list[str]:
    if not isinstance(release, dict):
        return ["must hold a JSON object"]
    problems = [
        f"'{key}' must be a non-empty string"
        for key in REQUIRED_KEYS
        if not isinstance(release.get(key), str) or not release[key].strip()
    ]
    problems += [
        f"unknown key '{key}', expected one of "
        f"{', '.join(REQUIRED_KEYS + OPTIONAL_KEYS)}"
        for key in release
        if key not in REQUIRED_KEYS + OPTIONAL_KEYS
        and not key.startswith(ANNOTATION_KEY_PREFIX)
    ]
    if "teamImagesId" in release and not isinstance(release["teamImagesId"], str):
        problems.append("'teamImagesId' must be a string")
    return problems
