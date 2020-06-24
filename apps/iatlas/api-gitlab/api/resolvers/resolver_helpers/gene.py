from sqlalchemy import orm
from api import db
from api.database import return_gene_query
from api.db_models import Gene
from . import build_option_args

valid_gene_node_mapping = {'entrez': 'entrez',
                           'hgnc': 'hgnc',
                           'description': 'description',
                           'friendlyName': 'friendly_name',
                           'ioLandscapeName': 'io_landscape_name',
                           'geneFamily': 'gene_family',
                           'geneFunction': 'gene_function',
                           'geneTypes': 'gene_types',
                           'immuneCheckpoint': 'immune_checkpoint',
                           'pathway': 'pathway',
                           'publications': 'publications',
                           'superCategory': 'super_category',
                           'therapyType': 'therapy_type'}


def build_gene_request(_obj, info, dataSet=None, related=None, geneType=None, entrez=None, byTag=False):
    """
    Builds a SQL request and returns values from the DB.

    The query may be larger or smaller depending on the requested fields.
    An example of the full query in SQL:


    """
    sess = db.session

    gene_1 = orm.aliased(Gene, name='g')

    option_args = build_option_args(
        info.field_nodes[0].selection_set, valid_gene_node_mapping)
    query = return_gene_query(*option_args, model=gene_1)

    if entrez:
        query = query.filter(gene_1.entrez.in_(entrez))

    return query


def request_gene(_obj, info, entrez=None):
    if entrez:
        entrez = [entrez]
        query = build_gene_request(_obj, info, entrez=entrez, byTag=False)
        return query.first()
    return None


def request_genes(_obj, info, entrez=None):
    query = build_gene_request(_obj, info, entrez=entrez, byTag=False)
    return query.all()
