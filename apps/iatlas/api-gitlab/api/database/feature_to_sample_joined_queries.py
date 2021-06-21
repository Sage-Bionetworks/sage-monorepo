from api.db_models import FeatureToSampleJoined
from .database_helpers import build_general_query

feature_to_sample_joined_related_fields = ['features', 'samples']

feature_to_sample_joined_core_fields = ['feature_id', 'sample_id', 'value',
                                        'feature_name', 'feature_display', 'feature_class', 'feature_order']


def return_feature_to_sample_joined_query(*args):
    return build_general_query(
        FeatureToSampleJoined, args=args,
        accepted_option_args=feature_to_sample_joined_related_fields,
        accepted_query_args=feature_to_sample_joined_core_fields)
