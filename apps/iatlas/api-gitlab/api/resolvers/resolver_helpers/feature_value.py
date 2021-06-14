from sqlalchemy import and_
from sqlalchemy.orm import aliased
from sqlalchemy.sql.expression import false, true
from api import db
from api.db_models import (Feature, FeatureClass, FeatureToSample,
                           MethodTag, Sample, Cohort, CohortToSample)
from .general_resolvers import build_join_condition, get_selected, get_value
from .sample import build_sample_graphql_response
from .feature import build_feature_graphql_response
from .paging_utils import get_pagination_queries


feature_value_request_fields = {'value', 'sample', 'feature'}


def build_feature_value_graphql_response(feature_value):
    response_dict = {
        'id': get_value(feature_value, 'id'),
        'value': get_value(feature_value, 'value'),
        'sample': {
            'name': get_value(feature_value, 'sample_name'),
        },
        'feature': {
            'name': get_value(feature_value, 'feature_name'),
            'display': get_value(feature_value, 'feature_display'),
            'unit': get_value(feature_value, 'feature_unit'),
            'order': get_value(feature_value, 'feature_order'),
            'germlineModule': get_value(feature_value, 'feature_germline_module'),
            'germlineCategory': get_value(feature_value, 'feature_germline_category'),
        }
    }
    return(response_dict)


def build_feature_values_query(requested, feature_requested, sample_requested, distinct=False, paging=None, feature=None, feature_class=None, max_value=None, min_value=None, sample=None, cohort=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    feature_to_sample_1 = aliased(FeatureToSample, name='fts')
    feature_1 = aliased(Feature, name='f')
    sample_1 = aliased(Sample, name='s')

    feature_class_1 = aliased(FeatureClass, name='fc')
    method_tag_1 = aliased(MethodTag, name='mt')

    cohort_1 = aliased(Cohort, name='c')
    cohort_to_sample_1 = aliased(CohortToSample, name='cts')

    core_field_mapping = {
        'id': feature_to_sample_1.id.label('id'),
        'value': feature_to_sample_1.value.label('value'),
    }

    feature_core_field_mapping = {
        'class': feature_class_1.name.label('feature_class'),
        'display': feature_1.display.label('feature_display'),
        'methodTag': method_tag_1.name.label('feature_method_tag'),
        'name': feature_1.name.label('feature_name'),
        'order': feature_1.order.label('feature_order'),
        'germlineModule': feature_1.germline_module.label('feature_germline_module'),
        'germlineCategory': feature_1.germline_category.label('feature_germline_category'),
        'unit': feature_1.unit.label('feature_unit')
    }

    sample_core_field_mapping = {
        'name': sample_1.name.label('sample_name'),
    }

    core = get_selected(requested, core_field_mapping)
    core |= {feature_to_sample_1.id.label('id')}
    core |= get_selected(feature_requested, feature_core_field_mapping)
    core |= get_selected(sample_requested, sample_core_field_mapping)

    query = sess.query(*core)
    query = query.select_from(feature_to_sample_1)

    feature_join_condition = build_join_condition(
        feature_to_sample_1.feature_id, feature_1.id)

    query = query.join(
        feature_1, and_(*feature_join_condition))

    sample_join_condition = build_join_condition(
        feature_to_sample_1.sample_id, sample_1.id)

    query = query.join(
        sample_1, and_(*sample_join_condition))

    if max_value:
        query = query.filter(feature_to_sample_1.value <= max_value)

    if min_value:
        query = query.filter(feature_to_sample_1.value >= min_value)

    if feature:
        query = query.filter(feature_1.name.in_(feature))

    if feature_class or 'featureClass' in requested:
        is_outer = not bool(feature_class)
        feature_class_join_condition = build_join_condition(
            feature_class_1.id, feature_1.class_id, filter_column=feature_class_1.name, filter_list=feature_class)
        query = query.join(feature_class_1, and_(
            *feature_class_join_condition), isouter=is_outer)

    if 'featureMethodTag' in requested:
        method_tag_join_condition = build_join_condition(
            method_tag_1.id, feature_1.method_tag_id)
        query = query.join(method_tag_1, and_(
            *method_tag_join_condition), isouter=True)

    if sample:
        query = query.filter(sample_1.name.in_(sample))

    if cohort:

        cohort_to_sample_subquery = sess.query(cohort_to_sample_1.sample_id)

        cohort_join_condition = build_join_condition(
            cohort_to_sample_1.cohort_id, cohort_1.id, filter_column=cohort_1.name, filter_list=cohort)
        cohort_to_sample_subquery = cohort_to_sample_subquery.join(cohort_1, and_(
            *cohort_join_condition), isouter=False)

        query = query.filter(
            sample_1.id.in_(cohort_to_sample_subquery))

    return get_pagination_queries(query, paging, distinct, cursor_field=feature_to_sample_1.id)
