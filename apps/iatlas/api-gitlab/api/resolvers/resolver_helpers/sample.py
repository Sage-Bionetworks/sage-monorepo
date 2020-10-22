from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import (Dataset, DatasetToSample, DatasetToTag, Feature, FeatureClass, FeatureToSample,
                           Patient, Sample, SampleToMutation, SampleToTag, Tag, TagToTag)
from .general_resolvers import build_join_condition, get_selected, get_value
from .patient import build_patient_graphql_response

simple_sample_request_fields = {'name'}

sample_request_fields = simple_sample_request_fields.union({'patient'})

feature_related_sample_request_fields = simple_sample_request_fields.union({
                                                                           'value'})

gene_related_sample_request_fields = simple_sample_request_fields.union({
                                                                        'rnaSeqExpr'})

mutation_related_sample_request_fields = sample_request_fields.union({
                                                                     'status'})

sample_by_mutation_status_request_fields = {'status', 'samples'}


def build_sample_graphql_response(sample):
    return {
        'name': get_value(sample),
        'patient': build_patient_graphql_response()(sample),
        'status': get_value(sample, 'status')
    }


def build_sample_mutation_join_condition(sample_to_mutation_model, sample_model, mutation_status, mutation_id=None, status=None):
    join_condition = build_join_condition(sample_to_mutation_model.sample_id, sample_model.id,
                                          filter_column=sample_to_mutation_model.mutation_id, filter_list=mutation_id)
    if mutation_status:
        join_condition.append(
            sample_to_mutation_model.status == mutation_status)
    return join_condition


def build_sample_request(
        requested, patient_requested, tag_status_requested, data_set=None, ethnicity=None, feature=None, feature_class=None, gender=None, max_age_at_diagnosis=None, max_height=None, max_weight=None, min_age_at_diagnosis=None, min_height=None, min_weight=None, mutation_id=None, mutation_status=None, patient=None, race=None, related=None, sample=None, tag=None, by_status=False, by_tag=False):
    '''
    Builds a SQL query.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request or in the 'samples' node if by mutation status or by tag.
        2nd position - a set of the requested fields in the 'patient' node of the graphql request. If 'patient' is not requested, this will be an empty set.
        3rd position - a set of the requested fields at the root of the graphql request if by mutation status or by tag. If not by mutation status or by tag, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `data_set` - a list of strings, data set names
        `ethnicity` - a list of strings, ethnicity enum
        `feature` - a list of strings, feature names
        `feature_class` - a list of strings, feature class names
        `gender` - a list of strings, gender enum
        `max_age_at_diagnosis` - an integer, a maximum age of a patient at the time of diagnosis
        `max_height` - an integer, a maximum height of a patient
        `max_weight` - an integer, a maximum weight of a patient
        `min_age_at_diagnosis` - an integer, a minimum age of a patient at the time of diagnosis
        `min_height` - an integer, a minimum height of a patient
        `min_weight` - an integer, a minimum weight of a patient
        `mutation_id` - a list integers, mutation ids
        `mutation_status` - a string, mutation status enum
        `patient` - a list of strings, patient barcodes
        `race` - a list of strings, race enum
        `related` - a list of strings, tag names related to data sets
        `sample` - a list of strings, sample names
        `tag` - a list of strings, tag names related to samples
        `by_status` - a boolean, true if the samples are by status
        `by_tag` - a boolean, true if the samples are by tag
    '''
    sess = db.session

    has_patient_filters = bool(
        patient or max_age_at_diagnosis or min_age_at_diagnosis or ethnicity or gender or max_height or min_height or race or max_weight or min_weight)

    data_set_to_sample_1 = aliased(DatasetToSample, name='ds')
    patient_1 = aliased(Patient, name='p')
    sample_1 = aliased(Sample, name='s')
    sample_to_mutation_1 = aliased(SampleToMutation, name='sm')
    tag_1 = aliased(Tag, name='t')

    core_field_mapping = {'name': sample_1.name.label('name')}
    patient_core_field_mapping = {'ageAtDiagnosis': patient_1.age_at_diagnosis.label('age_at_diagnosis'),
                                  'barcode': patient_1.barcode.label('barcode'),
                                  'ethnicity': patient_1.ethnicity.label('ethnicity'),
                                  'gender': patient_1.gender.label('gender'),
                                  'height': patient_1.height.label('height'),
                                  'race': patient_1.race.label('race'),
                                  'weight': patient_1.weight.label('weight')}
    tag_core_field_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                              'color': tag_1.color.label('color'),
                              'longDisplay': tag_1.long_display.label('tag_long_display'),
                              'shortDisplay': tag_1.short_display.label('tag_short_display')}

    # Only select fields that were requested.
    core = get_selected(requested, core_field_mapping)
    core.add(sample_1.id.label('id'))
    patient_core = get_selected(patient_requested, patient_core_field_mapping)
    tag_core = get_selected(tag_status_requested, tag_core_field_mapping)

    if by_status:
        core.add(sample_to_mutation_1.status.label('status'))

    if by_tag:
        core.add(tag_1.name.label('tag'))

    query = sess.query(*[*core, *patient_core, *tag_core])
    query = query.select_from(sample_1)

    if sample:
        query = query.filter(sample_1.name.in_(sample))

    if has_patient_filters or 'patient' in requested:
        is_outer = not has_patient_filters

        patient_join_condition = build_join_condition(
            sample_1.patient_id, patient_1.id, patient_1.barcode, patient)

        if bool(max_age_at_diagnosis):
            patient_join_condition.append(
                patient_1.age_at_diagnosis <= max_age_at_diagnosis)

        if bool(min_age_at_diagnosis):
            patient_join_condition.append(
                patient_1.age_at_diagnosis >= min_age_at_diagnosis)

        if bool(ethnicity):
            patient_join_condition.append(patient_1.ethnicity.in_(ethnicity))

        if bool(gender):
            patient_join_condition.append(patient_1.gender.in_(gender))

        if bool(max_height):
            patient_join_condition.append(patient_1.height <= max_height)

        if bool(min_height):
            patient_join_condition.append(patient_1.height >= min_height)

        if bool(race):
            patient_join_condition.append(patient_1.race.in_(race))

        if bool(max_weight):
            patient_join_condition.append(patient_1.weight <= max_weight)

        if bool(min_weight):
            patient_join_condition.append(patient_1.weight >= min_weight)

        query = query.join(patient_1, and_(
            *patient_join_condition), isouter=is_outer)

    if by_status:
        sample_mutation_join_condition = build_sample_mutation_join_condition(
            sample_to_mutation_1, sample_1, mutation_status, mutation_id)
        query = query.join(sample_to_mutation_1, and_(
            *sample_mutation_join_condition))

    if by_tag or related or tag:
        sample_to_tag_1 = aliased(SampleToTag, name='st')

        query = query.join(
            sample_to_tag_1, sample_to_tag_1.sample_id == sample_1.id)

        is_outer = not bool(tag)
        tag_join_condition = build_join_condition(
            tag_1.id, sample_to_tag_1.tag_id, tag_1.name, tag)
        query = query.join(tag_1, and_(*tag_join_condition), isouter=is_outer)

    if data_set or related:
        data_set_1 = aliased(Dataset, name='d')

        data_set_sub_query = sess.query(data_set_1.id).filter(
            data_set_1.name.in_(data_set)) if data_set else data_set

        data_set_to_sample_join_condition = build_join_condition(
            data_set_to_sample_1.sample_id, sample_1.id, data_set_to_sample_1.dataset_id, data_set_sub_query)
        query = query.join(
            data_set_to_sample_1, and_(*data_set_to_sample_join_condition))

    if feature or feature_class:
        feature_1 = aliased(Feature, name='f')
        feature_class_1 = aliased(FeatureClass, name='fc')
        feature_to_sample_1 = aliased(FeatureToSample, name='fs')

        query = query.join(feature_to_sample_1,
                           feature_to_sample_1.sample_id == sample_1.id)

        feature_join_condition = build_join_condition(
            feature_1.id, feature_to_sample_1.feature_id, feature_1.name, feature)
        query = query.join(feature_1, and_(*feature_join_condition))

        if feature_class:
            feature_class_join_condition = build_join_condition(
                feature_class_1.id, feature_1.class_id, feature_class_1.name, feature_class)
            query = query.join(
                feature_class_1, and_(*feature_class_join_condition))

    if related:
        data_set_to_tag_1 = aliased(DatasetToTag, name='dtt')
        related_tag_1 = aliased(Tag, name='rt')
        tag_to_tag_1 = aliased(TagToTag, name='tt')

        related_tag_sub_query = sess.query(related_tag_1.id).filter(
            related_tag_1.name.in_(related))

        data_set_tag_join_condition = build_join_condition(
            data_set_to_tag_1.dataset_id, data_set_to_sample_1.dataset_id, data_set_to_tag_1.tag_id, related_tag_sub_query)
        query = query.join(
            data_set_to_tag_1, and_(*data_set_tag_join_condition))

        query = query.join(tag_to_tag_1, and_(
            tag_to_tag_1.tag_id == tag_1.id, tag_to_tag_1.related_tag_id == data_set_to_tag_1.tag_id))

    order = []
    append_to_order = order.append
    if 'name' in requested:
        append_to_order(sample_1.name)
    if 'name' in tag_status_requested:
        append_to_order(tag_1.name)
    if 'shortDisplay' in tag_status_requested:
        append_to_order(tag_1.short_display)
    if 'longDisplay' in tag_status_requested:
        append_to_order(tag_1.long_display)
    if 'color' in tag_status_requested:
        append_to_order(tag_1.color)
    if 'characteristics' in tag_status_requested:
        append_to_order(tag_1.characteristics)
    if 'status' in tag_status_requested:
        append_to_order(sample_to_mutation_1.status)

    query = query.order_by(*order) if order else query

    return query


def request_samples(*args, **kwargs):
    '''
    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request or in the 'samples' node if by mutation status or by tag.
        2nd position - a set of the requested fields in the 'patient' node of the graphql request. If 'patient' is not requested, this will be an empty set.
        3rd position - a set of the requested fields at the root of the graphql request if by mutation status or by tag. If not by mutation status or by tag, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `data_set` - a list of strings, data set names
        `ethnicity` - a list of strings, ethnicity enum
        `feature` - a list of strings, feature names
        `feature_class` - a list of strings, feature class names
        `gender` - a list of strings, gender enum
        `max_age_at_diagnosis` - an integer, a maximum age of a patient at the time of diagnosis
        `max_height` - an integer, a maximum height of a patient
        `max_weight` - an integer, a maximum weight of a patient
        `min_age_at_diagnosis` - an integer, a minimum age of a patient at the time of diagnosis
        `min_height` - an integer, a minimum height of a patient
        `min_weight` - an integer, a minimum weight of a patient
        `mutation_id` - a list integers, mutation ids
        `mutation_status` - a string, mutation status enum
        `patient` - a list of strings, patient barcodes
        `race` - a list of strings, race enum
        `related` - a list of strings, tag names related to data sets
        `sample` - a list of strings, sample names
        `tag` - a list of strings, tag names related to samples
        `by_status` - a boolean, true if the samples are by status
        `by_tag` - a boolean, true if the samples are by tag
    '''
    query = build_sample_request(*args, **kwargs)
    return query.distinct().all()
