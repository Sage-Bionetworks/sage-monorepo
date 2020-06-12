from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import CopyNumberResult
from .database_helpers import build_option_args, build_query_args

accepted_option_args = ['feature', 'gene', 'tag']

accepted_query_args = ['id',
                       'direction',
                       'mean_normal',
                       'mean_cnv',
                       'p_value',
                       'log10_p_value',
                       't_stat',
                       'feature_id',
                       'gene_id',
                       'tag_id']


def return_copy_number_result_query(*args):
    option_args = build_option_args(*args, accepted_args=accepted_option_args)
    query_args = build_query_args(
        CopyNumberResult, * args, accepted_args=accepted_query_args)
    query = db.session.query(*query_args)
    if option_args:
        query = db.session.query(CopyNumberResult).options(*option_args)
    return query
