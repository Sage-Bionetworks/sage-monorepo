from .resolver_helpers import (get_requested, build_sample_graphql_response,
                               request_samples, simple_patient_request_fields, sample_request_fields)


def resolve_samples(_obj, info, ageAtDiagnosis=None, ethnicity=None, gender=None,
                    height=None, name=None, patient=None, race=None, weight=None):
    requested = get_requested(info, sample_request_fields)

    patient_requested = get_requested(
        info, simple_patient_request_fields, 'patient')

    samples = request_samples(requested, patient_requested, set(), age_at_diagnosis=ageAtDiagnosis,
                              ethnicity=ethnicity, gender=gender, height=height, patient=patient, race=race, sample=name, weight=weight)

    return map(build_sample_graphql_response, samples)
