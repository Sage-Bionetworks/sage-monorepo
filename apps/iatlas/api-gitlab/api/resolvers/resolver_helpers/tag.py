from sqlalchemy import and_, func, orm
from api import db
from api.db_models import (
    Dataset, DatasetToTag, DatasetToSample, Feature, FeatureClass,
    FeatureToSample, Sample, SampleToTag, Tag, TagToTag)
from .general_resolvers import build_join_condition, build_option_args, get_selection_set


def build_core_field_mapping(model):
    return {'characteristics': model.characteristics.label('characteristics'),
            'color': model.color.label('color'),
            'display': model.display.label('display'),
            'name': model.name.label('name')}


def build_related_join_condition(sample_to_tag_model, tag_to_tag_model, related=None):
    if bool(related):
        related_tag_1 = orm.aliased(Tag, name='rt')
        related = db.session.query(related_tag_1.id).filter(
            related_tag_1.name.in_(related))

    return build_join_condition(
        tag_to_tag_model.related_tag_id, sample_to_tag_model.tag_id, filter_column=tag_to_tag_model.related_tag_id, filter_list=related)


def build_related_request(_obj, info, data_set=None, related=None, by_data_set=True):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, by_data_set, child_node='related')
    data_set_selection_set = get_selection_set(
        info.field_nodes[0].selection_set, False)

    tag_1 = orm.aliased(Tag, name='t')
    data_set_1 = orm.aliased(Dataset, name='d')
    data_set_to_tag_1 = orm.aliased(DatasetToTag, name='dt')

    core_field_mapping = build_core_field_mapping(tag_1)

    data_set_core_field_mapping = {'dataSet': data_set_1.name.label('data_set'),
                                   'display': data_set_1.display.label('data_set_display')}

    core = build_option_args(selection_set, core_field_mapping)
    data_set_core = build_option_args(
        data_set_selection_set, data_set_core_field_mapping)
    option_args = []
    append_to_option_args = option_args.append

    query = sess.query(*[*core, *data_set_core])

    if related:
        query = query.filter(tag_1.name.in_(related))

    if data_set:
        data_set_to_tag_subquery = sess.query(data_set_to_tag_1.dataset_id).filter(
            data_set_to_tag_1.tag_id == tag_1.id)
        query = query.join(data_set_1, data_set_1.id.in_(
            data_set_to_tag_subquery)).filter(data_set_1.name.in_(data_set))

    return query


def build_tag_request(_obj, info, data_set=None, related=None, tag=None, feature=None,
                      feature_class=None, get_samples=False):
    """
    Builds a SQL request and returns values from the DB.
    """
    sess = db.session

    tag_1 = orm.aliased(Tag, name='t')
    dataset_1 = orm.aliased(Dataset, name='d')
    sample_1 = orm.aliased(Sample, name='s')
    sample_to_tag_1 = orm.aliased(SampleToTag, name='st1')
    sample_to_tag_2 = orm.aliased(SampleToTag, name='st2')
    tag_to_tag_1 = orm.aliased(TagToTag, name='tt')

    select_field_node_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                                 'color': tag_1.color.label('color'),
                                 'display': tag_1.display.label('display'),
                                 'name': tag_1.name.label('name'),
                                 'samples': func.array_agg(func.distinct(
                                     sample_1.name)).label('samples'),
                                 'sampleCount': func.count(func.distinct(sample_to_tag_2.sample_id)).label('sample_count'),
                                 'tag': tag_1.name.label('tag')}

    # Only select fields that were requested.
    selection_set = info.field_nodes[0].selection_set or []
    select_fields = build_option_args(selection_set, select_field_node_mapping)

    requested_nodes = []
    append_to_requested_nodes = requested_nodes.append
    for selection in selection_set.selections:
        append_to_requested_nodes(selection.name.value)

    if 'samples' in requested_nodes or get_samples:
        select_fields.append(func.array_agg(
            func.distinct(sample_1.name)).label('samples'))

    query = sess.query(*select_fields)
    query = query.select_from(sample_to_tag_1)

    if feature or feature_class:
        feature_1 = orm.aliased(Feature, name='f')
        feature_to_sample_1 = orm.aliased(FeatureToSample, name='fs')
        feature_sub_query = sess.query(feature_1.id)
        if feature:
            feature_sub_query = feature_sub_query.filter(
                feature_1.name.in_(feature))
        if feature_class:
            class_1 = orm.aliased(FeatureClass, name='fc')
            feature_sub_query = feature_sub_query.join(class_1, and_(
                feature_1.class_id == class_1.id, class_1.name.in_(feature_class)))
        query = query.join(feature_to_sample_1,
                           and_(feature_to_sample_1.sample_id == sample_to_tag_1.sample_id,
                                feature_to_sample_1.feature_id.in_(feature_sub_query)))

    if data_set:
        dataset_to_sample_1 = orm.aliased(DatasetToSample, name='ds')
        dataset_1 = orm.aliased(Dataset, name='d')
        query = query.join(dataset_to_sample_1,
                           and_(dataset_to_sample_1.sample_id == sample_to_tag_1.sample_id,
                                dataset_to_sample_1.dataset_id.in_(
                                    sess.query(dataset_1.id).filter(
                                        dataset_1.name.in_(data_set))
                                )))

    related_join_condition = build_related_join_condition(
        sample_to_tag_1, tag_to_tag_1, related)
    query = query.join(tag_to_tag_1, and_(*related_join_condition))
    query = query.join(sample_to_tag_2,
                       and_(sample_to_tag_2.sample_id == sample_to_tag_1.sample_id,
                            tag_to_tag_1.tag_id == sample_to_tag_2.tag_id))
    query = query.join(tag_1, tag_1.id == tag_to_tag_1.tag_id, isouter=True)

    if tag:
        query = query.filter(tag_1.name.in_(tag))

    if (get_samples or
        'sampleCount' in requested_nodes or
        'samples' in requested_nodes or
            'rnaExpValues' in requested_nodes):
        query = query.group_by(tag_1.name, tag_1.display,
                               tag_1.characteristics, tag_1.color)
        if 'samples' in requested_nodes or get_samples:
            query = query.join(
                sample_1, sample_1.id == sample_to_tag_2.sample_id, isouter=True)

    return query


def request_related(_obj, info, data_set=None, related=None):
    query = build_related_request(
        _obj, info, data_set=data_set, related=related)

    return query.distinct().all()


def request_tags(_obj, info, data_set=None, related=None, tag=None,
                 feature=None, feature_class=None, get_samples=False):
    query = build_tag_request(_obj, info, data_set=data_set, related=related, tag=tag,
                              feature=feature, feature_class=feature_class, get_samples=get_samples)

    return query.distinct().all()
