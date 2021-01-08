from itertools import groupby
from sqlalchemy import and_, func
from sqlalchemy.orm import aliased
from api import db
from api.db_models import (
    Dataset, DatasetToSample, DatasetToTag, Feature, FeatureClass, FeatureToSample,
    MethodTag, Sample, SampleToTag, Tag, TagToTag)
from .general_resolvers import build_join_condition, get_selected, get_selection_set, get_value

feature_class_request_fields = {'name'}

simple_feature_request_fields = {'display',
                                 'name',
                                 'order',
                                 'unit'}

feature_request_fields = simple_feature_request_fields.union({'class',
                                                              'methodTag',
                                                              'samples',
                                                              'valueMax',
                                                              'valueMin'})


def build_feature_graphql_response(max_min_dict=dict(), sample_dict=dict()):
    def f(feature):
        if not feature:
            return None
        feature_id = get_value(feature, 'id')
        max_min = max_min_dict.get(
            feature_id, dict()) if max_min_dict else dict()
        samples = sample_dict.get(feature_id, []) if sample_dict else []
        return {
            'class': get_value(feature, 'class'),
            'display': get_value(feature, 'feature_display') or get_value(feature, 'display'),
            'methodTag': get_value(feature, 'method_tag'),
            'name': get_value(feature, 'feature_name') or get_value(feature),
            'order': get_value(feature, 'order'),
            'samples': [{
                'name': get_value(sample),
                'value': get_value(sample, 'value')
            } for sample in samples],
            'unit': get_value(feature, 'unit'),
            'valueMax': max_min.get('value_max') if max_min else None,
            'valueMin': max_min.get('value_min') if max_min else None
        }
    return f


def build_features_query(requested, class_requested, tag_requested, data_set=None, feature=None, feature_class=None, max_value=None, method_tag=None, min_value=None, related=None, sample=None, tag=None, by_class=False, by_tag=False):
    """
    Builds a SQL request.
    """
    sess = db.session

    has_min_max = 'valueMax' in requested or 'valueMin' in requested

    data_set_to_sample_1 = aliased(DatasetToSample, name='dts')
    feature_1 = aliased(Feature, name='f')
    feature_class_1 = aliased(FeatureClass, name='fc')
    feature_to_sample_1 = aliased(FeatureToSample, name='fs1')
    method_tag_1 = aliased(MethodTag, name='mt')
    sample_1 = aliased(Sample, name='s')
    sample_to_tag_1 = aliased(SampleToTag, name='stt')
    tag_1 = aliased(Tag, name='t')

    core_field_mapping = {'class': feature_class_1.name.label('class'),
                          'display': feature_1.display.label('display'),
                          'methodTag': method_tag_1.name.label('method_tag'),
                          'name': feature_1.name.label('name'),
                          'order': feature_1.order.label('order'),
                          'unit': feature_1.unit.label('unit')}
    tag_core_field_mapping = {'characteristics': tag_1.characteristics.label('tag_characteristics'),
                              'color': tag_1.color.label('tag_color'),
                              'longDisplay': tag_1.long_display.label('tag_long_display'),
                              'shortDisplay': tag_1.short_display.label('tag_short_display')}

    # Only select fields that were requested.
    core = get_selected(requested, core_field_mapping)
    core |= {feature_1.id.label('id')}
    tag_core = get_selected(tag_requested, tag_core_field_mapping)

    if by_class:
        core |= {feature_class_1.name.label('class')}

    if by_tag:
        tag_core |= {tag_1.name.label('tag')}

    if has_min_max:
        core |= {feature_to_sample_1.value.label('value')}

    query = sess.query(*[*core, *tag_core])
    query = query.select_from(feature_1)

    if feature:
        query = query.filter(feature_1.name.in_(feature))

    if by_class or feature_class or 'class' in requested:
        is_outer = not bool(feature_class)
        feature_class_join_condition = build_join_condition(
            feature_class_1.id, feature_1.class_id, filter_column=feature_class_1.name, filter_list=feature_class)
        query = query.join(feature_class_1, and_(
            *feature_class_join_condition), isouter=is_outer)

    if 'methodTag' in requested or method_tag:
        is_outer = not bool(method_tag)
        method_tag_join_condition = build_join_condition(
            method_tag_1.id, feature_1.method_tag_id, filter_column=method_tag_1.name, filter_list=method_tag)
        query = query.join(method_tag_1, and_(
            *method_tag_join_condition), isouter=is_outer)

    if by_tag or sample or data_set or related or tag or 'value' in requested or has_min_max or 'sample' in requested:
        feature_sample_join_condition = [
            feature_1.id == feature_to_sample_1.feature_id]

        if max_value:
            feature_sample_join_condition.append(
                feature_to_sample_1.value <= max_value)

        if min_value:
            feature_sample_join_condition.append(
                feature_to_sample_1.value >= min_value)

        query = query.join(feature_to_sample_1, and_(
            *feature_sample_join_condition))

        sample_join_condition = build_join_condition(
            sample_1.id, feature_to_sample_1.sample_id, sample_1.name, sample)

        query = query.join(sample_1, and_(*sample_join_condition))

        if data_set or related:
            data_set_1 = aliased(Dataset, name='d')

            data_set_sub_query = sess.query(data_set_1.id).filter(
                data_set_1.name.in_(data_set)) if data_set else data_set

            data_set_to_sample_join_condition = build_join_condition(
                data_set_to_sample_1.sample_id, sample_1.id, data_set_to_sample_1.dataset_id, data_set_sub_query)
            query = query.join(
                data_set_to_sample_1, and_(*data_set_to_sample_join_condition))

        if by_tag or tag or related:
            sample_to_tag_join_condition = [
                sample_to_tag_1.sample_id == sample_1.id]

        if related:
            data_set_to_tag_1 = aliased(DatasetToTag, name='dtt')
            related_tag_1 = aliased(Tag, name='rt')
            tag_to_tag_1 = aliased(TagToTag, name='tt')

            related_tag_sub_query = sess.query(related_tag_1.id).filter(
                related_tag_1.name.in_(related))

            data_set_tag_join_condition = build_join_condition(
                data_set_to_tag_1.dataset_id, data_set_to_sample_1.dataset_id, data_set_to_tag_1.tag_id, related_tag_sub_query)
            query = query.join(
                data_set_to_tag_1, and_(*data_set_tag_join_condition))

            tag_to_tag_subquery = sess.query(tag_to_tag_1.tag_id).filter(
                tag_to_tag_1.related_tag_id == data_set_to_tag_1.tag_id)

            sample_to_tag_join_condition.append(
                sample_to_tag_1.tag_id.in_(tag_to_tag_subquery))

        if by_tag or tag or related:
            query = query.join(sample_to_tag_1, and_(
                *sample_to_tag_join_condition))

        if by_tag or tag:
            tag_join_condition = build_join_condition(
                tag_1.id, sample_to_tag_1.tag_id, tag_1.name, tag)

            query = query.join(tag_1, and_(*tag_join_condition))

    order = []
    append_to_order = order.append
    if by_tag:
        append_to_order(tag_1.name)
    if 'shortDisplay' in tag_requested:
        append_to_order(tag_1.short_display)
    if 'longDisplay' in tag_requested:
        append_to_order(tag_1.long_display)
    if 'color' in tag_requested:
        append_to_order(tag_1.color)
    if 'characteristics' in tag_requested:
        append_to_order(tag_1.characteristics)
    if by_class:
        append_to_order(feature_class_1.name)
    if 'order' in requested:
        append_to_order(feature_1.order)
    if 'display' in requested:
        append_to_order(feature_1.display)
    if 'name' in requested:
        append_to_order(feature_1.name)
    if 'class' in requested and not by_class:
        append_to_order(feature_class_1.name)
    if 'methodTag' in requested:
        append_to_order(method_tag_1.name)
    if 'unit' in requested:
        append_to_order(feature_1.unit)
    if not order:
        append_to_order(feature_1.id)

    return query.order_by(*order)


def get_samples(requested, sample_requested, data_set=None, max_value=None, min_value=None, related=None, sample=None, tag=None, feature_ids=set()):
    has_samples = 'samples' in requested
    has_max_min = 'valueMax' in requested or 'valueMin' in requested

    if feature_ids and (has_samples or has_max_min):
        sess = db.session

        data_set_to_sample_1 = aliased(DatasetToSample, name='dts')
        feature_to_sample_1 = aliased(FeatureToSample, name='fs')
        sample_1 = aliased(Sample, name='s')
        sample_to_tag_1 = aliased(SampleToTag, name='stt')

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

        feature_sample_join_condition = build_join_condition(
            feature_to_sample_1.sample_id, sample_1.id, feature_to_sample_1.feature_id, feature_ids)

        if max_value:
            feature_sample_join_condition.append(
                feature_to_sample_1.value <= max_value)

        if min_value:
            feature_sample_join_condition.append(
                feature_to_sample_1.value >= min_value)

        sample_query = sample_query.join(
            feature_to_sample_1, and_(*feature_sample_join_condition))

        if data_set or related:
            data_set_1 = aliased(Dataset, name='d')

            data_set_sub_query = sess.query(data_set_1.id).filter(
                data_set_1.name.in_(data_set)) if data_set else data_set

            data_set_to_sample_join_condition = build_join_condition(
                data_set_to_sample_1.sample_id, sample_1.id, data_set_to_sample_1.dataset_id, data_set_sub_query)
            sample_query = sample_query.join(
                data_set_to_sample_1, and_(*data_set_to_sample_join_condition))

        if tag or related:
            tag_1 = aliased(Tag, name='t')

            tag_sub_query = sess.query(tag_1.id).filter(
                tag_1.name.in_(tag)) if tag else tag
            sample_to_tag_join_condition = build_join_condition(
                sample_to_tag_1.sample_id, sample_1.id, sample_to_tag_1.tag_id, tag_sub_query)

        if related:
            data_set_to_tag_1 = aliased(DatasetToTag, name='dtt')
            related_tag_1 = aliased(Tag, name='rt')
            tag_to_tag_1 = aliased(TagToTag, name='tt')

            related_tag_sub_query = sess.query(related_tag_1.id).filter(
                related_tag_1.name.in_(related)) if related else related

            data_set_tag_join_condition = build_join_condition(
                data_set_to_tag_1.dataset_id, data_set_to_sample_1.dataset_id, data_set_to_tag_1.tag_id, related_tag_sub_query)
            sample_query = sample_query.join(
                data_set_to_tag_1, and_(*data_set_tag_join_condition))

            tag_to_tag_subquery = sess.query(tag_to_tag_1.tag_id).filter(
                tag_to_tag_1.related_tag_id == data_set_to_tag_1.tag_id)

            sample_to_tag_join_condition.append(
                sample_to_tag_1.tag_id.in_(tag_to_tag_subquery))

        if tag or related:
            sample_query = sample_query.join(sample_to_tag_1, and_(
                *sample_to_tag_join_condition))

        order = []
        append_to_order = order.append
        if 'name' in sample_requested:
            append_to_order(sample_1.name)
        if 'value' in sample_requested:
            append_to_order(feature_to_sample_1.value)
        sample_query = sample_query.order_by(*order) if order else sample_query

        return sample_query.distinct().all()

    return []


def request_features(requested, class_requested, tag_requested, data_set=None, feature=None, feature_class=None, max_value=None, min_value=None,
                     related=None, sample=None, tag=None, by_class=False, by_tag=False):
    query = build_features_query(requested, class_requested, tag_requested, data_set=data_set, feature=feature, feature_class=feature_class, max_value=max_value,
                                 min_value=min_value, related=related, sample=sample, tag=tag, by_class=by_class, by_tag=by_tag)

    return query.distinct().all()


def return_feature_derived_fields(requested, sample_requested, feature_ids=set(), data_set=None, max_value=None, min_value=None,
                                  related=None, sample=None, tag=None):
    samples = get_samples(requested, sample_requested, data_set=data_set, max_value=max_value, min_value=min_value, related=related,
                          sample=sample, tag=tag, feature_ids=feature_ids)

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
