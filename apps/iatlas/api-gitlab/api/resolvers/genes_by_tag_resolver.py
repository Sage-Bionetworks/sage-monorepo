from .resolver_helpers import (
    build_gene_request, build_option_args, get_selection_set, get_value, request_genes, request_tags)


def resolve_genes_by_tag(_obj, info, dataSet, related, tag=None, feature=None, featureClass=None, entrez=None, geneType=None):
    results = []
    append = results.append
    tag_results = request_tags(_obj, info=info, data_set=dataSet, related=related,
                               tag=tag, feature=feature, feature_class=featureClass,
                               get_samples=True)

    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)
    fields = build_option_args(selection_set, {'genes': 'genes'})
    want_genes = 'genes' in fields

    get_value_1 = get_value

    def build_results(row):
        sample = get_value_1(row, 'samples')
        gene_results = request_genes(_obj, info, entrez=entrez, gene_type=geneType,
                                     by_tag=True, sample=sample) if want_genes else []
        if gene_results:
            return {
                'characteristics': get_value_1(row, 'characteristics'),
                'color': get_value_1(row, 'color'),
                'display': get_value_1(row, 'display'),
                'tag': get_value_1(row, 'tag'),
                'genes': [{
                    'entrez': get_value_1(gene, 'entrez'),
                    'hgnc': get_value_1(gene, 'hgnc'),
                    'description': get_value_1(gene, 'description'),
                    'friendlyName': get_value_1(gene, 'friendly_name'),
                    'ioLandscapeName': get_value_1(gene, 'io_landscape_name')
                } for gene in gene_results]
            }

    return map(build_results, tag_results)


# def get_genes(_obj, info, entrez=entrez, gene_type=gene_type, by_tag=tags):
#     selection_set = get_selection_set(info.field_nodes[0].selection_set, False)
#     fields = build_option_args(selection_set, {'genes': 'genes'})
#     want_genes = 'genes' in fields

#     if want_genes:
#         gene_query = build_gene_request(_obj, info, entrez=entrez, gene_type=gene_type,
#                                         samples=samples, by_tag=by_tag)
#         genes = request_genes(_obj, info, entrez=entrez, gene_type=geneType,
#                                      by_tag=True, sample=get_value(row, 'samples')) if want_genes else [None]

#         gene_to_sample_1 = orm.aliased(GeneToSample, name='gs')


#         pub_gene_gene_type_join_condition = build_pub_gene_gene_type_join_condition(
#             genes, pub_gene_gene_type_1, pub_1)
#         pub_query = pub_query.join(pub_gene_gene_type_1, and_(
#             *pub_gene_gene_type_join_condition))

#         publications = pub_query.distinct().all()

#         gene_dict = {gene.id: gene for gene in genes}

#         for key, collection in groupby(publications, key=lambda publication: publication.gene_id):
#             set_committed_value(
#                 gene_dict[key], 'publications', list(collection))
