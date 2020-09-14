from .resolver_helpers import (build_cnr_graphql_response, build_copy_number_result_request, cnr_request_fields, data_set_request_fields,
                               feature_request_fields, gene_request_fields, get_requested, get_selection_set, simple_tag_request_fields)


def resolve_copy_number_results(_obj, info, dataSet=None, direction=None, entrez=None, feature=None, maxPValue=None,
                                maxLog10PValue=None, minLog10PValue=None, minMeanCnv=None, minMeanNormal=None,
                                minPValue=None, minTStat=None, page=1, tag=None):
    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, True, 'items')
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=cnr_request_fields)

    data_set_selection_set = get_selection_set(
        selection_set, 'dataSet' in requested, 'dataSet')
    data_set_requested = get_requested(
        selection_set=data_set_selection_set, requested_field_mapping=data_set_request_fields)

    feature_selection_set = get_selection_set(
        selection_set, 'feature' in requested, 'feature')
    feature_requested = get_requested(
        selection_set=feature_selection_set, requested_field_mapping=feature_request_fields)

    gene_selection_set = get_selection_set(
        selection_set, 'gene' in requested, 'gene')
    gene_requested = get_requested(
        selection_set=gene_selection_set, requested_field_mapping=gene_request_fields)

    tag_selection_set = get_selection_set(
        selection_set, 'tag' in requested, 'tag')
    tag_requested = get_requested(
        selection_set=tag_selection_set, requested_field_mapping=simple_tag_request_fields)

    cnr_results = build_copy_number_result_request(requested, data_set_requested, feature_requested, gene_requested, tag_requested,
                                                   data_set=dataSet, direction=direction, entrez=entrez, feature=feature,
                                                   max_p_value=maxPValue, max_log10_p_value=maxLog10PValue,
                                                   min_log10_p_value=minLog10PValue, min_mean_cnv=minMeanCnv,
                                                   min_mean_normal=minMeanNormal, min_p_value=minPValue, min_t_stat=minTStat,
                                                   tag=tag).paginate(page, 100000, False)

    return {
        'items': map(build_cnr_graphql_response, cnr_results.items),
        'page': cnr_results.page,
        'pages': cnr_results.pages,
        'total': cnr_results.total
    }
