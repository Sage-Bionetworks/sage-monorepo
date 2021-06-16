from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Dataset, DatasetToTag, RareVariantPathwayAssociation, Feature, Gene
from .general_resolvers import build_join_condition, get_selected, get_value
from .data_set import build_data_set_graphql_response
from .feature import build_feature_graphql_response
from .paging_utils import get_cursor, get_pagination_queries, Paging

rare_variant_pathway_association_request_fields = {
    'id',
    'dataSet',
    'feature',
    'pathway',
    'pValue',
    'min',
    'max',
    'mean',
    'q1',
    'q2',
    'q3',
    'nTotal',
    'nMutants'
}


def build_rvpa_graphql_response(rare_variant_pathway_association):
    return {
        'id': get_value(rare_variant_pathway_association, 'id'),
        'dataSet': build_data_set_graphql_response(rare_variant_pathway_association),
        'feature': build_feature_graphql_response()(rare_variant_pathway_association),
        'pathway': get_value(rare_variant_pathway_association, 'pathway'),
        'pValue': get_value(rare_variant_pathway_association, 'p_value'),
        'min': get_value(rare_variant_pathway_association, 'min'),
        'max': get_value(rare_variant_pathway_association, 'max'),
        'mean': get_value(rare_variant_pathway_association, 'mean'),
        'q1': get_value(rare_variant_pathway_association, 'q1'),
        'q2': get_value(rare_variant_pathway_association, 'q2'),
        'q3': get_value(rare_variant_pathway_association, 'q3'),
        'nMutants': get_value(rare_variant_pathway_association, 'n_mutants'),
        'nTotal': get_value(rare_variant_pathway_association, 'n_total'),

    }


def build_rare_variant_pathway_association_request(
        requested, data_set_requested, feature_requested, distinct=False, paging=None, data_set=None, feature=None, pathway=None, max_p_value=None, min_p_value=None):
    """
    Builds a SQL request.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request
        2nd position - a set of the requested fields in the 'dataSet' node of the graphql request. If 'dataSet' is not requested, this will be an empty set.
        3rd position - a set of the requested fields in the 'feature' node of the graphql request. If 'feature' is not requested, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `distinct` - a boolean, indicates whether duplicate records should be filtered out
        `paging` - a dict containing pagination metadata
        `data_set` - a list of strings, data set names
        `pathway` - a list of strings, pathway names
        `feature` - a list of strings, feature names
        `max_p_value` - a float, a maximum P value
        `min_p_value` - a float, a minimum P value
    """
    sess = db.session

    rare_variant_pathway_association_1 = aliased(
        RareVariantPathwayAssociation, name='rvpa')
    feature_1 = aliased(Feature, name='f')
    data_set_1 = aliased(Dataset, name='ds')

    core_field_mapping = {
        'id': rare_variant_pathway_association_1.id.label('id'),
        'pathway': rare_variant_pathway_association_1.pathway.label('pathway'),
        'pValue': rare_variant_pathway_association_1.p_value.label('p_value'),
        'min': rare_variant_pathway_association_1.min.label('min'),
        'max': rare_variant_pathway_association_1.max.label('max'),
        'mean': rare_variant_pathway_association_1.mean.label('mean'),
        'q1': rare_variant_pathway_association_1.q1.label('q1'),
        'q2': rare_variant_pathway_association_1.q2.label('q2'),
        'q3': rare_variant_pathway_association_1.q3.label('q3'),
        'nTotal': rare_variant_pathway_association_1.n_total.label('n_total'),
        'nMutants': rare_variant_pathway_association_1.n_mutants.label('n_mutants')}
    data_set_core_field_mapping = {
        'display': data_set_1.display.label('data_set_display'),
        'name': data_set_1.name.label('data_set_name'),
        'type': data_set_1.data_set_type.label('data_set_type')
    }
    feature_core_field_mapping = {
        'display': feature_1.display.label('feature_display'),
        'name': feature_1.name.label('feature_name'),
        'order': feature_1.order.label('feature_order'),
        'unit': feature_1.unit.label('feature_unit'),
        'germlineModule': feature_1.germline_module.label('feature_germline_module'),
        'germlineCategory': feature_1.germline_category.label('feature_germline_category')
    }

    core = get_selected(requested, core_field_mapping)
    core |= get_selected(data_set_requested, data_set_core_field_mapping)
    core |= get_selected(feature_requested, feature_core_field_mapping)

    query = sess.query(*core)
    query = query.select_from(rare_variant_pathway_association_1)

    if pathway:
        query = query.filter(
            rare_variant_pathway_association_1.pathway.in_(pathway))

    if max_p_value or max_p_value == 0:
        query = query.filter(
            rare_variant_pathway_association_1.p_value <= max_p_value)

    if min_p_value or min_p_value == 0:
        query = query.filter(
            rare_variant_pathway_association_1.p_value >= min_p_value)

    if 'dataSet' in requested or data_set:
        is_outer = not bool(data_set)
        data_set_join_condition = build_join_condition(
            data_set_1.id, rare_variant_pathway_association_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
        query = query.join(data_set_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'feature' in requested or feature:
        is_outer = not bool(feature)
        data_set_join_condition = build_join_condition(
            feature_1.id, rare_variant_pathway_association_1.feature_id, filter_column=feature_1.name, filter_list=feature)
        query = query.join(feature_1, and_(
            *data_set_join_condition), isouter=is_outer)

    return get_pagination_queries(query, paging, distinct, cursor_field=rare_variant_pathway_association_1.id)
