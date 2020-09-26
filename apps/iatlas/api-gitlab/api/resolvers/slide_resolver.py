from api.db_models import (Slide)
from .resolver_helpers import (build_slide_graphql_response, get_requested,
                               request_slides, simple_patient_request_fields, slide_request_fields)


def resolve_slides(_obj, info, maxAgeAtDiagnosis=None, minAgeAtDiagnosis=None, barcode=None, ethnicity=None, gender=None,
                   maxHeight=None, minHeight=None, name=None, race=None, maxWeight=None, minWeight=None, sample=None):
    requested = get_requested(info, slide_request_fields)

    patient_requested = get_requested(
        info, simple_patient_request_fields, 'patient')

    slide_results = request_slides(requested, patient_requested, max_age_at_diagnosis=maxAgeAtDiagnosis, min_age_at_diagnosis=minAgeAtDiagnosis, barcode=barcode,
                                   ethnicity=ethnicity, gender=gender, max_height=maxHeight, min_height=minHeight, name=name, race=race, max_weight=maxWeight, min_weight=minWeight, sample=sample)

    return map(build_slide_graphql_response, slide_results)
