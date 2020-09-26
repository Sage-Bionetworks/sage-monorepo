from .resolver_helpers import (get_requested, build_sample_graphql_response,
                               request_samples, simple_patient_request_fields, sample_request_fields)


def resolve_samples(_obj, info, maxAgeAtDiagnosis=None, minAgeAtDiagnosis=None, ethnicity=None, gender=None,
                    maxHeight=None, minHeight=None, name=None, patient=None, race=None, maxWeight=None, minWeight=None):
    requested = get_requested(info, sample_request_fields)

    patient_requested = get_requested(
        info, simple_patient_request_fields, 'patient')

    samples = request_samples(requested, patient_requested, set(), max_age_at_diagnosis=maxAgeAtDiagnosis, min_age_at_diagnosis=minAgeAtDiagnosis,
                              ethnicity=ethnicity, gender=gender, max_height=maxHeight, min_height=minHeight, patient=patient, race=race, sample=name, max_weight=maxWeight, min_weight=minWeight)

    return map(build_sample_graphql_response, samples)
