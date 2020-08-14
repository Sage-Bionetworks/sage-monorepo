from .resolver_helpers import get_value, request_therapy_types


def resolve_therapy_types(_obj, info, name=None):
    therapy_types = request_therapy_types(
        _obj, info, name=name)

    return [{
        'name': get_value(therapy_type, 'name'),
        'genes': get_value(therapy_type, 'genes', []),
    } for therapy_type in therapy_types]
