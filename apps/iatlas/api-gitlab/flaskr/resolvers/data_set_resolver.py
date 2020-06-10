from sqlalchemy import and_, func, or_, orm
import json
from collections import defaultdict
from flaskr import db
from flaskr.db_models import FeatureClass, Sample, SampleToTag, Tag, TagToTag


def resolve_dataSet(_obj, info, name, group, feature=None):
    # "SELECT sample_id FROM samples_to_tags WHERE tag_id IN (",
    # "SELECT tag_id FROM tags_to_tags WHERE related_tag_id IN (",
    # "SELECT id FROM tags WHERE display IN (",
    # string_values_to_query_list(display),
    # ")))"
    sess = db.session

    # Get all tag ids or the passed groups.
    group_tag_query = sess.query(Tag.id).filter(
        Tag.name.in_(group))
    # Get all tag ids or the passed names.
    name_tag_query = sess.query(Tag.id).filter(
        Tag.name.in_(name))
    # Get all the tags related to the passed groups.
    tag_to_group_query = sess.query(TagToTag.tag_id).filter(TagToTag.related_tag_id.in_(
        group_tag_query.subquery()))
    # Get all the passed groups' values.
    group_query = sess.query(Tag).filter(
        Tag.id.in_(tag_to_group_query.subquery()))
    group_sub_query = group_query.subquery()
    # Get all the sample ids associated with the passed names.
    samples_to_names_query = sess.query(
        SampleToTag.sample_id).filter(SampleToTag.tag_id.in_(name_tag_query.subquery()))
    # Get all the sample ids associated with the tags.
    sample_to_tag_query = sess.query(SampleToTag) \
        .filter(SampleToTag.tag_id.in_(tag_to_group_query.subquery())) \
        .filter(SampleToTag.sample_id.in_(samples_to_names_query.subquery()))
    sample_to_tag_sub_query = sample_to_tag_query.subquery()
    # Add the sample ids to the group values.
    group_with_sample_id_query = sess.query(
        group_sub_query.c.name,
        group_sub_query.c.display,
        group_sub_query.c.characteristics,
        group_sub_query.c.color,
        # sample_to_tag_sub_query.c.sample_id
    )\
        .add_columns(
        func.coalesce(func.Count(sample_to_tag_sub_query.c.tag_id),
                      0).label("num_samples"))\
        .join(sample_to_tag_sub_query, sample_to_tag_sub_query.c.tag_id == group_sub_query.c.id, isouter=True)\
        .group_by(group_sub_query.c.name, group_sub_query.c.display, group_sub_query.c.characteristics, group_sub_query.c.color, sample_to_tag_sub_query.c.sample_id)

    # Add the sample ids to the group values.
    # group_with_sample_id_query = sess.query(SampleToTag.sample_id, group_sub_query.c.display, group_sub_query.c.name, group_sub_query.c.characteristics, group_sub_query.c.color)\
    #     .filter(SampleToTag.tag_id.in_(tag_to_group_query.subquery())) \
    #     .filter(SampleToTag.sample_id.in_(samples_to_names_query.subquery())) \
    #     .join(group_sub_query)

    query = group_with_sample_id_query.distinct()

    # query = sess.query(Sample.id)
    # sample = query.first()
    results = query.all()
    tcga_samples = query.count()

    grouped = defaultdict(list)
    for row in results:
        print("row: ", len(results))
        print("row: ", row)
        grouped[row.name].append(row)

    # for group in grouped.items():
    #     print("group: ", group)

    # request = info.context
    # response_data = request.json["query"]

    # print("response_data: ", response_data)

    return [{
        "sampleGroup": name[0],
        "groupName": group[0],
        "groupSize": tcga_samples,
        "characteristics": feature[0] if feature is not None else None
    }]
