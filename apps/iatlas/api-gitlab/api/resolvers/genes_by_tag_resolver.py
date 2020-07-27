from itertools import groupby
from .resolver_helpers import (
    build_gene_graphql_response, build_option_args, get_gene_types, get_publications, get_samples, get_value, request_genes)


def resolve_genes_by_tag(_obj, info, dataSet, related, entrez=None, feature=None, featureClass=None, geneType=None, sample=None, tag=None):
    gene_results = request_genes(_obj, info, by_tag=True, data_set=dataSet, entrez=entrez, feature=feature, feature_class=featureClass,
                                 gene_type=geneType, related=related, sample=sample, tag=tag)

    tags_dict = dict()
    return_list = []
    append_to_return_list = return_list.append
    for tag, genes_list in groupby(gene_results, key=lambda g: g.tag):
        genes_list = list(genes_list)

        gene_dict = {gene.id: gene for gene in genes_list}
        gene_types = get_gene_types(
            info, gene_type=geneType, gene_dict=gene_dict)
        samples = get_samples(info, sample=sample,
                              gene_dict=gene_dict, by_tag=True)
        pubs = get_publications(
            info, gene_types=gene_types, gene_dict=gene_dict)

        types_dict = dict()
        for key, collection in groupby(gene_types, key=lambda gt: gt.gene_id):
            types_dict[key] = types_dict.get(key, []) + list(collection)

        samples_dict = dict()
        for key, collection in groupby(samples, key=lambda s: s.gene_id):
            samples_dict[key] = samples_dict.get(key, []) + list(collection)

        pubs_dict = dict()
        for key, collection in groupby(pubs, key=lambda pub: pub.gene_id):
            pubs_dict[key] = pubs_dict.get(key, []) + list(collection)

        genes = list(map(build_gene_graphql_response(
            types_dict, pubs_dict, samples_dict), genes_list))

        append_to_return_list({
            'characteristics': get_value(genes[0], 'characteristics'),
            'color': get_value(genes[0], 'color'),
            'display': get_value(genes[0], 'display'),
            'genes': genes,
            'tag': tag
        })

    return return_list
