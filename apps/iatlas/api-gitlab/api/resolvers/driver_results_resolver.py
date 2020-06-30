from .resolver_helpers import get_value, build_option_args, valid_driver_result_node_mapping
from api.database import return_driver_results_query
from api.db_models import DriverResult

def resolve_driver_results(_obj, info, features=[None]):
    option_args = build_option_args(
        info.field_nodes[0].selection_set,
        valid_driver_result_node_mapping
    )
    query = return_driver_results_query(*option_args)
    if features is not None:
        query = query.filter(DriverResult.feature_id.in_(features))
    driver_results = query.all()
    return [{
        "pValue":get_value(driver_result, "p_value"),
        "foldChange": get_value(driver_result, "fold_change"),
        "log10PValue": get_value(driver_result, "log10_p_value"),
        "log10FoldChange": get_value(driver_result, "log10_fold_change"),
        "n_wt": get_value(driver_result, "n_wt"),
        "n_mut": get_value(driver_result, "n_mut"),
        "feature": get_value(driver_result, "feature"),
        "gene": get_value(get_value(driver_result, "gene")),
        "mutationCode": get_value(driver_result, "mutation_code"),
        "tag": get_value(driver_result, "tag")
    } for driver_result in driver_results]

