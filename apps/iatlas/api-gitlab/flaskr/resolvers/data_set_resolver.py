from sqlalchemy import and_, func, or_
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
    # Get all the sample ids associated with the tags.
    sample_to_tag_sub_query = sess.query(
        SampleToTag.sample_id).filter(SampleToTag.tag_id.in_(tag_to_group_query.subquery())).subquery()
    # Limit samples_to_tags to only rows with tags in the passed names.
    reduced_to_names_query = sess.query(
        SampleToTag.sample_id).filter(SampleToTag.tag_id.in_(name_tag_query.subquery()))
    # Limit samples ids to only ids that are associated with names
    samples_to_names_query = reduced_to_names_query.join(
        sample_to_tag_sub_query, SampleToTag.sample_id == sample_to_tag_sub_query.c.sample_id)
    # .filter(or_(SampleToTag.tag_id.in_(name_tag_to_tag_sub_query)))

    query = sess.query(Sample.id)
    # sample = query.first()
    results = samples_to_names_query.distinct().all()
    tcga_samples = samples_to_names_query.distinct().count()

    print("results: ", results)

    # query = query.join(SampleToTag.query.join(
    #     Tag.query.filter(Tag.name.in_(name)), SampleToTag.tag_id == Tag.id),
    #     Sample.id == SampleToTag.sample_id)

    # result = query.all()

    # print("result: ", result)

    # query = Sample.query.with_entities(Sample.id)
    # query = Sample.query(label('sample_number', func.count(Sample.id)))

    # classes = FeatureClass.query.

    # User.query.filter(User.roles.any(Role.id.in_(
    #     [role.id for role in current_user.roles]))).all()

    return [{
        "sampleGroup": name[0],
        "groupName": group[0],
        "groupSize": tcga_samples,
        "characteristics": feature[0] if feature is not None else None
    }]
