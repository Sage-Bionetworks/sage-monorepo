from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Dataset, Feature, Snp, GermlineGwasResult
from .general_resolvers import build_join_condition, get_selected, get_value
from .data_set import build_data_set_graphql_response
from .feature import build_feature_graphql_response
from .snp import build_snp_graphql_response
from .paging_utils import get_pagination_queries

germline_gwas_result_request_fields = {'dataSet',
                                       'id',
                                       'feature',
                                       'snp',
                                       'pValue',
                                       'maf'}


def build_ggr_graphql_response(germline_gwas_result):
    return {
        'id': get_value(germline_gwas_result, 'id'),
        'pValue': get_value(germline_gwas_result, 'p_value'),
        'dataSet': build_data_set_graphql_response(germline_gwas_result),
        'feature': build_feature_graphql_response()(germline_gwas_result),
        'snp': build_snp_graphql_response(germline_gwas_result),
        'maf': get_value(germline_gwas_result, 'maf')
    }


def build_germline_gwas_result_request(
        requested, data_set_requested, feature_requested, snp_requested, data_set=None, distinct=False, feature=None, snp=None, max_p_value=None, min_p_value=None, paging=None):
    """
    Builds a SQL request.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request
        2nd position - a set of the requested fields in the 'dataset' node of the graphql request. If 'dataset' is not requested, this will be an empty set.
        3rd position - a set of the requested fields in the 'feature' node of the graphql request. If 'feature' is not requested, this will be an empty set.
        4th position - a set of the requested fields in the 'snp' node of the graphql request. If 'snp' is not requested, this will be an empty set.


    All keyword arguments are optional. Keyword arguments are:
        `dat_set` - a list of strings, data set names
        `distinct` - a boolean, indicates whether duplicate records should be filtered out
        `feature` - a list of strings, feature names
        `snp` - a list of strings
        `max_p_value` - a float, a maximum P value
        `min_p_value` - a float, a minimum P value
        `paging` - a dict containing pagination metadata
    """
    sess = db.session

    germline_gwas_result_1 = aliased(GermlineGwasResult, name='ggr')
    feature_1 = aliased(Feature, name='f')
    data_set_1 = aliased(Dataset, name='ds')
    snp_1 = aliased(Snp, name='snp')

    core_field_mapping = {
        'id': germline_gwas_result_1.id.label('id'),
        'pValue': germline_gwas_result_1.p_value.label('p_value'),
        'maf': germline_gwas_result_1.maf.label('maf')
    }
    data_set_core_field_mapping = {'display': data_set_1.display.label('data_set_display'),
                                   'name': data_set_1.name.label('data_set_name'),
                                   'type': data_set_1.data_set_type.label('data_set_type')}
    feature_core_field_mapping = {'display': feature_1.display.label('feature_display'),
                                  'name': feature_1.name.label('feature_name'),
                                  'order': feature_1.order.label('feature_order'),
                                  'unit': feature_1.unit.label('feature_unit'),
                                  'germlineCategory': feature_1.germline_category.label('feature_germline_category'),
                                  'germlineModule': feature_1.germline_module.label('feature_germline_module')}
    snp_core_field_mapping = {'rsid': snp_1.rsid.label('snp_rsid'),
                              'name': snp_1.name.label('snp_name'),
                              'bp': snp_1.bp.label('snp_bp'),
                              'chr': snp_1.chr.label('snp_chr')}

    core = get_selected(requested, core_field_mapping)
    core |= get_selected(data_set_requested, data_set_core_field_mapping)
    core |= get_selected(feature_requested, feature_core_field_mapping)
    core |= get_selected(snp_requested, snp_core_field_mapping)

    query = sess.query(*core)
    query = query.select_from(germline_gwas_result_1)

    if max_p_value or max_p_value == 0:
        query = query.filter(
            germline_gwas_result_1.p_value <= float(max_p_value))

    if min_p_value or min_p_value == 0:
        query = query.filter(
            germline_gwas_result_1.p_value >= float(min_p_value))

    if 'dataSet' in requested or data_set:
        is_outer = not bool(data_set)
        data_set_join_condition = build_join_condition(
            data_set_1.id, germline_gwas_result_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
        query = query.join(data_set_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'feature' in requested or feature:
        is_outer = not bool(feature)
        feature_join_condition = build_join_condition(
            feature_1.id, germline_gwas_result_1.feature_id, filter_column=feature_1.name, filter_list=feature)
        query = query.join(feature_1, and_(
            *feature_join_condition), isouter=is_outer)

    if 'snp' in requested or snp:
        is_outer = not bool(snp)
        snp_join_condition = build_join_condition(
            snp_1.id, germline_gwas_result_1.snp_id, filter_column=snp_1.name, filter_list=snp)
        query = query.join(snp_1, and_(
            *snp_join_condition), isouter=is_outer)

    return get_pagination_queries(query, paging, distinct, cursor_field=germline_gwas_result_1.id)
