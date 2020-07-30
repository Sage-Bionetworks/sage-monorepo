from .resolver_helpers import build_node_graphql_response, request_nodes


def resolve_nodes(_obj, info, dataSet=None, related=None, network=None):
    node_results = request_nodes(_obj, info, data_set=dataSet,
                                 related=related, network=network)

    return map(build_cnr_graphql_response, node_results)
