from .resolver_helpers import build_gene_graphql_response, gene_request_fields, gene_related_sample_request_fields, get_requested, request_gene, return_gene_derived_fields, simple_gene_type_request_fields, simple_publication_request_fields


def resolve_gene(_obj, info, entrez, sample=None):
    requested = get_requested(
        info=info, requested_field_mapping=gene_request_fields)
    gene_types_requested = get_requested(
        info, simple_gene_type_request_fields, 'geneTypes')
    publications_requested = get_requested(
        info, simple_publication_request_fields, 'publications')
    samples_requested = get_requested(
        info, gene_related_sample_request_fields, 'samples')

    gene = request_gene(requested, entrez=[entrez], sample=sample)

    if gene:
        pubs_dict, types_dict = return_gene_derived_fields(
            requested, gene_types_requested, publications_requested, samples_requested, entrez=[entrez], sample=sample)

        return build_gene_graphql_response(pubs_dict, types_dict)(gene)
    return None
