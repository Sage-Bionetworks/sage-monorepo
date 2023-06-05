from sqlalchemy import orm
from api import db
from api.db_models import PublicationToGeneToGeneSet
from .database_helpers import build_general_query

related_fields = ['gene_sets', 'genes', 'publications']

core_fields = ['gene_id', 'gene_set_id', 'publication_id']


def return_publication_to_gene_to_gene_set_query(*args, model=PublicationToGeneToGeneSet):
    return build_general_query(
        model, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
