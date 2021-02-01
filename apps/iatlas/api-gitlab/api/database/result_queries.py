from api import db
from api.db_models import CopyNumberResult, DriverResult, HeritabilityResult, GermlineGwasResult
from .database_helpers import build_option_args, build_query_args

accepted_cnr_option_args = ['data_set', 'feature', 'gene', 'tag']

accepted_cnr_query_args = ['id',
                           'direction',
                           'mean_normal',
                           'mean_cnv',
                           'p_value',
                           'log10_p_value',
                           't_stat',
                           'dataset_id',
                           'feature_id',
                           'gene_id',
                           'tag_id']

accepted_dr_option_args = accepted_cnr_option_args + ['mutation_code']

accepted_dr_query_args = ['id',
                          'p_value',
                          'fold_change',
                          'log10_p_value',
                          'log10_fold_change',
                          'n_wt',
                          'n_mut',
                          'dataset_id',
                          'feature_id',
                          'gene_id',
                          'mutation_code_id',
                          'tag_id']

accepted_hr_option_args = ['data_set', 'feature']

accepted_hr_query_args = ['dataset_id',
                          'id'
                          'feature_id',
                          'p_value',
                          'cluster',
                          'module',
                          'category',
                          'fdr',
                          'variance',
                          'se']

accepted_ggr_option_args = ['data_set', 'feature', 'snp']

accepted_ggr_query_args = ['dataset_id',
                           'id'
                           'feature_id',
                           'snp_id',
                           'p_value',
                           'maf',
                           'module',
                           'category']


def return_copy_number_result_query(*args):
    option_args = build_option_args(
        *args, accepted_args=accepted_cnr_option_args)
    query_args = build_query_args(
        CopyNumberResult, * args, accepted_args=accepted_cnr_query_args)
    query = db.session.query(*query_args)
    if option_args:
        query = db.session.query(CopyNumberResult).options(*option_args)
    return query


def return_driver_result_query(*args):
    option_args = build_option_args(
        *args, accepted_args=accepted_dr_option_args)
    query_args = build_query_args(
        DriverResult, *args, accepted_args=accepted_dr_query_args)
    query = db.session.query(*query_args)
    if option_args:
        query = db.session.query(DriverResult).options(*option_args)
    return query


def return_heritability_result_query(*args):
    option_args = build_option_args(
        *args, accepted_args=accepted_hr_option_args)
    query_args = build_query_args(
        HeritabilityResult, *args, accepted_args=accepted_hr_query_args)
    query = db.session.query(*query_args)
    if option_args:
        query = db.session.query(HeritabilityResult).options(*option_args)
    return query


def return_germline_gwas_result_query(*args):
    option_args = build_option_args(
        *args, accepted_args=accepted_ggr_option_args)
    query_args = build_query_args(
        GermlineGwasResult, *args, accepted_args=accepted_ggr_query_args)
    query = db.session.query(*query_args)
    if option_args:
        query = db.session.query(GermlineGwasResult).options(*option_args)
    return query
