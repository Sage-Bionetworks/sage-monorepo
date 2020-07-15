from api import db
from api.db_models import Publication
from .database_helpers import build_general_query

publication_related_fields = [
    'genes', 'gene_types', 'publication_gene_gene_type_assoc']

publication_core_fields = ['id', 'first_author_last_name',
                           'journal', 'pubmed_id', 'title', 'year']


def return_publication_query(*args):
    return build_general_query(
        Publication, args=args,
        accepted_option_args=publication_related_fields,
        accepted_query_args=publication_core_fields)
