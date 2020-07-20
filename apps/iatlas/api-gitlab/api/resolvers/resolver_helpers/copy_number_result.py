from sqlalchemy import orm
from api import db
from api.db_models import CopyNumberResult, Dataset, Feature, Gene, Tag
from .general_resolvers import build_option_args, get_selection_set


def build_copy_number_result_request(_obj, info, data_set=None, direction=None, entrez=None,
                                     feature=None, max_p_value=None, max_log10_p_value=None,
                                     min_log10_p_value=None, min_mean_cnv=None,
                                     min_mean_normal=None, min_p_value=None, min_t_stat=None,
                                     tag=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)

    copy_number_result_1 = orm.aliased(CopyNumberResult, name='dcnr')
    data_set_1 = orm.aliased(Dataset, name='ds')
    feature_1 = orm.aliased(Feature, name='f')
    gene_1 = orm.aliased(Gene, name='g')
    tag_1 = orm.aliased(Tag, name='t')

    core_field_mapping = {'direction': copy_number_result_1.direction.label('direction'),
                          'meanNormal': copy_number_result_1.mean_normal.label('mean_normal'),
                          'meanCnv': copy_number_result_1.mean_cnv.label('mean_cnv'),
                          'pValue': copy_number_result_1.p_value.label('p_value'),
                          'tStat': copy_number_result_1.t_stat.label('t_stat')}

    related_field_mapping = {'dataSet': 'data_set',
                             'feature': 'feature',
                             'gene': 'gene',
                             'tag': 'tag'}

    core = build_option_args(selection_set, core_field_mapping)
    relations = build_option_args(selection_set, related_field_mapping)
    option_args = []
    append_to_options_args = option_args.append

    query = sess.query(copy_number_result_1)

    if 'data_set' in relations or data_set:
        query = query.join(
            (data_set_1, copy_number_result_1.data_set), isouter=True)
        append_to_options_args(orm.contains_eager(
            copy_number_result_1.data_set.of_type(data_set_1)))

    if 'feature' in relations or feature:
        query = query.join(
            (feature_1, copy_number_result_1.feature), isouter=True)
        append_to_options_args(orm.contains_eager(
            copy_number_result_1.feature.of_type(feature_1)))

    if 'gene' in relations or entrez:
        query = query.join(
            (gene_1, copy_number_result_1.gene), isouter=True)
        append_to_options_args(orm.contains_eager(
            copy_number_result_1.gene.of_type(gene_1)))

    if 'tag' in relations or tag:
        query = query.join(
            (tag_1, copy_number_result_1.tag), isouter=True)
        append_to_options_args(orm.contains_eager(
            copy_number_result_1.tag.of_type(tag_1)))

    if option_args:
        query = query.options(*option_args)
    else:
        query = sess.query(*core)

    if data_set:
        query = query.filter(data_set_1.name.in_(data_set))

    if entrez:
        query = query.filter(gene_1.entrez.in_(entrez))

    if feature:
        query = query.filter(feature_1.name.in_(feature))

    if tag:
        query = query.filter(tag_1.name.in_(tag))

    if direction:
        query = query.filter(copy_number_result_1.direction == direction)

    if max_p_value or max_p_value == 0:
        query = query.filter(copy_number_result_1.p_value <= max_p_value)

    if (max_log10_p_value or max_log10_p_value == 0) and (not max_p_value or max_p_value == 0):
        query = query.filter(
            copy_number_result_1.log10_p_value <= max_log10_p_value)

    if min_log10_p_value or min_log10_p_value == 0:
        query = query.filter(
            copy_number_result_1.log10_p_value >= min_log10_p_value)

    if min_mean_cnv or min_mean_cnv == 0:
        query = query.filter(copy_number_result_1.mean_cnv >= min_mean_cnv)

    if min_mean_normal or min_mean_normal == 0:
        query = query.filter(
            copy_number_result_1.mean_normal >= min_mean_normal)

    if min_p_value or min_p_value == 0:
        query = query.filter(copy_number_result_1.p_value >= min_p_value)

    if min_t_stat or min_t_stat == 0:
        query = query.filter(copy_number_result_1.t_stat >= min_t_stat)

    return query


def request_copy_number_results(_obj, info, data_set=None, direction=None, entrez=None,
                                feature=None, max_p_value=None, max_log10_p_value=None,
                                min_log10_p_value=None, min_mean_cnv=None,
                                min_mean_normal=None, min_p_value=None, min_t_stat=None,
                                tag=None):
    query = build_copy_number_result_request(_obj, info, data_set=data_set, direction=direction, entrez=entrez,
                                             feature=feature, max_p_value=max_p_value, max_log10_p_value=max_log10_p_value,
                                             min_log10_p_value=min_log10_p_value, min_mean_cnv=min_mean_cnv,
                                             min_mean_normal=min_mean_normal, min_p_value=min_p_value, min_t_stat=min_t_stat,
                                             tag=tag)
    return query.distinct().all()
