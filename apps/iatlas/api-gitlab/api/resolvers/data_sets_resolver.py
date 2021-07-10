from .resolver_helpers import build_data_set_graphql_response, data_set_request_fields, simple_sample_request_fields, get_requested, build_data_set_request, get_selection_set
from .resolver_helpers.paging_utils import paginate, Paging, paging_fields
from ..telemetry import profile


@profile(__name__)
def resolve_data_sets(_obj, info, dataSet=None, sample=None, dataSetType=None, paging=None, distinct=False):

    selection_set = get_selection_set(info=info, child_node='items')

    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=data_set_request_fields)

    sample_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_sample_request_fields, child_node='samples')

    paging = paging if paging else Paging.DEFAULT

    query, count_query = build_data_set_request(
        requested, data_set=dataSet, sample=sample, data_set_type=dataSetType, paging=paging, distinct=distinct)

    pagination_requested = get_requested(info, paging_fields, 'paging')
    return paginate(query, count_query, paging, distinct, build_data_set_graphql_response(requested=requested, sample_requested=sample_requested, sample=sample), pagination_requested)
