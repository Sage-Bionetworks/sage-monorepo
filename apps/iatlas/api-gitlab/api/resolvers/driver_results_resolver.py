from .resolver_helpers import get_value, request_driver_results


def resolve_driver_results(_obj, info, dataSet=None, entrez=None, feature=None, mutationCode=None, tag=None):
    driver_results = request_driver_results(
        _obj, info, data_set=dataSet, entrez=entrez, feature=feature, mutationCode=mutationCode, tag=tag)

    return [{
        "pValue": get_value(driver_result, "p_value"),
        "foldChange": get_value(driver_result, "fold_change"),
        "log10PValue": get_value(driver_result, "log10_p_value"),
        "log10FoldChange": get_value(driver_result, "log10_fold_change"),
        "n_wt": get_value(driver_result, "n_wt"),
        "n_mut": get_value(driver_result, "n_mut"),
        "feature": get_value(driver_result, "feature"),
        "gene": get_value(driver_result, "gene"),
        "mutationCode": get_value(driver_result, "mutation_code"),
        "tag": get_value(driver_result, "tag"),
        "dataSet": get_value(driver_result, "data_set")
    } for driver_result in driver_results]
