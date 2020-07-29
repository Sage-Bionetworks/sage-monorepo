from .resolver_helpers import build_gene_graphql_response, request_gene, return_relations


def resolve_gene(_obj, info, entrez, sample=None):
    gene = request_gene(_obj, info, entrez=entrez, sample=sample)

    if gene:
        pubs_dict, samples_dict, types_dict = return_relations(
            info, gene_ids=[gene.id], sample=sample)

        return build_gene_graphql_response(types_dict, pubs_dict, samples_dict)(gene)
    return None
