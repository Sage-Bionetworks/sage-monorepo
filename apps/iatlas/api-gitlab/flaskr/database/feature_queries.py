from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import FeatureClass, Feature, MethodTag
from .database_helpers import accepted_simple_table_query_args, build_general_query

accepted_feature_option_args = ['feature_class', 'method_tag', 'samples']

accepted_feature_query_args = [
    'id', 'name', 'display', 'order', 'unit', 'class_id', 'method_tag_id']


def return_feature_class_query(*args):
    return build_general_query(
        FeatureClass, args=args,
        accepted_query_args=accepted_simple_table_query_args)


def return_feature_query(*args):
    return build_general_query(
        Feature, args=args,
        accepted_option_args=accepted_feature_option_args,
        accepted_query_args=accepted_feature_query_args)


def return_method_tag_query(*args):
    return build_general_query(
        MethodTag, args=args,
        accepted_query_args=accepted_simple_table_query_args)
