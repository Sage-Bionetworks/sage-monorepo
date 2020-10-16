import math
from collections import deque
from .resolver_helpers import (build_cnr_graphql_response, build_copy_number_result_request, cnr_request_fields, data_set_request_fields,
                               feature_request_fields, gene_request_fields, get_requested, get_selection_set, simple_tag_request_fields)

from .resolver_helpers.cursor_utils import get_limit


def resolve_copy_number_results(_obj, info, **kwargs):
    meta_requested = get_requested(
        selection_set=info.field_nodes[0].selection_set, requested_field_mapping={'page', 'pageInfo', 'totalCount'})

    edges = get_selection_set(info=info, child_node='edges')
    selection_set = get_selection_set(selection_set=edges, child_node='node')

    requested = get_requested(selection_set=selection_set, requested_field_mapping=cnr_request_fields)

    distinct = info.variable_values['distinct'] if 'distinct' in info.variable_values.keys() else False
    page = None
    if distinct == True:
        page = int(info.variable_values['page']) if 'page' in info.variable_values.keys() else 1
    else:
        requested.add('id') # Add the id as a cursor if not selecting distinct

    data_set_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=data_set_request_fields, child_node='dataSet')

    feature_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=feature_request_fields, child_node='feature')

    gene_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=gene_request_fields, child_node='gene')

    tag_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_tag_request_fields, child_node='tag')

    query, count_query = build_copy_number_result_request(requested, data_set_requested, feature_requested, gene_requested, tag_requested, data_set=kwargs.pop('dataSet', 0), **kwargs)

    first = kwargs.get('first')
    last = kwargs.get('last')
    limit, sort_order = get_limit(first, last)
    pageInfo = {}

    if distinct and page != None and not math.isnan(page):
        resp = query.paginate(page, limit)
        results = map(build_cnr_graphql_response, resp.items) # returns iterator
    else:
        resp = query.limit(limit+1).all() # request 1 more than we need, so we can determine if additional pages are available. returns list.
        if sort_order == 'ASC':
            hasNextPage = resp != None and (len(resp) == limit + 1)
            pageInfo['hasNextPage'] = hasNextPage
            pageInfo['hasPreviousPage'] = False
            if hasNextPage:
                resp.pop(-1) # remove the extra last item
        if sort_order == 'DESC':
            resp.reverse() # We have to reverse the list to get previous pages in the expected order
            pageInfo['hasNextPage'] = False
            hasPreviousPage = resp != None and (len(resp) == limit + 1)
            pageInfo['hasPreviousPage'] = hasPreviousPage
            if hasPreviousPage:
                resp.pop(0) # remove the extra first item

        results_map = map(build_cnr_graphql_response, resp) # returns iterator
        results = deque(results_map)
        pageInfo['startCursor'] = results[0]['cursor']
        pageInfo['endCursor'] = results[-1]['cursor']

    data = {
        'edges': results
    }
    if 'pageInfo' in meta_requested:
        data['pageInfo'] = pageInfo
    if 'page' in meta_requested:
        data['page'] = page
    # only call count if "totalCount" is requested
    if 'totalCount' in meta_requested:
        count = count_query.count() # TODO: Consider caching this value per query, and/or making count query in parallel
        data['totalCount'] = count
    return data
