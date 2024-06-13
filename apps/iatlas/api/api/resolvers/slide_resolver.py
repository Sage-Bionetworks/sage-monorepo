from .resolver_helpers import build_slide_graphql_response, get_requested, simple_patient_request_fields, slide_request_fields, get_selection_set, build_slide_request
from .resolver_helpers.paging_utils import paginate, Paging, paging_fields


def resolve_slides(_obj, info, maxAgeAtDiagnosis=None, minAgeAtDiagnosis=None, barcode=None, ethnicity=None, gender=None, maxHeight=None, minHeight=None, name=None, race=None, maxWeight=None, minWeight=None, sample=None, paging=None, distinct=False):

    selection_set = get_selection_set(info=info, child_node='items')

    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=slide_request_fields)

    patient_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_patient_request_fields, child_node='patient')

    paging = paging if paging else Paging.DEFAULT

    query, count_query = build_slide_request(
        requested, patient_requested, max_age_at_diagnosis=maxAgeAtDiagnosis, min_age_at_diagnosis=minAgeAtDiagnosis, barcode=barcode,
        ethnicity=ethnicity, gender=gender, max_height=maxHeight, min_height=minHeight, name=name, race=race, max_weight=maxWeight, min_weight=minWeight, sample=sample, paging=paging, distinct=distinct)

    pagination_requested = get_requested(info, paging_fields, 'paging')
    return paginate(query, count_query, paging, distinct, build_slide_graphql_response(), pagination_requested)
