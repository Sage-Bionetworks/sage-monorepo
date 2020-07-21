from .resolver_helpers import get_value, request_gene_functions


def resolve_gene_function(_obj, info, name=None):
    gene_functions = request_gene_functions(
        _obj, info, name=name)

    return [{
        "name": get_value(gene_function, "name"),
        "genes": get_value(gene_function, 'genes', []),
    } for gene_function in gene_functions]
