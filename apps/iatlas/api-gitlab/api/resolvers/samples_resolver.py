from .resolver_helpers import get_requested, build_sample_graphql_response, build_sample_request, simple_patient_request_fields, sample_request_fields, get_selection_set
from .resolver_helpers.paging_utils import paginate, Paging, paging_fields


def resolve_samples(_obj, info, maxAgeAtDiagnosis=None, minAgeAtDiagnosis=None, ethnicity=None, gender=None, maxHeight=None, minHeight=None, name=None, patient=None, race=None, maxWeight=None, minWeight=None, paging=None, distinct=False):

    selection_set = get_selection_set(info=info, child_node='items')

    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=sample_request_fields)

    patient_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_patient_request_fields, child_node='patient')

    paging = paging if paging else Paging.DEFAULT

    query, count_query = build_sample_request(requested, patient_requested, max_age_at_diagnosis=maxAgeAtDiagnosis, min_age_at_diagnosis=minAgeAtDiagnosis,
                                              ethnicity=ethnicity, gender=gender, max_height=maxHeight, min_height=minHeight, patient=patient, race=race, sample=name, max_weight=maxWeight, min_weight=minWeight, paging=paging, distinct=distinct)

    pagination_requested = get_requested(info, paging_fields, 'paging')
    return paginate(query, count_query, paging, distinct, build_sample_graphql_response(), pagination_requested)
