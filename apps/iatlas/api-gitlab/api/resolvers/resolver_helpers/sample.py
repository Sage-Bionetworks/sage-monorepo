from sqlalchemy import and_, orm
from api import db
from api.db_models import Patient, Sample, SampleToMutation, Tag
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


def build_sample_mutation_join_condition(sample_to_mutation_model, sample_model, mutation_status, mutation_id=None):
    join_condition = build_join_condition(sample_to_mutation_model.sample_id, sample_model.id,
                                          filter_column=sample_to_mutation_model.mutation_id, filter_list=mutation_id)
    if mutation_status:
        join_condition.append(
            sample_to_mutation_model.status == mutation_status)
    return join_condition


def build_sample_request(_obj, info, mutation_id=None, mutation_status=None, name=None, patient=None, by_status=False, by_tag=False):
    """
    Builds a SQL query.
    """
    sess = db.session

    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, (by_tag or by_status), child_node='samples')

    patient_1 = orm.aliased(Patient, name='p')
    sample_1 = orm.aliased(Sample, name='s')
    sample_to_mutation_1 = orm.aliased(SampleToMutation, name='sm')

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

    if by_status:
        core = core + [sample_to_mutation_1.status.label('status')]

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

    if by_status:
        is_inner = bool(mutation_status) or bool(mutation_id)
        sample_mutation_join_condition = build_sample_mutation_join_condition(
            sample_to_mutation_1, sample_1, mutation_status, mutation_id)
        query = query.join(sample_to_mutation_1, and_(
            *sample_mutation_join_condition), isouter=not is_inner)

    return query


def request_samples(_obj, info, mutation_id=None, mutation_status=None, name=None, patient=None, by_status=False, by_tag=False):
    query = build_sample_request(_obj, info, mutation_id=mutation_id, mutation_status=mutation_status,
                                 name=name, patient=patient, by_status=by_status, by_tag=by_tag)
    return query.distinct().all()
