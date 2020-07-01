from .resolver_helpers import get_value, request_genes


def resolve_genes_by_tag(_obj, info, dataSet, related, entrez=None, geneType=None):
    results = request_genes(_obj, info, dataSet=dataSet, related=related,
                            entrez=entrez, geneType=geneType, byTag=True)

    tag_map = dict()
    for row in results:
        gene_tag = get_value(row, 'tag')
        if not gene_tag in tag_map:
            tag_map[gene_tag] = [row]
        else:
            tag_map[gene_tag].append(row)

    return []

    return [{
        'characteristics': get_value(value[0], 'tag_characteristics'),
        'display': get_value(value[0], 'tag_display'),
        'genes': [{
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
        } for gene in value],
        'tag': key
    } for key, value in tag_map.items()]
