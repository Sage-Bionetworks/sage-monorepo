from .resolver_helpers import get_value, request_method_tags


def resolve_method_tags(_obj, info, name=None):
    method_tags = request_method_tags(
        _obj, info, name=name)

    return [{
        "name": get_value(method_tag, "name"),
        "features": get_value(method_tag, 'features', []),
    } for method_tag in method_tags]
