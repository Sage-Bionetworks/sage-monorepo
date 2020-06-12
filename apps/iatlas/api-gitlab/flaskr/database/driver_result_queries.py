from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import DriverResult
from .database_helpers import build_option_args, build_query_args

accepted_option_args = ['feature', 'gene', 'mutation_code', 'tag']

accepted_query_args = ['id',
                       'p_value',
                       'fold_change',
                       'log10_p_value',
                       'log10_fold_change',
                       'n_wt',
                       'n_mut',
                       'feature_id',
                       'gene_id',
                       'mutation_code_id',
                       'tag_id']


def return_driver_result_query(*args):
    option_args = build_option_args(*args, accepted_args=accepted_option_args)
    query_args = build_query_args(
        DriverResult, *args, accepted_args=accepted_query_args)
    query = db.session.query(*query_args)
    if option_args:
        query = db.session.query(DriverResult).options(*option_args)
    return query
