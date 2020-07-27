from .resolver_helpers import build_gene_graphql_response, request_genes, return_relations


def resolve_genes(_obj, info, entrez=None, sample=None, geneType=None):
    genes = request_genes(_obj, info, entrez=entrez,
                          gene_type=geneType, sample=sample)

    gene_dict = {gene.id: gene for gene in genes}

    pubs_dict, samples_dict, types_dict = return_relations(info, gene_dict=gene_dict,
                                                           gene_type=geneType, sample=sample)

    return map(build_gene_graphql_response(types_dict, pubs_dict, samples_dict), genes)
