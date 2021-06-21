from sqlalchemy import and_
from sqlalchemy.orm import aliased
from sqlalchemy.sql.expression import false, true
from api import db
from api.db_models import (FeatureToSampleJoined, Cohort, CohortToSample)
from .general_resolvers import build_join_condition, get_selected, get_value
from .sample import build_sample_graphql_response
from .feature import build_feature_graphql_response
from .paging_utils import get_pagination_queries
from api.telemetry import profile

feature_value_request_fields = {'value', 'sample', 'feature'}


def build_feature_value_graphql_response(feature_value):
    response_dict = {
        'id': get_value(feature_value, 'id'),
        'value': get_value(feature_value, 'value'),
        'sample': {
            'name': get_value(feature_value, 'sample_name'),
        },
        'feature': {
            'class': get_value(feature_value, 'feature_class'),
            'display': get_value(feature_value, 'feature_display'),
            'name': get_value(feature_value, 'feature_name'),
            'order': get_value(feature_value, 'feature_order')
        }
    }
    return(response_dict)


def build_feature_values_query(requested, feature_requested, sample_requested, distinct=False, paging=None, feature=None, feature_class=None, max_value=None, min_value=None, sample=None, cohort=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    feature_to_sample_1 = aliased(FeatureToSampleJoined, name='fts')

    cohort_1 = aliased(Cohort, name='c')
    cohort_to_sample_1 = aliased(CohortToSample, name='cts')

    core_field_mapping = {
        'id': feature_to_sample_1.id.label('id'),
        'value': feature_to_sample_1.value.label('value'),
    }

    feature_core_field_mapping = {
        'name': feature_to_sample_1.feature_name.label('feature_name'),
        'display': feature_to_sample_1.feature_display.label('feature_display'),
        'class': feature_to_sample_1.class_name.label('feature_class'),
        'order': feature_to_sample_1.feature_order.label('feature_order')
    }

    sample_core_field_mapping = {
        'name': feature_to_sample_1.sample_name.label('sample_name'),
    }

    core = get_selected(requested, core_field_mapping)
    core |= {feature_to_sample_1.id.label('id')}
    core |= get_selected(feature_requested, feature_core_field_mapping)
    core |= get_selected(sample_requested, sample_core_field_mapping)

    query = sess.query(*core)
    query = query.select_from(feature_to_sample_1)

    if max_value:
        query = query.filter(feature_to_sample_1.value <= max_value)

    if min_value:
        query = query.filter(feature_to_sample_1.value >= min_value)

    if feature:
        query = query.filter(feature_to_sample_1.feature_name.in_(feature))

    if feature_class:
        query = query.filter(feature_to_sample_1.class_name.in_(feature_class))

    if sample:
        query = query.filter(feature_to_sample_1.sample_name.in_(sample))

    if cohort:

        cohort_to_sample_subquery = sess.query(cohort_to_sample_1.sample_id)

        cohort_join_condition = build_join_condition(
            cohort_to_sample_1.cohort_id, cohort_1.id, filter_column=cohort_1.name, filter_list=cohort)
        cohort_to_sample_subquery = cohort_to_sample_subquery.join(cohort_1, and_(
            *cohort_join_condition), isouter=False)

        query = query.filter(
            feature_to_sample_1.sample_id.in_(cohort_to_sample_subquery))

    return get_pagination_queries(query, paging, distinct, cursor_field=feature_to_sample_1.id)
