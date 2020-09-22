from .resolver_helpers import (build_cnr_graphql_response, build_copy_number_result_request, cnr_request_fields, data_set_request_fields,
                               feature_request_fields, gene_request_fields, get_requested, get_selection_set, simple_tag_request_fields)

from .resolver_helpers.cursor_utils import get_limit

def resolve_copy_number_results(_obj, info, **kwargs):
    edges = get_selection_set(
        info.field_nodes[0].selection_set, True, 'edges')

    selection_set = get_selection_set(
        edges, True, 'node')

    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=cnr_request_fields)
    requested.add('id')

    data_set_selection_set = get_selection_set(
        selection_set, 'dataSet' in requested, 'dataSet')
    data_set_requested = get_requested(
        selection_set=data_set_selection_set, requested_field_mapping=data_set_request_fields)

    feature_selection_set = get_selection_set(
        selection_set, 'feature' in requested, 'feature')
    feature_requested = get_requested(
        selection_set=feature_selection_set, requested_field_mapping=feature_request_fields)

    gene_selection_set = get_selection_set(
        selection_set, 'gene' in requested, 'gene')
    gene_requested = get_requested(
        selection_set=gene_selection_set, requested_field_mapping=gene_request_fields)

    tag_selection_set = get_selection_set(
        selection_set, 'tag' in requested, 'tag')
    tag_requested = get_requested(
        selection_set=tag_selection_set, requested_field_mapping=simple_tag_request_fields)

    query = build_copy_number_result_request(requested, data_set_requested, feature_requested, gene_requested, tag_requested, data_set=kwargs.pop('dataSet', 0), **kwargs)
    count = query.count()
    results = query.limit(get_limit(**kwargs)).all()

    return {
        'totalCount': count,
        'edges': map(build_cnr_graphql_response, results)
    }
