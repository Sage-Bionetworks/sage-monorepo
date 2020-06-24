from .resolver_helpers import get_value, request_genes


def resolve_genes(_obj, info, entrez=None):
    genes = request_genes(_obj, info, entrez)

    return [
        {
            'id': gene.id,
            'entrez': gene.entrez,
            'hgnc': gene.hgnc,
            'description': gene.description,
            'friendlyName': gene.friendly_name,
            'ioLandscapeName': gene.io_landscape_name,
            'geneFamily': get_value(gene.gene_family),
            'geneFunction': get_value(gene.gene_function),
            'geneTypes': [{
                'name': get_value(gene_type),
                'display': get_value(gene_type, 'display')
            } for gene_type in get_value(gene, 'gene_types', [])],
            'immuneCheckpoint': get_value(gene.immune_checkpoint),
            'pathway': get_value(gene.pathway),
            'publications': [{
                'firstAuthorLastName': get_value(publication, 'first_author_last_name'),
                'journal': get_value(publication, 'journal'),
                'pubmedId': get_value(publication, 'pubmed_id'),
                'title': get_value(publication, 'title'),
                'year': get_value(publication, 'year'),
            } for publication in get_value(gene, 'publications', [])],
            'superCategory': get_value(gene.super_category),
            'therapyType': get_value(gene.therapy_type)
        } for gene in genes]
