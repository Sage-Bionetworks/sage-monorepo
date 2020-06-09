from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import CopyNumberResult, DriverResult


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
