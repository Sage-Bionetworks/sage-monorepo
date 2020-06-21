from flaskr.database import return_gene_query
from .resolver_helpers import build_option_args, get_value, valid_gene_node_mapping


def resolve_gene(_obj, info, entrez):
    option_args = build_option_args(
        info.field_nodes[0].selection_set, valid_gene_node_mapping)
    query = return_gene_query(*option_args)
    gene = query.filter_by(entrez=entrez).first()

    return {
        "id": get_value(gene, 'id'),
        "entrez": get_value(gene, 'entrez'),
        "hgnc": get_value(gene, 'hgnc'),
        "description": get_value(gene, 'description'),
        "friendlyName": get_value(gene, 'friendly_name'),
        "ioLandscapeName": get_value(gene, 'io_landscape_name'),
        "geneFamily": get_value(get_value(gene, 'gene_family')),
        "geneFunction": get_value(get_value(gene, 'gene_function')),
        "immuneCheckpoint": get_value(get_value(gene, 'immune_checkpoint')),
        "nodeType": get_value(get_value(gene, 'node_type')),
        "pathway": get_value(get_value(gene, 'pathway')),
        "superCategory": get_value(get_value(gene, 'super_category')),
        "therapyType": get_value(get_value(gene, 'therapy_type'))
    }
