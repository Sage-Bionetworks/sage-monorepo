from sqlalchemy import and_, func, or_, orm
import json
from collections import defaultdict
from flaskr import db
from flaskr.db_models import Feature, FeatureToSample, Sample, SampleToTag, Tag, TagToTag
from flaskr.database import return_sample_to_tag_query, return_tag_query, return_tag_to_tag_query
from .resolver_helpers import build_option_args, get_value, NoneType


def resolve_tags(_obj, info, dataSet, related, feature=None):
    sess = db.session

    # An example of the full query in SQL:
    # SELECT
    #     tags_1."name" AS "name",
    #     tags_1.display AS display,
    #     tags_1."characteristics" AS "characteristics",
    #     tags_1.color AS color,
    #     ARRAY_AGG(samples_to_tags_2.sample_id) AS samples,
    #     COUNT(DISTINCT samples_to_tags_2.sample_id) AS sample_count
    # FROM samples_to_tags AS samples_to_tags_1
    # INNER JOIN features_to_samples ON features_to_samples.sample_id = samples_to_tags_1.sample_id AND features_to_samples.feature_id
    #     IN(SELECT features.id FROM features WHERE features."name" IN('Neutrophils_Aggregate2'))
    # INNER JOIN tags_to_tags AS tags_to_tags_1 ON samples_to_tags_1.tag_id = tags_to_tags_1.tag_id AND tags_to_tags_1.related_tag_id
    #     IN(SELECT dataset_tags.id FROM tags AS dataset_tags WHERE dataset_tags."name" IN('TCGA'))
    # INNER JOIN tags_to_tags AS tags_to_tags_2 ON samples_to_tags_1.tag_id = tags_to_tags_2.related_tag_id AND tags_to_tags_2.related_tag_id
    #     IN(SELECT related_tags.id FROM tags AS related_tags WHERE related_tags."name" IN('Immune_Subtype'))
    # INNER JOIN samples_to_tags AS samples_to_tags_2 ON samples_to_tags_2.sample_id = samples_to_tags_1.sample_id
    #     AND tags_to_tags_2.tag_id = samples_to_tags_2.tag_id
    # JOIN tags AS tags_1 ON tags_1.id = tags_to_tags_2.tag_id
    # GROUP BY "name", display, "characteristics", color

    tag = orm.aliased(Tag, name='t')
    dataset_tag = orm.aliased(Tag, name='dt')
    related_tag = orm.aliased(Tag, name='rt')
    samples_to_tags_1 = orm.aliased(SampleToTag, name='st1')
    samples_to_tags_2 = orm.aliased(SampleToTag, name='st2')
    tags_to_tags_1 = orm.aliased(TagToTag, name='tt1')
    tags_to_tags_2 = orm.aliased(TagToTag, name='tt2')

    select_field_node_mapping = {'characteristics': tag.characteristics.label('characteristics'),
                                 'color': tag.color.label('color'),
                                 'display': tag.display.label('display'),
                                 'name': tag.name.label('name'),
                                 'sampleCount': func.count(func.distinct(samples_to_tags_2.sample_id)).label('sample_count'),
                                 'sampleIds': func.array_agg(func.distinct(samples_to_tags_2.sample_id)).label('samples')}

    # Only select fields that were requested.
    selection_set = info.field_nodes[0].selection_set or []
    select_fields = build_option_args(selection_set, select_field_node_mapping)
    requested_nodes = []
    for selection in selection_set.selections:
        requested_nodes.append(selection.name.value)

    query = sess.query(*select_fields)
    query = query.select_from(samples_to_tags_1)
    if type(feature) is not NoneType:
        query = query.join(FeatureToSample,
                           and_(FeatureToSample.sample_id == samples_to_tags_1.sample_id,
                                FeatureToSample.feature_id.in_(
                                    sess.query(Feature.id).filter(
                                        Feature.name.in_(feature))
                                )))
    query = query.join(tags_to_tags_1,
                       and_(samples_to_tags_1.tag_id == tags_to_tags_1.tag_id,
                            tags_to_tags_1.related_tag_id.in_(
                                sess.query(dataset_tag.id).filter(
                                    dataset_tag.name.in_(dataSet))
                            )))
    query = query.join(tags_to_tags_2,
                       and_(samples_to_tags_1.tag_id == tags_to_tags_2.related_tag_id,
                            tags_to_tags_2.related_tag_id.in_(
                                sess.query(related_tag.id).filter(
                                    related_tag.name.in_(related)))))
    query = query.join(samples_to_tags_2,
                       and_(samples_to_tags_2.sample_id == samples_to_tags_1.sample_id,
                            tags_to_tags_2.tag_id == samples_to_tags_2.tag_id))
    query = query.join(tag, tag.id == tags_to_tags_2.tag_id)
    if 'sampleCount' in requested_nodes or 'sampleIds' in requested_nodes:
        query = query.group_by(tag.name, tag.display,
                               tag.characteristics, tag.color)
    results = query.all()

    return [{
        "characteristics": get_value(row, 'characteristics'),
        "color": get_value(row, 'color'),
        "display": get_value(row, 'display'),
        "name": get_value(row, 'name'),
        "sampleCount": get_value(row, 'sample_count'),
        "sampleIds": get_value(row, 'samples'),
    } for row in results]
