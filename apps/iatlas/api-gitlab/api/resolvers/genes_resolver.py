from .resolver_helpers import build_gene_graphql_response, build_gene_request, get_selection_set, gene_related_sample_request_fields, gene_request_fields, get_requested, simple_gene_type_request_fields, simple_publication_request_fields
from .resolver_helpers.paging_utils import paginate, paging_fields, create_paging


def resolve_genes(
        _obj, info, distinct=False, paging=None, entrez=None, geneFamily=None, geneFunction=None, geneType=None, immuneCheckpoint=None, maxRnaSeqExpr=None, minRnaSeqExpr=None, pathway=None, cohort=None, sample=None, superCategory=None, therapyType=None):

    selection_set = get_selection_set(info=info, child_node='items')

    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=gene_request_fields)

    gene_types_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_gene_type_request_fields, child_node='geneTypes')

    publications_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_publication_request_fields, child_node='publications')

    samples_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=gene_related_sample_request_fields, child_node='samples')

    max_items = 10 if 'samples' in requested else 100_000

    paging = create_paging(paging, max_items)

    query, count_query = build_gene_request(
        requested, distinct=distinct, paging=paging, entrez=entrez, gene_family=geneFamily, gene_function=geneFunction, gene_type=geneType, immune_checkpoint=immuneCheckpoint, max_rna_seq_expr=maxRnaSeqExpr, min_rna_seq_expr=minRnaSeqExpr, pathway=pathway, cohort=cohort, sample=sample, super_category=superCategory, therapy_type=therapyType)

    pagination_requested = get_requested(info, paging_fields, 'paging')
    res = paginate(query, count_query, paging, distinct,
                   build_gene_graphql_response(requested, gene_types_requested, publications_requested, samples_requested, gene_type=geneType, max_rna_seq_expr=maxRnaSeqExpr, min_rna_seq_expr=minRnaSeqExpr, cohort=cohort, sample=sample), pagination_requested)
    return(res)
