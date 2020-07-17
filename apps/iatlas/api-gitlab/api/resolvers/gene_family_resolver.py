from .resolver_helpers import get_value, request_gene_family


def resolve_gene_family(_obj, info, name=None):
    gene_families = request_gene_family(
        _obj, info, name=name)

    return [{
        "name": get_value(gene_family, "name"),
    } for gene_family in gene_families]
