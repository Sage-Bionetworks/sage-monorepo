from .resolver_helpers import get_value, request_immune_checkpoints


def resolve_immune_checkpoints(_obj, info, name=None):
    immune_checkpoints = request_immune_checkpoints(
        _obj, info, name=name)

    return [{
        "name": get_value(immune_checkpoint, "name"),
        "genes": get_value(immune_checkpoint, 'genes', []),
    } for immune_checkpoint in immune_checkpoints]
