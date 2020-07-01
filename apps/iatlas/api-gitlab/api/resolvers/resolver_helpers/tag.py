from sqlalchemy import and_, func, orm
from api import db
from api.db_models import (
    Dataset, DatasetToSample, Feature, FeatureClass,
    FeatureToSample, Sample, SampleToTag, Tag, TagToTag)
from .general_resolvers import build_option_args


def request_tags(_obj, info, data_set, related, feature=None, feature_class=None, get_samples=False):
    """
    Builds a SQL request and returns values from the DB.
    """
    sess = db.session

    tag = orm.aliased(Tag, name='t')
    dataset_1 = orm.aliased(Dataset, name='d')
    dataset_to_sample_1 = orm.aliased(DatasetToSample, name='ds')
    related_tag = orm.aliased(Tag, name='rt')
    sample_1 = orm.aliased(Sample, name='s')
    sample_to_tag_1 = orm.aliased(SampleToTag, name='st1')
    sample_to_tag_2 = orm.aliased(SampleToTag, name='st2')
    tag_to_tag_1 = orm.aliased(TagToTag, name='tt')

    select_field_node_mapping = {'characteristics': tag.characteristics.label('characteristics'),
                                 'color': tag.color.label('color'),
                                 'display': tag.display.label('display'),
                                 'name': tag.name.label('name'),
                                 'rnaExpValues': func.array_agg(func.distinct(
                                     sample_1.name)).label('rna_exp_values'),
                                 'sampleCount': func.count(func.distinct(sample_to_tag_2.sample_id)).label('sample_count'),
                                 'tag': tag.name.label('tag')}

    # Only select fields that were requested.
    selection_set = info.field_nodes[0].selection_set or []
    select_fields = build_option_args(selection_set, select_field_node_mapping)

    requested_nodes = []
    for selection in selection_set.selections:
        requested_nodes.append(selection.name.value)

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

    query = query.join(dataset_to_sample_1,
                       and_(dataset_to_sample_1.sample_id == sample_to_tag_1.sample_id,
                            dataset_to_sample_1.dataset_id.in_(
                                sess.query(dataset_1.id).filter(
                                    dataset_1.name.in_(data_set))
                            )))
    query = query.join(tag_to_tag_1,
                       and_(sample_to_tag_1.tag_id == tag_to_tag_1.related_tag_id,
                            tag_to_tag_1.related_tag_id.in_(
                                sess.query(related_tag.id).filter(
                                    related_tag.name.in_(related)))))
    query = query.join(sample_to_tag_2,
                       and_(sample_to_tag_2.sample_id == sample_to_tag_1.sample_id,
                            tag_to_tag_1.tag_id == sample_to_tag_2.tag_id))
    query = query.join(tag, tag.id == tag_to_tag_1.tag_id, isouter=True)

    if (get_samples or
        'sampleCount' in requested_nodes or
        'samples' in requested_nodes or
            'rnaExpValues' in requested_nodes):
        query = query.group_by(tag.name, tag.display,
                               tag.characteristics, tag.color)
        if 'samples' in requested_nodes or get_samples:
            query = query.join(
                sample_1, sample_1.id == sample_to_tag_2.sample_id, isouter=True)

    # if 'rnaExpValues' in requested_nodes:

    results = query.distinct().all()

    return results
