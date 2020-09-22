import base64
import math

def to_cursor_hash(val):
    return str(base64.urlsafe_b64encode(str(val).encode("utf-8")), "utf-8")

def from_cursor_hash(encoded):
    return base64.b64decode(encoded)

def get_limit(**kwargs):
    first = kwargs.get('first')
    if first and not math.isnan(first):
        return int(first)
    last = kwargs.get('last')
    if last and not math.isnan(last):
        return int(last)
    return kwargs.get('default', 1)