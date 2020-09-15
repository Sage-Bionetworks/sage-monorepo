from sqlalchemy import and_
from sqlalchemy.orm import aliased
from itertools import groupby
from api import db
from api.db_models import Patient, Sample, Slide
from .general_resolvers import build_join_condition, get_selected, get_value
from .slide import build_slide_graphql_response

simple_patient_request_fields = {'ageAtDiagnosis',
                                 'barcode',
                                 'ethnicity',
                                 'gender',
                                 'height',
                                 'race',
                                 'weight'}

patient_request_fields = simple_patient_request_fields.union(
    {'samples', 'slides'})


def build_patient_graphql_response(sample_dict=dict(), slide_dict=dict()):
    def f(patient):
        if not patient:
            return None
        patient_id = get_value(patient, 'id')
        samples = sample_dict.get(patient_id, []) if sample_dict else []
        slides = slide_dict.get(patient_id, []) if slide_dict else []
        return {
            'ageAtDiagnosis': get_value(patient, 'age_at_diagnosis'),
            'barcode': get_value(patient, 'barcode'),
            'ethnicity': get_value(patient, 'ethnicity'),
            'gender': get_value(patient, 'gender'),
            'height': get_value(patient, 'height'),
            'race': get_value(patient, 'race'),
            'samples': map(get_value, samples),
            'slides': map(build_slide_graphql_response, slides),
            'weight': get_value(patient, 'weight')
        }
    return f


def build_patient_request(requested, age_at_diagnosis=None, barcode=None, ethnicity=None, gender=None, height=None,
                          race=None, weight=None, sample=None, slide=None):
    """
    Builds a SQL query.
    """
    sess = db.session

    patient_1 = aliased(Patient, name='p')
    sample_1 = aliased(Sample, name='s')
    slide_1 = aliased(Slide, name='sd')

    core_field_mapping = {'ageAtDiagnosis': patient_1.age_at_diagnosis.label('age_at_diagnosis'),
                          'barcode': patient_1.barcode.label('barcode'),
                          'ethnicity': patient_1.ethnicity.label('ethnicity'),
                          'gender': patient_1.gender.label('gender'),
                          'height': patient_1.height.label('height'),
                          'race': patient_1.race.label('race'),
                          'weight': patient_1.weight.label('weight')}

    # Only select fields that were requested.
    core = get_selected(requested, core_field_mapping)
    core.add(patient_1.id.label('id'))

    query = sess.query(*core)
    query = query.select_from(patient_1)

    if barcode:
        query = query.filter(patient_1.barcode.in_(barcode))

    if age_at_diagnosis:
        query = query.filter(patient_1.age_at_diagnosis.in_(age_at_diagnosis))

    if ethnicity:
        query = query.filter(patient_1.ethnicity.in_(ethnicity))

    if gender:
        query = query.filter(patient_1.gender.in_(gender))

    if height:
        query = query.filter(patient_1.height.in_(height))

    if race:
        query = query.filter(patient_1.race.in_(race))

    if weight:
        query = query.filter(patient_1.weight.in_(weight))

    if sample:
        sample_join_condition = build_join_condition(
            patient_1.id, sample_1.patient_id, filter_column=sample_1.name, filter_list=sample)
        query = query.join(sample_1, and_(
            *sample_join_condition), isouter=False)

    if slide:
        slide_join_condition = build_join_condition(
            patient_1.id, slide_1.patient_id, filter_column=slide_1.name, filter_list=slide)
        query = query.join(slide_1, and_(
            *slide_join_condition), isouter=False)

    order = []
    append_to_order = order.append
    if 'barcode' in requested:
        append_to_order(patient_1.barcode)
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

    return query


def get_samples(requested, patient_ids=set(), age_at_diagnosis=None, barcode=None, ethnicity=None,
                gender=None, height=None, race=None, weight=None, sample=None, slide=None):
    if patient_ids and 'samples' in requested:
        sess = db.session

        patient_1 = aliased(Patient, name='p')
        sample_1 = aliased(Sample, name='s')
        slide_1 = aliased(Slide, name='sd')

        # Always select the sample id and the patient id.
        core = {sample_1.id.label('id'),
                sample_1.name.label('name'),
                sample_1.patient_id.label('patient_id')}

        sample_query = sess.query(*core)
        sample_query = sample_query.select_from(sample_1)

        if sample:
            sample_query = sample_query.filter(sample_1.name.in_(sample))

        is_outer = not bool(
            barcode or age_at_diagnosis or ethnicity or gender or height or race or weight)

        patient_join_condition = build_join_condition(
            sample_1.patient_id, patient_1.id, patient_1.barcode, barcode)

        if bool(age_at_diagnosis):
            patient_join_condition.append(
                patient_1.age_at_diagnosis.in_(age_at_diagnosis))

        if bool(ethnicity):
            patient_join_condition.append(patient_1.ethnicity.in_(ethnicity))

        if bool(gender):
            patient_join_condition.append(patient_1.gender.in_(gender))

        if bool(height):
            patient_join_condition.append(patient_1.height.in_(height))

        if bool(race):
            patient_join_condition.append(patient_1.race.in_(race))

        if bool(weight):
            patient_join_condition.append(patient_1.weight.in_(weight))

        sample_query = sample_query.join(patient_1, and_(
            *patient_join_condition), isouter=is_outer)

        if slide:
            slide_join_condition = build_join_condition(
                slide_1.patient_id, patient_1.id, slide_1.name, slide)

            sample_query = sample_query.join(slide_1, and_(
                *slide_join_condition), isouter=False)

        order = []
        append_to_order = order.append
        if 'name' in requested:
            append_to_order(sample_1.name)

        sample_query = sample_query.order_by(*order) if order else sample_query

        return sample_query.distinct().all()

    return []


def get_slides(requested, slide_requested, patient_ids=set(), age_at_diagnosis=None, barcode=None, ethnicity=None,
               gender=None, height=None, race=None, weight=None, sample=None, slide=None):
    if patient_ids and 'slides' in requested:
        sess = db.session

        patient_1 = aliased(Patient, name='p')
        sample_1 = aliased(Sample, name='s')
        slide_1 = aliased(Slide, name='sd')

        core_field_mapping = {'description': slide_1.description.label('description'),
                              'name': slide_1.name.label('name')}

        core = get_selected(slide_requested, core_field_mapping)
        # Always select the sample id and the patient id.
        core |= {slide_1.id.label('id'),
                 slide_1.patient_id.label('patient_id')}

        slide_query = sess.query(*core)
        slide_query = slide_query.select_from(slide_1)

        if slide:
            slide_query = slide_query.filter(slide_1.name.in_(slide))

        is_outer = not bool(
            barcode or age_at_diagnosis or ethnicity or gender or height or race or weight)

        patient_join_condition = build_join_condition(
            slide_1.patient_id, slide_1.id, patient_1.barcode, barcode)

        if bool(age_at_diagnosis):
            patient_join_condition.append(
                patient_1.age_at_diagnosis.in_(age_at_diagnosis))

        if bool(ethnicity):
            patient_join_condition.append(patient_1.ethnicity.in_(ethnicity))

        if bool(gender):
            patient_join_condition.append(patient_1.gender.in_(gender))

        if bool(height):
            patient_join_condition.append(patient_1.height.in_(height))

        if bool(race):
            patient_join_condition.append(patient_1.race.in_(race))

        if bool(weight):
            patient_join_condition.append(patient_1.weight.in_(weight))

        slide_query = slide_query.join(patient_1, and_(
            *patient_join_condition), isouter=is_outer)

        if sample:
            sample_join_condition = build_join_condition(
                sample_1.patient_id, patient_1.id, sample_1.name, sample)

            slide_query = slide_query.join(sample_1, and_(
                *sample_join_condition), isouter=False)

        order = []
        append_to_order = order.append
        if 'name' in requested:
            append_to_order(slide_1.name)
        if 'description' in requested:
            append_to_order(slide_1.description)

        slide_query = slide_query.order_by(*order) if order else slide_query

        return slide_query.distinct().all()

    return []


def request_patients(requested, age_at_diagnosis=None, barcode=None, ethnicity=None, gender=None,
                     height=None, race=None, weight=None, sample=None, slide=None):
    query = build_patient_request(requested, age_at_diagnosis=age_at_diagnosis, barcode=barcode,
                                  ethnicity=ethnicity, gender=gender, height=height, race=race, weight=weight, sample=sample, slide=slide)
    return query.all()


def return_patient_derived_fields(requested, slide_requested, patient_ids=set(), age_at_diagnosis=None, barcode=None,
                                  ethnicity=None, gender=None, height=None, race=None, weight=None, sample=None, slide=None):
    samples = get_samples(requested, patient_ids=patient_ids, age_at_diagnosis=age_at_diagnosis, barcode=barcode,
                          ethnicity=ethnicity, gender=gender, height=height, race=race, weight=weight, sample=sample, slide=slide)

    samples_dict = dict()
    for key, collection in groupby(samples, key=lambda s: s.patient_id):
        samples_dict[key] = samples_dict.get(key, []) + list(collection)

    slides = get_slides(requested, slide_requested, patient_ids=patient_ids, age_at_diagnosis=age_at_diagnosis, barcode=barcode,
                        ethnicity=ethnicity, gender=gender, height=height, race=race, weight=weight, sample=sample, slide=slide)

    slides_dict = dict()
    for key, collection in groupby(slides, key=lambda s: s.patient_id):
        slides_dict[key] = slides_dict.get(key, []) + list(collection)

    return (samples_dict, slides_dict)
