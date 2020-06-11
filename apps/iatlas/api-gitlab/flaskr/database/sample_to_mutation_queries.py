from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import SampleToMutation
from .database_helpers import build_option_args


def return_sample_to_mutation_query(*argv):
    args = build_option_args(argv, accepted_args=['samples', 'mutations'])
    return db.session.query(SampleToMutation).options(*args)
