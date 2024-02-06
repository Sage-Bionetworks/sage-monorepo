"""Builds and updates the iAtlas database."""

from os import getenv
import logging
import uuid
from schematic_db.rdb.postgres import PostgresDatabase
from schematic_db.rdb.sql_alchemy_database import SQLConfig
from schematic_db.schema.schema import SchemaConfig, Schema, DatabaseConfig
from schematic_db.rdb_builder.rdb_builder import RDBBuilder
from schematic_db.rdb_updater.rdb_updater import RDBUpdater
from schematic_db.manifest_store.synapse_manifest_store import (
    SynapseManifestStore,
    ManifestStoreConfig,
)

logging.basicConfig(
    level=logging.INFO,
    format="[%(asctime)s] %(levelname)s [%(name)s.%(funcName)s:%(lineno)d] %(message)s",
    datefmt="%d/%b/%Y %H:%M:%S",
)
logging.getLogger("schematic_db").setLevel(logging.INFO)

env_vars = [
    "DB_USER",
    "DB_PW",
    "DB_HOST",
    "DB_NAME",
    "SCHEMA_URL",
    "SCHEMA_PROJECT_ID",
    "SCHEMA_ASSET_VIEW_ID",
    "SCHEMA_AUTH_TOKEN",
    "API_URL",
]
for var in env_vars:
    if getenv(var) is None:
        raise ValueError(f"{var} is None")

iatlas_config = [
    {
        "name": "cohorts",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "dataset_id",
                "foreign_table_name": "datasets",
                "foreign_column_name": "id",
            },
            {
                "column_name": "cohort_tag_id",
                "foreign_table_name": "tags",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {"column_name": "id", "datatype": "str", "required": True, "index": True},
            {
                "column_name": "dataset_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "cohort_tag_id",
                "datatype": "str",
                "required": False,
                "index": True,
            },
        ],
    },
    {
        "name": "colocalizations",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "dataset_id",
                "foreign_table_name": "datasets",
                "foreign_column_name": "id",
            },
            {
                "column_name": "coloc_dataset_id",
                "foreign_table_name": "datasets",
                "foreign_column_name": "id",
            },
            {
                "column_name": "gene_id",
                "foreign_table_name": "genes",
                "foreign_column_name": "id",
            },
            {
                "column_name": "feature_id",
                "foreign_table_name": "features",
                "foreign_column_name": "id",
            },
            {
                "column_name": "snp_id",
                "foreign_table_name": "snps",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {"column_name": "id", "datatype": "str", "required": True, "index": True},
            {
                "column_name": "dataset_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "coloc_dataset_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "gene_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "feature_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "snp_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "qtl_type",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "ecaviar_pp",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "plot_type",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "splice_loc",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "tissue",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "link",
                "datatype": "str",
                "required": False,
                "index": False,
            },
        ],
    },
    {
        "name": "copy_number_results",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "dataset_id",
                "foreign_table_name": "datasets",
                "foreign_column_name": "id",
            },
            {
                "column_name": "tag_id",
                "foreign_table_name": "tags",
                "foreign_column_name": "id",
            },
            {
                "column_name": "gene_id",
                "foreign_table_name": "genes",
                "foreign_column_name": "id",
            },
            {
                "column_name": "feature_id",
                "foreign_table_name": "features",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {"column_name": "id", "datatype": "str", "required": True, "index": True},
            {
                "column_name": "dataset_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "tag_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "gene_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "feature_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "snp_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "direction",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "mean_normal",
                "datatype": "float",
                "required": False,
                "index": False,
            },
            {
                "column_name": "mean_cnv",
                "datatype": "float",
                "required": False,
                "index": False,
            },
            {
                "column_name": "t_stat",
                "datatype": "float",
                "required": False,
                "index": False,
            },
            {
                "column_name": "p_value",
                "datatype": "float",
                "required": False,
                "index": False,
            },
            {
                "column_name": "log10_p_value",
                "datatype": "float",
                "required": False,
                "index": False,
            },
        ],
    },
    {
        "name": "datasets",
        "primary_key": "id",
        "foreign_keys": None,
        "columns": [
            {
                "column_name": "dataset_type",
                "datatype": "str",
                "required": True,
                "index": True,
            },
        ],
    },
    {
        "name": "driver_results",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "feature_id",
                "foreign_table_name": "features",
                "foreign_column_name": "id",
            },
            {
                "column_name": "dataset_id",
                "foreign_table_name": "datasets",
                "foreign_column_name": "id",
            },
            {
                "column_name": "tag_id",
                "foreign_table_name": "tags",
                "foreign_column_name": "id",
            },
            {
                "column_name": "mutation_id",
                "foreign_table_name": "mutations",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {"column_name": "id", "datatype": "str", "required": True, "index": True},
            {
                "column_name": "feature_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "dataset_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "tag_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "mutation_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "p_value",
                "datatype": "float",
                "required": False,
                "index": True,
            },
            {
                "column_name": "fold_change",
                "datatype": "float",
                "required": False,
                "index": True,
            },
            {
                "column_name": "log10_p_value",
                "datatype": "float",
                "required": False,
                "index": True,
            },
            {
                "column_name": "log10_fold_change",
                "datatype": "float",
                "required": False,
                "index": True,
            },
            {
                "column_name": "n_wildtype",
                "datatype": "int",
                "required": False,
                "index": True,
            },
            {
                "column_name": "n_mutants",
                "datatype": "int",
                "required": False,
                "index": True,
            },
        ],
    },
    {
        "name": "patients",
        "primary_key": "id",
        "foreign_keys": None,
        "columns": [
            {
                "column_name": "age_at_diagnosis",
                "datatype": "int",
                "required": False,
                "index": True,
            },
            {
                "column_name": "height",
                "datatype": "float",
                "required": False,
                "index": True,
            },
            {
                "column_name": "weight",
                "datatype": "float",
                "required": False,
                "index": True,
            },
            {
                "column_name": "ethnicity",
                "datatype": "str",
                "required": False,
                "index": True,
            },
            {
                "column_name": "gender",
                "datatype": "str",
                "required": False,
                "index": True,
            },
            {
                "column_name": "race",
                "datatype": "str",
                "required": False,
                "index": True,
            },
        ],
    },
    {
        "name": "genes",
        "primary_key": "id",
        "foreign_keys": None,
        "columns": [
            {
                "column_name": "entrez_id",
                "datatype": "int",
                "required": True,
                "index": True,
            },
            {
                "column_name": "hgnc_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "gene_family",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "gene_function",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "immune_checkpoint",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "gene_pathway",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "super_category",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "therapy_type",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "io_landscape_name",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "friendly_name",
                "datatype": "str",
                "required": False,
                "index": False,
            },
        ],
    },
    {
        "name": "tags",
        "primary_key": "id",
        "foreign_keys": None,
        "columns": [
            {"column_name": "name", "datatype": "str", "required": True, "index": True},
            {
                "column_name": "short_display",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "long_display",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "description",
                "datatype": "str",
                "required": False,
                "index": True,
            },
            {
                "column_name": "color",
                "datatype": "str",
                "required": False,
                "index": True,
            },
            {
                "column_name": "tag_type",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "order",
                "datatype": "int",
                "required": False,
                "index": True,
            },
        ],
    },
    {
        "name": "samples",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "patient_id",
                "foreign_table_name": "patients",
                "foreign_column_name": "id",
            }
        ],
        "columns": [
            {"column_name": "name", "datatype": "str", "required": True, "index": True},
            {
                "column_name": "patient_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
        ],
    },
    {
        "name": "mutation_types",
        "primary_key": "id",
        "columns": [
            {"column_name": "id", "datatype": "str", "required": True, "index": True},
            {"column_name": "name", "datatype": "str", "required": True, "index": True},
            {
                "column_name": "display",
                "datatype": "str",
                "required": True,
                "index": False,
            },
        ],
    },
    {
        "name": "mutations",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "gene_id",
                "foreign_table_name": "genes",
                "foreign_column_name": "id",
            },
            {
                "column_name": "mutation_type_id",
                "foreign_table_name": "mutation_types",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {"column_name": "id", "datatype": "str", "required": True, "index": True},
            {"column_name": "name", "datatype": "str", "required": True, "index": True},
            {
                "column_name": "mutation_code",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "mutation_type_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "gene_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
        ],
    },
    {
        "name": "features",
        "primary_key": "id",
        "foreign_keys": None,
        "columns": [
            {"column_name": "id", "datatype": "str", "required": True, "index": True},
            {"column_name": "name", "datatype": "str", "required": True, "index": True},
            {
                "column_name": "display",
                "datatype": "str",
                "required": False,
                "index": True,
            },
            {
                "column_name": "order",
                "datatype": "int",
                "required": False,
                "index": False,
            },
            {
                "column_name": "feature_class",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "method_tag",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "unit",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "germline_category",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "germline_module",
                "datatype": "str",
                "required": False,
                "index": False,
            },
        ],
    },
    {
        "name": "publications",
        "primary_key": "id",
        "foreign_keys": None,
        "columns": [
            {"column_name": "id", "datatype": "str", "required": True, "index": True},
            {
                "column_name": "publication_title",
                "datatype": "str",
                "required": False,
                "index": True,
            },
            {
                "column_name": "do_id",
                "datatype": "str",
                "required": False,
                "index": True,
            },
            {
                "column_name": "pubmed_id",
                "datatype": "int",
                "required": False,
                "index": True,
            },
            {
                "column_name": "first_author_last_name",
                "datatype": "str",
                "required": False,
                "index": True,
            },
            {
                "column_name": "journal",
                "datatype": "str",
                "required": False,
                "index": True,
            },
            {
                "column_name": "year",
                "datatype": "int",
                "required": False,
                "index": True,
            },
            {
                "column_name": "link",
                "datatype": "str",
                "required": False,
                "index": True,
            },
        ],
    },
    {
        "name": "nodes",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "node_feature_id",
                "foreign_table_name": "features",
                "foreign_column_name": "id",
            },
            {
                "column_name": "node_gene_id",
                "foreign_table_name": "genes",
                "foreign_column_name": "id",
            },
            {
                "column_name": "dataset_id",
                "foreign_table_name": "datasets",
                "foreign_column_name": "id",
            },
            {
                "column_name": "tag_1_id",
                "foreign_table_name": "tags",
                "foreign_column_name": "id",
            },
            {
                "column_name": "tag_2_id",
                "foreign_table_name": "tags",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "node_feature_id",
                "datatype": "str",
                "required": False,
                "index": True,
            },
            {
                "column_name": "node_gene_id",
                "datatype": "str",
                "required": False,
                "index": True,
            },
            {
                "column_name": "dataset_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "network",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "score",
                "datatype": "float",
                "required": False,
                "index": True,
            },
            {"column_name": "x", "datatype": "float", "required": False, "index": True},
            {"column_name": "y", "datatype": "float", "required": False, "index": True},
            {
                "column_name": "tag_1_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "tag_2_id",
                "datatype": "str",
                "required": False,
                "index": True,
            },
        ],
    },
    {
        "name": "tags_to_tags",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "tag_id",
                "foreign_table_name": "tags",
                "foreign_column_name": "id",
            },
            {
                "column_name": "related_tag_id",
                "foreign_table_name": "tags",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "tag_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "related_tag_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
        ],
    },
    {
        "name": "slides",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "patient_id",
                "foreign_table_name": "patients",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "patient_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
        ],
    },
    {
        "name": "samples_to_tags",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "sample_id",
                "foreign_table_name": "samples",
                "foreign_column_name": "id",
            },
            {
                "column_name": "tag_id",
                "foreign_table_name": "tags",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "sample_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "tag_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
        ],
    },
    {
        "name": "samples_to_mutations",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "sample_id",
                "foreign_table_name": "samples",
                "foreign_column_name": "id",
            },
            {
                "column_name": "mutation_id",
                "foreign_table_name": "mutations",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "sample_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "mutation_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "mutation_status",
                "datatype": "str",
                "required": True,
                "index": True,
            },
        ],
    },
    {
        "name": "rare_variant_pathway_associations",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "feature_id",
                "foreign_table_name": "features",
                "foreign_column_name": "id",
            },
            {
                "column_name": "dataset_id",
                "foreign_table_name": "datasets",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "feature_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "dataset_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "p_value",
                "datatype": "float",
                "required": False,
                "index": False,
            },
            {
                "column_name": "min",
                "datatype": "float",
                "required": False,
                "index": False,
            },
            {
                "column_name": "max",
                "datatype": "float",
                "required": False,
                "index": False,
            },
            {
                "column_name": "mean",
                "datatype": "float",
                "required": False,
                "index": False,
            },
            {
                "column_name": "n_mutants",
                "datatype": "int",
                "required": False,
                "index": False,
            },
            {
                "column_name": "n_total",
                "datatype": "int",
                "required": False,
                "index": False,
            },
            {
                "column_name": "pathway",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "q1",
                "datatype": "float",
                "required": False,
                "index": False,
            },
            {
                "column_name": "q2",
                "datatype": "float",
                "required": False,
                "index": False,
            },
            {
                "column_name": "q3",
                "datatype": "float",
                "required": False,
                "index": False,
            },
        ],
    },
    {
        "name": "tags_to_publications",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "publication_id",
                "foreign_table_name": "publications",
                "foreign_column_name": "id",
            },
            {
                "column_name": "tag_id",
                "foreign_table_name": "tags",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "publication_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "tag_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
        ],
    },
    {
        "name": "publications_to_genes_to_gene_sets",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "publication_id",
                "foreign_table_name": "publications",
                "foreign_column_name": "id",
            },
            {
                "column_name": "gene_id",
                "foreign_table_name": "genes",
                "foreign_column_name": "id",
            },
            {
                "column_name": "gene_set_id",
                "foreign_table_name": "gene_sets",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "publication_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "gene_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "gene_set_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
        ],
    },
    {
        "name": "heritability_results",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "feature_id",
                "foreign_table_name": "features",
                "foreign_column_name": "id",
            },
            {
                "column_name": "dataset_id",
                "foreign_table_name": "datasets",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "feature_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "dataset_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "p_value",
                "datatype": "float",
                "required": False,
                "index": False,
            },
            {
                "column_name": "fdr",
                "datatype": "float",
                "required": False,
                "index": False,
            },
            {
                "column_name": "variance",
                "datatype": "float",
                "required": False,
                "index": False,
            },
            {
                "column_name": "se",
                "datatype": "float",
                "required": False,
                "index": False,
            },
        ],
    },
    {
        "name": "genes_to_samples",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "gene_id",
                "foreign_table_name": "genes",
                "foreign_column_name": "id",
            },
            {
                "column_name": "sample_id",
                "foreign_table_name": "samples",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "gene_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "sample_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "rna_seq_expression",
                "datatype": "float",
                "required": False,
                "index": False,
            },
            {
                "column_name": "nanostring_expression",
                "datatype": "float",
                "required": False,
                "index": False,
            },
        ],
    },
    {
        "name": "genes_to_gene_sets",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "gene_id",
                "foreign_table_name": "genes",
                "foreign_column_name": "id",
            },
            {
                "column_name": "gene_set_id",
                "foreign_table_name": "gene_sets",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "gene_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "gene_set_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
        ],
    },
    {
        "name": "features_to_samples",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "feature_id",
                "foreign_table_name": "features",
                "foreign_column_name": "id",
            },
            {
                "column_name": "sample_id",
                "foreign_table_name": "samples",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "feature_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "sample_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "feature_to_sample_value",
                "datatype": "float",
                "required": True,
                "index": False,
            },
        ],
    },
    {
        "name": "germline_gwas_results",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "feature_id",
                "foreign_table_name": "features",
                "foreign_column_name": "id",
            },
            {
                "column_name": "dataset_id",
                "foreign_table_name": "datasets",
                "foreign_column_name": "id",
            },
            {
                "column_name": "snp_id",
                "foreign_table_name": "snps",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "feature_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "dataset_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "snp_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "p_value",
                "datatype": "float",
                "required": False,
                "index": False,
            },
            {
                "column_name": "maf",
                "datatype": "float",
                "required": False,
                "index": False,
            },
        ],
    },
    {
        "name": "datasets_to_tags",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "dataset_id",
                "foreign_table_name": "datasets",
                "foreign_column_name": "id",
            },
            {
                "column_name": "tag_id",
                "foreign_table_name": "tags",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "dataset_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "tag_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
        ],
    },
    {
        "name": "datasets_to_samples",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "dataset_id",
                "foreign_table_name": "datasets",
                "foreign_column_name": "id",
            },
            {
                "column_name": "sample_id",
                "foreign_table_name": "samples",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "dataset_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "sample_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
        ],
    },
    {
        "name": "cohorts_to_samples",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "cohort_id",
                "foreign_table_name": "cohorts",
                "foreign_column_name": "id",
            },
            {
                "column_name": "sample_id",
                "foreign_table_name": "samples",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "cohort_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "sample_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "cohorts_to_samples_tag_id",
                "datatype": "str",
                "required": False,
                "index": True,
            },
        ],
    },
    {
        "name": "cohorts_to_tags",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "cohort_id",
                "foreign_table_name": "cohorts",
                "foreign_column_name": "id",
            },
            {
                "column_name": "tag_id",
                "foreign_table_name": "tags",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "cohort_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "tag_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
        ],
    },
    {
        "name": "cohorts_to_features",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "cohort_id",
                "foreign_table_name": "cohorts",
                "foreign_column_name": "id",
            },
            {
                "column_name": "feature_id",
                "foreign_table_name": "features",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "cohort_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "feature_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
        ],
    },
    {
        "name": "cohorts_to_genes",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "cohort_id",
                "foreign_table_name": "cohorts",
                "foreign_column_name": "id",
            },
            {
                "column_name": "gene_id",
                "foreign_table_name": "genes",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "cohort_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "gene_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
        ],
    },
    {
        "name": "cohorts_to_mutations",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "cohort_id",
                "foreign_table_name": "cohorts",
                "foreign_column_name": "id",
            },
            {
                "column_name": "mutation_id",
                "foreign_table_name": "mutations",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {
                "column_name": "cohort_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "mutation_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
        ],
    },
    {
        "name": "edges",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "node_1_id",
                "foreign_table_name": "nodes",
                "foreign_column_name": "id",
            },
            {
                "column_name": "node_2_id",
                "foreign_table_name": "nodes",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {"column_name": "id", "datatype": "str", "required": True, "index": True},
            {
                "column_name": "node_1_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {
                "column_name": "node_2_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {"column_name": "name", "datatype": "str", "required": True, "index": True},
            {
                "column_name": "label",
                "datatype": "str",
                "required": False,
                "index": True,
            },
            {
                "column_name": "score",
                "datatype": "float",
                "required": False,
                "index": True,
            },
        ],
    },
    {
        "name": "neoantigens",
        "primary_key": "id",
        "foreign_keys": [
            {
                "column_name": "neoantigen_gene_id",
                "foreign_table_name": "genes",
                "foreign_column_name": "id",
            },
            {
                "column_name": "patient_id",
                "foreign_table_name": "patients",
                "foreign_column_name": "id",
            },
        ],
        "columns": [
            {"column_name": "id", "datatype": "str", "required": True, "index": True},
            {
                "column_name": "neoantigen_gene_id",
                "datatype": "str",
                "required": False,
                "index": True,
            },
            {
                "column_name": "patient_id",
                "datatype": "str",
                "required": True,
                "index": True,
            },
            {"column_name": "pmhc", "datatype": "str", "required": True, "index": True},
            {
                "column_name": "tpm",
                "datatype": "float",
                "required": False,
                "index": True,
            },
            {
                "column_name": "freq_pmhc",
                "datatype": "int",
                "required": True,
                "index": True,
            },
        ],
    },
    {
        "name": "snps",
        "primary_key": "id",
        "columns": [
            {"column_name": "id", "datatype": "str", "required": True, "index": True},
            {"column_name": "name", "datatype": "str", "required": True, "index": True},
            {
                "column_name": "rsid",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {
                "column_name": "chr",
                "datatype": "str",
                "required": False,
                "index": False,
            },
            {"column_name": "bp", "datatype": "int", "required": False, "index": False},
        ],
    },
]

db = PostgresDatabase(
    SQLConfig(
        username=getenv("DB_USER"),
        password=getenv("DB_PW"),
        host=getenv("DB_HOST"),
        name=getenv("DB_NAME"),
    )
)

schema = Schema(
    SchemaConfig(schema_url=getenv("SCHEMA_URL")), DatabaseConfig(iatlas_config)
)


builder = RDBBuilder(db, schema)
builder.build_database()

ms = SynapseManifestStore(
    ManifestStoreConfig(
        schema_url=getenv("SCHEMA_URL"),
        synapse_project_id=getenv("SCHEMA_PROJECT_ID"),
        synapse_asset_view_id=getenv("SCHEMA_ASSET_VIEW_ID"),
        synapse_auth_token=getenv("SCHEMA_AUTH_TOKEN"),
    )
)

updater = RDBUpdater(db, ms)
updater.update_database(method="insert")


def insert_as_chunk(df, db, table_name, chunk_size=20000):
    num_chunks = ((len(df) - 1) // chunk_size) + 1

    for i in range(num_chunks):
        # Getting chunk boundaries
        start_index = i * chunk_size
        end_index = (i + 1) * chunk_size

        # Selecting rows for the current chunk
        chunk = df[start_index:end_index]

        # Inserting rows into the table
        db.insert_table_rows(table_name, chunk)


cohorts_to_samples1 = db.execute_sql_query(
    (
        "SELECT DISTINCT c.id AS cohort_id, t.id AS cohorts_to_samples_tag_id, stt.sample_id "
        "FROM samples s "
        "INNER JOIN samples_to_tags stt ON s.id = stt.sample_id "
        "INNER JOIN tags t ON stt.tag_id = t.id "
        "INNER JOIN tags_to_tags ttt ON t.id = ttt.tag_id "
        "INNER JOIN tags t2 ON ttt.related_tag_id = t2.id "
        "INNER JOIN datasets_to_samples dts on s.id = dts.sample_id "
        "INNER JOIN cohorts c on t2.id = c.cohort_tag_id AND dts.dataset_id = c.dataset_id "
        "WHERE t.tag_type = 'group'"
    )
)
cohorts_to_samples1["id"] = [
    uuid.uuid1() for _ in range(len(cohorts_to_samples1.index))
]
insert_as_chunk(cohorts_to_samples1, db, "cohorts_to_samples")
# db.insert_table_rows("cohorts_to_samples", cohorts_to_samples1)


cohorts_to_samples2 = db.execute_sql_query(
    (
        "SELECT DISTINCT c.id AS cohort_id, dts.sample_id "
        "FROM cohorts c "
        "INNER JOIN datasets_to_samples dts ON c.dataset_id = dts.dataset_id "
        "WHERE c.cohort_tag_id is NULL"
    )
)
cohorts_to_samples2["id"] = [
    uuid.uuid1() for _ in range(len(cohorts_to_samples2.index))
]
insert_as_chunk(cohorts_to_samples2, db, "cohorts_to_samples")
# db.insert_table_rows("cohorts_to_samples", cohorts_to_samples2)


cohorts_to_features = db.execute_sql_query(
    (
        "SELECT DISTINCT cohort_id, feature_id "
        "FROM cohorts_to_samples cts "
        "INNER JOIN features_to_samples fts USING(sample_id)"
    )
)
cohorts_to_features["id"] = [
    uuid.uuid1() for _ in range(len(cohorts_to_features.index))
]
insert_as_chunk(cohorts_to_features, db, "cohorts_to_features")
# db.insert_table_rows("cohorts_to_features", cohorts_to_features)


cohorts_to_genes = db.execute_sql_query(
    (
        "SELECT DISTINCT cohort_id, gene_id "
        "FROM cohorts_to_samples cts "
        "INNER JOIN genes_to_samples gts USING(sample_id)"
    )
)
cohorts_to_genes["id"] = [uuid.uuid1() for _ in range(len(cohorts_to_genes.index))]
insert_as_chunk(cohorts_to_genes, db, "cohorts_to_genes")
# db.insert_table_rows("cohorts_to_genes", cohorts_to_genes)


cohorts_to_mutations = db.execute_sql_query(
    (
        "SELECT DISTINCT cohort_id, mutation_id "
        "FROM cohorts_to_samples cts "
        "INNER JOIN samples_to_mutations stm USING(sample_id)"
    )
)
cohorts_to_mutations["id"] = [
    uuid.uuid1() for _ in range(len(cohorts_to_mutations.index))
]
insert_as_chunk(cohorts_to_mutations, db, "cohorts_to_mutations")
# db.insert_table_rows("cohorts_to_mutations", cohorts_to_mutations)

cohorts_to_tags = db.execute_sql_query(
    (
        "SELECT DISTINCT cohort_id, tag_id "
        "FROM cohorts_to_samples cts "
        "INNER JOIN samples_to_tags sts USING(sample_id)"
    )
)
cohorts_to_tags["id"] = [uuid.uuid1() for _ in range(len(cohorts_to_tags.index))]
insert_as_chunk(cohorts_to_tags, db, "cohorts_to_tags")
# db.insert_table_rows("cohorts_to_tags", cohorts_to_tags)
