from sqlalchemy import orm

accepted_simple_table_query_args = ['id', 'name']


def build_option_args(*args, accepted_args=[]):
    option_args = []
    for arg in args:
        if arg in accepted_args:
            option_args.append(orm.joinedload(arg))
    return option_args


def build_query_args(model, *argv, accepted_args=[]):
    if argv:
        query_args = []
        for arg in argv:
            if arg in accepted_args:
                query_args.append(getattr(model, arg))
        return query_args
    return model
