from .resolver_helpers import get_rna_seq_expr, get_value, request_gene


def resolve_gene(_obj, info, entrez):
    gene = request_gene(_obj, info, entrez)

    if gene:
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
                'doId': get_value(publication, 'do_id'),
                'firstAuthorLastName': get_value(publication, 'first_author_last_name'),
                'journal': get_value(publication, 'journal'),
                'name': get_value(publication),
                'pubmedId': get_value(publication, 'pubmed_id'),
                'title': get_value(publication, 'title'),
                'year': get_value(publication, 'year'),
            } for publication in get_value(gene, 'publications', [])],
            'rnaSeqExpr': get_rna_seq_expr(gene),
            'superCategory': get_value(get_value(gene, 'super_category')),
            'therapyType': get_value(get_value(gene, 'therapy_type'))
        }
    return None
