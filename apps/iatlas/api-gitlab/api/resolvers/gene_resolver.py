from .resolver_helpers import build_gene_graphql_response, gene_request_fields, get_requested, request_gene, return_gene_derived_fields


def resolve_gene(_obj, info, entrez, sample=None):
    requested = get_requested(info, gene_request_fields)

    gene = request_gene(requested, entrez=entrez, sample=sample)

    if gene:
        pubs_dict, samples_dict, types_dict = return_gene_derived_fields(
            info, gene_ids=[gene.id], sample=sample)

        return build_gene_graphql_response(types_dict, pubs_dict, samples_dict)(gene)
    return None
