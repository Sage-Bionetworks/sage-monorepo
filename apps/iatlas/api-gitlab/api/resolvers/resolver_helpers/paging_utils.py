import base64
import math
import uuid
from collections import deque

from api.database.database_helpers import temp_table, execute_sql

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

def get_pagination_queries(query, paging, distinct, cursor_field=None):
    count_query = query
    if paging.get('type', Paging.CURSOR) == Paging.OFFSET or distinct == True:
        if distinct == True:
            return query.distinct(), count_query.distinct()
        return query, count_query

    # Handle cursor and sort order
    cursor, sort_order = get_cursor(paging.get('before'), paging.get('after'))
    order_by = cursor_field
    if sort_order == Paging.ASC:
        query = query.order_by(order_by)
    else:
        query = query.order_by(order_by.desc())

    if cursor:
        if sort_order == Paging.ASC:
            query = query.filter(cursor_field > cursor)
        else:
            query = query.filter(cursor_field < cursor)
    # end handle cursor

    return query, count_query

def create_temp_table(query, paging, distinct):
    paging_type = paging.get('type', Paging.CURSOR)
    page = None
    before = None
    after = None
    first = paging.get('first')
    last = paging.get('last')
    limit = paging.get('limit')
    limit, sort_order = get_limit(first, last, limit)

    table_name = f'_temp_{uuid.uuid4()}'.replace('-', '')

    if paging_type == Paging.OFFSET or distinct == True:
        page = paging.get('page', 1)
        # run the offset query
        query = query.limit(limit)
        query = query.offset((page-1) * limit)
    else:
        # request 1 more than we need, so we can determine if additional pages are available. returns list.
        # run the cursor query
        # Store query results in temp table
        query = query.limit(limit + 1)

    conn = temp_table(table_name, query)
    # items = query.all() # slower than querying the new temp table because we have to recreate filters and joins
    item_query = f'SELECT * FROM {table_name}' # instead grab everything from the new temp table
    items = execute_sql(item_query, conn=conn)
    return items, table_name, conn

def fetch_page(query, paging, distinct):
    paging_type = paging.get('type', Paging.CURSOR)
    page = paging.get('page', 1)
    first = paging.get('first')
    last = paging.get('last')
    limit = paging.get('limit')
    limit, order = get_limit(first, last, limit)
    if paging_type == Paging.OFFSET or distinct == True:
        return query.paginate(page, limit).items
    return query.limit(limit + 1).all()

def process_page(items, count_query, paging, distinct, response_builder):
    paging_type = paging.get('type', Paging.CURSOR)
    page = None
    first = paging.get('first')
    last = paging.get('last')
    limit = paging.get('limit')
    limit, order = get_limit(first, last, limit)

    pageInfo = {
        'type': paging_type,
        'page': page,
        'pages': None,
        'limit': limit,
        'returned': None,
        'total': None
    }

    if paging_type == Paging.OFFSET or distinct == True:
        # if distinct is True, paging type must be OFFSET
        pageInfo['type'] = Paging.OFFSET
        pageInfo['page'] = paging.get('page', 1)
        results = map(response_builder, items)
    else:
        returned = len(items)
        if order == Paging.ASC:
            hasNextPage = items != None and returned == limit + 1
            pageInfo['hasNextPage'] = hasNextPage
            pageInfo['hasPreviousPage'] = False
            if hasNextPage:
                items.pop(-1)  # remove the extra last item
        if order == Paging.DESC:
            items.reverse()  # We have to reverse the list to get previous pages in the expected order
            pageInfo['hasNextPage'] = False
            hasPreviousPage = items != None and returned == limit + 1
            pageInfo['hasPreviousPage'] = hasPreviousPage
            if hasPreviousPage:
                items.pop(0)  # remove the extra first item

        results_map = map(response_builder, items)
        results = deque(results_map)
        pageInfo['startCursor'] = to_cursor_hash(results[0]['id'])
        pageInfo['endCursor'] = to_cursor_hash(results[-1]['id'])

    if 'total' or 'pages' in paging:
        # TODO: Consider caching this value per query, and/or making count query in parallel
        count = count_query.count()
        pageInfo['total'] = count
        pageInfo['pages'] = math.ceil(count / limit)

    pageInfo['returned'] = len(items)
    return {
        'items': results,
        'paging': pageInfo
    }

def paginate(query, count_query, paging, distinct, response_builder):
    items = fetch_page(query, paging, distinct)
    return process_page(items, count_query, paging, distinct, response_builder)
