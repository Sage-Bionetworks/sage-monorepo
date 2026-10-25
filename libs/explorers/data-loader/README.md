# Explorers Data Loader

## Introduction

This Python library downloads an explorer data release from Synapse, then seeds and indexes the
data in MongoDB. The data projects of agora, model-ad and qtl (`apps/<product>/data`) each depend
on it and pass only the settings that differ between products.

## Usage

A data project calls `run` with its product settings:

```python
from explorers_data_loader import ProductSettings, run

run(ProductSettings(name="model-ad", insertion_ordered_collection_name="ui_config"))
```

`run` reads its configuration from the environment (`DB_USER`, `DB_PASS`, `DB_NAME`, `DB_HOST`,
`DB_PORT`, `DATA_FILE`, `DATA_VERSION`, and `SYNAPSE_AUTH_TOKEN` for the Synapse client) and its
collection list from the data project's `src/data/collections.csv` and
`src/data/collections-indexes.json`.

## How a release is loaded

The live collections keep serving the previous release until the new one is complete:

1. Drop any `<name>_staging` collections a failed run left behind.
2. Import every collection, `dataversion` last, into `<name>_staging` with `mongoimport`.
3. Build the indexes on the staging collections. A rename keeps a collection's indexes, so no
   collection is ever served unindexed.
4. Stop if any staging collection is missing or empty.
5. Rename each staging collection over its live collection (`renameCollection` with
   `dropTarget`), `dataversion` last. The APIs clear their caches when `dataversion` changes, so
   it only changes once every other collection is live.
6. Drop live collections the release doesn't include (orphans), such as one a release removed
   or renamed. This runs only after a successful swap, so a failed import never removes a
   collection the previous release still reads. A product lists any collections it keeps
   without importing them in `ProductSettings.kept_collection_names`. A collection that can't
   be dropped is a warning that the next run retries, not a failed run.

A failure before step 5 leaves the previous release fully live, and the next run drops the
leftover staging collections. A failure during step 5 leaves some collections on each release;
`run_completed` lists both, and re-running the import finishes the release.

## Logs

The loader writes one JSON object per line to stdout, so log tools (CloudWatch Logs Insights,
Grafana Loki) can filter and count runs without parsing messages. Set `LOG_FORMAT=text` for
readable lines while watching a local run.

Every record carries `run_id`, `product`, `data_file` and `data_version`. Records that mark a step
also carry an `event` name and that event's fields:

| Event                          | Fields                                                                                                                                                                                                                                                |
| ------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `run_started`                  | none beyond the run fields                                                                                                                                                                                                                            |
| `download_started`             | none beyond the run fields                                                                                                                                                                                                                            |
| `file_downloaded`              | `synapse_id`, `version`, `file`, `file_number`, `files`, `size_mb`, `duration_ms`                                                                                                                                                                     |
| `download_completed`           | `files`, `duration_ms`                                                                                                                                                                                                                                |
| `database_size_measured`       | `stage` (`before_import` or `after_load`), `collections`, `data_size_mb`, `storage_size_mb`, `index_size_mb`                                                                                                                                          |
| `database_size_unavailable`    | `stage`, `error`                                                                                                                                                                                                                                      |
| `staging_leftovers_dropped`    | `collections` (empty unless a previous run failed)                                                                                                                                                                                                    |
| `collection_imported`          | `collection`, `staging_collection`, `file`, `documents`, `duration_ms`                                                                                                                                                                                |
| `collection_import_failed`     | `collection`, `file`, `duration_ms`, `error`                                                                                                                                                                                                          |
| `indexes_created`              | `collection`, `indexes`, `collated_indexes`                                                                                                                                                                                                           |
| `staging_collections_verified` | `collections`                                                                                                                                                                                                                                         |
| `staging_verification_failed`  | `empty_or_missing`                                                                                                                                                                                                                                    |
| `collection_swapped`           | `collection`                                                                                                                                                                                                                                          |
| `collection_swap_failed`       | `collection`, `error`                                                                                                                                                                                                                                 |
| `orphan_dropped`               | `collection`                                                                                                                                                                                                                                          |
| `orphan_drop_failed`           | `collection`, `error`                                                                                                                                                                                                                                 |
| `orphan_cleanup_failed`        | `error` (the collections couldn't be listed, so none were dropped)                                                                                                                                                                                    |
| `orphan_cleanup_completed`     | `dropped`, `failed`                                                                                                                                                                                                                                   |
| `run_completed`                | `outcome` (`succeeded` or `failed`), `failed_step`, `error` (on failure), `live_release` (`new`, `previous` or `mixed`), `duration_ms`, `collections_imported`, `collections_swapped`, `collections_not_swapped`, `orphans_dropped`, `orphans_failed` |

`run_completed` is written once per run, whether it succeeds or fails, so it is the record to
build dashboards on. Its `live_release` says what the site serves once the run ends: the new
release, the previous one (any failure before the swap), or a mix (a swap that stopped partway). Event and field names are a contract with those queries: add new ones
freely, but rename or remove one only together with the queries that read it.

To read a JSON log locally, for example each collection's document count:

```
docker logs model-ad-data 2>/dev/null | jq -c 'select(.event == "collection_imported") | {collection, documents, duration_ms}'
```

## Development

```
nx prepare explorers-data-loader
nx test explorers-data-loader
nx lint explorers-data-loader
```
