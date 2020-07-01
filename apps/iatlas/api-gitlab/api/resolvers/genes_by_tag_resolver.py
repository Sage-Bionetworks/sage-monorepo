from .resolver_helpers import get_value, request_genes, request_tags


def resolve_genes_by_tag(_obj, info, dataSet, related, feature=None, featureClass=None, entrez=None, geneType=None):
    results = []
    tag_results = request_tags(_obj, info=info, data_set=dataSet,
                               related=related, feature=feature,
                               feature_class=featureClass, get_samples=True)

    for row in tag_results:
        gene_results = request_genes(_obj, info, data_set=dataSet, related=related,
                                     entrez=entrez, gene_type=geneType, by_tag=True,
                                     samples=get_value(row, 'samples'))

        tag_name = get_value(row, 'tag')

        print('tag_name: ', tag_name)
        print('gene_results length: ', len(gene_results))

        if gene_results:
            results.append({
                'characteristics': get_value(row, 'characteristics'),
                'color': get_value(row, 'color'),
                'display': get_value(row, 'display'),
                'tag': tag_name,
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
                } for gene in gene_results]
            })

    return results
