from flaskr.db_models import Gene
from flaskr.database import return_gene_query
from .resolver_helpers import build_option_args, get_value, valid_gene_node_mapping


def resolve_genes(_obj, info, entrez=None):
    option_args = build_option_args(
        info.field_nodes[0].selection_set, valid_gene_node_mapping)
    query = return_gene_query(*option_args)
    if entrez:
        query = query.filter(Gene.entrez.in_(entrez))
    genes = query.all()

    return [
        {
            "id": gene.id,
            "entrez": gene.entrez,
            "hgnc": gene.hgnc,
            "description": gene.description,
            "friendlyName": gene.friendly_name,
            "ioLandscapeName": gene.io_landscape_name,
            "geneFamily": get_value(gene.gene_family),
            "geneFunction": get_value(gene.gene_function),
            "immuneCheckpoint": get_value(gene.immune_checkpoint),
            "nodeType": get_value(gene.node_type),
            "pathway": get_value(gene.pathway),
            "superCategory": get_value(gene.super_category),
            "therapyType": get_value(gene.therapy_type)
        } for gene in genes]
