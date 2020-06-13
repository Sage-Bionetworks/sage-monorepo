from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import (Gene, GeneFamily, GeneFunction, GeneType,
                              ImmuneCheckpoint, NodeType, Pathway, SuperCategory, TherapyType)
from .database_helpers import general_core_fields, build_general_query

accepted_gene_option_args = ['copy_number_results',
                             'gene_family',
                             'gene_function',
                             'gene_types',
                             'immune_checkpoint',
                             'node_type',
                             'pathway',
                             'samples',
                             'super_category',
                             'therapy_type']

accepted_gene_query_args = ['entrez',
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


def return_gene_query(*args):
    return build_general_query(
        Gene, args=args,
        accepted_option_args=accepted_gene_option_args,
        accepted_query_args=accepted_gene_query_args)


def return_gene_family_query(*args):
    return build_general_query(
        GeneFamily, args=args,
        accepted_query_args=general_core_fields)


def return_gene_function_query(*args):
    return build_general_query(
        GeneFunction, args=args,
        accepted_query_args=general_core_fields)


def return_gene_type_query(*args):
    return build_general_query(
        GeneType, args=args,
        accepted_query_args=general_core_fields)


def return_immune_checkpoint_query(*args):
    return build_general_query(
        ImmuneCheckpoint, args=args,
        accepted_query_args=general_core_fields)


def return_node_type_query(*args):
    return build_general_query(
        NodeType, args=args,
        accepted_query_args=general_core_fields)


def return_pathway_query(*args):
    return build_general_query(
        Pathway, args=args,
        accepted_query_args=general_core_fields)


def return_super_category_query(*args):
    return build_general_query(
        SuperCategory, args=args,
        accepted_query_args=general_core_fields)


def return_therapy_type_query(*args):
    return build_general_query(
        TherapyType, args=args,
        accepted_query_args=general_core_fields)
