from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Dataset, Colocalization, Feature, Gene, Snp
from .general_resolvers import build_join_condition, get_selected, get_value
from .data_set import build_data_set_graphql_response
from .feature import build_feature_graphql_response
from .gene import build_gene_graphql_response
from .snp import build_snp_graphql_response
from .paging_utils import get_pagination_queries

colocalization_request_fields = {
    'id',
    'dataSet',
    'colocDataSet',
    'feature',
    'gene',
    'snp',
    'qtlType',
    'eCaviarPP',
    'plotType',
    'tissue',
    'spliceLoc',
    'plotLink'
}


def build_coloc_graphql_response(colocalization):
    return {
        'id': get_value(colocalization, 'id'),
        'dataSet': build_data_set_graphql_response(colocalization),
        'colocDataSet': build_data_set_graphql_response(colocalization, prefix='coloc_data_set_'),
        'feature': build_feature_graphql_response()(colocalization),
        'gene': build_gene_graphql_response()(colocalization),
        'snp': build_snp_graphql_response(colocalization),
        'qtlType': get_value(colocalization, 'qtl_type'),
        'eCaviarPP': get_value(colocalization, 'ecaviar_pp'),
        'plotType': get_value(colocalization, 'plot_type'),
        'tissue': get_value(colocalization, 'tissue'),
        'spliceLoc': get_value(colocalization, 'splice_loc'),
        'plotLink': get_value(colocalization, 'plot_link')
    }


def build_colocalization_request(
        requested, data_set_requested, coloc_data_set_requested, feature_requested, gene_requested, snp_requested, distinct=False, paging=None, data_set=None, coloc_data_set=None, feature=None, entrez=None, snp=None, qtl_type=None, ecaviar_pp=None, plot_type=None):
    """
    Builds a SQL request.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request
        2nd position - a set of the requested fields in the 'dataSet' node of the graphql request. If 'dataSet' is not requested, this will be an empty set.
        3rd position - a set of the requested fields in the 'colocDataSet' node of the graphql request. If 'colocDataSet' is not requested, this will be an empty set.
        4th position - a set of the requested fields in the 'feature' node of the graphql request. If 'feature' is not requested, this will be an empty set.
        5th position - a set of the requested fields in the 'gene' node of the graphql request. If 'gene' is not requested, this will be an empty set.
        6th position - a set of the requested fields in the 'snp' node of the graphql request. If 'snp' is not requested, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `distinct` - a boolean, indicates whether duplicate records should be filtered out
        `paging` - a dict containing pagination metadata
        `data_set` - a list of strings, data set names
        `coloc_data_set` - a list of strings, data set names
        `feature` - a list of strings, feature names
        `entrez` - a list of ints, entrez ids
        `snp` - a list of strings, snp names
        `qtl_type` - a string, (either 'sQTL' or 'eQTL')
        `ecaviar_pp` - a string, (either 'C1' or 'C2')
        `plot_type` - a string, (either '3 Level Plot' or 'Expanded Region')
    """
    sess = db.session

    colocalization_1 = aliased(Colocalization, name='coloc')
    data_set_1 = aliased(Dataset, name='ds')
    coloc_data_set_1 = aliased(Dataset, name='cds')
    feature_1 = aliased(Feature, name='f')
    gene_1 = aliased(Gene, name='g')
    snp_1 = aliased(Snp, name='s')

    core_field_mapping = {
        'id': colocalization_1.id.label('id'),
        'qtlType': colocalization_1.qtl_type.label('qtl_type'),
        'eCaviarPP': colocalization_1.ecaviar_pp.label('ecaviar_pp'),
        'plotType': colocalization_1.plot_type.label('plot_type'),
        'tissue': colocalization_1.tissue.label('tissue'),
        'spliceLoc': colocalization_1.splice_loc.label('splice_loc'),
        'plotLink': colocalization_1.plot_link.label('plot_link')
    }
    data_set_core_field_mapping = {'display': data_set_1.display.label('data_set_display'),
                                   'name': data_set_1.name.label('data_set_name'),
                                   'type': data_set_1.data_set_type.label('data_set_type')}
    coloc_data_set_core_field_mapping = {'display': coloc_data_set_1.display.label('coloc_data_set_display'),
                                         'name': coloc_data_set_1.name.label('coloc_data_set_name'),
                                         'type': coloc_data_set_1.data_set_type.label('coloc_data_set_type')}
    feature_core_field_mapping = {'display': feature_1.display.label('feature_display'),
                                  'name': feature_1.name.label('feature_name'),
                                  'order': feature_1.order.label('feature_order'),
                                  'unit': feature_1.unit.label('feature_unit'),
                                  'germlineCategory': feature_1.germline_category.label('feature_germline_category'),
                                  'germlineModule': feature_1.germline_module.label('feature_germline_module')}
    gene_core_field_mapping = {'entrez': gene_1.entrez.label('entrez'),
                               'hgnc': gene_1.hgnc.label('hgnc')}
    snp_core_field_mapping = {'rsid': snp_1.rsid.label('snp_rsid'),
                              'name': snp_1.name.label('snp_name'),
                              'bp': snp_1.bp.label('snp_bp'),
                              'chr': snp_1.chr.label('snp_chr')}

    core = get_selected(requested, core_field_mapping)
    core |= get_selected(data_set_requested, data_set_core_field_mapping)
    core |= get_selected(coloc_data_set_requested,
                         coloc_data_set_core_field_mapping)
    core |= get_selected(feature_requested, feature_core_field_mapping)
    core |= get_selected(gene_requested, gene_core_field_mapping)
    core |= get_selected(snp_requested, snp_core_field_mapping)

    query = sess.query(*core)
    query = query.select_from(colocalization_1)

    if qtl_type:
        query = query.filter(colocalization_1.qtl_type == qtl_type)

    if ecaviar_pp:
        query = query.filter(colocalization_1.ecaviar_pp == ecaviar_pp)

    if plot_type:
        query = query.filter(colocalization_1.plot_type == plot_type)

    if 'dataSet' in requested or data_set:
        is_outer = not bool(data_set)
        data_set_join_condition = build_join_condition(
            data_set_1.id, colocalization_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
        query = query.join(data_set_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'colocDataSet' in requested or coloc_data_set:
        is_outer = not bool(data_set)
        data_set_join_condition = build_join_condition(
            coloc_data_set_1.id, colocalization_1.coloc_dataset_id, filter_column=coloc_data_set_1.name, filter_list=coloc_data_set)
        query = query.join(coloc_data_set_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'feature' in requested or feature:
        is_outer = not bool(feature)
        feature_join_condition = build_join_condition(
            feature_1.id, colocalization_1.feature_id, filter_column=feature_1.name, filter_list=feature)
        query = query.join(feature_1, and_(
            *feature_join_condition), isouter=is_outer)

    if 'gene' in requested or entrez:
        is_outer = not bool(entrez)
        gene_join_condition = build_join_condition(
            gene_1.id, colocalization_1.gene_id, filter_column=gene_1.entrez, filter_list=entrez)
        query = query.join(gene_1, and_(
            *gene_join_condition), isouter=is_outer)

    if 'snp' in requested or snp:
        is_outer = not bool(snp)
        snp_join_condition = build_join_condition(
            snp_1.id, colocalization_1.snp_id, filter_column=snp_1.name, filter_list=snp)
        query = query.join(snp_1, and_(
            *snp_join_condition), isouter=is_outer)

    return get_pagination_queries(query, paging, distinct, cursor_field=colocalization_1.id)
