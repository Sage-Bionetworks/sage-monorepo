from itertools import groupby
from .resolver_helpers import build_gene_graphql_response, gene_related_sample_request_fields, gene_request_fields, get_requested, get_selection_set, get_value, request_genes, return_gene_derived_fields, simple_gene_type_request_fields, simple_publication_request_fields, simple_tag_request_fields


def resolve_genes_by_tag(_obj, info, dataSet=None, entrez=None, feature=None, featureClass=None, geneFamily=None, geneFunction=None, geneType=None, immuneCheckpoint=None, maxRnaSeqExpr=None, minRnaSeqExpr=None, pathway=None, related=None, sample=None, superCategory=None, tag=None, therapyType=None):
    # Get all the fields requested in the graphql request to be used throughout the resolver.
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

    # Get the genes
    gene_results = request_genes(
        requested, tag_requested, data_set=dataSet, entrez=entrez, feature=feature, feature_class=featureClass, gene_family=geneFamily, gene_function=geneFunction, gene_type=geneType, immune_checkpoint=immuneCheckpoint, max_rna_seq_expr=maxRnaSeqExpr, min_rna_seq_expr=minRnaSeqExpr, pathway=pathway, related=related, sample=sample, super_category=superCategory, tag=tag, therapy_type=therapyType, distinct=True)

    # Group the genes by tag. "gene_tag" is the tag to group by (the variable "tag" is already in  use).
    tag_dict = dict()
    for gene_tag, genes_list in groupby(gene_results, key=lambda g: g.tag):
        tag_dict[gene_tag] = tag_dict.get(gene_tag, []) + list(genes_list)

    def build_response(gene_set):
        gene_tag, genes = gene_set

        # Passing the gene_ids can be more performant than a large subquery on genes, but only if there are not a huge amount of gene ids.
        gene_ids = set(gene.id for gene in genes) if len(genes) < 250 else []

        # If there were geneTypes, publications, or samples requested, make separate queries for them, but only if some genes were returned initially.
        pubs_dict, samples_dict, types_dict = return_gene_derived_fields(
            requested, gene_types_requested, publications_requested, samples_requested, data_set=dataSet, entrez=entrez, feature=feature, feature_class=featureClass, gene_family=geneFamily, gene_function=geneFunction, gene_type=geneType, immune_checkpoint=immuneCheckpoint, max_rna_seq_expr=maxRnaSeqExpr, min_rna_seq_expr=minRnaSeqExpr, pathway=pathway, related=related, sample=sample, super_category=superCategory, tag=tag, therapy_type=therapyType, gene_ids=gene_ids) if genes else (dict(), dict(), dict())
        return {
            'characteristics': get_value(genes[0], 'characteristics'),
            'color': get_value(genes[0], 'color'),
            'genes': map(build_gene_graphql_response(pubs_dict, samples_dict, types_dict), genes),
            'longDisplay': get_value(genes[0], 'tag_long_display'),
            'shortDisplay': get_value(genes[0], 'tag_short_display'),
            'tag': gene_tag
        }

    return map(build_response, tag_dict.items())
