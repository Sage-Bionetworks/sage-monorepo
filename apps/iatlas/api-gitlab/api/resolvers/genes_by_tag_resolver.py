from itertools import groupby
from .resolver_helpers import build_gene_graphql_response, get_value, request_genes, return_relations


def resolve_genes_by_tag(_obj, info, dataSet, related, entrez=None, feature=None, featureClass=None, geneType=None, sample=None, tag=None):
    gene_results = request_genes(_obj, info, by_tag=True, data_set=dataSet, entrez=entrez, feature=feature, feature_class=featureClass,
                                 gene_type=geneType, related=related, sample=sample, tag=tag)

    return_list = []
    append_to_return_list = return_list.append
    for tag, genes_list in groupby(gene_results, key=lambda g: g.tag):
        genes_list = list(genes_list)

        gene_dict = {gene.id: gene for gene in genes_list}

        pubs_dict, samples_dict, types_dict = return_relations(
            info, gene_dict=gene_dict, gene_type=geneType, sample=sample, by_tag=True)

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
