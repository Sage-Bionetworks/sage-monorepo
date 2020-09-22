from api.db_models import (Slide)
from .resolver_helpers import (build_slide_graphql_response, get_requested, get_selection_set,
                               request_slides, simple_patient_request_fields, slide_request_fields)


def resolve_slides(_obj, info, ageAtDiagnosis=None, barcode=None, ethnicity=None, gender=None,
                   height=None, name=None, race=None, weight=None, sample=None):
    selection_set = get_selection_set(info.field_nodes[0].selection_set, True)
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=slide_request_fields)

    patient_selection_set = get_selection_set(
        selection_set, 'patient' in requested, 'patient')
    patient_requested = get_requested(
        selection_set=patient_selection_set, requested_field_mapping=simple_patient_request_fields)

    slide_results = request_slides(requested, patient_requested, age_at_diagnosis=ageAtDiagnosis, barcode=barcode,
                                   ethnicity=ethnicity, gender=gender, height=height, name=name, race=race, weight=weight, sample=sample)

    return map(build_slide_graphql_response, slide_results)
