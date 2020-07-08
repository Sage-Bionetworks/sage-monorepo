from sqlalchemy import and_, orm
from api import db
from api.db_models import Patient, Sample, Tag
from .general_resolvers import build_option_args, get_selection_set


def build_sample_request(_obj, info, name=None, patient=None, by_tag=False):
    """
    Builds a SQL query.
    """
    sess = db.session

    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, by_tag, child_node='samples')

    patient_1 = orm.aliased(Patient, name='p')
    sample_1 = orm.aliased(Sample, name='s')

    core_field_mapping = {'name': sample_1.name.label('name')}
    related_field_mapping = {'patient': 'patient'}

    core = build_option_args(selection_set, core_field_mapping)
    relations = build_option_args(selection_set, related_field_mapping)
    option_args = []

    query = sess.query(sample_1)

    if 'patient' in relations or patient:
        query = query.join((patient_1, sample_1.patient), isouter=True)
        option_args.append(orm.contains_eager(
            sample_1.patient.of_type(patient_1)))

    if option_args:
        query = query.options(*option_args)
    else:
        query = query.with_entities(*core)

    if name:
        query = query.filter(sample_1.name.in_(name))

    if patient:
        query = query.filter(patient_1.barcode.in_(patient))

    return query


def request_samples(_obj, info, name=None, patient=None, by_tag=False):
    query = build_sample_request(
        _obj, info, name=name, patient=patient, by_tag=by_tag)
    query = query.distinct()
    return query.all()
