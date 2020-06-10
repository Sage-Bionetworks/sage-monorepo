from sqlalchemy import orm


def build_option_args(*args, accepted_args=[]):
    option_args = []
    for arg in args:
        if arg in accepted_args:
            option_args.append(orm.joinedload(arg))
    return option_args
