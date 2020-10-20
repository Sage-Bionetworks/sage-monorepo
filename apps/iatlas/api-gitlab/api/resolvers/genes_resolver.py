from .resolver_helpers import build_gene_graphql_response, gene_related_sample_request_fields, gene_request_fields, get_requested, request_genes, return_gene_derived_fields, simple_gene_type_request_fields, simple_publication_request_fields


def resolve_genes(
        _obj, info, dataSet=None, entrez=None, feature=None, featureClass=None, geneFamily=None, geneFunction=None, geneType=None, immuneCheckpoint=None, maxRnaSeqExpr=None, minRnaSeqExpr=None, pathway=None, related=None, sample=None, superCategory=None, tag=None, therapyType=None):
    requested = get_requested(info, gene_request_fields)
    gene_types_requested = get_requested(
        info, simple_gene_type_request_fields, 'geneTypes')
    publications_requested = get_requested(
        info, simple_publication_request_fields, 'publications')
    samples_requested = get_requested(
        info, gene_related_sample_request_fields, 'samples')

    genes = request_genes(
        requested, set(), data_set=dataSet, entrez=entrez, feature=feature, feature_class=featureClass, gene_family=geneFamily, gene_function=geneFunction, gene_type=geneType, immune_checkpoint=immuneCheckpoint, max_rna_seq_expr=maxRnaSeqExpr, min_rna_seq_expr=minRnaSeqExpr, pathway=pathway, related=related, sample=sample, super_category=superCategory, tag=tag, therapy_type=therapyType)

    pubs_dict, samples_dict, types_dict = return_gene_derived_fields(
        requested, gene_types_requested, publications_requested, samples_requested, data_set=dataSet, entrez=entrez, feature=feature, feature_class=featureClass, gene_family=geneFamily, gene_function=geneFunction, gene_type=geneType, immune_checkpoint=immuneCheckpoint, max_rna_seq_expr=maxRnaSeqExpr, min_rna_seq_expr=minRnaSeqExpr, pathway=pathway, related=related, sample=sample, super_category=superCategory, tag=tag, therapy_type=therapyType) if genes else (dict(), dict(), dict())

    return map(build_gene_graphql_response(types_dict, pubs_dict, samples_dict), genes)
