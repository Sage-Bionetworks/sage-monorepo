from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import FeatureClass, Feature, MethodTag
from .database_helpers import accepted_simple_table_query_args, build_query_args


def return_feature_class_query(*args):
    query_args = build_query_args(
        FeatureClass, *args, accepted_args=accepted_simple_table_query_args)
    if query_args:
        return db.session.query(*query_args)
    return db.session.query(FeatureClass)


def return_feature_query():
    return db.session.query(Feature)


def return_method_tag_query():
    return db.session.query(MethodTag)
