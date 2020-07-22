from sqlalchemy import and_, orm
from api import db
from api.db_models import Patient, Sample, Tag
from .general_resolvers import build_join_condition, build_option_args, get_selection_set, get_value


def build_graphql_response(sample):
    return {
        'name': get_value(sample, 'name'),
        'patient': {
            'age': get_value(sample, 'age'),
            'barcode': get_value(sample, 'barcode'),
            'ethnicity': get_value(sample, 'ethnicity'),
            'gender': get_value(sample, 'gender'),
            'height': get_value(sample, 'height'),
            'race': get_value(sample, 'race'),
            'weight': get_value(sample, 'weight')
        }
    }


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
    core = build_option_args(selection_set, core_field_mapping)
    core = [sample_1.id.label('id')] if not core else core
    related_field_mapping = {'patient': 'patient'}
    relations = build_option_args(selection_set, related_field_mapping)

    if 'patient' in relations:
        patient_selection_set = get_selection_set(
            selection_set, child_node='patient')
        patient_core_field_mapping = {'age': patient_1.age.label('age'),
                                      'barcode': patient_1.barcode.label('barcode'),
                                      'ethnicity': patient_1.ethnicity.label('ethnicity'),
                                      'gender': patient_1.gender.label('gender'),
                                      'height': patient_1.height.label('height'),
                                      'race': patient_1.race.label('race'),
                                      'weight': patient_1.weight.label('weight')}
        core = core + build_option_args(
            patient_selection_set, patient_core_field_mapping)

    print('core: ', core)
    query = sess.query(*core)
    query = query.select_from(sample_1)

    if name:
        query = query.filter(sample_1.name.in_(name))

    if 'patient' in relations or patient:
        is_outer = not bool(patient)
        patient_join_condition = build_join_condition(
            patient_1.id, sample_1.patient_id, filter_column=patient_1.barcode, filter_list=patient)
        query = query.join(patient_1, and_(
            *patient_join_condition), isouter=is_outer)

    return query


def request_samples(_obj, info, name=None, patient=None, by_tag=False):
    query = build_sample_request(
        _obj, info, name=name, patient=patient, by_tag=by_tag)
    return query.distinct().all()
