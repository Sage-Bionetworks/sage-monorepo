from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Dataset, DatasetToTag, HeritabilityResult, Feature
from .general_resolvers import build_join_condition, get_selected, get_value
from .data_set import build_data_set_graphql_response
from .feature import build_feature_graphql_response
from .paging_utils import get_cursor, get_pagination_queries, Paging

heritability_result_request_fields = {'dataSet',
                                      'id'
                                      'feature',
                                      'pValue',
                                      'cluster',
                                      'module',
                                      'category',
                                      'fdr',
                                      'variance',
                                      'se',
                                      'yMin',
                                      'yMax'}


def build_hr_graphql_response(heritability_result):
    return {
        'id': get_value(heritability_result, 'id'),
        'pValue': get_value(heritability_result, 'p_value'),
        'dataSet': build_data_set_graphql_response(heritability_result),
        'feature': build_feature_graphql_response()(heritability_result),
        'cluster': get_value(heritability_result, 'cluster'),
        'module': get_value(heritability_result, 'module'),
        'category': get_value(heritability_result, 'category'),
        'fdr': get_value(heritability_result, 'fdr'),
        'variance': get_value(heritability_result, 'variance'),
        'se': get_value(heritability_result, 'se'),
        'yMin': get_value(heritability_result, 'y_min'),
        'yMax': get_value(heritability_result, 'y_max')
    }


def build_heritability_result_request(
        requested, data_set_requested, feature_requested, data_set=None, distinct=False, feature=None, max_p_value=None, min_p_value=None, module=None, cluster=None, paging=None):
    """
    Builds a SQL request.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request
        2nd position - a set of the requested fields in the 'dataSet' node of the graphql request. If 'dataSet' is not requested, this will be an empty set.
        3rd position - a set of the requested fields in the 'feature' node of the graphql request. If 'feature' is not requested, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `data_set` - a list of strings, data set names
        `distinct` - a boolean, indicates whether duplicate records should be filtered out
        `feature` - a list of strings, feature names
        `max_p_value` - a float, a maximum P value
        `min_p_value` - a float, a minimum P value
        `module` a string
        `cluster` a string
        `paging` - a dict containing pagination metadata
    """
    sess = db.session

    heritability_result_1 = aliased(HeritabilityResult, name='hr')
    feature_1 = aliased(Feature, name='f')
    data_set_1 = aliased(Dataset, name='ds')

    core_field_mapping = {
        'id': heritability_result_1.id.label('id'),
        'pValue': heritability_result_1.p_value.label('p_value'),
        'se': heritability_result_1.se.label('se'),
        'variance': heritability_result_1.variance.label('variance'),
        'fdr': heritability_result_1.fdr.label('fdr'),
        'y_min': heritability_result_1.y_min.label('y_min'),
        'y_max': heritability_result_1.y_max.label('y_max'),
        'category': heritability_result_1.category.label('category'),
        'cluster': heritability_result_1.cluster.label('cluster'),
        'module': heritability_result_1.module.label('module')
    }
    data_set_core_field_mapping = {'display': data_set_1.display.label('data_set_display'),
                                   'name': data_set_1.name.label('data_set_name'),
                                   'type': data_set_1.data_set_type.label('data_set_type')}
    feature_core_field_mapping = {'display': feature_1.display.label('feature_display'),
                                  'name': feature_1.name.label('feature_name'),
                                  'order': feature_1.order.label('order'),
                                  'unit': feature_1.unit.label('unit')}

    core = get_selected(requested, core_field_mapping)
    core |= get_selected(data_set_requested, data_set_core_field_mapping)
    core |= get_selected(feature_requested, feature_core_field_mapping)

    query = sess.query(*core)
    query = query.select_from(heritability_result_1)

    if module:
        query = query.filter(heritability_result_1.module.in_(module))

    if cluster:
        query = query.filter(heritability_result_1.cluster.in_(cluster))

    if max_p_value or max_p_value == 0:
        query = query.filter(heritability_result_1.p_value <= max_p_value)

    if min_p_value or min_p_value == 0:
        query = query.filter(heritability_result_1.p_value >= min_p_value)

    if 'dataSet' in requested or data_set:
        is_outer = not bool(data_set)
        data_set_join_condition = build_join_condition(
            data_set_1.id, heritability_result_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
        query = query.join(data_set_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'feature' in requested or feature:
        is_outer = not bool(feature)
        data_set_join_condition = build_join_condition(
            feature_1.id, heritability_result_1.feature_id, filter_column=feature_1.name, filter_list=feature)
        query = query.join(feature_1, and_(
            *data_set_join_condition), isouter=is_outer)

    return get_pagination_queries(query, paging, distinct, cursor_field=heritability_result_1.id)
