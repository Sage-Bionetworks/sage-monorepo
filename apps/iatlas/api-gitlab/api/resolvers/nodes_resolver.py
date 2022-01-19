from .resolver_helpers import build_node_graphql_response, build_node_request, feature_request_fields, get_selection_set, gene_request_fields, get_requested, node_request_fields, simple_data_set_request_fields, simple_tag_request_fields
from .resolver_helpers.paging_utils import paging_fields, create_paging, paginate, paging_fields


def resolve_nodes(_obj, info, dataSet=None, distinct=False, entrez=None, feature=None, featureClass=None, geneType=None, maxScore=None, minScore=None, nTags=None, network=None, related=None, paging=None, tag=None):

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

    paging = create_paging(paging, 1000)

    query, count_query = build_node_request(
        requested, data_set_requested, feature_requested, gene_requested, data_set=dataSet, distinct=distinct, entrez=entrez, feature=feature, feature_class=featureClass, gene_type=geneType, max_score=maxScore, min_score=minScore, network=network, n_tags=nTags, related=related, paging=paging, tag=tag)

    pagination_requested = get_requested(info, paging_fields, 'paging')
    return paginate(query, count_query, paging, distinct, build_node_graphql_response(requested, tag_requested), pagination_requested)
