from sqlalchemy import and_
from sqlalchemy.orm import aliased
from sqlalchemy.sql.expression import false
from api import db
from api.db_models import Cohort, Dataset, Tag, Sample, Feature, Gene, Mutation, MutationCode, CohortToSample, CohortToFeature, CohortToGene, CohortToMutation
from .general_resolvers import build_join_condition, get_selected, get_value
from .paging_utils import get_pagination_queries
from .data_set import build_data_set_graphql_response
from .feature import build_feature_graphql_response
from .gene import build_gene_graphql_response
from .mutation import build_mutation_graphql_response
from .sample import build_cohort_sample_graphql_response
from .tag import build_simple_tag_graphql_response
from itertools import groupby

cohort_request_fields = {'id', 'name',
                         'dataSet', 'tag', 'samples', 'features', 'genes', 'mutations'}


def build_cohort_graphql_response(sample_dict={}, feature_dict={}, gene_dict={}, mutation_dict={}):
    def f(cohort):
        if not cohort:
            return None
        else:
            cohort_id = get_value(cohort, 'cohort_id')
            samples = sample_dict.get(cohort_id, []) if sample_dict else []
            features = feature_dict.get(cohort_id, []) if feature_dict else []
            genes = gene_dict.get(cohort_id, []) if gene_dict else []
            mutations = mutation_dict.get(
                cohort_id, []) if mutation_dict else []
            dict = {
                'id': cohort_id,
                'name': get_value(cohort, 'cohort_name'),
                'dataSet': build_data_set_graphql_response(cohort),
                'tag': build_simple_tag_graphql_response(
                    cohort) if get_value(cohort, 'tag_name') else None,
                'samples': map(build_cohort_sample_graphql_response, samples),
                'features': map(build_feature_graphql_response(), features),
                'genes': map(build_gene_graphql_response(), genes),
                'mutations': map(build_mutation_graphql_response(), mutations)
            }
            return(dict)

    return f


def build_cohort_request(requested, data_set_requested, tag_requested, cohort=None, data_set=None, tag=None, distinct=False, paging=None):
    """
    Builds a SQL request.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request
        2nd position - a set of the requested fields in the 'dataSet' node of the graphql request. If 'dataSet' is not requested, this will be an empty set.
        3rd position - a set of the requested fields in the 'tag' node of the graphql request. If 'tag' is not requested, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `cohort` - a list of strings, cohorts
        `data_set` - a list of strings, data set names
        `tag` - a list of strings, tag names
    """
    sess = db.session

    cohort_1 = aliased(Cohort, name='c')
    data_set_1 = aliased(Dataset, name='ds')
    tag_1 = aliased(Tag, name='t')

    core_field_mapping = {
        'id': cohort_1.id.label('cohort_id'),
        'name': cohort_1.name.label('cohort_name'),
    }

    data_set_core_field_mapping = {
        'display': data_set_1.display.label('data_set_display'),
        'name': data_set_1.name.label('data_set_name'),
        'type': data_set_1.data_set_type.label('data_set_type')
    }

    tag_core_field_mapping = {
        'characteristics': tag_1.characteristics.label('tag_characteristics'),
        'color': tag_1.color.label('tag_color'),
        'longDisplay': tag_1.long_display.label('tag_long_display'),
        'name': tag_1.name.label('tag_name'),
        'shortDisplay': tag_1.short_display.label('tag_short_display')
    }

    core = get_selected(requested, core_field_mapping)
    core |= {cohort_1.id.label('cohort_id')}
    core |= get_selected(data_set_requested, data_set_core_field_mapping)
    core |= get_selected(tag_requested, tag_core_field_mapping)

    query = sess.query(*core)
    query = query.select_from(cohort_1)

    if cohort:
        query = query.filter(cohort_1.name.in_(cohort))

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


def get_cohort_samples(requested, sample_requested, sample_tag_requested, cohort=None, data_set=None, tag=None):
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
            'id': cohort_1.id.label('cohort_id'),
        }

        sample_core_field_mapping = {
            'name': sample_1.name.label('sample_name'),
        }

        sample_tag_core_field_mapping = {
            'characteristics': tag_2.characteristics.label('tag_characteristics'),
            'color': tag_2.color.label('tag_color'),
            'longDisplay': tag_2.long_display.label('tag_long_display'),
            'name': tag_2.name.label('tag_name'),
            'shortDisplay': tag_2.short_display.label('tag_short_display')
        }

        core = get_selected(requested, core_field_mapping)
        core |= get_selected(sample_requested, sample_core_field_mapping)
        core |= get_selected(sample_tag_requested,
                             sample_tag_core_field_mapping)

        query = sess.query(*core)
        query = query.select_from(cohort_1)

        if cohort:
            query = query.filter(cohort_1.name.in_(cohort))

        if data_set:
            data_set_join_condition = build_join_condition(
                data_set_1.id, cohort_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
            query = query.join(data_set_1, and_(
                *data_set_join_condition), isouter=False)

        if tag:
            tag_join_condition = build_join_condition(
                tag_1.id, cohort_1.tag_id, filter_column=tag_1.name, filter_list=tag)
            query = query.join(tag_1, and_(
                *tag_join_condition), isouter=False)

        cohort_to_sample_join_condition = build_join_condition(
            cohort_to_sample_1.cohort_id, cohort_1.id)

        query = query.join(cohort_to_sample_1, and_(
            *cohort_to_sample_join_condition), isouter=False)

        sample_join_condition = build_join_condition(
            sample_1.id, cohort_to_sample_1.sample_id)

        query = query.join(sample_1, and_(
            *sample_join_condition), isouter=False)

        if 'tag' in sample_requested:
            sample_tag_join_condition = build_join_condition(
                tag_2.id, cohort_to_sample_1.tag_id)
            query = query.join(tag_2, and_(
                *sample_tag_join_condition), isouter=True)

        samples = query.all()
        sample_dict = dict()
        for key, collection in groupby(samples, key=lambda s: s.cohort_id):
            sample_dict[key] = sample_dict.get(key, []) + list(collection)
        return(sample_dict)


def get_cohort_features(requested, feature_requested, cohort=None, data_set=None, tag=None):
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
            'id': cohort_1.id.label('cohort_id'),
            'name': cohort_1.name.label('cohort_name'),
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

        if cohort:
            query = query.filter(cohort_1.name.in_(cohort))

        if data_set:
            data_set_join_condition = build_join_condition(
                data_set_1.id, cohort_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
            query = query.join(data_set_1, and_(
                *data_set_join_condition), isouter=False)

        if tag:
            tag_join_condition = build_join_condition(
                tag_1.id, cohort_1.tag_id, filter_column=tag_1.name, filter_list=tag)
            query = query.join(tag_1, and_(
                *tag_join_condition), isouter=False)

        cohort_to_feature_join_condition = build_join_condition(
            cohort_to_feature_1.cohort_id, cohort_1.id)

        query = query.join(cohort_to_feature_1, and_(
            *cohort_to_feature_join_condition), isouter=False)

        feature_join_condition = build_join_condition(
            feature_1.id, cohort_to_feature_1.feature_id)

        query = query.join(feature_1, and_(
            *feature_join_condition), isouter=False)

        import logging
        logger = logging.getLogger('cohort resolver')
        logger.info(query)

        features = query.all()
        feature_dict = dict()
        for key, collection in groupby(features, key=lambda f: f.cohort_id):
            feature_dict[key] = feature_dict.get(key, []) + list(collection)
        return(feature_dict)


def get_cohort_genes(requested, gene_requested, cohort=None, data_set=None, tag=None):
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
            'id': cohort_1.id.label('cohort_id'),
            'name': cohort_1.name.label('cohort_name'),
        }

        gene_core_field_mapping = {
            'gene_id': gene_1.id.label('gene_id'),
            'hgnc': gene_1.hgnc.label('gene_hgnc'),
            'entrez': gene_1.entrez.label('gene_entrez'),
        }

        core = get_selected(requested, core_field_mapping)
        core |= get_selected(gene_requested, gene_core_field_mapping)

        query = sess.query(*core)
        query = query.select_from(cohort_1)

        if cohort:
            query = query.filter(cohort_1.name.in_(cohort))

        if data_set:
            data_set_join_condition = build_join_condition(
                data_set_1.id, cohort_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
            query = query.join(data_set_1, and_(
                *data_set_join_condition), isouter=False)

        if tag:
            tag_join_condition = build_join_condition(
                tag_1.id, cohort_1.tag_id, filter_column=tag_1.name, filter_list=tag)
            query = query.join(tag_1, and_(
                *tag_join_condition), isouter=False)

        cohort_to_gene_join_condition = build_join_condition(
            cohort_to_gene_1.cohort_id, cohort_1.id)

        query = query.join(cohort_to_gene_1, and_(
            *cohort_to_gene_join_condition), isouter=False)

        gene_join_condition = build_join_condition(
            gene_1.id, cohort_to_gene_1.gene_id)

        query = query.join(gene_1, and_(
            *gene_join_condition), isouter=False)

        genes = query.all()
        gene_dict = dict()
        for key, collection in groupby(genes, key=lambda g: g.cohort_id):
            gene_dict[key] = gene_dict.get(key, []) + list(collection)
        return(gene_dict)


def get_cohort_mutations(requested, mutation_requested, mutation_gene_requested, cohort=None, data_set=None, tag=None):

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
            'id': cohort_1.id.label('cohort_id'),
        }

        mutation_core_field_mapping = {
            'mutationCode': mutation_code_1.code.label('mutation_code')
        }

        mutation_gene_core_field_mapping = {
            'hgnc': gene_1.hgnc.label('gene_hgnc'),
            'entrez': gene_1.entrez.label('gene_entrez'),
        }

        core = get_selected(requested, core_field_mapping)
        core |= get_selected(mutation_requested, mutation_core_field_mapping)
        core |= get_selected(mutation_gene_requested,
                             mutation_gene_core_field_mapping)

        query = sess.query(*core)
        query = query.select_from(cohort_1)

        if cohort:
            query = query.filter(cohort_1.name.in_(cohort))

        if data_set:
            data_set_join_condition = build_join_condition(
                data_set_1.id, cohort_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
            query = query.join(data_set_1, and_(
                *data_set_join_condition), isouter=False)

        if tag:
            tag_join_condition = build_join_condition(
                tag_1.id, cohort_1.tag_id, filter_column=tag_1.name, filter_list=tag)
            query = query.join(tag_1, and_(
                *tag_join_condition), isouter=False)

        cohort_to_mutation_join_condition = build_join_condition(
            cohort_to_mutation_1.cohort_id, cohort_1.id)

        query = query.join(cohort_to_mutation_1, and_(
            *cohort_to_mutation_join_condition), isouter=False)

        mutation_join_condition = build_join_condition(
            mutation_1.id, cohort_to_mutation_1.mutation_id)

        query = query.join(mutation_1, and_(
            *mutation_join_condition), isouter=False)

        if 'mutationCode' in mutation_requested:
            mutation_code_join_condition = build_join_condition(
                mutation_1.mutation_code_id, mutation_code_1.id)

            query = query.join(mutation_code_1, and_(
                *mutation_code_join_condition), isouter=False)

        if 'gene' in mutation_requested:
            mutation_gene_join_condition = build_join_condition(
                mutation_1.gene_id, gene_1.id)

            query = query.join(gene_1, and_(
                *mutation_gene_join_condition), isouter=False)

        mutations = query.all()
        mutation_dict = dict()
        for key, collection in groupby(mutations, key=lambda m: m.cohort_id):
            mutation_dict[key] = mutation_dict.get(key, []) + list(collection)

        return(mutation_dict)
