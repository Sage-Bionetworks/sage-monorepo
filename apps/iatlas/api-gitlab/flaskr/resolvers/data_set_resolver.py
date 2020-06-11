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
    group_tag_id_query = sess.query(Tag.id).filter(Tag.name.in_(group))
    # Get all tag ids or the passed names.
    name_tag_alias = orm.aliased(Tag, name='nt')
    name_tag_id_query = sess.query(
        name_tag_alias.id).filter(name_tag_alias.name.in_(name))
    # Get all the tags related to the passed groups.
    tag_to_group_query = sess.query(TagToTag.tag_id).filter(TagToTag.related_tag_id.in_(
        group_tag_id_query.subquery()))
    # Get all the passed groups' values.
    tag_alias = orm.aliased(Tag, name='t')
    group_query = sess.query(tag_alias.id, tag_alias.name, tag_alias.display, tag_alias.characteristics, tag_alias.color).filter(
        tag_alias.id.in_(tag_to_group_query.subquery()))
    group_id_query = sess.query(tag_alias.id).filter(
        tag_alias.id.in_(tag_to_group_query.subquery()))
    # group_sub_query = group_query.subquery()
    # Get all the sample ids associated with the passed names.
    sample_to_name_alias = orm.aliased(SampleToTag, name='stn')
    samples_to_names_query = sess.query(
        sample_to_name_alias.sample_id).filter(sample_to_name_alias.tag_id.in_(name_tag_id_query.subquery()))
    # Get all the sample ids associated with the tags.
    # sample_to_tag_query = sess.query(SampleToTag) \
    #     .filter(SampleToTag.tag_id.in_(tag_to_group_query.subquery())) \
    #     .filter(SampleToTag.sample_id.in_(samples_to_names_query.subquery()))
    # sample_to_tag_sub_query = sample_to_tag_query.subquery()
    # Add the sample ids to the group values.
    # group_with_sample_id_query = group_query\
    #     .join(sample_to_tag_sub_query, sample_to_tag_sub_query.c.tag_id == tag_alias.id)\
    #     .group_by(tag_alias.name, tag_alias.display, tag_alias.characteristics, tag_alias.color, sample_to_tag_sub_query.c.sample_id)\
    #     .add_columns(func.Count(sample_to_tag_sub_query.c.tag_id).label('num_samples'))

    group_tag_alias = orm.aliased(Tag, name='gt')
    desired_tag_alias = orm.aliased(Tag, name='dt')

    # Add the sample ids to the group values.
    # group_with_sample_id_query = sess.query(SampleToTag.sample_id, group_sub_query.c.display, group_sub_query.c.name, group_sub_query.c.characteristics, group_sub_query.c.color)\
    #     .filter(SampleToTag.tag_id.in_(tag_to_group_query.subquery())) \
    #     .filter(SampleToTag.sample_id.in_(samples_to_names_query.subquery())) \
    #     .join(group_sub_query)

    new_alias = orm.aliased(SampleToTag, name="na")
    sub_query = sess.query(new_alias).filter(
        new_alias.tag_id.in_(group_id_query)).subquery()

    sample_to_tag_alias = orm.aliased(SampleToTag, name='st')
    sample_count_query = sess.query(sample_to_tag_alias.sample_id, func.count('*').label('num_samples'))\
        .filter(sample_to_tag_alias.tag_id.in_(name_tag_id_query))\
        .join(sub_query, sample_to_tag_alias.sample_id == sub_query.c.sample_id)\
        .group_by(sample_to_tag_alias.sample_id)

    test_query = sample_count_query.distinct().all()
    query = group_query.distinct()

    # query = sess.query(Sample.id)
    # sample = query.first()
    results = query.all()
    # tcga_samples = query.count()
    print("row: ", len(test_query))
    for row in test_query[0:5]:
        print("row: ", row.num_samples)

    sample_to_tag_alias = orm.aliased(SampleToTag, name='st')
    sample_count_query = sess.query(sample_to_tag_alias.sample_id).filter(
        sample_to_tag_alias.tag_id.in_(name_tag_id_query))

    return [{
        "sampleGroup": row.name,
        "groupName": row.display,
        "groupSize": sample_count_query.filter(sample_to_tag_alias.tag_id == row.id).filter(
            sample_to_tag_alias.sample_id.in_(samples_to_names_query)).distinct().count(),
        "characteristics": row.characteristics,
        "color": row.color,
    } for row in results]

    # for group in grouped.items():
    #     print("group: ", group)

    # request = info.context
    # response_data = request.json["query"]

    # print("response_data: ", response_data)

    # return [{
    #     "sampleGroup": name[0],
    #     "groupName": group[0],
    #     "groupSize": tcga_samples,
    #     "characteristics": feature[0] if feature is not None else None
    # }]
