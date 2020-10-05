from .resolver_helpers import get_value, request_driver_results


def resolve_driver_results(_obj, info, dataSet=None, entrez=None, feature=None, maxPValue=None,
                           maxLog10PValue=None, minFoldChange=None, minLog10FoldChange=None,
                           minLog10PValue=None, minPValue=None, minNumMutants=None, minNumWildTypes=None,
                           mutationCode=None, page=1, tag=None):

    driver_results = request_driver_results(_obj, info, data_set=dataSet, entrez=entrez, feature=feature,
                                            max_p_value=maxPValue, max_log10_p_value=maxLog10PValue, min_fold_change=minFoldChange, min_log10_fold_change=minLog10FoldChange, min_log10_p_value=minLog10PValue, min_p_value=minPValue,
                                            min_n_mut=minNumMutants, min_n_wt=minNumWildTypes, mutation_code=mutationCode, tag=tag).paginate(page, 10000, False)
    return {
        'items': map(build_dr_graphql_response, driver_results.items),
        'page': driver_results.page,
        'pages': driver_results.pages,
        'total': driver_results.total
    }


def build_dr_graphql_response(driver_result):
    return {
        'pValue': get_value(driver_result, 'p_value'),
        'foldChange': get_value(driver_result, 'fold_change'),
        'log10PValue': get_value(driver_result, 'log10_p_value'),
        'log10FoldChange': get_value(driver_result, 'log10_fold_change'),
        'numWildTypes': get_value(driver_result, 'n_wt'),
        'numMutants': get_value(driver_result, 'n_mut'),
        'dataSet': {
            'display': get_value(driver_result, 'data_set_display'),
            'name': get_value(driver_result, 'data_set_name'),
        },
        'feature': {
            'display': get_value(driver_result, 'feature_display'),
            'name': get_value(driver_result, 'feature_name'),
            'order': get_value(driver_result, 'order'),
            'unit': get_value(driver_result, 'unit')
        },
        'gene': {
            'entrez': get_value(driver_result, 'entrez'),
            'hgnc': get_value(driver_result, 'hgnc'),
            'description': get_value(driver_result, 'description'),
            'friendlyName': get_value(driver_result, 'friendlyName'),
            'ioLandscapeName': get_value(driver_result, 'ioLandscapeName')
        },
        'mutationCode': get_value(driver_result, 'code'),
        'tag': {
            'characteristics': get_value(driver_result, 'characteristics'),
            'color': get_value(driver_result, 'color'),
            'longDisplay': get_value(driver_result, 'tag_long_display'),
            'name': get_value(driver_result, 'tag_name'),
            'shortDisplay': get_value(driver_result, 'tag_short_display'),
        }
    }
