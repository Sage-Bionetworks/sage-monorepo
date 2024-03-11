from sqlalchemy import orm
from api import db
from .database_helpers import general_core_fields, build_general_query
from api.db_models import Snp

snp_core_fields = [
    'id', 'name', 'rsid', 'chr', 'bp']


def return_snp_query(*args):
    return build_general_query(
        Snp, args=args,
        accepted_query_args=snp_core_fields)
