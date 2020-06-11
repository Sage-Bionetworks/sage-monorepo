from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import Mutation, MutationCode, MutationType
from .database_helpers import build_option_args


def return_mutation_query(*argv):
    args = build_option_args(argv, accepted_args=[
                             'gene', 'mutation_code', 'mutation_type', 'samples'])
    return db.session.query(Mutation).options(*args)


def return_mutation_code_query():
    return db.session.query(MutationCode)


def return_mutation_type_query():
    return db.session.query(MutationType)
