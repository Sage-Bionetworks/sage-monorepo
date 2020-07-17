from sqlalchemy import orm
from api import db
from api.db_models import (
    DriverResult, Gene, MutationCode, Tag, Feature, Dataset)
from .general_resolvers import build_option_args, get_selection_set


def build_driver_result_request(_obj, info, feature=None, entrez=None, mutationCode=None, tag=None, data_set=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, child_node='driver_results')

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
                          'n_wt': driver_result_1.n_wt.label('n_wt'),
                          'n_mut': driver_result_1.n_mut.label('n_mut')}

    related_field_mapping = {'feature': 'feature',
                             'gene': 'gene',
                             'mutationCode': 'mutationCode',
                             'tag': 'tag',
                             'dataSet': 'data_set'}

    core = build_option_args(selection_set, core_field_mapping)
    relations = build_option_args(selection_set, related_field_mapping)
    option_args = []
    entity_args = []

    query = sess.query(driver_result_1)

    if 'feature' in relations or feature:
        query = query.join((feature_1, driver_result_1.feature), isouter=True)
        option_args.append(orm.contains_eager(
            driver_result_1.feature.of_type(feature_1)))

    if 'gene' in relations or entrez:
        query = query.join(
            (gene_1, driver_result_1.gene), isouter=True)
        option_args.append(orm.contains_eager(
            driver_result_1.gene.of_type(gene_1)))

    if 'mutationCode' in relations or mutationCode:
        query = query.join(
            (mutation_code_1, driver_result_1.mutation_code), isouter=True)
        option_args.append(orm.contains_eager(
            driver_result_1.mutation_code.of_type(mutation_code_1)))

    if 'tag' in relations or tag:
        query = query.join(
            (tag_1, driver_result_1.tag), isouter=True)
        option_args.append(orm.contains_eager(
            driver_result_1.tag.of_type(tag_1)))

    if 'data_set' in relations or data_set:
        query = query.join(
            (data_set_1, driver_result_1.data_set), isouter=True)
        option_args.append(orm.contains_eager(
            driver_result_1.data_set.of_type(data_set_1)))

    if option_args:
        query = query.options(*option_args)
    else:
        query = sess.query(*core)

    if data_set:
        query = query.filter(data_set_1.name.in_(data_set))

    if mutationCode:
        query = query.filter(mutation_code_1.code.in_(mutationCode))

    if entrez:
        query = query.filter(gene_1.entrez.in_(entrez))

    if feature:
        query = query.filter(feature_1.name.in_(feature))

    if tag:
        query = query.filter(tag_1.name.in_(tag))

    return query


def request_driver_results(_obj, info, feature=None, entrez=None, mutationCode=None, tag=None, data_set=None, limit=None):
    query = build_driver_result_request(
        _obj, info, feature=feature, entrez=entrez, mutationCode=mutationCode, tag=tag, data_set=data_set)
    query = query.distinct()
    if limit:
        query = query.limit(limit)
    return query.all()
