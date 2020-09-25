from sqlalchemy import and_, orm
from api import db
from api.db_models import Dataset, DriverResult, Feature, Gene, MutationCode, Tag
from .general_resolvers import build_join_condition, build_option_args, get_selection_set


def build_driver_result_request(_obj, info, data_set=None, entrez=None, feature=None, max_p_value=None,
                                max_log10_p_value=None, min_fold_change=None, min_log10_fold_change=None,
                                min_log10_p_value=None, min_p_value=None, min_n_mut=None, min_n_wt=None,
                                mutation_code=None, tag=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(info=info, child_node='items')

    driver_result_1 = orm.aliased(DriverResult, name='dr')
    gene_1 = orm.aliased(Gene, name='g')
    mutation_code_1 = orm.aliased(MutationCode, name='mc')
    tag_1 = orm.aliased(Tag, name='t')
    feature_1 = orm.aliased(Feature, name='f')
    data_set_1 = orm.aliased(Dataset, name='ds')

    core_field_mapping = {'pValue': driver_result_1.p_value.label('p_value'),
                          'foldChange': driver_result_1.fold_change.label('fold_change'),
                          'log10PValue': driver_result_1.log10_p_value.label('log10_p_value'),
                          'log10FoldChange': driver_result_1.log10_fold_change.label('log10_fold_change'),
                          'numWildTypes': driver_result_1.n_wt.label('n_wt'),
                          'numMutants': driver_result_1.n_mut.label('n_mut')}

    related_field_mapping = {'feature': 'feature',
                             'gene': 'gene',
                             'mutationCode': 'mutation_code',
                             'tag': 'tag',
                             'dataSet': 'data_set'}

    core = build_option_args(selection_set, core_field_mapping)
    relations = build_option_args(selection_set, related_field_mapping)
    option_args = []

    if 'data_set' in relations:
        data_set_selection_set = get_selection_set(
            selection_set, child_node='dataSet')
        data_set_core_field_mapping = {'display': data_set_1.display.label('data_set_display'),
                                       'name': data_set_1.name.label('data_set_name')}
        core |= build_option_args(
            data_set_selection_set, data_set_core_field_mapping)

    if 'feature' in relations:
        feature_selection_set = get_selection_set(
            selection_set, child_node='feature')
        feature_core_field_mapping = {'display': feature_1.display.label('feature_display'),
                                      'name': feature_1.name.label('feature_name'),
                                      'order': feature_1.order.label('order'),
                                      'unit': feature_1.unit.label('unit')}
        core |= build_option_args(
            feature_selection_set, feature_core_field_mapping)

    if 'gene' in relations:
        gene_selection_set = get_selection_set(
            selection_set, child_node='gene')
        gene_core_field_mapping = {'entrez': gene_1.entrez.label('entrez'),
                                   'hgnc': gene_1.hgnc.label('hgnc'),
                                   'description': gene_1.description.label('description'),
                                   'friendlyName': gene_1.friendly_name.label('friendly_name'),
                                   'ioLandscapeName': gene_1.io_landscape_name.label('io_landscape_name')}
        core |= build_option_args(
            gene_selection_set, gene_core_field_mapping)

    if 'mutation_code' in relations:
        core.add(mutation_code_1.code.label('code'))

    if 'tag' in relations:
        tag_selection_set = get_selection_set(
            selection_set, child_node='tag')
        tag_core_field_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                                  'color': tag_1.color.label('color'),
                                  'display': tag_1.display.label('tag_display'),
                                  'name': tag_1.name.label('tag_name')}
        core |= build_option_args(
            tag_selection_set, tag_core_field_mapping)

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
        query = query.filter(driver_result_1.n_mut >= min_n_mut)

    if min_n_wt or min_n_wt == 0:
        query = query.filter(driver_result_1.n_wt >= min_n_wt)

    if 'data_set' in relations or data_set:
        is_outer = not bool(data_set)
        data_set_join_condition = build_join_condition(
            data_set_1.id, driver_result_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
        query = query.join(data_set_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'gene' in relations or entrez:
        is_outer = not bool(entrez)
        data_set_join_condition = build_join_condition(
            gene_1.id, driver_result_1.gene_id, filter_column=gene_1.entrez, filter_list=entrez)
        query = query.join(gene_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'feature' in relations or feature:
        is_outer = not bool(feature)
        data_set_join_condition = build_join_condition(
            feature_1.id, driver_result_1.feature_id, filter_column=feature_1.name, filter_list=feature)
        query = query.join(feature_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'mutation_code' in relations or mutation_code:
        is_outer = not bool(mutation_code)
        mutation_code_join_condition = build_join_condition(
            mutation_code_1.id, driver_result_1.mutation_code_id, filter_column=mutation_code_1.code, filter_list=mutation_code)
        query = query.join(mutation_code_1, and_(
            *mutation_code_join_condition), isouter=is_outer)

    if 'tag' in relations or tag:
        is_outer = not bool(tag)
        data_set_join_condition = build_join_condition(
            tag_1.id, driver_result_1.tag_id, filter_column=tag_1.name, filter_list=tag)
        query = query.join(tag_1, and_(
            *data_set_join_condition), isouter=is_outer)

    return query


def request_driver_results(_obj, info, data_set=None, entrez=None, feature=None, max_p_value=None,
                           max_log10_p_value=None, min_fold_change=None, min_log10_fold_change=None,
                           min_log10_p_value=None, min_p_value=None, min_n_mut=None, min_n_wt=None,
                           mutation_code=None, tag=None):
    query = build_driver_result_request(
        _obj, info, data_set=data_set, entrez=entrez, feature=feature, max_p_value=max_p_value, max_log10_p_value=max_log10_p_value,
        min_fold_change=min_fold_change, min_log10_fold_change=min_log10_fold_change, min_log10_p_value=min_log10_p_value,
        min_p_value=min_p_value, min_n_mut=min_n_mut, min_n_wt=min_n_wt, mutation_code=mutation_code, tag=tag)
    return query.distinct()
