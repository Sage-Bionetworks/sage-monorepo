from .resolver_helpers import (build_node_graphql_response, build_node_request, data_set_request_fields, feature_request_fields,
                               get_selection_set, gene_request_fields, get_requested, node_request_fields, return_node_derived_fields,
                               tag_request_fields)
from api.telemetry import profile


def resolve_nodes(_obj, info, dataSet=None, related=None, network=None, page=1):
    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, True, 'items')
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=node_request_fields)

    data_set_selection_set = get_selection_set(
        selection_set, 'dataSet' in requested, 'dataSet')
    data_set_requested = get_requested(
        selection_set=data_set_selection_set, requested_field_mapping=data_set_request_fields)

    feature_selection_set = get_selection_set(
        selection_set, 'feature' in requested, 'feature')
    feature_requested = get_requested(
        selection_set=feature_selection_set, requested_field_mapping=feature_request_fields)

    gene_selection_set = get_selection_set(
        selection_set, 'gene' in requested, 'gene')
    gene_requested = get_requested(
        selection_set=gene_selection_set, requested_field_mapping=gene_request_fields)

    tag_selection_set = get_selection_set(
        selection_set, 'tags' in requested, 'tags')
    tag_requested = get_requested(
        selection_set=tag_selection_set, requested_field_mapping=tag_request_fields)

    tag_dict = dict()

    if 'tags' in requested:
        tag_dict = return_node_derived_fields(
            tag_requested, data_set=dataSet, related=related, network=network)

    node_results = build_node_request(requested, data_set_requested, feature_requested, gene_requested,
                                      data_set=dataSet, related=related, network=network).paginate(page, 100000, False, 100000)

    return {
        'items': map(build_node_graphql_response(tag_dict), node_results.items),
        'page': node_results.page,
        'pages': node_results.pages,
        'total': node_results.total
    }
