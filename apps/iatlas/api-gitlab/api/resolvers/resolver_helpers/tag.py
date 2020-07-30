from itertools import groupby
from sqlalchemy import and_, func
from sqlalchemy.orm import aliased
from api import db
from api.db_models import (Dataset, DatasetToTag, DatasetToSample, Feature, FeatureClass,
                           FeatureToSample, Sample, SampleToTag, Tag, TagToTag)
from .general_resolvers import build_join_condition, build_option_args, get_selection_set, get_value


def build_related_graphql_response(related_set=set()):
    data_set, related_tag = related_set
    return {
        'display': get_value(related_tag[0], 'data_set_display'),
        'dataSet': data_set,
        'related': list(map(build_tag_graphql_response(), related_tag))
    }


def build_related_request(_obj, info, data_set=None, related=None, by_data_set=True):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, by_data_set, child_node='related')
    tag_selection_set = get_selection_set(
        info.field_nodes[0].selection_set, False)
    data_set_selection_set = get_selection_set(
        info.field_nodes[0].selection_set, False)

    related_1 = aliased(Tag, name='t')
    data_set_1 = aliased(Dataset, name='d')
    data_set_to_tag_1 = aliased(DatasetToTag, name='dt')

    core_field_mapping = {'characteristics': related_1.characteristics.label('characteristics'),
                          'color': related_1.color.label('color'),
                          'display': related_1.display.label('display'),
                          'name': related_1.name.label('name')}
    data_set_core_field_mapping = {
        'display': data_set_1.display.label('data_set_display')}
    requested_field_mapping = {'characteristics': 'characteristics',
                               'color': 'color',
                               'display': 'display',
                               'name': 'name'}
    tag_requested_field_mapping = {'dataSet': 'data_set',
                                   'display': 'display'}

    requested = build_option_args(selection_set, requested_field_mapping)
    tag_requested = build_option_args(
        tag_selection_set, tag_requested_field_mapping)

    core = build_option_args(selection_set, core_field_mapping)
    data_set_core = build_option_args(
        data_set_selection_set, data_set_core_field_mapping)

    if by_data_set or 'data_set' in tag_requested:
        data_set_core.append(data_set_1.name.label('data_set'))

    query = sess.query(*[*core, *data_set_core])

    if related:
        query = query.filter(related_1.name.in_(related))

    query = query.join(data_set_to_tag_1,
                       data_set_to_tag_1.tag_id == related_1.id)

    data_set_join_condition = build_join_condition(
        data_set_1.id, data_set_to_tag_1.dataset_id, data_set_1.name, data_set)
    query = query.join(data_set_1, and_(*data_set_join_condition))

    order = set()
    add_to_order = order.add
    if 'name' in requested:
        add_to_order(related_1.name)
    if 'display' in requested:
        add_to_order(related_1.display)
    if 'color' in requested:
        add_to_order(related_1.color)
    if 'characteristics' in requested:
        add_to_order(related_1.characteristics)

    query = query.order_by(*order) if order else query

    return query


def build_tag_graphql_response(sample_dict=dict()):
    def f(tag):
        tag_id = get_value(tag, 'id')
        return {
            'characteristics': get_value(tag, 'characteristics'),
            'color': get_value(tag, 'color'),
            'display': get_value(tag, 'display'),
            'name': get_value(tag, 'name'),
            'sampleCount': get_value(tag, 'sample_count'),
            'samples': [sample.name for sample in sample_dict[tag_id]] if sample_dict else [],
        }
    return f


def build_tag_request(_obj, info, data_set=None, feature=None, feature_class=None,
                      related=None, sample=None, tag=None, get_samples=False):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)

    tag_1 = aliased(Tag, name='t')
    sample_to_tag_1 = aliased(SampleToTag, name='st')

    requested = set()
    add_to_requested = requested.add
    for selection in selection_set.selections:
        add_to_requested(selection.name.value)

    core_field_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                          'color': tag_1.color.label('color'),
                          'display': tag_1.display.label('display'),
                          'name': tag_1.name.label('name'),
                          'sampleCount': func.count(func.distinct(sample_to_tag_1.sample_id)).label('sample_count'),
                          'tag': tag_1.name.label('tag')}

    # Only select fields that were requested.
    core = build_option_args(selection_set, core_field_mapping)
    core.append(tag_1.id.label('id'))

    query = sess.query(*core)
    query = query.select_from(tag_1)

    if tag:
        query = query.filter(tag_1.name.in_(tag))

    if data_set or feature or feature_class or related or sample or get_samples or ('sampleCount' in requested):
        sample_1 = aliased(Sample, name='s')
        data_set_to_sample_1 = aliased(DatasetToSample, name='dts')

        is_outer = not bool(sample)

        sample_sub_query = sess.query(sample_1.id).filter(
            sample_1.name.in_(sample)) if sample else sample

        sample_tag_join_condition = build_join_condition(
            sample_to_tag_1.tag_id, tag_1.id, sample_to_tag_1.sample_id, sample_sub_query)
        query = query.join(
            sample_to_tag_1, and_(*sample_tag_join_condition), isouter=is_outer)

        if data_set or related:
            data_set_1 = aliased(Dataset, name='d')

            data_set_sub_query = sess.query(data_set_1.id).filter(
                data_set_1.name.in_(data_set)) if data_set else data_set

            data_set_to_sample_join_condition = build_join_condition(
                data_set_to_sample_1.sample_id, sample_to_tag_1.sample_id, data_set_to_sample_1.dataset_id, data_set_sub_query)
            query = query.join(
                data_set_to_sample_1, and_(*data_set_to_sample_join_condition))

        if feature or feature_class:
            feature_1 = aliased(Feature, name='f')
            feature_class_1 = aliased(FeatureClass, name='fc')
            feature_to_sample_1 = aliased(FeatureToSample, name='fs')

            query = query.join(feature_to_sample_1,
                               feature_to_sample_1.sample_id == sample_to_tag_1.sample_id)

            feature_join_condition = build_join_condition(
                feature_1.id, feature_to_sample_1.feature_id, feature_1.name, feature)
            query = query.join(feature_1, and_(*feature_join_condition))

            if feature_class:
                feature_class_join_condition = build_join_condition(
                    feature_class_1.id, feature_1.class_id, feature_class_1.name, feature_class)
                query = query.join(
                    feature_class_1, and_(*feature_class_join_condition))

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

            query = query.join(tag_to_tag_1, and_(
                tag_to_tag_1.tag_id == tag_1.id, tag_to_tag_1.related_tag_id == data_set_to_tag_1.tag_id))

        if 'sampleCount' in requested:
            group_by = set()
            add_to_group_by = group_by.add
            if 'name' in requested:
                add_to_group_by(tag_1.name)
            if 'display' in requested:
                add_to_group_by(tag_1.display)
            if 'color' in requested:
                add_to_group_by(tag_1.color)
            if 'characteristics' in requested:
                add_to_group_by(tag_1.characteristics)
            add_to_group_by(tag_1.id)

            query = query.group_by(*group_by)

    order = set()
    add_to_order = order.add
    if 'name' in requested:
        add_to_order(tag_1.name)
    if 'display' in requested:
        add_to_order(tag_1.display)
    if 'color' in requested:
        add_to_order(tag_1.color)
    if 'characteristics' in requested:
        add_to_order(tag_1.characteristics)

    query = query.order_by(*order) if order else query

    return query


def get_samples(info, data_set=None, feature=None, feature_class=None, related=None, sample=None, tag_ids=set()):
    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)
    requested = build_option_args(selection_set, {'samples': 'samples'})
    has_samples = 'samples' in requested

    if tag_ids:
        sess = db.session

        data_set_to_sample_1 = aliased(DatasetToSample, name='ds')
        sample_1 = aliased(Sample, name='s')
        sample_to_tag_1 = aliased(SampleToTag, name='st')

        sample_core_field_mapping = {'name': sample_1.name.label('name')}

        sample_core = build_option_args(
            selection_set, sample_core_field_mapping)
        # Always select the sample id and the gene id.
        sample_core = sample_core + [sample_1.id.label('id'),
                                     sample_to_tag_1.tag_id.label('tag_id')]

        sample_query = sess.query(*sample_core)
        sample_query = sample_query.select_from(sample_1)

        if sample:
            sample_query = sample_query.filter(sample_1.name.in_(sample))

        sample_tag_join_condition = build_join_condition(
            sample_to_tag_1.sample_id, sample_1.id, sample_to_tag_1.tag_id, tag_ids)

        sample_query = sample_query.join(
            sample_to_tag_1, and_(*sample_tag_join_condition))

        if data_set or related:
            data_set_1 = aliased(Dataset, name='d')

            data_set_sub_query = sess.query(data_set_1.id).filter(
                data_set_1.name.in_(data_set)) if data_set else data_set

            data_set_to_sample_join_condition = build_join_condition(
                data_set_to_sample_1.sample_id, sample_1.id, data_set_to_sample_1.dataset_id, data_set_sub_query)
            sample_query = sample_query.join(
                data_set_to_sample_1, and_(*data_set_to_sample_join_condition))

        if feature or feature_class:
            feature_1 = aliased(Feature, name='f')
            feature_class_1 = aliased(FeatureClass, name='fc')
            feature_to_sample_1 = aliased(FeatureToSample, name='fs')

            sample_query = sample_query.join(feature_to_sample_1,
                                             feature_to_sample_1.sample_id == sample_1.id)

            feature_join_condition = build_join_condition(
                feature_1.id, feature_to_sample_1.feature_id, feature_1.name, feature)
            sample_query = sample_query.join(
                feature_1, and_(*feature_join_condition))

            if feature_class:
                feature_class_join_condition = build_join_condition(
                    feature_class_1.id, feature_1.class_id, feature_class_1.name, feature_class)
                sample_query = sample_query.join(
                    feature_class_1, and_(*feature_class_join_condition))

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

            sample_query = sample_query.join(tag_to_tag_1, and_(
                tag_to_tag_1.tag_id == sample_to_tag_1.tag_id, tag_to_tag_1.related_tag_id == data_set_to_tag_1.tag_id))

        order = set()
        if 'name' in requested:
            order.add(sample_1.name)
        else:
            order.add(sample_1.id)
        sample_query = sample_query.order_by(*order)

        return sample_query.distinct().all()

    return []


def request_related(_obj, info, data_set=None, related=None):
    query = build_related_request(
        _obj, info, data_set=data_set, related=related)

    return query.distinct().all()


def request_tags(_obj, info, data_set=None, feature=None, feature_class=None,
                 related=None, sample=None, tag=None, get_samples=False):
    query = build_tag_request(_obj, info, data_set=data_set, feature=feature, feature_class=feature_class,
                              related=related, sample=sample, tag=tag, get_samples=get_samples)

    return query.distinct().all()


def return_tag_derived_fields(info, data_set=None, feature=None, feature_class=None, related=None, sample=None, tag_ids=set()):
    samples = get_samples(info, data_set=data_set, feature=feature, feature_class=feature_class,
                          related=related, sample=sample, tag_ids=tag_ids)

    sample_dict = dict()
    for key, collection in groupby(samples, key=lambda s: s.tag_id):
        sample_dict[key] = sample_dict.get(key, []) + list(collection)

    return sample_dict
