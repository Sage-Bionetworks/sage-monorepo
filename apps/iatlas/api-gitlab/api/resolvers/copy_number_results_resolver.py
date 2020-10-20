import math
from collections import deque

from .resolver_helpers import (build_cnr_graphql_response, build_copy_number_result_request, cnr_request_fields, data_set_request_fields,
                               feature_request_fields, gene_request_fields, get_requested, get_selection_set, simple_tag_request_fields)

from .resolver_helpers.paging_utils import get_limit, to_cursor_hash, Paging


def resolve_copy_number_results(_obj, info, dataSet=None, direction=None, distinct=False, entrez=None, feature=None, maxPValue=None,
                                maxLog10PValue=None, minLog10PValue=None, minMeanCnv=None, minMeanNormal=None,
                                minPValue=None, minTStat=None, paging=None, tag=None):
    pagination_set = get_selection_set(info=info, child_node='paging')
    pagination_requested = get_requested(selection_set=pagination_set, requested_field_mapping={'type', 'page', 'pages', 'total', 'first', 'last', 'before', 'after'})

    selection_set = get_selection_set(info=info, child_node='items')

    requested = get_requested(selection_set=selection_set, requested_field_mapping=cnr_request_fields)
    distinct = info.variable_values['distinct'] if 'distinct' in info.variable_values.keys() else False
    if distinct == False:
        requested.add('id') # Add the id as a cursor if not selecting distinct

    paging = paging if paging else {'type': Paging.CURSOR, 'first': Paging.MAX_LIMIT}
    paging_type = paging.get('type', Paging.CURSOR)

    data_set_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=data_set_request_fields, child_node='dataSet')

    feature_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=feature_request_fields, child_node='feature')

    gene_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=gene_request_fields, child_node='gene')

    tag_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_tag_request_fields, child_node='tag')

    query, count_query = build_copy_number_result_request(
        requested, data_set_requested, feature_requested, gene_requested, tag_requested,
                                                   data_set=dataSet, direction=direction, distinct=distinct, entrez=entrez, feature=feature, max_p_value=maxPValue, max_log10_p_value=maxLog10PValue,min_log10_p_value=minLog10PValue, min_mean_cnv=minMeanCnv,min_mean_normal=minMeanNormal, min_p_value=minPValue, min_t_stat=minTStat, paging=paging, tag=tag)

    page = None
    before = None
    after = None
    first = paging.get('first')
    last = paging.get('last')
    limit = paging.get('limit')
    limit, sort_order = get_limit(first, last, limit)
    pageInfo = {
        'type': paging_type,
        'page': page,
        'pages': None,
        'limit': limit,
        'total': None
    }

    if paging_type == Paging.OFFSET or distinct == True:
        page = paging.get('page', 1)
        pageInfo['page'] = page
        pageInfo['type'] = Paging.OFFSET # if distinct is True, paging type must be OFFSET
        resp = query.paginate(page, limit)
        results = map(build_cnr_graphql_response, resp.items) # returns iterator
    else:
        resp = query.limit(limit+1).all() # request 1 more than we need, so we can determine if additional pages are available. returns list.
        if sort_order == Paging.ASC:
            hasNextPage = resp != None and (len(resp) == limit + 1)
            pageInfo['hasNextPage'] = hasNextPage
            pageInfo['hasPreviousPage'] = False
            if hasNextPage:
                resp.pop(-1) # remove the extra last item
        if sort_order == Paging.DESC:
            resp.reverse() # We have to reverse the list to get previous pages in the expected order
            pageInfo['hasNextPage'] = False
            hasPreviousPage = resp != None and (len(resp) == limit + 1)
            pageInfo['hasPreviousPage'] = hasPreviousPage
            if hasPreviousPage:
                resp.pop(0) # remove the extra first item

        results_map = map(build_cnr_graphql_response, resp) # returns iterator
        results = deque(results_map)
        pageInfo['startCursor'] = to_cursor_hash(results[0]['id'])
        pageInfo['endCursor'] = to_cursor_hash(results[-1]['id'])

    if 'total' or 'pages' in pagination_requested:
        count = count_query.count() # TODO: Consider caching this value per query, and/or making count query in parallel
        pageInfo['total'] = count
        pageInfo['pages'] = math.ceil(count/limit)

    data = {
        'items': results,
        'paging': pageInfo
    }


    return data
