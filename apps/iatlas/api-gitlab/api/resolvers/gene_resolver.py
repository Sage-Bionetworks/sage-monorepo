from api.database import return_gene_query
from .resolver_helpers import build_option_args, get_value, valid_gene_node_mapping


def resolve_gene(_obj, info, entrez):
    option_args = build_option_args(
        info.field_nodes[0].selection_set, valid_gene_node_mapping)
    query = return_gene_query(*option_args)
    gene = query.filter_by(entrez=entrez).first()

    return {
        'entrez': get_value(gene, 'entrez'),
        'hgnc': get_value(gene, 'hgnc'),
        'description': get_value(gene, 'description'),
        'friendlyName': get_value(gene, 'friendly_name'),
        'ioLandscapeName': get_value(gene, 'io_landscape_name'),
        'geneFamily': get_value(get_value(gene, 'gene_family')),
        'geneFunction': get_value(get_value(gene, 'gene_function')),
        'geneTypes': [{
            'name': get_value(gene_type),
            'display': get_value(gene_type, 'display')
        } for gene_type in get_value(gene, 'gene_types', [])],
        'immuneCheckpoint': get_value(get_value(gene, 'immune_checkpoint')),
        'pathway': get_value(get_value(gene, 'pathway')),
        'publications': [{
            'firstAuthorLastName': get_value(publication, 'first_author_last_name'),
            'journal': get_value(publication, 'journal'),
            'pubmedId': get_value(publication, 'pubmed_id'),
            'title': get_value(publication, 'title'),
            'year': get_value(publication, 'year'),
        } for publication in get_value(gene, 'publications', [])],
        'superCategory': get_value(get_value(gene, 'super_category')),
        'therapyType': get_value(get_value(gene, 'therapy_type'))
    }
