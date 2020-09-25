from api.db_models import (Patient)
from .resolver_helpers import (build_patient_graphql_response, get_requested, patient_request_fields,
                               request_patients, return_patient_derived_fields, simple_sample_request_fields, simple_slide_request_fields)


def resolve_patients(_obj, info, ageAtDiagnosis=None, barcode=None, ethnicity=None, gender=None, height=None, race=None, sample=None, slide=None, weight=None):
    requested = get_requested(
        info=info, requested_field_mapping=patient_request_fields)

    slide_requested = get_requested(
        info=info, requested_field_mapping=simple_slide_request_fields, child_node='slides')

    patient_results = request_patients(requested, age_at_diagnosis=ageAtDiagnosis, barcode=barcode,
                                       ethnicity=ethnicity, gender=gender, height=height, race=race, sample=sample, slide=slide, weight=weight)
    patient_ids = set(patient.id for patient in patient_results)

    (sample_dict, slide_dict) = return_patient_derived_fields(requested, slide_requested, patient_ids=patient_ids,
                                                              age_at_diagnosis=ageAtDiagnosis, barcode=barcode, ethnicity=ethnicity, gender=gender, height=height, race=race, sample=sample, slide=slide, weight=weight)

    return map(build_patient_graphql_response(sample_dict, slide_dict), patient_results)
