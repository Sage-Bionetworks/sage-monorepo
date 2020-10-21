from .resolver_helpers import (build_node_graphql_response, build_node_request, data_set_request_fields, feature_request_fields,
                               get_selection_set, gene_request_fields, get_requested, node_request_fields, return_node_derived_fields,
                               simple_tag_request_fields)
from api.telemetry import profile


def resolve_nodes(_obj, info, dataSet=None, entrez=None, feature=None, maxScore=None, minScore=None, network=None, related=None, tag=None, page=1):
    # The selection is nested under the 'items' node.
    selection_set = get_selection_set(info=info, child_node='items')
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=node_request_fields)

    data_set_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=data_set_request_fields, child_node='dataSet')

    feature_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=feature_request_fields, child_node='feature')

    gene_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=gene_request_fields, child_node='gene')

    tag_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_tag_request_fields, child_node='tags')

    node_results = build_node_request(
        requested, data_set_requested, feature_requested, gene_requested, data_set=dataSet, entrez=entrez, feature=feature, max_score=maxScore, min_score=minScore, network=network, related=related, tag=tag).paginate(page, 100000, False)

    tag_dict = return_node_derived_fields(
        requested, tag_requested, data_set=dataSet, entrez=entrez, feature=feature, max_score=maxScore, min_score=minScore, network=network, related=related, tag=tag) if node_results.items else dict()

    return {
        'items': map(build_node_graphql_response(tag_dict), node_results.items),
        'page': node_results.page,
        'pages': node_results.pages,
        'total': node_results.total
    }
