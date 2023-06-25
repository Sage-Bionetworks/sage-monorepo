from .resolver_helpers import get_value, request_gene_sets
from .resolver_helpers.gene import build_gene_graphql_response


def resolve_gene_types(_obj, info, name=None):
    gene_types = request_gene_sets(_obj, info, name=name)

    return [{
        'display': get_value(gene_type, 'display'),
        'genes': map(build_gene_graphql_response(prefix=""), get_value(gene_type, 'genes', [])),
        'name': get_value(gene_type, 'name')
    } for gene_type in gene_types]
