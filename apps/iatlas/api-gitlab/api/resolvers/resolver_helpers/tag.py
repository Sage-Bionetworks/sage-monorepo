from itertools import groupby
from sqlalchemy import and_, func
from sqlalchemy.orm import aliased
from api import db
from api.db_models import (Dataset, DatasetToTag, DatasetToSample, Feature, FeatureClass,
                           FeatureToSample, Sample, SampleToTag, Tag, TagToTag)
from .general_resolvers import build_join_condition, get_selected, get_value

related_request_fields = {'dataSet',
                          'display',
                          'related'}

simple_tag_request_fields = {'characteristics',
                             'color',
                             'longDisplay',
                             'name',
                             'shortDisplay',
                             'tag'}

tag_request_fields = simple_tag_request_fields.union(
    {'related', 'sampleCount', 'samples'})


def build_related_graphql_response(related_set=set()):
    data_set, related_tag = related_set
    return {
        'display': get_value(related_tag[0], 'data_set_display'),
        'dataSet': data_set,
        'related': list(map(build_tag_graphql_response(), related_tag))
    }


def build_related_request(requested, related_requested, data_set=None, related=None, by_data_set=True):
    """
    Builds a SQL request.
    """
    sess = db.session

    related_1 = aliased(Tag, name='t')
    data_set_1 = aliased(Dataset, name='d')

    core_field_mapping = {'characteristics': related_1.characteristics.label('characteristics'),
                          'color': related_1.color.label('color'),
                          'longDisplay': related_1.long_display.label('long_display'),
                          'name': related_1.name.label('name'),
                          'shortDisplay': related_1.short_display.label('short_display')}
    data_set_core_field_mapping = {
        'display': data_set_1.display.label('data_set_display')}

    core = get_selected(related_requested, core_field_mapping)
    data_set_core = get_selected(requested, data_set_core_field_mapping)

    if by_data_set or 'dataSet' in requested:
        data_set_core.add(data_set_1.name.label('data_set'))

    query = sess.query(*[*core, *data_set_core])

    if related:
        query = query.filter(related_1.name.in_(related))

    if data_set or by_data_set or 'dataSet' in requested:
        data_set_to_tag_1 = aliased(DatasetToTag, name='dt')

        query = query.join(data_set_to_tag_1,
                           data_set_to_tag_1.tag_id == related_1.id)

        data_set_join_condition = build_join_condition(
            data_set_1.id, data_set_to_tag_1.dataset_id, data_set_1.name, data_set)
        query = query.join(data_set_1, and_(*data_set_join_condition))

    order = []
    append_to_order = order.append
    if 'name' in related_requested:
        append_to_order(related_1.name)
    if 'shortDisplay' in related_requested:
        append_to_order(related_1.short_display)
    if 'longDisplay' in related_requested:
        append_to_order(related_1.long_display)
    if 'color' in related_requested:
        append_to_order(related_1.color)
    if 'characteristics' in related_requested:
        append_to_order(related_1.characteristics)

    query = query.order_by(*order) if order else query

    return query


def build_tag_graphql_response(related_dict=dict(), sample_dict=dict()):
    def f(tag):
        if not tag:
            return None
        tag_id = get_value(tag, 'id')
        related = related_dict.get(tag_id, []) if related_dict else []
        samples = sample_dict.get(tag_id, []) if sample_dict else []
        return {
            'characteristics': get_value(tag, 'characteristics'),
            'color': get_value(tag, 'color'),
            'longDisplay': get_value(tag, 'tag_long_display') or get_value(tag, 'long_display'),
            'name': get_value(tag, 'name'),
            'related': [build_tag_graphql_response()(r) for r in related],
            'sampleCount': get_value(tag, 'sample_count'),
            'samples': [sample.name for sample in samples],
            'shortDisplay': get_value(tag, 'tag_short_display') or get_value(tag, 'short_display')
        }
    return f


def build_tag_request(requested, data_set=None, feature=None, feature_class=None,
                      related=None, sample=None, tag=None, get_samples=False):
    """
    Builds a SQL request.
    """
    sess = db.session

    tag_1 = aliased(Tag, name='t')
    sample_to_tag_1 = aliased(SampleToTag, name='st')

    core_field_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                          'color': tag_1.color.label('color'),
                          'longDisplay': tag_1.long_display.label('long_display'),
                          'name': tag_1.name.label('name'),
                          'sampleCount': func.count(func.distinct(sample_to_tag_1.sample_id)).label('sample_count'),
                          'shortDisplay': tag_1.short_display.label('short_display'),
                          'tag': tag_1.name.label('tag')}

    # Only select fields that were requested.
    core = get_selected(requested, core_field_mapping)
    core.add(tag_1.id.label('id'))

    query = sess.query(*core)
    query = query.select_from(tag_1)

    if tag:
        query = query.filter(tag_1.name.in_(tag))

    if data_set or feature or feature_class or related or sample or get_samples or ('sampleCount' in requested):
        sample_1 = aliased(Sample, name='s')
        data_set_to_sample_1 = aliased(DatasetToSample, name='dts')

        is_outer = not bool(sample)

        sample_sub_query = sess.query(sample_1.id).filter(
            sample_1.name.in_(sample)) if sample else None

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

    order = []
    append_to_order = order.append
    if 'name' in requested:
        append_to_order(tag_1.name)
    if 'shortDisplay' in requested:
        append_to_order(tag_1.short_display)
    if 'longDisplay' in requested:
        append_to_order(tag_1.long_display)
    if 'color' in requested:
        append_to_order(tag_1.color)
    if 'characteristics' in requested:
        append_to_order(tag_1.characteristics)

    query = query.order_by(*order) if order else query

    return query


def get_related(requested, related_requested, tag_ids=set()):
    has_related = 'related' in requested

    if has_related:
        sess = db.session

        related_tag_1 = aliased(Tag, name='rt')
        tag_1 = aliased(Tag, name='t')
        tag_to_tag_1 = aliased(TagToTag, name='tt')

        related_core_field_mapping = {
            'characteristics': related_tag_1.characteristics.label('characteristics'),
            'color': related_tag_1.color.label('color'),
            'longDisplay': related_tag_1.long_display.label('long_display'),
            'name': related_tag_1.name.label('name'),
            'shortDisplay': related_tag_1.short_display.label('short_display')}

        related_core = get_selected(
            related_requested, related_core_field_mapping)
        # Always select the related id and the tag id.
        related_core |= {related_tag_1.id.label(
            'id'), tag_to_tag_1.tag_id.label('tag_id')}

        related_query = sess.query(*related_core)
        related_query = related_query.select_from(related_tag_1)

        tag_sub_query = sess.query(tag_1.id).filter(tag_1.id.in_(tag_ids))

        tag_tag_join_condition = build_join_condition(
            tag_to_tag_1.related_tag_id, related_tag_1.id, tag_to_tag_1.tag_id, tag_sub_query)
        related_query = related_query.join(
            tag_to_tag_1, and_(*tag_tag_join_condition))

        order = []
        append_to_order = order.append
        if 'name' in related_requested:
            append_to_order(related_tag_1.name)
        if 'shortDisplay' in related_requested:
            append_to_order(related_tag_1.short_display)
        if 'longDisplay' in related_requested:
            append_to_order(related_tag_1.long_display)
        if 'color' in related_requested:
            append_to_order(related_tag_1.color)
        if 'characteristics' in related_requested:
            append_to_order(related_tag_1.characteristics)

        related_query = related_query.order_by(
            *order) if order else related_query

        return related_query.distinct().all()

    return []


def get_samples(requested, data_set=None, feature=None, feature_class=None, related=None, sample=None, tag_ids=set()):
    has_samples = 'samples' in requested

    if tag_ids and has_samples:
        sess = db.session

        data_set_to_sample_1 = aliased(DatasetToSample, name='ds')
        sample_1 = aliased(Sample, name='s')
        sample_to_tag_1 = aliased(SampleToTag, name='st')

        # Always select the sample id and the gene id.
        sample_core = {sample_1.id.label('id'),
                       sample_1.name.label('name'),
                       sample_to_tag_1.tag_id.label('tag_id')}

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

        order = []
        append_to_order = order.append
        if 'name' in requested:
            append_to_order(sample_1.name)
        if not order:
            append_to_order(sample_1.id)
        sample_query = sample_query.order_by(*order)

        return sample_query.distinct().all()

    return []


def request_related(requested=None, related_requested=None, data_set=None, related=None, by_data_set=True):
    query = build_related_request(
        requested=requested, related_requested=related_requested, data_set=data_set, related=related, by_data_set=by_data_set)

    return query.distinct().all()


def request_tags(requested, data_set=None, feature=None, feature_class=None,
                 related=None, sample=None, tag=None, get_samples=False):
    query = build_tag_request(requested, data_set=data_set, feature=feature, feature_class=feature_class,
                              related=related, sample=sample, tag=tag, get_samples=get_samples)

    return query.distinct().all()


def return_tag_derived_fields(requested, related_requested, data_set=None, feature=None, feature_class=None, related=None, sample=None, tag=None, tag_ids=None):
    related_tags = get_related(requested, related_requested, tag_ids=tag_ids)

    related_dict = dict()
    for key, collection in groupby(related_tags, key=lambda r: r.tag_id):
        related_dict[key] = related_dict.get(key, []) + list(collection)

    samples = get_samples(
        requested, data_set=data_set, feature=feature, feature_class=feature_class, related=related, sample=sample, tag_ids=tag_ids)

    sample_dict = dict()
    for key, collection in groupby(samples, key=lambda s: s.tag_id):
        sample_dict[key] = sample_dict.get(key, []) + list(collection)

    return (related_dict, sample_dict)
