from itertools import groupby
from .resolver_helpers import build_gene_graphql_response, get_value, request_genes, return_relations


def resolve_genes_by_tag(_obj, info, dataSet, related, entrez=None, feature=None, featureClass=None, geneType=None, sample=None, tag=None):
    gene_results = request_genes(_obj, info, by_tag=True, data_set=dataSet, entrez=entrez, feature=feature, feature_class=featureClass,
                                 gene_type=geneType, related=related, sample=sample, tag=tag)
    gene_ids = set(gene.id for gene in gene_results)

    pubs_dict, samples_dict, types_dict = return_relations(
        info, gene_ids=gene_ids, gene_type=geneType, sample=sample, by_tag=True)

    tag_dict = dict()
    for tag, genes_list in groupby(gene_results, key=lambda g: g.tag):
        tag_dict[tag] = tag_dict.get(tag, []) + list(genes_list)

    return [{
        'characteristics': get_value(genes_list[0], 'characteristics'),
        'color': get_value(genes_list[0], 'color'),
        'display': get_value(genes_list[0], 'display'),
        'genes': list(map(build_gene_graphql_response(
            types_dict, pubs_dict, samples_dict), genes_list)),
        'tag': tag
    } for tag, genes_list in tag_dict.items()]
