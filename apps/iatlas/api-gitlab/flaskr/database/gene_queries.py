from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import (Gene, GeneFamily, GeneFunction, GeneType,
                              ImmuneCheckpoint, NodeType, Pathway, SuperCategory, TherapyType)
from .database_helpers import build_option_args, build_query_args

accepted_gene_option_args = ['gene_family',
                             'gene_function',
                             'immune_checkpoint',
                             'node_type',
                             'pathway',
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
    option_args = build_option_args(
        *args, accepted_args=accepted_gene_option_args)
    query_args = build_query_args(
        Gene, *args, accepted_args=accepted_gene_query_args)
    query = db.session.query(*query_args)
    if option_args:
        query = db.session.query(Gene).options(*option_args)
    return query


def return_gene_family_query():
    return db.session.query(GeneFamily)


def return_gene_function_query():
    return db.session.query(GeneFunction)


def return_gene_type_query():
    return db.session.query(GeneType)


def return_immune_checkpoint_query():
    return db.session.query(ImmuneCheckpoint)


def return_node_type_query():
    return db.session.query(NodeType)


def return_pathway_query():
    return db.session.query(Pathway)


def return_super_category_query():
    return db.session.query(SuperCategory)


def return_therapy_type_query():
    return db.session.query(TherapyType)
