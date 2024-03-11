from api import db
from api.db_models import Gene, GeneSet
from .database_helpers import general_core_fields, build_general_query

gene_related_fields = ['copy_number_results',
                       'driver_results',
                       'gene_sample_assoc',
                       'gene_set_assoc',
                       'gene_sets',
                       'publications',
                       'publication_gene_gene_set_assoc',
                       'samples']

gene_core_fields = ['id',
                    'entrez_id',
                    'hgnc_id',
                    'description',
                    'friendly_name',
                    'io_landscape_name',
                    'gene_family',
                    'gene_function',
                    'immune_checkpoint',
                    'gene_pathway',
                    'super_category',
                    'therapy_type']

gene_set_related_fields = [
    'genes', 'gene_set_assoc', 'publications', 'publication_gene_gene_set_assoc']

sub_related_fields = ['genes']


def return_gene_query(*args, model=Gene):
    return build_general_query(
        model, args=args,
        accepted_option_args=gene_related_fields,
        accepted_query_args=gene_core_fields)

def return_gene_set_query(*args):
    return build_general_query(
        GeneSet, args=args,
        accepted_option_args=gene_set_related_fields,
        accepted_query_args=general_core_fields)
