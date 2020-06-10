from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import SampleToMutation


def return_sample_to_mutation_query(*argv):
    accepted_args = ['samples', 'mutations']
    args = []
    for arg in argv:
        if arg in accepted_args:
            args.append(orm.joinedload(arg))
    return db.session.query(SampleToMutation).options(args)
