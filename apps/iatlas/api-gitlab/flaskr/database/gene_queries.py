from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import (Gene, GeneFamily, GeneFunction, GeneType,
                              ImmuneCheckpoint, NodeType, Pathway, SuperCategory, TherapyType)
from .database_helpers import general_core_fields, build_general_query

gene_related_fields = ['copy_number_results',
                       'driver_results',
                       'gene_family',
                       'gene_function',
                       'gene_sample_assoc',
                       'gene_type_assoc',
                       'gene_types',
                       'immune_checkpoint',
                       'node_type',
                       'pathway',
                       'samples',
                       'super_category',
                       'therapy_type']

gene_core_fields = ['entrez',
                    'hgnc',
                    'description',
                    'friendly_name',
                    'io_landscape_name',
                    'references',
                    'gene_family_id',
                    'gene_function_id',
                    'immune_checkpoint_id',
                    'node_type_id',
                    'pathway_id',
                    'super_cat_id',
                    'therapy_type_id']

gene_type_related_fields = ['gene_type_assoc', 'genes']

sub_related_fields = ['genes']


def return_gene_query(*args):
    return build_general_query(
        Gene, args=args,
        accepted_option_args=gene_related_fields,
        accepted_query_args=gene_core_fields)


def return_gene_family_query(*args):
    return build_general_query(
        GeneFamily, args=args,
        accepted_option_args=sub_related_fields,
        accepted_query_args=general_core_fields)


def return_gene_function_query(*args):
    return build_general_query(
        GeneFunction, args=args,
        accepted_option_args=sub_related_fields,
        accepted_query_args=general_core_fields)


def return_gene_type_query(*args):
    return build_general_query(
        GeneType, args=args,
        accepted_option_args=gene_type_related_fields,
        accepted_query_args=general_core_fields)


def return_immune_checkpoint_query(*args):
    return build_general_query(
        ImmuneCheckpoint, args=args,
        accepted_option_args=sub_related_fields,
        accepted_query_args=general_core_fields)


def return_node_type_query(*args):
    return build_general_query(
        NodeType, args=args,
        accepted_option_args=sub_related_fields,
        accepted_query_args=general_core_fields)


def return_pathway_query(*args):
    return build_general_query(
        Pathway, args=args,
        accepted_option_args=sub_related_fields,
        accepted_query_args=general_core_fields)


def return_super_category_query(*args):
    return build_general_query(
        SuperCategory, args=args,
        accepted_option_args=sub_related_fields,
        accepted_query_args=general_core_fields)


def return_therapy_type_query(*args):
    return build_general_query(
        TherapyType, args=args,
        accepted_option_args=sub_related_fields,
        accepted_query_args=general_core_fields)
