from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import TagToTag
from .database_helpers import build_option_args


def return_tag_to_tag_query(*argv):
    args = build_option_args(argv, accepted_args=['related_tags', 'tags'])
    return db.session.query(TagToTag).options(*args)
