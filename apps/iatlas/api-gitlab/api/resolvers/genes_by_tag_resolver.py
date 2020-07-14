from .resolver_helpers import (
    build_option_args, get_rna_seq_expr, get_selection_set, get_value, request_genes, request_tags)


def resolve_genes_by_tag(_obj, info, dataSet, related, tag=None, feature=None, featureClass=None, entrez=None, geneType=None):
    results = []
    append = results.append
    tag_results = request_tags(_obj, info=info, data_set=dataSet, related=related,
                               tag=tag, feature=feature, feature_class=featureClass,
                               get_samples=True)

    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)
    fields = build_option_args(selection_set, {'genes': 'genes'})
    want_genes = 'genes' in fields

    for row in tag_results:
        gene_results = request_genes(_obj, info, entrez=entrez, gene_type=geneType,
                                     by_tag=True, samples=get_value(row, 'samples')) if want_genes else [None]

        tag_name = get_value(row, 'tag')

        if gene_results:
            genes = []
            append_to_genes = genes.append
            for gene in gene_results:
                append_to_genes({
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
                        'year': get_value(publication, 'year')
                    } for publication in get_value(gene, 'publications', [])],
                    'rnaSeqExpr': get_rna_seq_expr(gene),
                    'superCategory': get_value(get_value(gene, 'super_category')),
                    'therapyType': get_value(get_value(gene, 'therapy_type'))
                })
            append({
                'characteristics': get_value(row, 'characteristics'),
                'color': get_value(row, 'color'),
                'display': get_value(row, 'display'),
                'tag': tag_name,
                'genes': genes
            })

    return results
