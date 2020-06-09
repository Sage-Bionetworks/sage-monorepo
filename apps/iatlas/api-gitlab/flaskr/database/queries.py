from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import *


def return_copy_number_results_query():
    return db.session.query(CopyNumberResult)


def return_copy_number_results_with_relations_query():
    query = return_copy_number_results_query()
    return query.options(orm.joinedload(
        'feature'), orm.joinedload(
        'gene'), orm.joinedload(
        'tag'))


def return_driver_results_query():
    return db.session.query(DriverResult)


def return_driver_results_with_relations_query():
    query = return_driver_results_query()
    return query.options(orm.joinedload(
        'feature'), orm.joinedload(
        'gene'), orm.joinedload(
        'mutation_code'), orm.joinedload(
        'tag'))


def return_gene_query():
    return db.session.query(Gene)


def return_gene_with_relations_query():
    query = return_gene_query()
    return query.options(orm.joinedload(
        'gene_family'), orm.joinedload(
        'gene_function'), orm.joinedload(
        'immune_checkpoint'), orm.joinedload(
        'node_type'), orm.joinedload(
        'pathway'), orm.joinedload(
        'super_category'), orm.joinedload(
        'therapy_type'))
