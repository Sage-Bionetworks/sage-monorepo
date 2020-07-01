from api.db_models import (Mutation, MutationCode, MutationType, Gene, Sample)
from .resolver_helpers import get_value, build_option_args
from api.database import return_mutation_query


valid_mutation_node_mapping = {
    'gene': 'gene',
    'mutationType': 'mutation_type',
    'mutationCode': 'mutation_code',
    'samples': 'samples'
}

def resolve_mutation(_obj, info, id):
    option_args = build_option_args(
        info.field_nodes[0].selection_set,
        valid_mutation_node_mapping
    )
    query = return_mutation_query(*option_args)
    mutation = query.filter_by(id=id).first()
    # samples = get_value(mutation, 'samples')
    # sample_names = []
    # for sample in samples:
    #     sample_names.append(sample.name)
    return {
        "id": get_value(mutation, 'id'),
        "gene": get_value(get_value(mutation, 'gene'), 'hgnc'),
        "mutationCode": get_value(get_value(mutation, 'mutation_code'), 'code'),
        "mutationType": get_value(get_value(mutation, 'mutation_type')),
        "samples": get_value(mutation, 'samples')
    }

def resolve_mutations(_obj, info, id=None):
    option_args = build_option_args(
        info.field_nodes[0].selection_set,
        valid_mutation_node_mapping
    )
    query = return_mutation_query(*option_args)
    if id is not None:
        query = query.filter(Mutation.id.in_(id))
    mutations = query.all()
    # for mutation in mutations:
    #     samples = get_value(mutation, 'samples')
    #     sample_names = []
    #     for sample in samples:
    #         sample_names.append(sample.name)
    #     mutation.samples = sample_names
    return [{
        "id": get_value(mutation, 'id'),
        "gene": get_value(get_value(mutation, 'gene'), 'hgnc'),
        "mutationCode": get_value(get_value(mutation, 'mutation_code'), 'code'),
        "mutationType": get_value(get_value(mutation, 'mutation_type')),
        "samples": get_value(mutation, 'samples')
    } for mutation in mutations]