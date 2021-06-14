from sqlalchemy import orm
from api import db
from api.db_models import CohortToGene
from .database_helpers import build_general_query

accepted_cohort_to_gene_option_args = ['cohort', 'gene']

accepted_cohort_to_gene_query_args = [
    'cohort_id', 'gene_id']


def return_cohort_to_gene_query(*args):
    return build_general_query(
        CohortToGene, args=args,
        accepted_option_args=accepted_cohort_to_gene_option_args,
        accepted_query_args=accepted_cohort_to_gene_query_args)
