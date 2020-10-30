from .resolver_helpers import (build_edge_graphql_response, build_edge_request, edge_request_fields,
                               get_requested, get_selection_set, node_request_fields)
from .resolver_helpers.paging_utils import paginate, Paging, paging_fields


def resolve_edges(_obj, info, distinct=False, maxScore=None, minScore=None, node1=None, node2=None, paging=None):
    '''
    All keyword arguments are optional. Keyword arguments are:
        `maxScore` - a float, a maximum score value
        `minScore` - a float, a minimum score value
        `node1` - a list of strings, starting node names
        `node2` - a list of strings, ending node names
    '''
    selection_set = get_selection_set(info=info, child_node='items')
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=edge_request_fields)

    node_1_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=node_request_fields, child_node='node1')

    node_2_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=node_request_fields, child_node='node2')

    if distinct == False:
        requested.add('id')  # Add the id as a cursor if not selecting distinct

    paging = paging if paging else Paging.DEFAULT

    query, count_query = build_edge_request(
        requested, node_1_requested, node_2_requested, distinct=distinct, max_score=maxScore, min_score=minScore, node_start=node1, node_end=node2, paging=paging)

    pagination_requested = get_requested(info, paging_fields, 'paging')
    return paginate(query, count_query, paging, distinct, build_edge_graphql_response, pagination_requested)
