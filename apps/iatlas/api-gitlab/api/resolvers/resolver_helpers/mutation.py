from sqlalchemy import and_
from sqlalchemy.orm import aliased
from itertools import groupby
from api import db
from api.db_models import Gene, Mutation, MutationCode, MutationType, Patient, Sample, SampleToMutation, Cohort, CohortToMutation, CohortToSample
from .general_resolvers import build_join_condition, get_selected, get_value
from .paging_utils import get_pagination_queries

mutation_request_fields = {
    'id',
    'gene',
    'name',
    'mutationCode',
    'mutationType',
    'samples'
}


def get_mutation_column_labels(requested, mutation, mutation_code, add_id=False):
    mapping = {
        'name': mutation.name.label('mutation_name'),
        'mutationCode': mutation_code.code.label('mutation_code')
    }
    labels = get_selected(requested, mapping)

    if add_id:
        labels |= {mutation.id.label('mutation_id')}

    return(labels)


def get_mutation_type_column_labels(requested, mutation_type):
    mapping = {
        'display': mutation_type.display.label('display'),
        'name': mutation_type.name.label('name')
    }
    labels = get_selected(requested, mapping)
    return(labels)


def build_mutation_graphql_response(requested=[], sample_requested=[], status=None, sample=None, cohort=None, prefix='mutation_'):
    from .gene import build_gene_graphql_response
    from .mutation_type import build_mutation_type_graphql_response
    from .sample import build_sample_graphql_response

    def f(mutation):
        if not mutation:
            return None
        mutation_id = get_value(mutation, prefix + 'id')
        samples = get_samples(mutation_id=mutation_id, requested=requested,
                              sample_requested=sample_requested, status=status, sample=sample, cohort=cohort)
        return {
            'id': mutation_id,
            'name': get_value(mutation, prefix + 'name'),
            'mutationCode': get_value(mutation, prefix + 'code'),
            'status': get_value(mutation, prefix + 'status'),
            'gene': build_gene_graphql_response()(mutation),
            'mutationType': build_mutation_type_graphql_response(mutation),
            'samples': map(build_sample_graphql_response(), samples)
        }
    return f


def build_mutation_request(requested, gene_requested, mutation_type_requested, distinct=False, paging=None, cohort=None, entrez=None, mutation=None, mutation_code=None, mutation_type=None, sample=None):
    '''
    Builds a SQL request

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request
        2nd position - a set of the requested fields in the 'gene' node of the graphql request. If 'gene' is not requested, this will be an empty set.
        3rd position - a set of the requested fields in the 'mutationType' node of the graphql request. If 'mutationType' is not requested, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `distinct` - a boolean, indicates whether duplicate records should be filtered out
        `paging` - a dict containing pagination metadata
        `cohort` - a list of strings, cohort names
        `entrez` - a list of integers, gene entrez ids
        `mutation` - a list of strings, mutation names
        `mutation_code` - a list of strings, mutation codes
        `mutation_type` - a list of strings, mutation type names
        `sample` - a list of strings, sample names
    '''
    from .gene import get_simple_gene_column_labels
    sess = db.session

    gene_1 = aliased(Gene, name='g')
    mutation_1 = aliased(Mutation, name='m')
    mutation_code_1 = aliased(MutationCode, name='mc')
    mutation_type_1 = aliased(MutationType, name='mt')
    sample_1 = aliased(Sample, name='s')
    sample_to_mutation_1 = aliased(SampleToMutation, name='sm')
    cohort_1 = aliased(Cohort, name='c')
    cohort_to_mutation_1 = aliased(CohortToMutation, name='ctm')

    mutation_core = get_mutation_column_labels(
        requested, mutation_1, mutation_code_1, add_id=True)

    gene_core = get_simple_gene_column_labels(gene_requested, gene_1)

    mutation_type_core = get_mutation_type_column_labels(
        mutation_type_requested, mutation_type_1)

    query = sess.query(*[*mutation_core, *gene_core, *mutation_type_core])
    query = query.select_from(mutation_1)

    if mutation:
        query = query.filter(mutation_1.name.in_(mutation))

    query = build_simple_mutation_request(query, requested, mutation_1, gene_1, mutation_code_1,
                                          mutation_type_1, entrez=entrez, mutation_code=mutation_code, mutation_type=mutation_type)

    if sample:
        sample_subquery = sess.query(
            sample_to_mutation_1.mutation_id)

        sample_join_condition = build_join_condition(
            sample_to_mutation_1.sample_id, sample_1.id, filter_column=sample_1.name, filter_list=sample)

        sample_subquery = sample_subquery.join(sample_1, and_(
            *sample_join_condition), isouter=False)

        query = query.filter(mutation_1.id.in_(sample_subquery))

    if cohort:
        cohort_subquery = sess.query(cohort_to_mutation_1.mutation_id)

        cohort_join_condition = build_join_condition(
            cohort_to_mutation_1.cohort_id, cohort_1.id, filter_column=cohort_1.name, filter_list=cohort)

        cohort_subquery = cohort_subquery.join(cohort_1, and_(
            *cohort_join_condition), isouter=False)

        query = query.filter(mutation_1.id.in_(cohort_subquery))

    return get_pagination_queries(query, paging, distinct, cursor_field=mutation_1.id)


def build_simple_mutation_request(query, requested, mutation_obj, gene_obj, mutation_code_obj, mutation_type_obj, entrez=None, mutation_code=None, mutation_type=None):
    '''
    Builds a SQL request

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request
        2nd position - a set of the requested fields in the 'gene' node of the graphql request. If 'gene' is not requested, this will be an empty set.
        3rd position - a set of the requested fields in the 'mutationType' node of the graphql request. If 'mutationType' is not requested, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `distinct` - a boolean, indicates whether duplicate records should be filtered out
        `paging` - a dict containing pagination metadata
        `cohort` - a list of strings, cohort names
        `entrez` - a list of integers, gene entrez ids
        `mutation` - a list of strings, mutation names
        `mutation_code` - a list of strings, mutation codes
        `mutation_type` - a list of strings, mutation type names
        `sample` - a list of strings, sample names
    '''

    if 'gene' in requested or entrez:
        is_outer = not bool(entrez)
        gene_join_condition = build_join_condition(
            gene_obj.id, mutation_obj.gene_id, filter_column=gene_obj.entrez, filter_list=entrez)
        query = query.join(gene_obj, and_(
            *gene_join_condition), isouter=is_outer)

    if 'mutationCode' in requested or mutation_code:
        is_outer = not bool(mutation_code)
        mutation_code_join_condition = build_join_condition(
            mutation_code_obj.id, mutation_obj.mutation_code_id, filter_column=mutation_code_obj.code, filter_list=mutation_code)
        query = query.join(mutation_code_obj, and_(
            *mutation_code_join_condition), isouter=is_outer)

    if 'mutationType' in requested or mutation_type:
        is_outer = not bool(mutation_type)
        mutation_type_join_condition = build_join_condition(
            mutation_type_obj.id, mutation_obj.mutation_type_id, filter_column=mutation_type_obj.name, filter_list=mutation_type)
        query = query.join(mutation_type_obj, and_(
            *mutation_type_join_condition), isouter=is_outer)

    return(query)


def get_samples(mutation_id, requested, sample_requested, status=None, sample=None, cohort=None):

    if 'samples' not in requested:
        return []

    sess = db.session

    sample_1 = aliased(Sample, name='s')
    sample_to_mutation_1 = aliased(SampleToMutation, name='stm')
    cohort_1 = aliased(Cohort, name='c')
    cohort_to_sample_1 = aliased(CohortToSample, name='cts')

    core_field_mapping = {
        'name': sample_1.name.label('sample_name'),
        'status': sample_to_mutation_1.status.label('sample_mutation_status')
    }

    core = get_selected(sample_requested, core_field_mapping)

    query = sess.query(*core)
    query = query.select_from(sample_to_mutation_1)
    query = query.filter(sample_to_mutation_1.mutation_id == mutation_id)

    if status:
        query = query.filter(sample_to_mutation_1.status.in_(status))

    sample_join_condition = build_join_condition(
        sample_to_mutation_1.sample_id, sample_1.id, filter_column=sample_1.name, filter_list=sample)

    query = query.join(
        sample_1, and_(*sample_join_condition))

    if cohort:
        cohort_subquery = sess.query(cohort_to_sample_1.sample_id)

        cohort_join_condition = build_join_condition(
            cohort_to_sample_1.cohort_id, cohort_1.id, filter_column=cohort_1.name, filter_list=cohort)

        cohort_subquery = cohort_subquery.join(cohort_1, and_(
            *cohort_join_condition), isouter=False)

        query = query.filter(
            sample_to_mutation_1.sample_id.in_(cohort_subquery))

        import logging
        logger = logging.getLogger('test mutation')
        logger.info(cohort_subquery)

    return query.all()
