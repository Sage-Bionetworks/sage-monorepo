import base64
import math
from collections import deque

class Paging:
    OFFSET = 'OFFSET'
    CURSOR = 'CURSOR'
    MAX_LIMIT = 100000
    ASC = 'ASC'
    DESC = 'DESC'

paging_fields = {'type', 'page', 'pages', 'total', 'first', 'last', 'before', 'after'}

def to_cursor_hash(val):
    return str(base64.b64encode(str(val).encode("utf-8")), "utf-8")

def from_cursor_hash(encoded):
    return str(base64.b64decode(str(encoded)), "utf-8")

def get_cursor(before, after):
    if after != None:
        return (from_cursor_hash(after), Paging.ASC)
    if before != None:
        return (from_cursor_hash(before), Paging.DESC)
    return (None, Paging.ASC)

def parse_limit(n):
    return min(Paging.MAX_LIMIT, int(n))

def get_limit(first, last, limit):
    if first and not math.isnan(first):
        return (parse_limit(first), Paging.ASC)
    if last and not math.isnan(last):
        return (parse_limit(last), Paging.DESC)
    if limit and not math.isnan(limit):
        return (parse_limit(limit), Paging.ASC)
    return (Paging.MAX_LIMIT, Paging.ASC)

def paginate(query, count_query, paging, distinct, response_builder):
    paging_type = paging.get('type', Paging.CURSOR)
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
        # if distinct is True, paging type must be OFFSET
        pageInfo['type'] = Paging.OFFSET
        resp = query.paginate(page, limit)
        results = map(build_cnr_graphql_response,
                      resp.items)  # returns iterator
    else:
        # request 1 more than we need, so we can determine if additional pages are available. returns list.
        resp = query.limit(limit + 1).all()
        if sort_order == Paging.ASC:
            hasNextPage = resp != None and (len(resp) == limit + 1)
            pageInfo['hasNextPage'] = hasNextPage
            pageInfo['hasPreviousPage'] = False
            if hasNextPage:
                resp.pop(-1)  # remove the extra last item
        if sort_order == Paging.DESC:
            resp.reverse()  # We have to reverse the list to get previous pages in the expected order
            pageInfo['hasNextPage'] = False
            hasPreviousPage = resp != None and (len(resp) == limit + 1)
            pageInfo['hasPreviousPage'] = hasPreviousPage
            if hasPreviousPage:
                resp.pop(0)  # remove the extra first item

        results_map = map(response_builder, resp)  # returns iterator
        results = deque(results_map)
        pageInfo['startCursor'] = to_cursor_hash(results[0]['id'])
        pageInfo['endCursor'] = to_cursor_hash(results[-1]['id'])

    if 'total' or 'pages' in pagination_requested:
        # TODO: Consider caching this value per query, and/or making count query in parallel
        count = count_query.count()
        pageInfo['total'] = count
        pageInfo['pages'] = math.ceil(count / limit)

    return {
        'items': results,
        'paging': pageInfo
    }