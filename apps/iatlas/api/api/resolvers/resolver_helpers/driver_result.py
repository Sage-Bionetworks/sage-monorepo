from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Dataset, DatasetToTag, DriverResult, Feature, Gene, Mutation, MutationType, Tag
from .general_resolvers import build_join_condition, get_selected, get_value
from .paging_utils import get_pagination_queries

driver_result_request_fields = {
    'dataSet',
    'feature',
    'mutation',
    'tag',
    'pValue',
    'foldChange',
    'log10PValue',
    'log10FoldChange',
    'numWildTypes',
    'numMutants'
}


def build_dr_graphql_response(driver_result):
    from .data_set import build_data_set_graphql_response
    from .feature import build_feature_graphql_response
    from .mutation import build_mutation_graphql_response
    from .tag import build_tag_graphql_response

    dict = {
        'id': get_value(driver_result, 'id'),
        'pValue': get_value(driver_result, 'p_value'),
        'foldChange': get_value(driver_result, 'fold_change'),
        'log10PValue': get_value(driver_result, 'log10_p_value'),
        'log10FoldChange': get_value(driver_result, 'log10_fold_change'),
        'numWildTypes': get_value(driver_result, 'n_wt'),
        'numMutants': get_value(driver_result, 'n_mut'),
        'dataSet': build_data_set_graphql_response()(driver_result),
        'feature': build_feature_graphql_response()(driver_result),
        'mutation': build_mutation_graphql_response()(driver_result),
        'tag': build_tag_graphql_response()(driver_result)
    }
    return(dict)


def build_driver_result_request(requested, data_set_requested, feature_requested, mutation_requested, mutation_gene_requested, mutation_type_requested, tag_requested, data_set=None, distinct=False, entrez=None, feature=None, max_p_value=None, max_log10_p_value=None, min_fold_change=None, min_log10_fold_change=None, min_log10_p_value=None, min_p_value=None, min_n_mut=None, min_n_wt=None, mutation=None, mutation_code=None, paging=None, related=None, tag=None):
    """
    Builds a SQL request.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request
        2nd position - a set of the requested fields in the 'dataSet' node of the graphql request. If 'dataSet' is not requested, this will be an empty set.
        3rd position - a set of the requested fields in the 'feature' node of the graphql request. If 'feature' is not requested, this will be an empty set.
        4th position - a set of the requested fields in the 'mutation' node of the graphql request. If 'mutation' is not requested, this will be an empty set.
        5th position - a set of the requested fields in the 'gene' node of the graphql request. If 'gene' is not requested, this will be an empty set.
        6th position - a set of the requested fields in the 'mutationType' node of the graphql request. If 'mutationType' is not requested, this will be an empty set.
        7th position - a set of the requested fields in the 'tag' node of the graphql request. If 'tag' is not requested, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `data_set` - a list of strings, data set names
        `distinct` - a boolean, indicates whether duplicate records should be filtered out
        `entrez` - a list of integers, gene entrez ids
        `feature` - a list of strings, feature names
        `max_p_value` - a float, a maximum P value
        `max_log10_p_value` - a float, a minimum calculated log10 P value
        `min_fold_change` - a float, a minimum fold change value
        `min_log10_fold_change` - a float, a minimum calculated log 10 fold change value
        `min_log10_p_value` - a float, a minimum calculated log 10 P value
        `min_p_value` - a float, a minimum P value
        `min_n_mut` - a float, a minimum number of mutants
        `min_n_wt` - a float, a minimum number of wild types
        `mutation` - a list of strings, mutations
        `mutation_code` - a list of strings, mutation codes
        `paging` - a dict containing pagination metadata
        `related` - a list of strings, tags related to the dataset that is associated with the result.
        `tag` - a list of strings, tag names
    """
    from .tag import get_tag_column_labels
    from .gene import get_simple_gene_column_labels
    from .mutation import get_mutation_column_labels, get_mutation_type_column_labels, build_simple_mutation_request
    sess = db.session

    driver_result_1 = aliased(DriverResult, name='dr')
    gene_1 = aliased(Gene, name='g')
    mutation_1 = aliased(Mutation, name='m')
    mutation_type_1 = aliased(MutationType, name='mt')
    tag_1 = aliased(Tag, name='t')
    feature_1 = aliased(Feature, name='f')
    data_set_1 = aliased(Dataset, name='ds')

    core_field_mapping = {
        'id': driver_result_1.id.label('id'),
        'pValue': driver_result_1.p_value.label('p_value'),
        'foldChange': driver_result_1.fold_change.label('fold_change'),
        'log10PValue': driver_result_1.log10_p_value.label('log10_p_value'),
        'log10FoldChange': driver_result_1.log10_fold_change.label('log10_fold_change'),
        'numWildTypes': driver_result_1.n_wildtype.label('n_wt'),
        'numMutants': driver_result_1.n_mutants.label('n_mut')
    }
    data_set_core_field_mapping = {
        'display': data_set_1.display.label('data_set_display'),
        'name': data_set_1.name.label('data_set_name'),
        'type': data_set_1.dataset_type.label('data_set_type')
    }
    feature_core_field_mapping = {
        'display': feature_1.display.label('feature_display'),
        'name': feature_1.name.label('feature_name'),
        'order': feature_1.order.label('feature_order'),
        'unit': feature_1.unit.label('feature_unit')
    }

    core = get_selected(requested, core_field_mapping)
    core |= get_selected(data_set_requested, data_set_core_field_mapping)
    core |= get_selected(feature_requested, feature_core_field_mapping)
    core |= get_tag_column_labels(tag_requested, tag_1)
    core |= get_mutation_column_labels(
        mutation_requested, mutation_1
    )
    core |= get_simple_gene_column_labels(mutation_gene_requested, gene_1)
    core |= get_mutation_type_column_labels(
        mutation_type_requested, mutation_type_1)

    core |= {driver_result_1.id.label('id')}

    query = sess.query(*core)
    query = query.select_from(driver_result_1)

    if max_p_value or max_p_value == 0:
        query = query.filter(driver_result_1.p_value <= max_p_value)

    if (max_log10_p_value or max_log10_p_value == 0) and (not max_p_value and max_p_value != 0):
        query = query.filter(
            driver_result_1.log10_p_value <= max_log10_p_value)

    if min_fold_change or min_fold_change == 0:
        query = query.filter(
            driver_result_1.fold_change >= min_fold_change)

    if (min_log10_fold_change or min_log10_fold_change == 0) and (not min_fold_change and min_fold_change != 0):
        query = query.filter(
            driver_result_1.log10_fold_change >= min_log10_fold_change)

    if (min_log10_p_value or min_log10_p_value == 0) and (not min_p_value and min_p_value != 0):
        query = query.filter(
            driver_result_1.log10_p_value >= min_log10_p_value)

    if min_p_value or min_p_value == 0:
        query = query.filter(driver_result_1.p_value >= min_p_value)

    if min_n_mut or min_n_mut == 0:
        query = query.filter(driver_result_1.n_mutants >= min_n_mut)

    if min_n_wt or min_n_wt == 0:
        query = query.filter(driver_result_1.n_wildtype >= min_n_wt)

    if 'dataSet' in requested or data_set or related:
        is_outer = not bool(data_set)
        data_set_join_condition = build_join_condition(
            data_set_1.id, driver_result_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
        query = query.join(data_set_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'feature' in requested or feature:
        is_outer = not bool(feature)
        data_set_join_condition = build_join_condition(
            feature_1.id, driver_result_1.feature_id, filter_column=feature_1.name, filter_list=feature)
        query = query.join(feature_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'tag' in requested or tag:
        is_outer = not bool(tag)
        data_set_join_condition = build_join_condition(
            tag_1.id, driver_result_1.tag_id, filter_column=tag_1.name, filter_list=tag)
        query = query.join(tag_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if related:
        data_set_to_tag_1 = aliased(DatasetToTag, name='dtt')
        related_tag_1 = aliased(Tag, name='rt')

        related_tag_sub_query = sess.query(related_tag_1.id).filter(
            related_tag_1.name.in_(related))

        data_set_tag_join_condition = build_join_condition(
            data_set_to_tag_1.dataset_id, data_set_1.id, data_set_to_tag_1.tag_id, related_tag_sub_query)
        query = query.join(
            data_set_to_tag_1, and_(*data_set_tag_join_condition))

    if 'mutation' in requested or mutation:
        is_outer = not bool(mutation)
        mutation_join_condition = build_join_condition(
            driver_result_1.mutation_id, mutation_1.id, filter_column=mutation_1.name, filter_list=mutation)
        query = query.join(mutation_1, and_(
            *mutation_join_condition), isouter=is_outer)

    query = build_simple_mutation_request(
        query, mutation_requested, mutation_1, gene_1, mutation_type_1, entrez=entrez, mutation_code=mutation_code
    )

    return get_pagination_queries(query, paging, distinct, cursor_field=driver_result_1.id)
