from sqlalchemy import and_
from sqlalchemy.orm import aliased
from sqlalchemy.sql.expression import false
from api import db
from api.db_models import Cohort, Dataset, Tag, Sample, Feature, Gene, Mutation, MutationCode, CohortToSample, CohortToFeature, CohortToGene, CohortToMutation
from .general_resolvers import build_join_condition, get_selected, get_value
from .paging_utils import get_cursor, get_pagination_queries, Paging
from .data_set import build_data_set_graphql_response
from .tag import build_tag_graphql_response
from itertools import groupby

cohort_request_fields = {'id', 'name',
                         'dataSet', 'tag', 'clinical', 'samples', 'features', 'genes', 'mutations'}


def build_cohort_graphql_response(sample_dict={}, feature_dict={}, gene_dict={}, mutation_dict={}):

    def f(cohort):
        if not cohort:
            return None
        else:
            cohort_id = get_value(cohort, 'id')
            samples = sample_dict.get(cohort_id, []) if sample_dict else []
            features = feature_dict.get(cohort_id, []) if feature_dict else []
            genes = gene_dict.get(cohort_id, []) if gene_dict else []
            mutations = mutation_dict.get(
                cohort_id, []) if mutation_dict else []
            dict = {
                'id': cohort_id,
                'name': get_value(cohort, 'cohort_name') or get_value(cohort),
                'clinical': get_value(cohort, 'cohort_clinical') or get_value(cohort, 'clinical'),
                'dataSet': build_data_set_graphql_response(cohort),
                'tag': build_tag_graphql_response()(cohort),
                'samples': [{
                    'name': get_value(sample, 'sample_name'),
                    'clinical_value': get_value(sample, 'sample_clinical_value'),
                    'tag': {
                        'name': get_value(sample, 'sample_name'),
                        'characteristics': get_value(sample, 'sample_tag_characteristics'),
                        'color': get_value(sample, 'sample_tag_color'),
                        'longDisplay': get_value(sample, 'sample_tag_long_display'),
                        'shortDisplay': get_value(sample, 'sample_tag_short_display')
                    }
                } for sample in samples],
                'features': [{
                    'name': get_value(feature, 'feature_name'),
                    'display': get_value(feature, 'feature_display')
                } for feature in features],
                'genes': [{
                    'entrez': get_value(gene, 'entrez'),
                    'hgnc': get_value(gene, 'hgnc')
                } for gene in genes],
                'mutations': [{
                    'mutationCode': get_value(mutation, 'mutation_code'),
                    'gene': {
                        'entrez': get_value(mutation, 'entrez'),
                        'hgnc': get_value(mutation, 'hgnc')
                    }
                } for mutation in mutations],
            }
            return(dict)

    return f


def build_cohort_request(requested, data_set_requested, tag_requested, sample_requested, feature_requested, gene_requested, mutation_requested, name=None, data_set=None, tag=None, clinical=None, distinct=False, paging=None):
    """
    Builds a SQL request.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request
        2nd position - a set of the requested fields in the 'dataSet' node of the graphql request. If 'dataSet' is not requested, this will be an empty set.
        3rd position - a set of the requested fields in the 'tag' node of the graphql request. If 'tag' is not requested, this will be an empty set.
        4th position - a set of the requested fields in the 'sample' node of the graphql request. If 'feature' is not requested, this will be an empty set.
        5th position - a set of the requested fields in the 'feature' node of the graphql request. If 'feature' is not requested, this will be an empty set.
        6th position - a set of the requested fields in the 'gene' node of the graphql request. If 'feature' is not requested, this will be an empty set.
        7th position - a set of the requested fields in the 'mutation' node of the graphql request. If 'feature' is not requested, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `name` - a list of strings
        `data_set` - a list of strings, data set names
        `tag` - a list of strings, tag names
        `clinical` - a list of strings, clincial variable names
    """
    sess = db.session

    cohort_1 = aliased(Cohort, name='c')
    data_set_1 = aliased(Dataset, name='ds')
    tag_1 = aliased(Tag, name='t')
    cohort_to_sample_1 = aliased(CohortToFeature, name='cts')
    cohort_to_feature_1 = aliased(CohortToFeature, name='ctf')
    cohort_to_gene_1 = aliased(CohortToGene, name='ctg')
    cohort_to_mutation_1 = aliased(CohortToGene, name='ctm')

    core_field_mapping = {
        'id': cohort_1.id.label('id'),
        'name': cohort_1.name.label('cohort_name'),
        'clinical': cohort_1.clinical.label('clinical')
    }

    data_set_core_field_mapping = {'display': data_set_1.display.label('data_set_display'),
                                   'name': data_set_1.name.label('data_set_name'),
                                   'type': data_set_1.data_set_type.label('data_set_type')}
    tag_core_field_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                              'color': tag_1.color.label('color'),
                              'longDisplay': tag_1.long_display.label('tag_long_display'),
                              'name': tag_1.name.label('tag_name'),
                              'shortDisplay': tag_1.short_display.label('tag_short_display')}

    core = get_selected(requested, core_field_mapping)
    core |= get_selected(data_set_requested, data_set_core_field_mapping)
    core |= get_selected(tag_requested, tag_core_field_mapping)

    query = sess.query(*core)
    query = query.select_from(cohort_1)

    if name:
        query = query.filter(cohort_1.name.in_(name))

    if clinical:
        query = query.filter(cohort_1.clinical.in_(clinical))

    if 'dataSet' in requested or data_set:
        is_outer = not bool(data_set)
        data_set_join_condition = build_join_condition(
            data_set_1.id, cohort_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
        query = query.join(data_set_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'tag' in requested or tag:
        is_outer = not bool(tag)
        data_set_join_condition = build_join_condition(
            tag_1.id, cohort_1.tag_id, filter_column=tag_1.name, filter_list=tag)
        query = query.join(tag_1, and_(
            *data_set_join_condition), isouter=is_outer)

    return get_pagination_queries(query, paging, distinct, cursor_field=cohort_1.id)


def get_cohort_samples(requested, sample_requested, sample_tag_requested, name=None, data_set=None, tag=None, clinical=None):
    if 'samples' not in requested:
        return([])
    else:
        sess = db.session

        cohort_1 = aliased(Cohort, name='c')
        data_set_1 = aliased(Dataset, name='ds')
        tag_1 = aliased(Tag, name='t1')
        cohort_to_sample_1 = aliased(CohortToSample, name='cts')
        sample_1 = aliased(Sample, name='s')
        tag_2 = aliased(Tag, name='t2')

        core_field_mapping = {
            'id': cohort_1.id.label('id'),
        }

        sample_core_field_mapping = {
            'name': sample_1.name.label('sample_name'),
            'clinical_value': cohort_to_sample_1.clinical_value.label('sample_clinical_value')
        }

        sample_tag_core_field_mapping = {
            'characteristics': tag_2.characteristics.label('sample_tag_characteristics'),
            'color': tag_2.color.label('sample_tag_color'),
            'longDisplay': tag_2.long_display.label('sample_tag_long_display'),
            'name': tag_2.name.label('sample_tag_name'),
            'shortDisplay': tag_2.short_display.label('sample_tag_short_display')
        }

        core = get_selected(requested, core_field_mapping)
        core |= get_selected(sample_requested, sample_core_field_mapping)
        core |= get_selected(sample_tag_requested,
                             sample_tag_core_field_mapping)

        query = sess.query(*core)
        query = query.select_from(cohort_1)

        if name:
            query = query.filter(cohort_1.name.in_(name))

        if clinical:
            query = query.filter(cohort_1.clinical.in_(clinical))

        if data_set:
            data_set_join_condition = build_join_condition(
                data_set_1.id, cohort_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
            query = query.join(data_set_1, and_(
                *data_set_join_condition), isouter=false)

        if tag:
            tag_join_condition = build_join_condition(
                tag_1.id, cohort_1.tag_id, filter_column=tag_1.name, filter_list=tag)
            query = query.join(tag_1, and_(
                *tag_join_condition), isouter=false)

        cohort_to_sample_join_condition = build_join_condition(
            cohort_to_sample_1.cohort_id, cohort_1.id)

        query = query.join(cohort_to_sample_1, and_(
            *cohort_to_sample_join_condition), isouter=false)

        sample_join_condition = build_join_condition(
            sample_1.id, cohort_to_sample_1.sample_id)

        query = query.join(sample_1, and_(
            *sample_join_condition), isouter=false)

        if 'tag' in sample_requested:
            sample_tag_join_condition = build_join_condition(
                tag_2.id, cohort_to_sample_1.tag_id)
            query = query.join(tag_2, and_(
                *sample_tag_join_condition), isouter=false)

        samples = query.all()
        sample_dict = dict()
        for key, collection in groupby(samples, key=lambda s: s.id):
            sample_dict[key] = sample_dict.get(key, []) + list(collection)
        return(sample_dict)


def get_cohort_features(requested, feature_requested, name=None, data_set=None, tag=None, clinical=None):
    if 'features' not in requested:
        return([])
    else:
        sess = db.session

        cohort_1 = aliased(Cohort, name='c')
        data_set_1 = aliased(Dataset, name='ds')
        tag_1 = aliased(Tag, name='t')
        cohort_to_feature_1 = aliased(CohortToFeature, name='ctf')
        feature_1 = aliased(Feature, name='f')

        core_field_mapping = {
            'id': cohort_1.id.label('id'),
            'name': cohort_1.name.label('cohort_name'),
            'clinical': cohort_1.clinical.label('clinical')
        }

        feature_core_field_mapping = {
            'feature_id': feature_1.id.label('feature_id'),
            'display': feature_1.display.label('feature_display'),
            'name': feature_1.name.label('feature_name'),
        }

        core = get_selected(requested, core_field_mapping)
        core |= get_selected(feature_requested, feature_core_field_mapping)

        query = sess.query(*core)
        query = query.select_from(cohort_1)

        if name:
            query = query.filter(cohort_1.name.in_(name))

        if clinical:
            query = query.filter(cohort_1.clinical.in_(clinical))

        if data_set:
            data_set_join_condition = build_join_condition(
                data_set_1.id, cohort_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
            query = query.join(data_set_1, and_(
                *data_set_join_condition), isouter=false)

        if tag:
            tag_join_condition = build_join_condition(
                tag_1.id, cohort_1.tag_id, filter_column=tag_1.name, filter_list=tag)
            query = query.join(tag_1, and_(
                *tag_join_condition), isouter=false)

        cohort_to_feature_join_condition = build_join_condition(
            cohort_to_feature_1.cohort_id, cohort_1.id)

        query = query.join(cohort_to_feature_1, and_(
            *cohort_to_feature_join_condition), isouter=false)

        feature_join_condition = build_join_condition(
            feature_1.id, cohort_to_feature_1.feature_id)

        query = query.join(feature_1, and_(
            *feature_join_condition), isouter=false)

        features = query.all()
        feature_dict = dict()
        for key, collection in groupby(features, key=lambda f: f.id):
            feature_dict[key] = feature_dict.get(key, []) + list(collection)
        return(feature_dict)


def get_cohort_genes(requested, gene_requested, name=None, data_set=None, tag=None, clinical=None):
    if 'genes' not in requested:
        return([])
    else:
        sess = db.session

        cohort_1 = aliased(Cohort, name='c')
        data_set_1 = aliased(Dataset, name='ds')
        tag_1 = aliased(Tag, name='t')
        cohort_to_gene_1 = aliased(CohortToGene, name='ctg')
        gene_1 = aliased(Gene, name='g')

        core_field_mapping = {
            'id': cohort_1.id.label('id'),
            'name': cohort_1.name.label('cohort_name'),
            'clinical': cohort_1.clinical.label('clinical')
        }

        gene_core_field_mapping = {
            'gene_id': gene_1.id.label('gene_id'),
            'hgnc': gene_1.hgnc.label('hgnc'),
            'entrez': gene_1.entrez.label('entrez'),
        }

        core = get_selected(requested, core_field_mapping)
        core |= get_selected(gene_requested, gene_core_field_mapping)

        query = sess.query(*core)
        query = query.select_from(cohort_1)

        if name:
            query = query.filter(cohort_1.name.in_(name))

        if clinical:
            query = query.filter(cohort_1.clinical.in_(clinical))

        if data_set:
            data_set_join_condition = build_join_condition(
                data_set_1.id, cohort_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
            query = query.join(data_set_1, and_(
                *data_set_join_condition), isouter=false)

        if tag:
            tag_join_condition = build_join_condition(
                tag_1.id, cohort_1.tag_id, filter_column=tag_1.name, filter_list=tag)
            query = query.join(tag_1, and_(
                *tag_join_condition), isouter=false)

        cohort_to_gene_join_condition = build_join_condition(
            cohort_to_gene_1.cohort_id, cohort_1.id)

        query = query.join(cohort_to_gene_1, and_(
            *cohort_to_gene_join_condition), isouter=false)

        gene_join_condition = build_join_condition(
            gene_1.id, cohort_to_gene_1.gene_id)

        query = query.join(gene_1, and_(
            *gene_join_condition), isouter=false)

        genes = query.all()
        gene_dict = dict()
        for key, collection in groupby(genes, key=lambda g: g.id):
            gene_dict[key] = gene_dict.get(key, []) + list(collection)
        return(gene_dict)


def get_cohort_mutations(requested, mutation_requested, mutation_gene_requested, name=None, data_set=None, tag=None, clinical=None):

    if 'mutations' not in requested:
        return([])
    else:
        sess = db.session

        cohort_1 = aliased(Cohort, name='c')
        data_set_1 = aliased(Dataset, name='ds')
        tag_1 = aliased(Tag, name='t')
        cohort_to_mutation_1 = aliased(CohortToMutation, name='ctm')
        mutation_1 = aliased(Mutation, name='m')
        gene_1 = aliased(Gene, name='g')
        mutation_code_1 = aliased(MutationCode, name='mc')

        core_field_mapping = {
            'id': cohort_1.id.label('id'),
        }

        mutation_core_field_mapping = {
            'mutationCode': mutation_code_1.code.label('mutation_code')
        }

        mutation_gene_core_field_mapping = {
            'hgnc': gene_1.hgnc.label('hgnc'),
            'entrez': gene_1.entrez.label('entrez'),
        }

        core = get_selected(requested, core_field_mapping)
        core |= get_selected(mutation_requested, mutation_core_field_mapping)
        core |= get_selected(mutation_gene_requested,
                             mutation_gene_core_field_mapping)

        query = sess.query(*core)
        query = query.select_from(cohort_1)

        if name:
            query = query.filter(cohort_1.name.in_(name))

        if clinical:
            query = query.filter(cohort_1.clinical.in_(clinical))

        if data_set:
            data_set_join_condition = build_join_condition(
                data_set_1.id, cohort_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
            query = query.join(data_set_1, and_(
                *data_set_join_condition), isouter=false)

        if tag:
            tag_join_condition = build_join_condition(
                tag_1.id, cohort_1.tag_id, filter_column=tag_1.name, filter_list=tag)
            query = query.join(tag_1, and_(
                *tag_join_condition), isouter=false)

        cohort_to_mutation_join_condition = build_join_condition(
            cohort_to_mutation_1.cohort_id, cohort_1.id)

        query = query.join(cohort_to_mutation_1, and_(
            *cohort_to_mutation_join_condition), isouter=false)

        mutation_join_condition = build_join_condition(
            mutation_1.id, cohort_to_mutation_1.mutation_id)

        query = query.join(mutation_1, and_(
            *mutation_join_condition), isouter=false)

        if 'mutationCode' in mutation_requested:
            mutation_code_join_condition = build_join_condition(
                mutation_1.mutation_code_id, mutation_code_1.id)

            query = query.join(mutation_code_1, and_(
                *mutation_code_join_condition), isouter=false)

        if 'gene' in mutation_requested:
            mutation_gene_join_condition = build_join_condition(
                mutation_1.gene_id, gene_1.id)

            query = query.join(gene_1, and_(
                *mutation_gene_join_condition), isouter=false)

        mutations = query.all()
        mutation_dict = dict()
        for key, collection in groupby(mutations, key=lambda m: m.id):
            mutation_dict[key] = mutation_dict.get(key, []) + list(collection)
        return(mutation_dict)
