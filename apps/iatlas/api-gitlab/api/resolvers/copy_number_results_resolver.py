from .resolver_helpers import build_cnr_graphql_response, request_copy_number_results


def resolve_copy_number_results(_obj, info, dataSet=None, direction=None, entrez=None, feature=None, maxPValue=None,
                                maxLog10PValue=None, minLog10PValue=None, minMeanCnv=None, minMeanNormal=None,
                                minPValue=None, minTStat=None, tag=None):
    copy_number_results = request_copy_number_results(_obj, info, data_set=dataSet, direction=direction, entrez=entrez,
                                                      feature=feature, max_p_value=maxPValue, max_log10_p_value=maxLog10PValue,
                                                      min_log10_p_value=minLog10PValue, min_mean_cnv=minMeanCnv,
                                                      min_mean_normal=minMeanNormal, min_p_value=minPValue, min_t_stat=minTStat,
                                                      tag=tag)

    return map(build_cnr_graphql_response, copy_number_results)
