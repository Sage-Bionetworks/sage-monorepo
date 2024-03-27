from .resolver_helpers import build_patient_graphql_response, build_patient_request, get_requested, patient_request_fields, simple_slide_request_fields, get_selection_set
from .resolver_helpers.paging_utils import paginate, Paging, paging_fields


def resolve_patients(_obj, info, distinct=False, paging=None, maxAgeAtDiagnosis=None, minAgeAtDiagnosis=None, barcode=None, dataSet=None, ethnicity=None, gender=None, maxHeight=None, minHeight=None, race=None, sample=None, slide=None, maxWeight=None, minWeight=None):

    selection_set = get_selection_set(info=info, child_node='items')

    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=patient_request_fields)

    slide_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_slide_request_fields, child_node='slides')

    paging = paging if paging else Paging.DEFAULT

    query, count_query = build_patient_request(
        requested, paging=paging, distinct=distinct, max_age_at_diagnosis=maxAgeAtDiagnosis, min_age_at_diagnosis=minAgeAtDiagnosis, barcode=barcode, data_set=dataSet, ethnicity=ethnicity, gender=gender, max_height=maxHeight, min_height=minHeight, race=race, sample=sample, slide=slide, max_weight=maxWeight, min_weight=minWeight
    )

    pagination_requested = get_requested(info, paging_fields, 'paging')

    res = paginate(query, count_query, paging, distinct,
                   build_patient_graphql_response(requested=requested, slide_requested=slide_requested, sample=sample, slide=slide), pagination_requested)

    return(res)
