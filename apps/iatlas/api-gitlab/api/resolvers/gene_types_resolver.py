from .resolver_helpers import get_value, request_gene_types


def resolve_gene_types(_obj, info, name=None):
    gene_types = request_gene_types(_obj, info, name=name)

    print('gene_types: ', gene_types)

    return [{
        'display': get_value(gene_type, 'display'),
        'genes': get_value(gene_type, 'genes', []),
        'name': get_value(gene_type, 'name')
    } for gene_type in gene_types]
