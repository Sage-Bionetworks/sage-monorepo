from .resolver_helpers import get_value, request_pathways


def resolve_pathways(_obj, info, name=None):
    pathways = request_pathways(
        _obj, info, name=name)

    return [{
        "name": get_value(pathway, "name"),
        "genes": get_value(pathway, 'genes', []),
    } for pathway in pathways]
