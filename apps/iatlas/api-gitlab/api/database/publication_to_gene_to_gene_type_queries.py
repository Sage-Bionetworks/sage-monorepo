from sqlalchemy import orm
from api import db
from api.db_models import PublicationToGeneToGeneType
from .database_helpers import build_general_query

related_fields = ['gene_types', 'genes', 'publications']

core_fields = ['gene_id', 'gene_type_id', 'publication_id']


def return_publication_to_gene_to_gene_type_query(*args, model=PublicationToGeneToGeneType):
    return build_general_query(
        model, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
