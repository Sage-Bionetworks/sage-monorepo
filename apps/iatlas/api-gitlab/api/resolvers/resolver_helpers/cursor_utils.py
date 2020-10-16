import base64
import math

MAX_LIMIT = 100000
ASC = 'ASC'
DESC = 'DESC'

def to_cursor_hash(val):
    return str(base64.b64encode(str(val).encode("utf-8")), "utf-8")

def from_cursor_hash(encoded):
    return str(base64.b64decode(str(encoded)), "utf-8")

def get_cursor(before, after):
    if after != None:
        return (from_cursor_hash(after), ASC)
    if before != None:
        return (from_cursor_hash(before), DESC)
    return (None, ASC)

def get_limit(first, last, limit):
    if first and not math.isnan(first):
        return (int(first), ASC)
    if last and not math.isnan(last):
        return (int(last), DESC)
    if limit and not math.isnan(limit):
        return (int(limit), ASC)
    return (MAX_LIMIT, ASC)