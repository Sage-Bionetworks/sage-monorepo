from itertools import groupby
from .resolver_helpers import build_gene_graphql_response, gene_request_fields, get_requested, get_value, request_genes, return_gene_derived_fields


def resolve_genes_by_tag(_obj, info, dataSet, related, entrez=None, feature=None, featureClass=None, geneType=None, sample=None, tag=None):
    tag_field_mapping = {'characteristics': 'characteristics',
                         'color': 'color',
                         'display': 'display',
                         'tag': 'tag'}

    requested = get_requested(
        info, gene_request_fields, True, child_node='genes')

    tag_requested = get_requested(info, tag_field_mapping)
    tag_requested.add('by_tag')
    gene_results = request_genes(requested, tag_requested=tag_requested, data_set=dataSet, entrez=entrez, feature=feature, feature_class=featureClass,
                                 gene_type=geneType, related=related, sample=sample, tag=tag)
    gene_ids = set(gene.id for gene in gene_results)

    tag_dict = dict()
    for gene_tag, genes_list in groupby(gene_results, key=lambda g: g.tag):
        tag_dict[gene_tag] = tag_dict.get(gene_tag, []) + list(genes_list)

    def build_response(feature_set):
        gene_tag, genes = feature_set
        pubs_dict, samples_dict, types_dict = return_gene_derived_fields(info, data_set=dataSet, feature=feature, feature_class=featureClass, gene_type=geneType,
                                                                         related=related, sample=sample, tag=tag, gene_ids=gene_ids, by_tag=True)
        return {
            'characteristics': get_value(genes[0], 'characteristics'),
            'color': get_value(genes[0], 'color'),
            'display': get_value(genes[0], 'display'),
            'genes': list(map(build_gene_graphql_response(types_dict, pubs_dict, samples_dict), genes)),
            'tag': gene_tag
        }

    return map(build_response, tag_dict.items())
