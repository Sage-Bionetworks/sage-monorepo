from sqlalchemy import orm
from api import db
from api.db_models import DriverResult
from .database_helpers import build_general_query

related_fields = ['features', 'genes', 'mutations', 'tags']

core_fields = ['p_value', 'fold_change', 'log10_p_value', 'log10_fold_change', 'n_wt', 'n_mut', 'feature_id', 'gene_id', 'mutation_code_id', 'tag_id']


def return_driver_results_query(*args):
    return build_general_query(
        DriverResult, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
