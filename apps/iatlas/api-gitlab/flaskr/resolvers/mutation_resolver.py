from sqlalchemy import orm
from .resolver_helpers import get_field_value, build_option_args
from flaskr.database import return_mutation_query


def resolve_mutation(_obj, info, id):
    option_args = build_option_args(
        info.field_nodes[0].selection_set,
        {'gene': 'gene',
         'mutationCode': 'mutation_code',
         'mutationType': 'mutation_type',
         'samples': 'samples'
        }
    )
    query = return_mutation_query(*option_args)
    mutation = query.filter_by(id=id).first()

    return {
        "id": mutation.id,
        "gene": get_field_value(mutation.gene, "entrez"),
        "mutationCode": get_field_value(mutation.mutation_code, "code"),
        "mutationType": get_field_value(mutation.mutation_type),
        "samples": None
    }