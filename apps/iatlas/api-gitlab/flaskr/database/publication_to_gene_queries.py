from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import PublicationToGene
from .database_helpers import build_general_query

related_fields = ['genes', 'publications']

core_fields = ['gene_id', 'publication_id']


def return_publication_to_gene_query(*args):
    return build_general_query(
        PublicationToGene, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
