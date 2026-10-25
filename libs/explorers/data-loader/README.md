# Explorers Data Loader

## Introduction

This Python library downloads an explorer data release from Synapse, then seeds and indexes the
data in MongoDB. The data projects of agora, model-ad and qtl (`apps/<product>/data`) each depend
on it and pass only the settings that differ between products.

## Usage

A data project calls `run` with its product settings:

```python
from explorers_data_loader import ProductSettings, run

run(ProductSettings(insertion_ordered_collection_name="ui_config"))
```

`run` reads its configuration from the environment (`DB_USER`, `DB_PASS`, `DB_NAME`, `DB_HOST`,
`DB_PORT`, `DATA_FILE`, `DATA_VERSION`, and `SYNAPSE_AUTH_TOKEN` for the Synapse client) and its
collection list from the data project's `src/data/collections.csv` and
`src/data/collections-indexes.json`.

## Development

```
nx prepare explorers-data-loader
nx test explorers-data-loader
nx lint explorers-data-loader
```
