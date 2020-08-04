from .resolver_helpers import (build_node_graphql_response, build_node_request, data_set_request_fields, feature_request_fields,
                               gene_request_fields, get_requested, node_request_fields, return_node_derived_fields, tag_request_fields)


def resolve_nodes(_obj, info, dataSet=None, related=None, network=None):
    requested = get_requested(info, node_request_fields)

    data_set_requested = get_requested(
        info, data_set_request_fields, 'dataSet' in requested, 'dataSet')

    feature_requested = get_requested(
        info, feature_request_fields, 'feature' in requested, 'feature')

    gene_requested = get_requested(
        info, gene_request_fields, 'gene' in requested, 'gene')

    tag_requested = get_requested(
        info, tag_request_fields, 'tags' in requested, 'tags')

    tag_dict = dict()

    if 'tags' in requested:
        tag_dict = return_node_derived_fields(
            tag_requested, data_set=dataSet, related=related, network=network)

    return map(build_node_graphql_response(tag_dict),
               build_node_request(requested, data_set_requested, feature_requested, gene_requested,
                                  data_set=dataSet, related=related, network=network).limit(1000).yield_per(1000).all())
