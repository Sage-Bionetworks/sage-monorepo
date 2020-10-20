from itertools import groupby
from .resolver_helpers import build_gene_graphql_response, gene_related_sample_request_fields, gene_request_fields, get_requested, get_selection_set, get_value, request_genes, return_gene_derived_fields, simple_gene_type_request_fields, simple_publication_request_fields, simple_tag_request_fields


def resolve_genes_by_tag(_obj, info, dataSet=None, entrez=None, feature=None, featureClass=None, geneFamily=None, geneFunction=None, geneType=None, immuneCheckpoint=None, maxRnaSeqExpr=None, minRnaSeqExpr=None, pathway=None, related=None, sample=None, superCategory=None, tag=None, therapyType=None):
    gene_selection_set = get_selection_set(info=info, child_node='genes')
    requested = get_requested(
        selection_set=gene_selection_set, requested_field_mapping=gene_request_fields)
    gene_types_requested = get_requested(
        selection_set=gene_selection_set, requested_field_mapping=simple_gene_type_request_fields, child_node='geneTypes')
    publications_requested = get_requested(
        selection_set=gene_selection_set, requested_field_mapping=simple_publication_request_fields, child_node='publications')
    samples_requested = get_requested(
        selection_set=gene_selection_set, requested_field_mapping=gene_related_sample_request_fields, child_node='samples')
    tag_requested = get_requested(info, simple_tag_request_fields)

    gene_results = request_genes(
        requested, tag_requested, data_set=dataSet, entrez=entrez, feature=feature, feature_class=featureClass, gene_family=geneFamily, gene_function=geneFunction, gene_type=geneType, immune_checkpoint=immuneCheckpoint, max_rna_seq_expr=maxRnaSeqExpr, min_rna_seq_expr=minRnaSeqExpr, pathway=pathway, related=related, sample=sample, super_category=superCategory, tag=tag, therapy_type=therapyType, distinct=True)

    tag_dict = dict()
    for gene_tag, genes_list in groupby(gene_results, key=lambda g: g.tag):
        tag_dict[gene_tag] = tag_dict.get(gene_tag, []) + list(genes_list)

    pubs_dict, samples_dict, types_dict = return_gene_derived_fields(
        requested, gene_types_requested, publications_requested, samples_requested, data_set=dataSet, entrez=entrez, feature=feature, feature_class=featureClass, gene_family=geneFamily, gene_function=geneFunction, gene_type=geneType, immune_checkpoint=immuneCheckpoint, max_rna_seq_expr=maxRnaSeqExpr, min_rna_seq_expr=minRnaSeqExpr, pathway=pathway, related=related, sample=sample, super_category=superCategory, tag=tag, therapy_type=therapyType) if gene_results else (dict(), dict(), dict())

    def build_response(gene_set):
        gene_tag, genes = gene_set
        return {
            'characteristics': get_value(genes[0], 'characteristics'),
            'color': get_value(genes[0], 'color'),
            'genes': map(build_gene_graphql_response(types_dict, pubs_dict, samples_dict), genes),
            'longDisplay': get_value(genes[0], 'tag_long_display'),
            'shortDisplay': get_value(genes[0], 'tag_short_display'),
            'tag': gene_tag
        }

    return map(build_response, tag_dict.items())
