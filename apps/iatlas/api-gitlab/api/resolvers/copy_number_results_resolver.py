import math
from collections import deque
from .resolver_helpers import (build_cnr_graphql_response, build_copy_number_result_request, cnr_request_fields, data_set_request_fields,
                               feature_request_fields, gene_request_fields, get_requested, get_selection_set, simple_tag_request_fields)

from .resolver_helpers.cursor_utils import get_limit, to_cursor_hash


def resolve_copy_number_results(_obj, info, **kwargs):
    pagination_set = get_selection_set(info=info, child_node='pagination')
    pagination_requested = get_requested(selection_set=pagination_set, requested_field_mapping={'page', 'pages', 'total', 'cursorInfo', 'offsetInfo'})

    selection_set = get_selection_set(info=info, child_node='items')

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

    pagination = kwargs.get('pagination', {})
    cursor = pagination.get('cursorInput')
    first = cursor.get('first') if cursor else None
    last = cursor.get('last') if cursor else None
    offset = pagination.get('offsetInput')
    limit = offset.get('limit') if offset else None
    limit, sort_order = get_limit(first, last, limit)
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
        pageInfo['startCursor'] = to_cursor_hash(results[0]['id'])
        pageInfo['endCursor'] = to_cursor_hash(results[-1]['id'])

    data = {
        'items': results
    }

    print('pagination_requested', pagination_requested)
    if 'cursorInfo' in pagination_requested or 'offsetInfo' in pagination_requested:
        pagination = {}
        if 'cursorInfo' in pagination_requested:
            pagination['cursorInfo'] = pageInfo
        if 'offsetInfo' in pagination_requested:
            offsetInfo = {
                'page': page,
                'limit': limit
            }
            pagination['offsetInfo'] = offsetInfo
        # only call count if "totalCount" is requested
        if 'total' or 'pages' in pagination_requested:
            count = count_query.count() # TODO: Consider caching this value per query, and/or making count query in parallel
            pagination['total'] = count
            pagination['pages'] = math.ceil(count/limit)
        data['pagination'] = pagination
    return data
