from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import (Dataset, DatasetToSample, Feature, FeatureClass, FeatureToSample,
                           Patient, Sample)
from .general_resolvers import build_join_condition, get_selected, get_value
from .patient import build_patient_graphql_response
from .paging_utils import get_pagination_queries
from .response_utils import build_simple_tag_graphql_response

simple_sample_request_fields = {'name'}

cohort_sample_request_fields = {'name', 'tag'}

sample_request_fields = simple_sample_request_fields.union({'patient'})

feature_related_sample_request_fields = simple_sample_request_fields.union({
                                                                           'value'})

gene_related_sample_request_fields = simple_sample_request_fields.union({
                                                                        'rnaSeqExpr'})

mutation_related_sample_request_fields = sample_request_fields.union({
                                                                     'status'})

sample_by_mutation_status_request_fields = {'status', 'samples'}


def build_sample_graphql_response(prefix='sample_'):
    def f(sample):
        if not sample:
            return None
        else:
            dict = {
                'id': get_value(sample, prefix + 'id'),
                'name': get_value(sample, prefix + 'name'),
                'status': get_value(sample, prefix + 'mutation_status'),
                'rnaSeqExpr': get_value(sample, prefix + 'gene_rna_seq_expr'),
                'value': get_value(sample, prefix + 'feature_value'),
                'patient': build_patient_graphql_response()(sample),
                'tag': build_simple_tag_graphql_response()(sample)
            }
            return(dict)
    return(f)


def build_sample_mutation_join_condition(sample_to_mutation_model, sample_model, mutation_status, mutation_id=None, status=None):
    join_condition = build_join_condition(sample_to_mutation_model.sample_id, sample_model.id,
                                          filter_column=sample_to_mutation_model.mutation_id, filter_list=mutation_id)
    if mutation_status:
        join_condition.append(
            sample_to_mutation_model.status == mutation_status)
    return join_condition


def build_sample_request(
        requested, patient_requested, data_set=None, ethnicity=None, feature=None, feature_class=None, gender=None, max_age_at_diagnosis=None, max_height=None, max_weight=None, min_age_at_diagnosis=None, min_height=None, min_weight=None, patient=None, race=None, sample=None, distinct=False, paging=None):
    '''
    Builds a SQL query.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request or in the 'samples' node if by mutation status or by tag.
        2nd position - a set of the requested fields in the 'patient' node of the graphql request. If 'patient' is not requested, this will be an empty set.

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
        `patient` - a list of strings, patient barcodes
        `race` - a list of strings, race enum
        `sample` - a list of strings, sample names
        `distinct` - a boolean, indicates whether duplicate records should be filtered out
        `paging` - a dict containing pagination metadata
    '''
    sess = db.session

    has_patient_filters = bool(
        patient or max_age_at_diagnosis or min_age_at_diagnosis or ethnicity or gender or max_height or min_height or race or max_weight or min_weight)

    data_set_to_sample_1 = aliased(DatasetToSample, name='ds')
    patient_1 = aliased(Patient, name='p')
    sample_1 = aliased(Sample, name='s')

    core_field_mapping = {
        'name': sample_1.name.label('sample_name')
    }
    patient_core_field_mapping = {
        'ageAtDiagnosis': patient_1.age_at_diagnosis.label('patient_age_at_diagnosis'),
        'barcode': patient_1.barcode.label('patient_barcode'),
        'ethnicity': patient_1.ethnicity.label('patient_ethnicity'),
        'gender': patient_1.gender.label('patient_gender'),
        'height': patient_1.height.label('patient_height'),
        'race': patient_1.race.label('patient_race'),
        'weight': patient_1.weight.label('patient_weight')
    }

    core = get_selected(requested, core_field_mapping)
    core.add(sample_1.id.label('sample_id'))
    patient_core = get_selected(patient_requested, patient_core_field_mapping)

    query = sess.query(*[*core, *patient_core])
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

    if data_set:
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

    order = []
    append_to_order = order.append
    if 'name' in requested:
        append_to_order(sample_1.name)

    query = query.order_by(*order) if order else query

    return get_pagination_queries(query, paging, distinct, cursor_field=sample_1.id)
