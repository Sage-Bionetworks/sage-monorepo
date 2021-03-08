from .resolver_helpers import build_coloc_graphql_response, build_colocalization_request, colocalization_request_fields, get_requested, get_selection_set, simple_data_set_request_fields, simple_feature_request_fields, simple_gene_request_fields, snp_request_fields

from .resolver_helpers.paging_utils import paginate, Paging, paging_fields
import logging


def resolve_colocalizations(
        _obj, info, distinct=False, paging=None, dataSet=None, colocDataSet=None, feature=None, entrez=None, snp=None, qtlType=None, eCaviarPP=None, plotType=None):
    # The selection is nested under the 'items' node.
    selection_set = get_selection_set(info=info, child_node='items')
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=colocalization_request_fields)

    data_set_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_data_set_request_fields, child_node='dataSet')

    coloc_data_set_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_data_set_request_fields, child_node='dataSet')

    feature_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_feature_request_fields, child_node='feature')

    gene_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_gene_request_fields, child_node='gene')

    snp_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=snp_request_fields, child_node='snp')

    if distinct == False:
        # Add the id as a cursor if not selecting distinct
        requested.add('id')

    paging = paging if paging else Paging.DEFAULT

    query, count_query = build_colocalization_request(
        requested, data_set_requested, coloc_data_set_requested, feature_requested, gene_requested, snp_requested, distinct=distinct, paging=paging, data_set=dataSet, coloc_data_set=colocDataSet, feature=feature, entrez=entrez, snp=snp, qtl_type=qtlType, ecaviar_pp=eCaviarPP, plot_type=plotType)

    pagination_requested = get_requested(info, paging_fields, 'paging')
    res = paginate(query, count_query, paging, distinct,
                   build_coloc_graphql_response, pagination_requested)

    return(res)
