from .resolver_helpers import get_value, request_gene_families


def resolve_gene_family(_obj, info, name=None):
    gene_families = request_gene_families(
        _obj, info, name=name)

    return [{
        "name": get_value(gene_family, "name"),
        "genes": get_value(gene_family, 'genes', []),
    } for gene_family in gene_families]
