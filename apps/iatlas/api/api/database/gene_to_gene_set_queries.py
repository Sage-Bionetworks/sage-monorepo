from sqlalchemy import orm
from api import db
from api.db_models import GeneToGeneSet
from .database_helpers import build_general_query

related_fields = ["genes", "gene_sets"]

core_fields = ["gene_id", "gene_set_id"]


def return_gene_to_gene_set_query(*args):
    return build_general_query(
        GeneToGeneSet,
        args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields,
    )
