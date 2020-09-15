from .resolver_helpers import (get_requested, get_selection_set, build_sample_graphql_response,
                               request_samples, simple_patient_request_fields, sample_request_fields)


def resolve_samples(_obj, info, ageAtDiagnosis=None, ethnicity=None, gender=None,
                    height=None, name=None, patient=None, race=None, weight=None):
    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, True, 'items')
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=sample_request_fields)

    patient_selection_set = get_selection_set(
        selection_set, 'patient' in requested, 'patient')
    patient_requested = get_requested(
        selection_set=patient_selection_set, requested_field_mapping=simple_patient_request_fields)

    samples = request_samples(requested, patient_requested, set(), age_at_diagnosis=ageAtDiagnosis, ethnicity=ethnicity,
                              gender=gender, height=height, patient=patient, race=race, sample=name, weight=weight)

    return map(build_sample_graphql_response, samples)
