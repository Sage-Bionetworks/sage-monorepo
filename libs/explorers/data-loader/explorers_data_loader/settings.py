from dataclasses import dataclass
from os import getenv, path


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

    # The UI shows this collection's documents in the order they are stored
    insertion_ordered_collection_name: str | None = None


@dataclass(frozen=True)
class LoaderPaths:
    download_dir: str
    collections_filepath: str
    collections_indexes_filepath: str

    @classmethod
    def from_working_dir(cls, working_dir: str) -> "LoaderPaths":
        config_dir = path.join(working_dir, "src", "data")
        return cls(
            download_dir=path.join(working_dir, "local", "data"),
            collections_filepath=path.join(config_dir, "collections.csv"),
            collections_indexes_filepath=path.join(
                config_dir, "collections-indexes.json"
            ),
        )
