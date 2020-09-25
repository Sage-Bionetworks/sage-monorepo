from itertools import groupby
from .resolver_helpers import (build_sample_graphql_response, get_requested, get_selection_set, get_value, request_samples,
                               sample_by_mutation_status_request_fields, sample_request_fields, simple_patient_request_fields)


def resolve_samples_by_mutations_status(_obj, info, ageAtDiagnosis=None, ethnicity=None, gender=None, height=None,
                                        mutationId=None, mutationStatus=None, patient=None, race=None, sample=None, weight=None):
    status_requested = get_requested(
        info, sample_by_mutation_status_request_fields)

    sample_selection_set = get_selection_set(info=info, child_node='samples')
    requested = get_requested(
        selection_set=sample_selection_set, requested_field_mapping=sample_request_fields)

    patient_requested = get_requested(
        selection_set=sample_selection_set, requested_field_mapping=simple_patient_request_fields, child_node='patient')

    sample_results = request_samples(requested, patient_requested, status_requested, age_at_diagnosis=ageAtDiagnosis, ethnicity=ethnicity, gender=gender,
                                     height=height, mutation_id=mutationId, mutation_status=mutationStatus, patient=patient, race=race, sample=sample, weight=weight, by_status=True)

    status_dict = dict()
    for sample_status, samples_list in groupby(sample_results, key=lambda s: s.status):
        status_dict[sample_status] = status_dict.get(
            sample_status, []) + list(samples_list)

    def build_response(sample_set):
        status, samples = sample_set
        return {
            'samples': map(build_sample_graphql_response, samples),
            'status': status
        }

    return map(build_response, status_dict.items())
