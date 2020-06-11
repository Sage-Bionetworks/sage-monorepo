from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import CopyNumberResult, DriverResult
from .database_helpers import build_option_args


def return_copy_number_result_query(*argv):
    args = build_option_args(argv, accepted_args=['feature', 'gene', 'tag'])
    return db.session.query(CopyNumberResult).options(*args)


def return_driver_result_query(*argv):
    args = build_option_args(argv, accepted_args=[
                             'feature', 'gene', 'mutation_code', 'tag'])
    return db.session.query(DriverResult).options(*args)
