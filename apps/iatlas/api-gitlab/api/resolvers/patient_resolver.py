from api.db_models import (Patient)
from .resolver_helpers import (build_patient_graphql_response, get_requested, patient_request_fields,
                               request_patients, return_patient_derived_fields, simple_sample_request_fields, simple_slide_request_fields)


def resolve_patients(
        _obj, info, maxAgeAtDiagnosis=None, minAgeAtDiagnosis=None, barcode=None, dataSet=None, ethnicity=None, gender=None, maxHeight=None, minHeight=None, race=None, sample=None, slide=None, maxWeight=None, minWeight=None):
    requested = get_requested(
        info=info, requested_field_mapping=patient_request_fields)

    slide_requested = get_requested(
        info=info, requested_field_mapping=simple_slide_request_fields, child_node='slides')

    patient_results = request_patients(
        requested, max_age_at_diagnosis=maxAgeAtDiagnosis, min_age_at_diagnosis=minAgeAtDiagnosis, barcode=barcode, data_set=dataSet, ethnicity=ethnicity, gender=gender, max_height=maxHeight, min_height=minHeight, race=race, sample=sample, slide=slide, max_weight=maxWeight, min_weight=minWeight)
    patient_ids = set(patient.id for patient in patient_results)

    (sample_dict, slide_dict) = return_patient_derived_fields(
        requested, slide_requested, patient_ids=patient_ids, max_age_at_diagnosis=maxAgeAtDiagnosis, min_age_at_diagnosis=minAgeAtDiagnosis, barcode=barcode, data_set=dataSet, ethnicity=ethnicity, gender=gender, max_height=maxHeight, min_height=minHeight, race=race, sample=sample, slide=slide, max_weight=maxWeight, min_weight=minWeight)

    return map(build_patient_graphql_response(sample_dict, slide_dict), patient_results)
