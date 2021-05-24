from .resolver_helpers import build_gene_graphql_response, build_gene_request, get_selection_set, gene_related_sample_request_fields, gene_request_fields, get_requested, request_genes, return_gene_derived_fields, simple_gene_type_request_fields, simple_publication_request_fields, simple_data_set_request_fields, simple_tag_request_fields
from .resolver_helpers.paging_utils import paginate, Paging, paging_fields, create_paging
import logging


def resolve_genes(
        _obj, info, distinct=False, paging=None, dataSet=None, entrez=None, geneFamily=None, geneFunction=None, geneType=None, immuneCheckpoint=None, maxRnaSeqExpr=None, minRnaSeqExpr=None, pathway=None, related=None, sample=None, superCategory=None, tag=None, therapyType=None, feature=None, featureClass=None):

    selection_set = get_selection_set(info=info, child_node='items')

    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=gene_request_fields)

    gene_types_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_gene_type_request_fields, child_node='geneTypes')

    publications_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_publication_request_fields, child_node='publications')

    tag_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_tag_request_fields, child_node='tag')

    max_items = 10 if 'samples' in requested else 100_000

    paging = create_paging(paging, max_items)

    query, count_query = build_gene_request(
        requested, tag_requested, distinct=distinct, paging=paging, data_set=dataSet, entrez=entrez, gene_family=geneFamily, gene_function=geneFunction, gene_type=geneType, immune_checkpoint=immuneCheckpoint, max_rna_seq_expr=maxRnaSeqExpr, min_rna_seq_expr=minRnaSeqExpr, pathway=pathway, related=related, sample=sample, super_category=superCategory, tag=tag, therapy_type=therapyType)

    pubs_dict, types_dict = return_gene_derived_fields(
        requested, gene_types_requested, publications_requested, distinct, paging, data_set=dataSet, entrez=entrez, gene_family=geneFamily, gene_function=geneFunction, gene_type=geneType, immune_checkpoint=immuneCheckpoint, max_rna_seq_expr=maxRnaSeqExpr, min_rna_seq_expr=minRnaSeqExpr, pathway=pathway, related=related, sample=sample, super_category=superCategory, tag=tag, therapy_type=therapyType)

    pagination_requested = get_requested(info, paging_fields, 'paging')
    res = paginate(query, count_query, paging, distinct,
                   build_gene_graphql_response(pubs_dict, types_dict), pagination_requested)
    return(res)
