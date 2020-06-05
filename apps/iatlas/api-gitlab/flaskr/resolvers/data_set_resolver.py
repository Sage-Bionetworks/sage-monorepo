from sqlalchemy import func
from flaskr.db_models import FeatureClass, Sample, SampleToTag


def resolve_dataSet(_obj, info, name, group, feature=None):
    # query = Sample.query.with_entities(Sample.id)

    # query = query.join(SampleToTag, Sample.id == SampleToTag.sample_id)

    # query = Sample.query.with_entities(Sample.id)
    # query = Sample.query(label('sample_number', func.count(Sample.id)))
    samples = Sample.query.with_entities(Sample.id).count()

    # classes = FeatureClass.query.

    # User.query.filter(User.roles.any(Role.id.in_(
    #     [role.id for role in current_user.roles]))).all()

    return [{
        "sampleGroup": name[0],
        "groupName": group[0],
        "groupSize": samples,
        "characteristics": feature[0] if feature is not None else None
    }]
