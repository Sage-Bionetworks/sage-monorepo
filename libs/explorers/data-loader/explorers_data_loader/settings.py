from dataclasses import dataclass
from os import getenv, path

LOAD_TEAM_IMAGES_ENV = "LOAD_TEAM_IMAGES"
TEAM_IMAGES_ID_ENV = "TEAM_IMAGES_ID"

BOOLEAN_ENV_VALUES = {"true": True, "false": False}


def read_required_env(*names: str) -> dict[str, str]:
    """Read environment variables, failing with every missing name at once"""
    values = {name: getenv(name) for name in names}
    missing = [name for name, value in values.items() if not value]
    if missing:
        raise ValueError(
            f"Missing required environment variables: {', '.join(missing)}"
        )
    return {name: value for name, value in values.items() if value}


@dataclass(frozen=True)
class DatabaseSettings:
    user: str
    password: str
    name: str
    host: str
    port: str

    @classmethod
    def from_env(cls) -> "DatabaseSettings":
        values = read_required_env(
            "DB_USER", "DB_PASS", "DB_NAME", "DB_HOST", "DB_PORT"
        )
        return cls(
            user=values["DB_USER"],
            password=values["DB_PASS"],
            name=values["DB_NAME"],
            host=values["DB_HOST"],
            port=values["DB_PORT"],
        )

    @property
    def client_uri(self) -> str:
        return f"mongodb://{self.user}:{self.password}@{self.host}:{self.port}"

    @property
    def import_uri(self) -> str:
        return f"{self.client_uri}/{self.name}?authSource=admin"


@dataclass(frozen=True)
class ReleaseSettings:
    data_file: str
    data_version: str

    @classmethod
    def from_env(cls) -> "ReleaseSettings":
        values = read_required_env("DATA_FILE", "DATA_VERSION")
        return cls(data_file=values["DATA_FILE"], data_version=values["DATA_VERSION"])


@dataclass(frozen=True)
class ProductSettings:
    """Settings that differ between the explorer products"""

    # Identifies the product on every log record
    name: str
    # The product serves team member photos from GridFS, loaded from the
    # Synapse folder TEAM_IMAGES_ID unless LOAD_TEAM_IMAGES turns them off
    loads_team_images: bool = False


@dataclass(frozen=True)
class TeamImagesSettings:
    folder_id: str

    @classmethod
    def from_env(cls) -> "TeamImagesSettings | None":
        """The team images folder to load, or None when LOAD_TEAM_IMAGES is false"""
        if not read_bool_env(LOAD_TEAM_IMAGES_ENV, default=True):
            return None
        values = read_required_env(TEAM_IMAGES_ID_ENV)
        return cls(folder_id=values[TEAM_IMAGES_ID_ENV])


def read_bool_env(name: str, default: bool) -> bool:
    value = getenv(name)
    if not value:
        return default
    normalized = value.strip().lower()
    if normalized not in BOOLEAN_ENV_VALUES:
        raise ValueError(f"{name} must be true or false, not '{value}'")
    return BOOLEAN_ENV_VALUES[normalized]


@dataclass(frozen=True)
class LoaderPaths:
    download_dir: str
    team_images_dir: str
    collections_filepath: str

    @classmethod
    def from_working_dir(cls, working_dir: str) -> "LoaderPaths":
        config_dir = path.join(working_dir, "src", "data")
        download_dir = path.join(working_dir, "local", "data")
        return cls(
            download_dir=download_dir,
            team_images_dir=path.join(download_dir, "team_images"),
            collections_filepath=path.join(config_dir, "collections.json"),
        )
