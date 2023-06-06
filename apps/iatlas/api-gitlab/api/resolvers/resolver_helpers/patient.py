from sqlalchemy import and_
from sqlalchemy.orm import aliased
from itertools import groupby
from api import db
from api.db_models import Dataset, DatasetToSample, Patient, Sample, Slide
from .general_resolvers import build_join_condition, get_selected, get_value
from .paging_utils import get_pagination_queries

simple_patient_request_fields = {
    'ageAtDiagnosis',
    'barcode',
    'ethnicity',
    'gender',
    'height',
    'race',
    'weight'
}

patient_request_fields = simple_patient_request_fields.union(
    {'samples', 'slides'})


def build_patient_graphql_response(requested=dict(), slide_requested=dict(), sample=None, slide=None, prefix='patient_'):
    from .slide import build_slide_graphql_response

    def f(patient):
        if not patient:
            return None
        else:
            patient_id = get_value(patient, prefix + 'id')
            samples = get_samples(patient_id, requested, sample)
            slides = get_slides(patient_id, requested, slide_requested, slide)
            dict = {
                'id': patient_id,
                'ageAtDiagnosis': get_value(patient, prefix + 'age_at_diagnosis'),
                'barcode': get_value(patient, prefix + 'barcode'),
                'ethnicity': get_value(patient, prefix + 'ethnicity'),
                'gender': get_value(patient, prefix + 'gender'),
                'height': get_value(patient, prefix + 'height'),
                'race': get_value(patient, prefix + 'race'),
                'weight': get_value(patient, prefix + 'weight'),
                'samples': map(get_value, samples),
                'slides': map(build_slide_graphql_response(), slides)
            }
            return(dict)
    return f


def build_patient_request(
    requested, distinct=False, paging=None, max_age_at_diagnosis=None, min_age_at_diagnosis=None, barcode=None, data_set=None, ethnicity=None, gender=None, max_height=None, min_height=None,
    race=None, max_weight=None, min_weight=None, sample=None, slide=None
):
    """
    Builds a SQL query.
    """
    sess = db.session

    patient_1 = aliased(Patient, name='p')
    sample_1 = aliased(Sample, name='s')
    slide_1 = aliased(Slide, name='sd')

    core_field_mapping = {
        'ageAtDiagnosis': patient_1.age_at_diagnosis.label('patient_age_at_diagnosis'),
        'barcode': patient_1.name.label('patient_barcode'),
        'ethnicity': patient_1.ethnicity.label('patient_ethnicity'),
        'gender': patient_1.gender.label('patient_gender'),
        'height': patient_1.height.label('patient_height'),
        'race': patient_1.race.label('patient_race'),
        'weight': patient_1.weight.label('patient_weight')
    }

    core = get_selected(requested, core_field_mapping)
    core.add(patient_1.id.label('patient_id'))

    query = sess.query(*core)
    query = query.select_from(patient_1)

    if barcode:
        query = query.filter(patient_1.name.in_(barcode))

    if max_age_at_diagnosis:
        query = query.filter(patient_1.age_at_diagnosis <=
                             max_age_at_diagnosis)

    if min_age_at_diagnosis:
        query = query.filter(patient_1.age_at_diagnosis >=
                             min_age_at_diagnosis)

    if ethnicity:
        query = query.filter(patient_1.ethnicity.in_(ethnicity))

    if gender:
        query = query.filter(patient_1.gender.in_(gender))

    if max_height:
        query = query.filter(patient_1.height <= max_height)

    if min_height:
        query = query.filter(patient_1.height >= min_height)

    if race:
        query = query.filter(patient_1.race.in_(race))

    if max_weight:
        query = query.filter(patient_1.weight <= max_weight)

    if min_weight:
        query = query.filter(patient_1.weight >= min_weight)

    if sample or data_set:
        data_set_1 = aliased(Dataset, name='d')
        data_set_to_sample_1 = aliased(DatasetToSample, name='ds')

        is_outer = not bool(sample)

        sample_join_condition = build_join_condition(
            patient_1.id, sample_1.patient_id, filter_column=sample_1.name, filter_list=sample)
        query = query.join(sample_1, and_(
            *sample_join_condition), isouter=is_outer)

        data_set_sub_query = sess.query(data_set_1.id).filter(
            data_set_1.name.in_(data_set)) if data_set else None

        data_set_to_sample_join_condition = build_join_condition(
            data_set_to_sample_1.sample_id, sample_1.id, data_set_to_sample_1.dataset_id, data_set_sub_query)
        query = query.join(data_set_to_sample_1, and_(
            *data_set_to_sample_join_condition))

    if slide:
        slide_join_condition = build_join_condition(
            patient_1.id, slide_1.patient_id, filter_column=slide_1.name, filter_list=slide)
        query = query.join(slide_1, and_(
            *slide_join_condition), isouter=False)

    order = []
    append_to_order = order.append
    if 'barcode' in requested:
        append_to_order(patient_1.name)
    if 'ageAtDiagnosis' in requested:
        append_to_order(patient_1.age_at_diagnosis)
    if 'gender' in requested:
        append_to_order(patient_1.gender)
    if 'race' in requested:
        append_to_order(patient_1.race)
    if 'ethnicity' in requested:
        append_to_order(patient_1.ethnicity)
    if 'weight' in requested:
        append_to_order(patient_1.weight)
    if 'height' in requested:
        append_to_order(patient_1.height)

    query = query.order_by(*order) if order else query

    return get_pagination_queries(query, paging, distinct, cursor_field=patient_1.id)


def get_samples(id, requested, sample=None):

    if 'samples' in requested:

        sess = db.session
        sample_1 = aliased(Sample, name='s')
        core = {sample_1.name.label('name')}
        query = sess.query(*core)
        query = query.select_from(sample_1)

        query = query.filter(sample_1.patient_id == id)

        if sample:
            query = query.filter(sample_1.name.in_(sample))

        order = []
        append_to_order = order.append
        if 'name' in requested:
            append_to_order(sample_1.name)

        query = query.order_by(*order) if order else query

        import logging
        logger = logging.getLogger("patient response")
        logger.info(query)

        x = query.all()
        logger.info(x)
        return(x)

    return []


def get_slides(id, requested, slide_requested, slide=None):
    if 'slides' not in requested:
        return []

    else:
        sess = db.session
        slide_1 = aliased(Slide, name='sd')

        core_field_mapping = {
            'description': slide_1.description.label('slide_description'),
            'name': slide_1.name.label('slide_name')
        }

        core = get_selected(slide_requested, core_field_mapping)

        query = sess.query(*core)
        query = query.select_from(slide_1)

        query = query.filter(slide_1.patient_id == id)

        if slide:
            query = query.filter(slide_1.name.in_(slide))

        order = []
        append_to_order = order.append
        if 'name' in slide_requested:
            append_to_order(slide_1.name)
        if 'description' in slide_requested:
            append_to_order(slide_1.description)

        query = query.order_by(*order) if order else query

        return query.all()
