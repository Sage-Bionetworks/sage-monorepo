from .resolver_helpers import build_node_graphql_response, build_node_request, feature_request_fields, fetch_nodes_with_tags, get_selection_set, gene_request_fields, get_requested, node_request_fields, simple_data_set_request_fields, simple_tag_request_fields
from .resolver_helpers.paging_utils import fetch_page, paginate, Paging, paging_fields, process_page, create_paging
from api.telemetry import profile


def resolve_nodes(
        _obj, info, dataSet=None, distinct=False, entrez=None, feature=None, featureClass=None, geneType=None, maxScore=None, minScore=None, network=None, related=None, paging=None, tag=None):
    # The selection is nested under the 'items' node.
    selection_set = get_selection_set(info=info, child_node='items')
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=node_request_fields)

    data_set_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_data_set_request_fields, child_node='dataSet')

    feature_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=feature_request_fields, child_node='feature')

    gene_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=gene_request_fields, child_node='gene')

    tag_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_tag_request_fields, child_node='tags')

    if distinct == False:
        requested.add('id')  # Add the id as a cursor if not selecting distinct

    paging = create_paging(paging, 12_000)

    query, count_query = build_node_request(
        requested, data_set_requested, feature_requested, gene_requested, data_set=dataSet, distinct=distinct, entrez=entrez, feature=feature, feature_class=featureClass, gene_type=geneType, max_score=maxScore, min_score=minScore, network=network, related=related, paging=paging, tag=tag)

    items = {}
    tag_dict = {}
    # Verify that we are indeed requesting tags before running any queries.
    if len(tag_requested):
        items, tag_dict = fetch_nodes_with_tags(
            query, paging, distinct, tag_requested)
        items = list(items)

    else:
        # tags not requested, proceed as normal
        items = fetch_page(query, paging, distinct)

    pagination_requested = get_requested(info, paging_fields, 'paging')
    return process_page(items, count_query, paging, distinct, build_node_graphql_response(tag_dict), pagination_requested)
