from sqlalchemy import orm
from flaskr import db

accepted_simple_table_query_args = ['id', 'name']


def build_general_query(model, args=[], accepted_option_args=[], accepted_query_args=[]):
    option_args = build_option_args(*args, accepted_args=accepted_option_args)
    query_args = build_query_args(
        model, *args, accepted_args=accepted_query_args)
    query = db.session.query(*query_args)
    if option_args:
        # If option args are found, the whole model must be queried.
        return db.session.query(model).options(*option_args)
    return db.session.query(*query_args)


def build_option_args(*args, accepted_args=[]):
    option_args = []
    for arg in args:
        if arg in accepted_args:
            option_args.append(orm.joinedload(arg))
    return option_args


def build_query_args(model, *argv, accepted_args=[]):
    query_args = []
    for arg in argv:
        if arg in accepted_args:
            query_args.append(getattr(model, arg))
    if not query_args:
        return [model]
    return query_args
