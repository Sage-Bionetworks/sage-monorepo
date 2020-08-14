from .resolver_helpers import get_value, request_super_categories


def resolve_super_categories(_obj, info, name=None):
    super_categories = request_super_categories(
        _obj, info, name=name)

    return [{
        'name': get_value(super_category, 'name'),
        'genes': get_value(super_category, 'genes', []),
    } for super_category in super_categories]
