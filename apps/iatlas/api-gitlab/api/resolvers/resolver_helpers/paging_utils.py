import base64
import math

class Paging:
    OFFSET = 'OFFSET'
    CURSOR = 'CURSOR'
    MAX_LIMIT = 100000
    ASC = 'ASC'
    DESC = 'DESC'

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
    print('max', Paging.MAX_LIMIT)
    return min(Paging.MAX_LIMIT, int(n))

def get_limit(first, last, limit):
    if first and not math.isnan(first):
        return (parse_limit(first), Paging.ASC)
    if last and not math.isnan(last):
        return (parse_limit(last), Paging.DESC)
    if limit and not math.isnan(limit):
        return (parse_limit(limit), Paging.ASC)
    return (Paging.MAX_LIMIT, Paging.ASC)