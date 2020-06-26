from sqlalchemy import orm
from api import db
from api.db_models import GeneToType
from .database_helpers import build_general_query

related_fields = ['genes', 'types']

core_fields = ['gene_id', 'type_id']


def return_gene_to_type_query(*args):
    return build_general_query(
        GeneToType, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
