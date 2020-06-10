from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import Tag


def return_tag_query(*argv):
    accepted_args = ['related_tags', 'samples', 'tags']
    args = []
    for arg in argv:
        if arg in accepted_args:
            args.append(orm.joinedload(arg))
    return db.session.query(Tag).options(args)
