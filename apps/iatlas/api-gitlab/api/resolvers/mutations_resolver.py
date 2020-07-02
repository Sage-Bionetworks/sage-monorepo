from .resolver_helpers import get_value, request_mutations


def resolve_mutations(_obj, info, entrez=None, mutationCode=None, mutationType=None):
    mutations = request_mutations(
        _obj, info, entrez=entrez, mutation_code=mutationCode, mutation_type=mutationType)

    return [{
        'id': get_value(mutation, 'id'),
        'gene': get_value(mutation, 'gene'),
        'mutationCode': get_value(get_value(mutation, 'mutation_code'), 'code'),
        'mutationType': get_value(mutation, 'mutation_type'),
        'samples': get_value(mutation, 'samples')
    } for mutation in mutations]
