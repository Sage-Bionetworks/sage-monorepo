from flaskr.db_models import (Mutation, MutationCode, MutationType, Gene, Sample)
from .resolver_helpers import get_child_value, get_value, build_option_args
from flaskr.database import return_mutation_query


valid_mutation_node_mapping = {
    'gene': 'gene',
    'mutationType': 'mutation_type',
    'mutationCode': 'mutation_code',
    'sample': 'sample'
}

def resolve_mutation(_obj, info, id):
    option_args = build_option_args(
        info.field_nodes[0].selection_set,
        valid_mutation_node_mapping
    )
    query = return_mutation_query(*option_args)
    mutation = query.filter_by(id=id).first()

    return {
        "id": get_value(mutation, 'id'),
        "gene": get_child_value(get_value(mutation, 'gene_id')),
        "mutationCode": get_child_value(mutation, 'mutation_code_id'),
        "mutationType": get_child_value(mutation, 'mutation_type_id'),
        "samples": None
    }