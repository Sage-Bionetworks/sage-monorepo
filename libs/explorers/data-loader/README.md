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

## Logs

The loader writes one JSON object per line to stdout, so log tools (CloudWatch Logs Insights,
Grafana Loki) can filter and count runs without parsing messages. Set `LOG_FORMAT=text` for
readable lines while watching a local run.

Every record carries `run_id`, `product`, `data_file` and `data_version`. Records that mark a step
also carry an `event` name and that event's fields:

| Event                      | Fields                                                                                                          |
| -------------------------- | --------------------------------------------------------------------------------------------------------------- |
| `run_started`              | none beyond the run fields                                                                                      |
| `download_completed`       | `files`, `duration_ms`                                                                                          |
| `collection_imported`      | `collection`, `file`, `documents`, `duration_ms`                                                                |
| `collection_import_failed` | `collection`, `file`, `duration_ms`, `error`                                                                    |
| `indexes_created`          | `collection`, `indexes`, `collated_indexes`                                                                     |
| `run_completed`            | `outcome` (`succeeded` or `failed`), `failed_step`, `error` (on failure), `duration_ms`, `collections_imported` |

`run_completed` is written once per run, whether it succeeds or fails, so it is the record to
build dashboards on. Event and field names are a contract with those queries: add new ones
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
