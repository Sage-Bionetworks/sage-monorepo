from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import FeatureClass, Feature, MethodTag
from .database_helpers import general_core_fields, build_general_query

related_fields = [
    'copy_number_results', 'feature_class', 'method_tag', 'samples']

core_fields = [
    'id', 'name', 'display', 'order', 'unit', 'class_id', 'method_tag_id']


def return_feature_class_query(*args):
    return build_general_query(
        FeatureClass, args=args,
        accepted_query_args=general_core_fields)


def return_feature_query(*args):
    return build_general_query(
        Feature, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)


def return_method_tag_query(*args):
    return build_general_query(
        MethodTag, args=args,
        accepted_query_args=general_core_fields)
