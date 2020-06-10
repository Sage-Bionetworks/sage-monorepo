from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import Mutation, MutationCode, MutationType


def return_mutation_query(*argv):
    accepted_args = ['gene', 'mutation_code', 'mutation_type', 'samples']
    args = []
    for arg in argv:
        if arg in accepted_args:
            args.append(orm.joinedload(arg))
    return db.session.query(Mutation).options(args)


def return_mutation_code_query():
    return db.session.query(MutationCode)


def return_mutation_type_query():
    return db.session.query(MutationType)
