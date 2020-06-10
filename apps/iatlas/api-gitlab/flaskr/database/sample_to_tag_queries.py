from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import SampleToTag


def return_sample_to_tag_query(*argv):
    accepted_args = ['samples', 'tags']
    args = []
    for arg in argv:
        if arg in accepted_args:
            args.append(orm.joinedload(arg))
    return db.session.query(SampleToTag).options(args)
