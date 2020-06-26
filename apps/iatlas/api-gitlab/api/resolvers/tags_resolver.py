from sqlalchemy import and_, func, or_, orm
import json
from collections import defaultdict
from api import db
from api.db_models import (
    Dataset, DatasetToSample, Feature, FeatureClass,
    FeatureToSample, Sample, SampleToTag, Tag, TagToTag)
from api.database import return_sample_to_tag_query, return_tag_query, return_tag_to_tag_query
from .resolver_helpers import build_option_args, get_value


def resolve_tags(_obj, info, dataSet, related, feature=None, featureClass=None):
    sess = db.session
    """
    Builds a SQL request and returns values from the DB.

    The query may be larger or smaller depending on the requested fields.
    An example of the full query in SQL:

    SELECT DISTINCT
        tags_1."name" AS "name",
        tags_1.display AS display,
        tags_1."characteristics" AS "characteristics",
        tags_1.color AS color,
        ARRAY_AGG(sample_to_tag_2.sample_id) AS samples,
        COUNT(DISTINCT sample_to_tag_2.sample_id) AS sample_count
    FROM samples_to_tags AS sample_to_tag_1
    INNER JOIN features_to_samples ON features_to_samples.sample_id = sample_to_tag_1.sample_id AND features_to_samples.feature_id
        IN(SELECT features.id FROM features WHERE features."name" IN('Neutrophils_Aggregate2'))
    INNER JOIN datasets_to_samples AS datasets_to_samples_1 ON sample_to_tag_1.sample_id = datasets_to_samples_1.sample_id AND datasets_to_samples_1.dataset_id
        IN(SELECT dataset_1.id FROM datasets AS dataset_1 WHERE dataset_1."name" IN('TCGA'))
    INNER JOIN tags_to_tags AS tag_to_tag_1 ON sample_to_tag_1.tag_id = tag_to_tag_1.related_tag_id AND tag_to_tag_1.related_tag_id
        IN(SELECT related_tags.id FROM tags AS related_tags WHERE related_tags."name" IN('Immune_Subtype'))
    INNER JOIN samples_to_tags AS sample_to_tag_2 ON sample_to_tag_2.sample_id = sample_to_tag_1.sample_id
        AND tag_to_tag_1.tag_id = sample_to_tag_2.tag_id
    JOIN tags AS tags_1 ON tags_1.id = tag_to_tag_1.tag_id
    GROUP BY "name", display, "characteristics", color
    """

    tag = orm.aliased(Tag, name='t')
    dataset_1 = orm.aliased(Dataset, name='d')
    dataset_to_sample_1 = orm.aliased(DatasetToSample, name='ds')
    related_tag = orm.aliased(Tag, name='rt')
    sample_to_tag_1 = orm.aliased(SampleToTag, name='st1')
    sample_to_tag_2 = orm.aliased(SampleToTag, name='st2')
    tag_to_tag_1 = orm.aliased(TagToTag, name='tt')

    select_field_node_mapping = {'characteristics': tag.characteristics.label('characteristics'),
                                 'color': tag.color.label('color'),
                                 'display': tag.display.label('display'),
                                 'name': tag.name.label('name'),
                                 'sampleCount': func.count(func.distinct(sample_to_tag_2.sample_id)).label('sample_count'),
                                 'sampleIds': func.array_agg(func.distinct(sample_to_tag_2.sample_id)).label('samples')}

    # Only select fields that were requested.
    selection_set = info.field_nodes[0].selection_set or []
    select_fields = build_option_args(selection_set, select_field_node_mapping)
    requested_nodes = []
    for selection in selection_set.selections:
        requested_nodes.append(selection.name.value)

    query = sess.query(*select_fields)
    query = query.select_from(sample_to_tag_1)

    if feature or featureClass:
        feature_1 = orm.aliased(Feature, name='f')
        feature_to_sample_1 = orm.aliased(FeatureToSample, name='fs')
        feature_sub_query = sess.query(feature_1.id)
        if feature:
            feature_sub_query = feature_sub_query.filter(
                feature_1.name.in_(feature))
        if featureClass:
            class_1 = orm.aliased(FeatureClass, name='fc')
            feature_sub_query = feature_sub_query.join(class_1, and_(
                feature_1.class_id == class_1.id, class_1.name.in_(featureClass)))
        query = query.join(feature_to_sample_1,
                           and_(feature_to_sample_1.sample_id == sample_to_tag_1.sample_id,
                                feature_to_sample_1.feature_id.in_(feature_sub_query)))

    query = query.join(dataset_to_sample_1,
                       and_(dataset_to_sample_1.sample_id == sample_to_tag_1.sample_id,
                            dataset_to_sample_1.dataset_id.in_(
                                sess.query(dataset_1.id).filter(
                                    dataset_1.name.in_(dataSet))
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
