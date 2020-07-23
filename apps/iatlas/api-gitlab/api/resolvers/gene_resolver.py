from itertools import groupby
from .resolver_helpers import build_gene_graphql_response, get_gene_types, get_publications, get_samples, request_gene


def resolve_gene(_obj, info, entrez, sample=None):
    gene = request_gene(_obj, info, entrez=entrez, sample=sample)

    if gene:
        gene_dict = {gene.id: gene}
        samples = get_samples(info, sample=sample, gene_dict=gene_dict)
        gene_types = get_gene_types(info, gene_dict=gene_dict)
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

        return build_gene_graphql_response(types_dict, pubs_dict, samples_dict)(gene) if gene else None
