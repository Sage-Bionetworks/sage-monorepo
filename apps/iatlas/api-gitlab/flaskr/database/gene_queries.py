from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import (Gene, GeneFamily, GeneFunction, GeneType,
                              ImmuneCheckpoint, NodeType, Pathway, SuperCategory, TherapyType)
from . import build_option_args


def return_gene_query(*args):
    option_args = build_option_args(*args, accepted_args=['gene_family',
                                                          'gene_function',
                                                          'immune_checkpoint',
                                                          'node_type',
                                                          'pathway',
                                                          'super_category',
                                                          'therapy_type'])

    return db.session.query(Gene).options(*option_args)


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
