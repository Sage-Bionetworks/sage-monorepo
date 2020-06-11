from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import SampleToTag
from . import build_option_args


def return_sample_to_tag_query(*argv):
    args = build_option_args(argv, accepted_args=['samples', 'tags'])
    return db.session.query(SampleToTag).options(*args)
