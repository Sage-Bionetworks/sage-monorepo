from .resolver_helpers import build_gene_graphql_response, gene_request_fields, get_requested, request_genes, return_gene_derived_fields
from api.telemetry import profile


@profile(__name__)
def resolve_genes(_obj, info, entrez=None, sample=None, geneType=None):
    requested = get_requested(info, gene_request_fields)
    genes = request_genes(requested, entrez=entrez,
                          gene_type=geneType, sample=sample)

    gene_ids = set(gene.id for gene in genes)

    pubs_dict, samples_dict, types_dict = return_gene_derived_fields(info, gene_ids=gene_ids,
                                                                     gene_type=geneType, sample=sample)

    return map(build_gene_graphql_response(types_dict, pubs_dict, samples_dict), genes)
