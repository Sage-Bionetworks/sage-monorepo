from itertools import groupby
from sqlalchemy import and_, func
from sqlalchemy.orm import aliased
from sqlalchemy.sql.expression import false, true
from api import db
from api.db_models import (Feature, FeatureClass, FeatureToSample,
                           MethodTag, Sample, Cohort, CohortToSample, CohortToFeature)
from .general_resolvers import build_join_condition, get_selected, get_value
from .paging_utils import get_pagination_queries, fetch_page

feature_class_request_fields = {'name'}

simple_feature_request_fields = {'display',
                                 'name',
                                 'order',
                                 'unit',
                                 'germlineModule',
                                 'germlineCategory'}

feature_request_fields = simple_feature_request_fields.union({'class',
                                                              'methodTag',
                                                              'samples',
                                                              'valueMax',
                                                              'valueMin'})


def build_feature_graphql_response(max_min_dict=dict(), sample_dict=dict()):

    def f(feature):
        if not feature:
            return None
        feature_id = get_value(feature, 'feature_id')
        max_min = max_min_dict.get(
            feature_id, dict()) if max_min_dict else dict()
        samples = sample_dict.get(feature_id, []) if sample_dict else []
        result = {
            'id': feature_id,
            'class': get_value(feature, 'feature_class'),
            'display': get_value(feature, 'feature_display'),
            'methodTag': get_value(feature, 'feature_method_tag'),
            'name': get_value(feature, 'feature_name'),
            'order': get_value(feature, 'feature_order'),
            'germlineModule': get_value(feature, 'feature_germline_module'),
            'germlineCategory': get_value(feature, 'feature_germline_category'),
            'unit': get_value(feature, 'feature_unit'),
            'samples': [{
                'name': get_value(sample),
                'value': get_value(sample, 'value')
            } for sample in samples],
            'valueMax': max_min.get('value_max') if max_min else None,
            'valueMin': max_min.get('value_min') if max_min else None
        }
        return(result)
    return f


def build_features_query(requested, distinct=False, paging=None, feature=None, feature_class=None, max_value=None, min_value=None, sample=None, cohort=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    has_min_max = 'valueMax' in requested or 'valueMin' in requested

    feature_1 = aliased(Feature, name='f')
    feature_class_1 = aliased(FeatureClass, name='fc')
    feature_to_sample_1 = aliased(FeatureToSample, name='fts')
    method_tag_1 = aliased(MethodTag, name='mt')
    sample_1 = aliased(Sample, name='s')
    cohort_1 = aliased(Cohort, name='c')
    cohort_to_feature_1 = aliased(CohortToFeature, name='ctf')

    core_field_mapping = {
        'id': feature_1.id.label('feature_id'),
        'class': feature_class_1.name.label('feature_class'),
        'display': feature_1.display.label('feature_display'),
        'methodTag': method_tag_1.name.label('feature_method_tag'),
        'name': feature_1.name.label('feature_name'),
        'order': feature_1.order.label('feature_order'),
        'germlineModule': feature_1.germline_module.label('feature_germline_module'),
        'germlineCategory': feature_1.germline_category.label('feature_germline_category'),
        'unit': feature_1.unit.label('feature_unit')
    }

    core = get_selected(requested, core_field_mapping)
    core |= {feature_1.id.label('feature_id')}

    query = sess.query(*core)
    query = query.select_from(feature_1)

    if feature:
        query = query.filter(feature_1.name.in_(feature))

    if feature_class or 'class' in requested:
        is_outer = not bool(feature_class)
        feature_class_join_condition = build_join_condition(
            feature_class_1.id, feature_1.class_id, filter_column=feature_class_1.name, filter_list=feature_class)
        query = query.join(feature_class_1, and_(
            *feature_class_join_condition), isouter=is_outer)

    if 'methodTag' in requested:
        method_tag_join_condition = build_join_condition(
            method_tag_1.id, feature_1.method_tag_id)
        query = query.join(method_tag_1, and_(
            *method_tag_join_condition), isouter=True)

    if has_min_max or sample:
        feature_to_sample_subquery = sess.query(feature_to_sample_1.feature_id)

        if max_value:
            feature_to_sample_subquery = feature_to_sample_subquery.filter(
                feature_to_sample_1.value <= max_value)

        if min_value:
            feature_to_sample_subquery = feature_to_sample_subquery.filter(
                feature_to_sample_1.value >= min_value)

        if sample:

            sample_join_condition = build_join_condition(
                feature_to_sample_1.sample_id, sample_1.id, filter_column=sample_1.name, filter_list=sample)
            cohort_subquery = feature_to_sample_subquery.join(sample_1, and_(
                *sample_join_condition), isouter=False)

            feature_to_sample_subquery = feature_to_sample_subquery.filter(
                sample_1.name.in_(sample))

        query = query.filter(feature_1.id.in_(feature_to_sample_subquery))

    if cohort:
        cohort_subquery = sess.query(cohort_to_feature_1.feature_id)

        cohort_join_condition = build_join_condition(
            cohort_to_feature_1.cohort_id, cohort_1.id, filter_column=cohort_1.name, filter_list=cohort)
        cohort_subquery = cohort_subquery.join(cohort_1, and_(
            *cohort_join_condition), isouter=False)

        query = query.filter(feature_1.id.in_(cohort_subquery))

    if 'samples' not in requested:
        order = []
        append_to_order = order.append
        if 'class' in requested:
            append_to_order(feature_class_1.name)
        if 'order' in requested:
            append_to_order(feature_1.order)
        if 'display' in requested:
            append_to_order(feature_1.display)
        if 'name' in requested:
            append_to_order(feature_1.name)
        if not order:
            append_to_order(feature_1.id)

    return get_pagination_queries(query, paging, distinct, cursor_field=feature_1.id)


def get_samples(requested, sample_requested, distinct, paging, max_value=None, min_value=None, feature=None, feature_class=None, cohort=None, sample=None, feature_ids=set()):
    has_samples = 'samples' in requested
    has_max_min = 'valueMax' in requested or 'valueMin' in requested

    if (has_samples or has_max_min):
        sess = db.session

        feature_to_sample_1 = aliased(FeatureToSample, name='fts')
        sample_1 = aliased(Sample, name='s')
        cohort_1 = aliased(Cohort, name='c')
        cohort_to_sample_1 = aliased(CohortToSample, name='cts')

        sample_core_field_mapping = {'name': sample_1.name.label('name')}

        sample_core = get_selected(sample_requested, sample_core_field_mapping)
        # Always select the sample id and the feature id.
        sample_core |= {sample_1.id.label(
            'id'), feature_to_sample_1.feature_id.label('feature_id')}

        if has_max_min or 'value' in sample_requested:
            sample_core |= {feature_to_sample_1.value.label('value')}

        sample_query = sess.query(*sample_core)
        sample_query = sample_query.select_from(sample_1)

        if sample:
            sample_query = sample_query.filter(sample_1.name.in_(sample))

        if not feature_ids:
            query, _count_query = build_features_query(
                set(), distinct=distinct, paging=paging, feature=feature, feature_class=feature_class, max_value=max_value, min_value=min_value, sample=sample, cohort=cohort)

            res = fetch_page(query, paging, distinct)
            features = list(set(feature.feature_id for feature in res)
                            ) if len(res) > 0 else []
        else:
            features = feature_ids

        feature_sample_join_condition = build_join_condition(
            feature_to_sample_1.sample_id, sample_1.id, feature_to_sample_1.feature_id, features)

        if max_value:
            feature_sample_join_condition.append(
                feature_to_sample_1.value <= max_value)

        if min_value:
            feature_sample_join_condition.append(
                feature_to_sample_1.value >= min_value)

        sample_query = sample_query.join(
            feature_to_sample_1, and_(*feature_sample_join_condition))

        if cohort:
            cohort_subquery = sess.query(cohort_to_sample_1.sample_id)

            cohort_join_condition = build_join_condition(
                cohort_to_sample_1.cohort_id, cohort_1.id, filter_column=cohort_1.name, filter_list=cohort)
            cohort_subquery = cohort_subquery.join(cohort_1, and_(
                *cohort_join_condition), isouter=False)

            sample_query = sample_query.filter(
                sample_1.id.in_(cohort_subquery))

        samples = sample_query.distinct().all()
        return samples

    return []


def request_features(requested, class_requested, tag_requested, distinct, paging, data_set=None, feature=None, feature_class=None, max_value=None, min_value=None,
                     related=None, sample=None, tag=None, by_class=False, by_tag=False):
    query, count_query = build_features_query(requested, class_requested, tag_requested, distinct, paging, data_set=data_set, feature=feature, feature_class=feature_class, max_value=max_value,
                                              min_value=min_value, related=related, sample=sample, tag=tag, by_class=by_class, by_tag=by_tag)

    return query.distinct().all()


def return_feature_derived_fields(requested, sample_requested, distinct, paging, **kwargs):

    samples = get_samples(requested, sample_requested,
                          distinct=distinct, paging=paging, **kwargs)

    has_max_min = 'valueMax' in requested or 'valueMin' in requested

    max_min_value_dict = dict()
    sample_dict = dict()
    for key, collection in groupby(samples, key=lambda s: s.feature_id):
        sample_dict[key] = sample_dict.get(key, []) + list(collection)

    if has_max_min:
        for f_id, features in sample_dict.items():
            max_min_dict = {'value_max': None, 'value_min': None}
            if 'valueMax' in requested:
                value_max = max(features, key=lambda f: get_value(f, 'value'))
                max_min_dict['value_max'] = get_value(value_max, 'value')
            if 'valueMin' in requested:
                value_min = min(features, key=lambda f: get_value(f, 'value'))
                max_min_dict['value_min'] = get_value(value_min, 'value')
            max_min_value_dict[f_id] = max_min_dict

    return (max_min_value_dict, sample_dict)
