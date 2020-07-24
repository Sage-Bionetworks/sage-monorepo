from itertools import groupby
from .resolver_helpers import (
    build_gene_request, build_option_args, get_selection_set, get_value, request_genes, request_tags)


def resolve_genes_by_tag(_obj, info, dataSet, related, entrez=None, feature=None, featureClass=None, geneType=None, sample=None, tag=None):
    genes = request_genes(_obj, info, by_tag=True, data_set=dataSet, entrez=entrez, feature=feature, feature_class=featureClass,
                          gene_type=geneType, related=related, sample=sample)

    tags_dict = dict()
    for key, collection in groupby(genes, key=lambda g: g.tag):
        tags_dict[key] = tags_dict.get(key, []) + list(collection)

    return [{
        'characteristics': get_value(value[0], 'tag_characteristics'),
        'color': get_value(value[0], 'tag_color'),
        'display': get_value(value[0], 'tag_display'),
        'genes': [{
            'entrez': get_value(gene, 'entrez'),
            'hgnc': get_value(gene, 'hgnc'),
            'description': get_value(gene, 'description'),
            'friendlyName': get_value(gene, 'friendly_name'),
            'ioLandscapeName': get_value(gene, 'io_landscape_name')
        } for gene in value],
        'tag': key
    } for key, value in tags_dict.items()]
