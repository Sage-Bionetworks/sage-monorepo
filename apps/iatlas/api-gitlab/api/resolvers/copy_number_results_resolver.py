from .resolver_helpers import get_value, request_copy_number_results


def resolve_copy_number_results(_obj, info, dataSet=None, direction=None, entrez=None, feature=None, maxPValue=None,
                                maxLog10PValue=None, minLog10PValue=None, minMeanCnv=None, minMeanNormal=None,
                                minPValue=None, minTStat=None, tag=None):
    copy_number_results = request_copy_number_results(_obj, info, data_set=dataSet, direction=direction, entrez=entrez,
                                                      feature=feature, max_p_value=maxPValue, max_log10_p_value=maxLog10PValue,
                                                      min_log10_p_value=minLog10PValue, min_mean_cnv=minMeanCnv,
                                                      min_mean_normal=minMeanNormal, min_p_value=minPValue, min_t_stat=minTStat,
                                                      tag=tag)

    return map(build_cnr_graphql_response, copy_number_results)


def build_cnr_graphql_response(copy_number_result):
    return {
        'direction': get_value(copy_number_result, 'direction'),
        'meanNormal': get_value(copy_number_result, 'mean_normal'),
        'meanCnv': get_value(copy_number_result, 'mean_cnv'),
        'pValue': get_value(copy_number_result, 'p_value'),
        'log10PValue': get_value(copy_number_result, 'log10_p_value'),
        'tStat': get_value(copy_number_result, 't_stat'),
        'dataSet': {
            'display': get_value(copy_number_result, 'data_set_display'),
            'name': get_value(copy_number_result, 'data_set_name'),
        },
        'feature': {
            'display': get_value(copy_number_result, 'feature_display'),
            'name': get_value(copy_number_result, 'feature_name'),
            'order': get_value(copy_number_result, 'order'),
            'unit': get_value(copy_number_result, 'unit')
        },
        'gene': {
            'entrez': get_value(copy_number_result, 'entrez'),
            'hgnc': get_value(copy_number_result, 'hgnc'),
            'description': get_value(copy_number_result, 'description'),
            'friendlyName': get_value(copy_number_result, 'friendlyName'),
            'ioLandscapeName': get_value(copy_number_result, 'ioLandscapeName')
        },
        'tag': {
            'characteristics': get_value(copy_number_result, 'characteristics'),
            'color': get_value(copy_number_result, 'color'),
            'display': get_value(copy_number_result, 'tag_display'),
            'name': get_value(copy_number_result, 'tag_name'),
        }
    }
