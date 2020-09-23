from .resolver_helpers import (build_edge_graphql_response, build_edge_request, edge_request_fields,
                               get_requested, get_selection_set, node_request_fields)


def resolve_edges(_obj, info, dataSet=None, related=None, network=None, page=1):
    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, True, 'items')
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=edge_request_fields)

    node_1_requested = set()
    if 'node1' in requested:
        node_1_selection_set = get_selection_set(
            selection_set, True, 'node1')
        node_1_requested = get_requested(
            selection_set=node_1_selection_set, requested_field_mapping=node_request_fields)

    node_2_requested = set()
    if 'node2' in requested:
        node_2_selection_set = get_selection_set(
            selection_set, True, 'node2')
        node_2_requested = get_requested(
            selection_set=node_2_selection_set, requested_field_mapping=node_request_fields)

    edge_results = build_edge_request(
        requested, node_1_requested, node_2_requested, data_set=dataSet, related=related, network=network).paginate(page, 100000, False)

    return {
        'items': map(build_edge_graphql_response, edge_results.items),
        'page': edge_results.page,
        'pages': edge_results.pages,
        'total': edge_results.total
    }
