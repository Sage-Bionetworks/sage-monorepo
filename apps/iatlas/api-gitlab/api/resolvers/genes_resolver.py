from itertools import groupby
from .resolver_helpers import build_gene_graphql_response, get_gene_types, get_publications, get_samples, request_genes


def resolve_genes(_obj, info, entrez=None, sample=None, geneType=None):
    genes = request_genes(_obj, info, entrez=entrez,
                          gene_type=geneType, sample=sample)

    print('has genes: ', bool(genes))
    gene_dict = {gene.id: gene for gene in genes}
    print('gene_dict: ', gene_dict)
    gene_types = get_gene_types(info, gene_type=geneType, gene_dict=gene_dict)
    print('has gene_types: ', bool(gene_types))
    samples = get_samples(info, sample=sample, gene_dict=gene_dict)
    print('has samples: ', bool(samples))
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

    return map(build_gene_graphql_response(types_dict, pubs_dict, samples_dict), genes)
