from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import CopyNumberResult, DriverResult


def return_copy_number_result_query(*argv):
    accepted_args = ['feature', 'gene', 'tag']
    args = []
    for arg in argv:
        if arg in accepted_args:
            args.append(orm.joinedload(arg))
    return db.session.query(CopyNumberResult).options(args)


def return_driver_result_query(*argv):
    accepted_args = ['feature', 'gene', 'mutation_code', 'tag']
    args = []
    for arg in argv:
        if arg in accepted_args:
            args.append(orm.joinedload(arg))
    return db.session.query(DriverResult).options(args)
