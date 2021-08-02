from logging import Logger
from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Cohort, Dataset, Tag, Sample, Feature, Gene, Mutation, MutationCode, CohortToSample, CohortToFeature, CohortToGene, CohortToMutation
from .general_resolvers import build_join_condition, get_selected, get_value
from .paging_utils import get_pagination_queries

cohort_request_fields = {'id', 'name',
                         'dataSet', 'tag', 'samples', 'features', 'genes', 'mutations'}


def build_cohort_graphql_response(requested=[], sample_requested=[], sample_tag_requested=[], feature_requested=[], gene_requested=[], mutation_requested=[], mutation_gene_requested=[]):
    from .data_set import build_data_set_graphql_response
    from .feature import build_feature_graphql_response
    from .gene import build_gene_graphql_response
    from .mutation import build_mutation_graphql_response
    from .sample import build_sample_graphql_response
    from .tag import build_tag_graphql_response

    def f(cohort):
        if not cohort:
            return None
        else:
            cohort_id = get_value(cohort, 'cohort_id')
            samples = get_samples(cohort_id, requested,
                                  sample_requested, sample_tag_requested)
            features = get_features(cohort_id, requested, feature_requested)
            genes = get_genes(cohort_id, requested, gene_requested)
            mutations = get_mutations(
                cohort_id, requested, mutation_requested, mutation_gene_requested)
            dict = {
                'id': cohort_id,
                'name': get_value(cohort, 'cohort_name'),
                'dataSet': build_data_set_graphql_response()(cohort),
                'tag': build_tag_graphql_response()(cohort),
                'samples': map(build_sample_graphql_response(), samples),
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


def get_samples(id, requested, sample_requested, tag_requested):
    if 'samples' not in requested:
        return([])
    else:
        sess = db.session

        cohort_to_sample_1 = aliased(CohortToSample, name='cts')
        sample_1 = aliased(Sample, name='s')
        tag_1 = aliased(Tag, name='t2')

        sample_core_field_mapping = {
            'name': sample_1.name.label('sample_name'),
        }

        tag_core_field_mapping = {
            'characteristics': tag_1.characteristics.label('tag_characteristics'),
            'color': tag_1.color.label('tag_color'),
            'longDisplay': tag_1.long_display.label('tag_long_display'),
            'name': tag_1.name.label('tag_name'),
            'shortDisplay': tag_1.short_display.label('tag_short_display')
        }

        core = get_selected(sample_requested, sample_core_field_mapping)
        core |= get_selected(tag_requested, tag_core_field_mapping)

        query = sess.query(*core)
        query = query.select_from(cohort_to_sample_1)
        query = query.filter(cohort_to_sample_1.cohort_id == id)

        sample_join_condition = build_join_condition(
            cohort_to_sample_1.sample_id, sample_1.id)

        query = query.join(sample_1, and_(
            *sample_join_condition), isouter=False)

        if 'tag' in sample_requested:
            sample_tag_join_condition = build_join_condition(
                tag_1.id, cohort_to_sample_1.tag_id)
            query = query.join(tag_1, and_(
                *sample_tag_join_condition), isouter=True)

        samples = query.all()
        return(samples)


def get_features(id, requested, feature_requested):
    if 'features' not in requested:
        return([])
    else:
        sess = db.session

        cohort_to_feature_1 = aliased(CohortToFeature, name='ctf')
        feature_1 = aliased(Feature, name='f')

        feature_core_field_mapping = {
            'display': feature_1.display.label('feature_display'),
            'name': feature_1.name.label('feature_name'),
        }

        core = get_selected(feature_requested, feature_core_field_mapping)

        query = sess.query(*core)
        query = query.select_from(cohort_to_feature_1)
        query = query.filter(cohort_to_feature_1.cohort_id == id)

        feature_join_condition = build_join_condition(
            cohort_to_feature_1.feature_id, feature_1.id)

        query = query.join(feature_1, and_(
            *feature_join_condition), isouter=False)

        features = query.all()
        return(features)


def get_genes(id, requested, gene_requested):
    if 'genes' not in requested:
        return([])
    else:
        sess = db.session

        cohort_to_gene_1 = aliased(CohortToGene, name='ctg')
        gene_1 = aliased(Gene, name='g')

        gene_core_field_mapping = {
            'hgnc': gene_1.hgnc.label('gene_hgnc'),
            'entrez': gene_1.entrez.label('gene_entrez'),
        }

        core = get_selected(gene_requested, gene_core_field_mapping)

        query = sess.query(*core)
        query = query.select_from(cohort_to_gene_1)
        query = query.filter(cohort_to_gene_1.cohort_id == id)

        gene_join_condition = build_join_condition(
            cohort_to_gene_1.gene_id, gene_1.id)

        query = query.join(gene_1, and_(
            *gene_join_condition), isouter=False)

        genes = query.all()
        return(genes)


def get_mutations(id, requested, mutation_requested, mutation_gene_requested):

    if 'mutations' not in requested:
        return([])
    else:
        sess = db.session

        cohort_to_mutation_1 = aliased(CohortToMutation, name='ctm')
        mutation_1 = aliased(Mutation, name='m')
        gene_1 = aliased(Gene, name='g')
        mutation_code_1 = aliased(MutationCode, name='mc')

        mutation_core_field_mapping = {
            'mutationCode': mutation_code_1.code.label('mutation_code')
        }

        mutation_gene_core_field_mapping = {
            'hgnc': gene_1.hgnc.label('gene_hgnc'),
            'entrez': gene_1.entrez.label('gene_entrez'),
        }

        core = get_selected(mutation_requested, mutation_core_field_mapping)
        core |= get_selected(mutation_gene_requested,
                             mutation_gene_core_field_mapping)

        query = sess.query(*core)
        query = query.select_from(cohort_to_mutation_1)
        query = query.filter(cohort_to_mutation_1.cohort_id == id)

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
        return(mutations)
