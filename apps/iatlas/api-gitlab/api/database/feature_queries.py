from sqlalchemy import orm
from api import db
from api.db_models import FeatureClass, Feature, MethodTag
from .database_helpers import general_core_fields, build_general_query

feature_related_fields = [
    'copy_number_results', 'driver_results',
    'feature_class', 'feature_sample_assoc', 'method_tag', 'samples']

feature_core_fields = [
    'id', 'name', 'display', 'order', 'unit', 'class_id', 'method_tag_id', 'germline_category', 'germline_module']


def return_feature_class_query(*args):
    return build_general_query(
        FeatureClass, args=args,
        accepted_option_args=['features'],
        accepted_query_args=general_core_fields)


def return_feature_query(*args):
    return build_general_query(
        Feature, args=args,
        accepted_option_args=feature_related_fields,
        accepted_query_args=feature_core_fields)


def return_method_tag_query(*args):
    return build_general_query(
        MethodTag, args=args,
        accepted_option_args=['features'],
        accepted_query_args=general_core_fields)
