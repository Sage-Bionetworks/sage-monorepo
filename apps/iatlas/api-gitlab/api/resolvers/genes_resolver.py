from .resolver_helpers import build_gene_graphql_response, gene_request_fields, get_requested, request_genes, return_gene_derived_fields


def resolve_genes(
        _obj, info, dataSet=None, entrez=None, geneType=None, maxRnaSeqExp=None, minRnaSeqExp=None, related=None, sample=None, tag=None):
    requested = get_requested(info, gene_request_fields)
    genes = request_genes(
        requested, data_set=dataSet, entrez=entrez, gene_type=geneType, max_rna_seq_expr=maxRnaSeqExp, min_rna_seq_expr=minRnaSeqExp, related=related, sample=sample, tag=tag)

    gene_ids = set(gene.id for gene in genes)

    pubs_dict, samples_dict, types_dict = return_gene_derived_fields(
        info, gene_ids=gene_ids, gene_type=geneType, sample=sample)

    return map(build_gene_graphql_response(types_dict, pubs_dict, samples_dict), genes)
