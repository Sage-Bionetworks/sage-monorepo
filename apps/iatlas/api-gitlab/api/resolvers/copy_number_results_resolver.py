from .resolver_helpers import get_value, request_copy_number_results


def resolve_copy_number_results(_obj, info, dataSet=None, direction=None, entrez=None, feature=None, maxPValue=None,
                                maxLog10PValue=None, minLog10PValue=None, minMeanCnv=None, minMeanNormal=None,
                                minPValue=None, minTStat=None, tag=None):
    copy_number_results = request_copy_number_results(_obj, info, data_set=dataSet, direction=direction, entrez=entrez,
                                                      feature=feature, max_p_value=maxPValue, max_log10_p_value=maxLog10PValue,
                                                      min_log10_p_value=minLog10PValue, min_mean_cnv=minMeanCnv,
                                                      min_mean_normal=minMeanNormal, min_p_value=minPValue, min_t_stat=minTStat,
                                                      tag=tag)

    return [{
        'direction': get_value(copy_number_result, 'direction'),
        'meanNormal': get_value(copy_number_result, 'mean_normal'),
        'meanCnv': get_value(copy_number_result, 'mean_cnv'),
        'pValue': get_value(copy_number_result, 'p_value'),
        'log10PValue': get_value(copy_number_result, 'log10_p_value'),
        'tStat': get_value(copy_number_result, 't_stat'),
        'dataSet': get_value(copy_number_result, 'data_set'),
        'feature': get_value(copy_number_result, 'feature'),
        'gene': get_value(copy_number_result, 'gene'),
        'mutationCode': get_value(copy_number_result, 'mutation_code'),
        'tag': get_value(copy_number_result, 'tag')
    } for copy_number_result in copy_number_results]
