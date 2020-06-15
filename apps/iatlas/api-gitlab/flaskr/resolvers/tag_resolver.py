from sqlalchemy import and_, func, or_, orm
import json
from collections import defaultdict
from flaskr import db
from flaskr.db_models import FeatureClass, Sample, SampleToTag, Tag, TagToTag
from flaskr.database import return_sample_to_tag_query, return_tag_query, return_tag_to_tag_query
from .resolver_helpers import get_value


def resolve_tags(_obj, info, dataSet, related, feature=None):
    sess = db.session

    # SELECT
    #     tags_1."name" AS "name",
    #     tags_1.display AS display,
    #     tags_1."characteristics" AS "characteristics",
    #     tags_1.color AS color,
    #     ARRAY_AGG(samples_to_tags_2.sample_id) AS samples,
    #     COUNT(DISTINCT samples_to_tags_2.sample_id) AS sample_count
    # FROM samples_to_tags AS samples_to_tags_1
    # INNER JOIN tags_to_tags AS tags_to_tags_1 ON samples_to_tags_1.tag_id = tags_to_tags_1.tag_id AND tags_to_tags_1.related_tag_id
    #     IN(SELECT dataset_tags.id FROM tags AS dataset_tags WHERE dataset_tags."name" IN('TCGA'))
    # INNER JOIN tags_to_tags AS tags_to_tags_2 ON samples_to_tags_1.tag_id = tags_to_tags_2.related_tag_id AND tags_to_tags_2.related_tag_id
    #     IN(SELECT related_tags.id FROM tags AS related_tags WHERE related_tags."name" IN('Immune_Subtype'))
    # INNER JOIN samples_to_tags AS samples_to_tags_2 ON samples_to_tags_2.sample_id = samples_to_tags_1.sample_id
    #     AND tags_to_tags_2.tag_id = samples_to_tags_2.tag_id
    # JOIN tags AS tags_1 ON tags_1.id = tags_to_tags_2.tag_id
    # GROUP BY "name", display, "characteristics", color

    selection_set = info.field_nodes[0].selection_set

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

    select_fields = []
    if selection_set is not None:
        for selection in selection_set.selections:
            if selection.name.value in select_field_node_mapping:
                select_fields.append(
                    select_field_node_mapping.get(selection.name.value))

    query = sess.query(*select_fields).\
        select_from(samples_to_tags_1).\
        join(tags_to_tags_1,
             and_(samples_to_tags_1.tag_id == tags_to_tags_1.tag_id,
                  tags_to_tags_1.related_tag_id.in_(
                      sess.query(dataset_tag.id).filter(
                          dataset_tag.name.in_(dataSet))
                  ))).\
        join(tags_to_tags_2,
             and_(samples_to_tags_1.tag_id == tags_to_tags_2.related_tag_id,
                  tags_to_tags_2.related_tag_id.in_(
                      sess.query(related_tag.id).filter(
                          related_tag.name.in_(related))))).\
        join(samples_to_tags_2,
             and_(samples_to_tags_2.sample_id == samples_to_tags_1.sample_id,
                  tags_to_tags_2.tag_id == samples_to_tags_2.tag_id)).\
        join(tag, tag.id == tags_to_tags_2.tag_id).\
        group_by(tag.name, tag.display, tag.characteristics, tag.color)
    results = query.all()

    return [{
        "characteristics": get_value(row, 'characteristics'),
        "color": get_value(row, 'color'),
        "display": get_value(row, 'display'),
        "name": get_value(row, 'name'),
        "sampleCount": get_value(row, 'sample_count'),
        "sampleIds": get_value(row, 'samples'),
    } for row in results]
