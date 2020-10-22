import math
from collections import deque

from .resolver_helpers import (build_cnr_graphql_response, build_copy_number_result_request, cnr_request_fields,
                               feature_request_fields, gene_request_fields, get_requested, get_selection_set, simple_data_set_request_fields, simple_tag_request_fields)

from .resolver_helpers.paging_utils import paginate, Paging, paging_fields


def resolve_copy_number_results(_obj, info, dataSet=None, direction=None, distinct=False, entrez=None, feature=None, maxPValue=None,
                                maxLog10PValue=None, minLog10PValue=None, minMeanCnv=None, minMeanNormal=None,
                                minPValue=None, minTStat=None, paging=None, tag=None):

    selection_set = get_selection_set(info=info, child_node='items')
    requested = get_requested(selection_set=selection_set, requested_field_mapping=cnr_request_fields)

    if distinct == False:
        requested.add('id')  # Add the id as a cursor if not selecting distinct

    pagination_set = get_selection_set(info=info, child_node='paging')
    pagination_requested = get_requested(selection_set=pagination_set, requested_field_mapping=paging_fields)
    paging = paging if paging else {'type': Paging.CURSOR, 'first': Paging.MAX_LIMIT}

    data_set_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_data_set_request_fields, child_node='dataSet')

    feature_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=feature_request_fields, child_node='feature')

    gene_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=gene_request_fields, child_node='gene')

    tag_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_tag_request_fields, child_node='tag')

    query, count_query = build_copy_number_result_request(
        requested, data_set_requested, feature_requested, gene_requested, tag_requested,
        data_set=dataSet, direction=direction, distinct=distinct, entrez=entrez, feature=feature, max_p_value=maxPValue, max_log10_p_value=maxLog10PValue, min_log10_p_value=minLog10PValue, min_mean_cnv=minMeanCnv, min_mean_normal=minMeanNormal, min_p_value=minPValue, min_t_stat=minTStat, paging=paging, tag=tag)

    return paginate(query, count_query, paging, distinct, build_cnr_graphql_response)