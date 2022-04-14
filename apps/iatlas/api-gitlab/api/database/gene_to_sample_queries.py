from sqlalchemy import orm
from api import db
from api.db_models import GeneToSample
from .database_helpers import build_general_query

related_fields = ['gene', 'sample']

core_fields = ['gene_id', 'sample_id', 'rna_seq_expr', 'nanostring_expr']


def return_gene_to_sample_query(*args):
    return build_general_query(
        GeneToSample, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
