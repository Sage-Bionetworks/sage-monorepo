from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import (
    Dataset, DatasetToSample, DatasetToTag, Feature, FeatureClass, FeatureToSample,
    MethodTag, Sample, SampleToTag, Tag, TagToTag)
from .general_resolvers import build_join_condition, build_option_args, get_selection_set, get_value


def build_feature_graphql_response(feature):
    return {
        'class': get_value(feature, 'class'),
        'display': get_value(feature, 'display'),
        'methodTag': get_value(feature, 'method_tag'),
        'name': get_value(feature),
        'order': get_value(feature, 'order'),
        'sample': get_value(feature, 'sample'),
        'unit': get_value(feature, 'unit'),
        'value': return_feature_value(feature)
    }


def build_features_query(_obj, info, data_set=None, feature=None, feature_class=None, method_tag=None, related=None, sample=None, tag=None, by_class=False, by_tag=False):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, by_class or by_tag)

    tag_or_class_selection_set = info.field_nodes[0].selection_set

    data_set_1 = aliased(Dataset, name='d')
    feature_1 = aliased(Feature, name='f')
    feature_class_1 = aliased(FeatureClass, name='fc')
    feature_to_sample_1 = aliased(FeatureToSample, name='fs1')
    method_tag_1 = aliased(MethodTag, name='mt')
    sample_1 = aliased(Sample, name='s')
    sample_to_tag_1 = aliased(SampleToTag, name='stt')
    tag_1 = aliased(Tag, name='t')

    core_field_mapping = {'display': feature_1.display.label('display'),
                          'methodTag': method_tag_1.name.label('method_tag'),
                          'name': feature_1.name.label('name'),
                          'order': feature_1.order.label('order'),
                          'sample': sample_1.name.label('sample'),
                          'unit': feature_1.unit.label('unit')}
    tag_core_field_mapping = {'characteristics': tag_1.characteristics.label('tag_characteristics'),
                              'color': tag_1.color.label('tag_color'),
                              'display': tag_1.display.label('tag_display')}
    core_requested_field_mapping = {'class': 'class',
                                    'display': 'display',
                                    'methodTag': 'method_tag',
                                    'name': 'name',
                                    'order': 'order',
                                    'sample': 'sample',
                                    'unit': 'unit',
                                    'value': 'value'}
    requested_field_mapping = {'characteristics': 'characteristics',
                               'class': 'class',
                               'color': 'color',
                               'display': 'display',
                               'tag': 'tag'}

    # Only select fields that were requested.

    core_requested = build_option_args(
        selection_set, core_requested_field_mapping)
    requested = build_option_args(
        tag_or_class_selection_set, requested_field_mapping) if by_class or by_tag else []
    core = build_option_args(selection_set, core_field_mapping)
    core.append(feature_1.id.label('id'))

    if by_class or 'class' in core_requested:
        core.append(feature_class_1.name.label('class'))

    if by_tag:
        core = core + \
            build_option_args(tag_or_class_selection_set,
                              tag_core_field_mapping)
        core.append(tag_1.name.label('tag'))

    if 'value' in core_requested:
        core.append(feature_to_sample_1.value.label('value'))
        core.append(feature_to_sample_1.inf_value.label('inf'))

    query = sess.query(*core)
    query = query.select_from(feature_1)

    if feature:
        query = query.filter(feature_1.name.in_(feature))

    if by_class or 'class' in core_requested or feature_class:
        is_outer = not bool(feature_class)
        feature_class_join_condition = build_join_condition(
            feature_class_1.id, feature_1.class_id, filter_column=feature_class_1.name, filter_list=feature_class)
        query = query.join(feature_class_1, and_(
            *feature_class_join_condition), isouter=is_outer)

    if 'method_tag' in core_requested or method_tag:
        is_outer = not bool(method_tag)
        method_tag_join_condition = build_join_condition(
            method_tag_1.id, feature_1.method_tag_id, filter_column=method_tag_1.name, filter_list=method_tag)
        query = query.join(method_tag_1, and_(
            *method_tag_join_condition), isouter=is_outer)

    if by_tag or sample or data_set or related or tag or 'value' in core_requested or 'sample' in core_requested:

        query = query.join(feature_to_sample_1, feature_1.id ==
                           feature_to_sample_1.feature_id)

        sample_join_condition = [sample_1.id == feature_to_sample_1.sample_id]

        if sample:
            sample_join_condition = sample_join_condition + \
                [sample_1.name.in_(sample)]

        query = query.join(sample_1, and_(*sample_join_condition))

        if data_set or related:
            data_set_to_sample_1 = aliased(DatasetToSample, name='dts')

            data_set_to_sample_sub_query = sess.query(data_set_to_sample_1.dataset_id).filter(
                data_set_to_sample_1.sample_id == sample_1.id)
            data_set_join_condition = [
                data_set_1.id.in_(data_set_to_sample_sub_query)]
            if data_set:
                data_set_join_condition.append(
                    data_set_1.name.in_(data_set))
            query = query.join(data_set_1, and_(*data_set_join_condition))

        if by_tag or tag or related:
            sample_to_tag_join_condition = [
                sample_to_tag_1.sample_id == sample_1.id]

        if related:
            data_set_to_tag_1 = aliased(DatasetToTag, name='dtt')
            related_tag_1 = aliased(Tag, name='rt')
            tag_to_tag_1 = aliased(TagToTag, name='tt')

            data_set_to_tag_subquery = sess.query(
                data_set_to_tag_1.tag_id).filter(data_set_to_tag_1.dataset_id == data_set_1.id)
            related_tag_join_condition = [related_tag_1.name.in_(
                related), related_tag_1.id.in_(data_set_to_tag_subquery)]
            query = query.join(related_tag_1, and_(
                *related_tag_join_condition))

            tag_to_tag_subquery = sess.query(tag_to_tag_1.tag_id).filter(
                tag_to_tag_1.related_tag_id == related_tag_1.id)

            sample_to_tag_join_condition.append(
                sample_to_tag_1.tag_id.in_(tag_to_tag_subquery))

        if by_tag or tag or related:
            query = query.join(sample_to_tag_1, and_(
                *sample_to_tag_join_condition))

        if by_tag or tag:
            tag_join_condition = build_tag_join_condition(
                tag_1.id, sample_to_tag_1.tag_id, tag_1.name, tag)

            query = query.join(tag_1, and_(*tag_join_condition))

    order = []
    append_to_order = order.append
    if by_tag:
        append_to_order(tag_1.name)
    if 'display' in requested:
        append_to_order(tag_1.display)
    if 'color' in requested:
        append_to_order(tag_1.color)
    if 'characteristics' in requested:
        append_to_order(tag_1.characteristics)
    if by_class or 'class' in requested:
        append_to_order(feature_class_1.name)
    if 'order' in core_requested:
        append_to_order(feature_1.order)
    if 'display' in core_requested:
        append_to_order(feature_1.display)
    if 'name' in core_requested:
        append_to_order(feature_1.name)
    if 'class' in core_requested and not by_class and 'class' not in requested:
        append_to_order(feature_class_1.name)
    if 'sample' in core_requested:
        append_to_order(sample_1.name)
    if 'method_tag' in core_requested:
        append_to_order(method_tag_1.name)
    if 'unit' in core_requested:
        append_to_order(feature_1.unit)
    else:
        append_to_order(feature_1.id)

    return query.order_by(*order)


def build_tag_join_condition(join_column, column, filter_1_column=None, filter_1_list=None, filter_2_column=None, filter_2_list=None):
    join_condition = [join_column == column]
    if bool(filter_1_list):
        join_condition.append(filter_1_column.in_(filter_1_list))
    if bool(filter_2_list):
        join_condition.append(filter_2_column.in_(filter_2_list))
    return join_condition


def request_features(_obj, info, data_set=None, feature=None, feature_class=None, related=None, sample=None, tag=None, by_class=False, by_tag=False):
    query = build_features_query(_obj, info, data_set=data_set, feature=feature, feature_class=feature_class,
                                 related=related, sample=sample, tag=tag, by_class=by_class, by_tag=by_tag)

    return query.distinct().all()


def return_feature_value(row):
    infinity = get_value(row, 'inf')
    if infinity:
        infinity = str(infinity)
    return infinity or get_value(row, 'value')
