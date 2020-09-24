from sqlalchemy import and_
from sqlalchemy.orm import aliased
from itertools import groupby
from api import db
from api.db_models import Gene, Mutation, MutationCode, MutationType, Patient, Sample, SampleToMutation
from .general_resolvers import build_join_condition, get_selected, get_selection_set, get_value
from .gene import build_gene_graphql_response
from .mutation_type import build_mutation_type_graphql_response
from .sample import build_sample_graphql_response
import logging

log = logging.getLogger('mutation resolver helper')
log.setLevel(logging.DEBUG)

mutation_by_sample_request_fields = {'name', 'mutations'}

mutation_request_fields = {'id',
                           'gene',
                           'mutationCode',
                           'mutationType',
                           'samples',
                           'status'}


def build_mutation_graphql_response(sample_dict=dict()):
    def f(mutation):
        if not mutation:
            return None
        mutation_id = get_value(mutation, 'id')
        samples = sample_dict.get(mutation_id, []) if sample_dict else []
        return {
            'id': get_value(mutation, 'id'),
            'gene': build_gene_graphql_response()(mutation),
            'mutationCode': get_value(mutation, 'code'),
            'mutationType': build_mutation_type_graphql_response(mutation),
            'samples': map(build_sample_graphql_response, samples),
            'status': get_value(mutation, 'status')
        }
    return f


def build_mutation_by_sample_graphql_response(mutation_set):
    mutations = mutation_set[1] or []
    return {
        'name': get_value(mutations[0], 'sample_name'),
        'mutations': map(build_mutation_graphql_response(), mutations)
    }


def build_mutation_request(requested, gene_requested, mutation_type_requested, sample_requested, data_set=None, entrez=None, feature=None, feature_class=None, mutation_code=None, mutation_id=None, mutation_type=None, related=None, sample=None, status=None, tag=None, by_sample=False):
    """
    Builds a SQL request
    """
    sess = db.session

    gene_1 = aliased(Gene, name='g')
    mutation_1 = aliased(Mutation, name='m')
    mutation_code_1 = aliased(MutationCode, name='mc')
    mutation_type_1 = aliased(MutationType, name='mt')
    sample_1 = aliased(Sample, name='s')
    sample_to_mutation_1 = aliased(SampleToMutation, name='sm')

    core_field_mapping = {
        'status': sample_to_mutation_1.status.label('status')}
    gene_core_field_mapping = {'entrez': gene_1.entrez.label('entrez'),
                               'hgnc': gene_1.hgnc.label('hgnc'),
                               'description': gene_1.description.label('description'),
                               'friendlyName': gene_1.friendly_name.label('friendly_name'),
                               'ioLandscapeName': gene_1.io_landscape_name.label('io_landscape_name')}
    mutation_type_field_mapping = {'display': mutation_type_1.display.label('display'),
                                   'name': mutation_type_1.name.label('name')}
    sample_core_field_mapping = {'name': sample_1.name.label('sample_name')}

    core = get_selected(requested, core_field_mapping)
    core |= {mutation_1.id.label('id')}
    gene_core = get_selected(gene_requested, gene_core_field_mapping)
    mutation_type_core = get_selected(
        mutation_type_requested, mutation_type_field_mapping)
    sample_core = get_selected(sample_requested, sample_core_field_mapping)

    if 'mutationCode' in requested:
        core |= {mutation_code_1.code.label('code')}

    if by_sample:
        sample_core |= {sample_1.id.label('sample_id')}

    query = sess.query(*[*core, *gene_core, *mutation_type_core, *sample_core])
    query = query.select_from(mutation_1)

    if mutation_id:
        query = query.filter(mutation_1.id.in_(mutation_id))

    if 'gene' in requested or entrez:
        is_outer = not bool(entrez)
        gene_join_condition = build_join_condition(
            gene_1.id, mutation_1.gene_id, filter_column=gene_1.entrez, filter_list=entrez)
        query = query.join(gene_1, and_(
            *gene_join_condition), isouter=is_outer)

    if 'mutationCode' in requested or mutation_code:
        is_outer = not bool(mutation_code)
        mutation_code_join_condition = build_join_condition(
            mutation_code_1.id, mutation_1.mutation_code_id, filter_column=mutation_code_1.code, filter_list=mutation_code)
        query = query.join(mutation_code_1, and_(
            *mutation_code_join_condition), isouter=is_outer)

    if 'mutationType' in requested or mutation_type:
        is_outer = not bool(mutation_type)
        mutation_type_join_condition = build_join_condition(
            mutation_type_1.id, mutation_1.mutation_type_id, filter_column=mutation_type_1.name, filter_list=mutation_type)
        query = query.join(mutation_type_1, and_(
            *mutation_type_join_condition), isouter=is_outer)

    if by_sample or status or sample:
        sample_mutation_join_condition = build_join_condition(
            sample_to_mutation_1.mutation_id, mutation_1.id, filter_column=sample_to_mutation_1.status, filter_list=status)

        query = query.join(sample_to_mutation_1, and_(
            *sample_mutation_join_condition))

        if by_sample or sample:
            sample_join_condition = build_join_condition(
                sample_to_mutation_1.sample_id, sample_1.id, filter_column=sample_1.name, filter_list=sample)
            query = query.join(sample_1, and_(*sample_join_condition))

    order = []
    append_to_order = order.append
    if by_sample and not 'name' in sample_requested:
        append_to_order(sample_1.id)
    if by_sample and 'name' in sample_requested:
        append_to_order(sample_1.name)
    if 'status' in requested:
        append_to_order(sample_to_mutation_1.status)
    if 'id' in requested:
        append_to_order(mutation_1.id)
    if 'mutationCode' in requested:
        append_to_order(mutation_code_1.code)
    if not order:
        append_to_order(mutation_1.id)
    query = query.order_by(*order)

    return query


def get_samples(requested, patient_requested, sample_requested, entrez=None, mutation_code=None, mutation_id=None, mutation_type=None, mutation_ids=set(), sample=None, status=None):
    has_samples = 'samples' in requested

    if mutation_ids and has_samples:
        sess = db.session

        mutation_1 = aliased(Mutation, name='m')
        patient_1 = aliased(Patient, name='p')
        sample_1 = aliased(Sample, name='s')
        sample_to_mutation_1 = aliased(SampleToMutation, name='sm')

        core_field_mapping = {'name': sample_1.name.label('name'),
                              'status': sample_to_mutation_1.status.label('status')}
        patient_field_mapping = {'ageAtDiagnosis': patient_1.age_at_diagnosis.label('age_at_diagnosis'),
                                 'barcode': patient_1.barcode.label('barcode'),
                                 'ethnicity': patient_1.ethnicity.label('ethnicity'),
                                 'gender': patient_1.gender.label('gender'),
                                 'height': patient_1.height.label('height'),
                                 'race': patient_1.race.label('race'),
                                 'weight': patient_1.weight.label('weight')}
        core = get_selected(sample_requested, core_field_mapping)
        # Always select the sample id and the mutation id.
        core |= {sample_to_mutation_1.sample_id.label('id'),
                 mutation_1.id.label('mutation_id')}
        patient_core = get_selected(patient_requested, patient_field_mapping)

        sample_query = sess.query(*[*core, *patient_core])
        sample_query = sample_query.select_from(mutation_1)

        if mutation_id:
            sample_query = sample_query.filter(mutation_1.id.in_(mutation_id))

        if entrez or mutation_code or mutation_type:
            gene_1 = aliased(Gene, name='g')
            mutation_1 = aliased(Mutation, name='m')
            mutation_code_1 = aliased(MutationCode, name='mc')
            mutation_type_1 = aliased(MutationType, name='mt')

            if entrez:
                gene_join_condition = build_join_condition(
                    gene_1.id, mutation_1.gene_id, filter_column=gene_1.entrez, filter_list=entrez)
                sample_query = sample_query.join(
                    gene_1, and_(*gene_join_condition))

            if mutation_code:
                mutation_code_join_condition = build_join_condition(
                    mutation_code_1.id, mutation_1.mutation_code_id, filter_column=mutation_code_1.code, filter_list=mutation_code)
                sample_query = sample_query.join(
                    mutation_code_1, and_(*mutation_code_join_condition))

            if mutation_type:
                mutation_type_join_condition = build_join_condition(
                    mutation_type_1.id, mutation_1.mutation_type_id, filter_column=mutation_type_1.name, filter_list=mutation_type)
                sample_query = sample_query.join(
                    mutation_type_1, and_(*mutation_type_join_condition))

        sample_mutation_join_condition = build_join_condition(
            sample_to_mutation_1.mutation_id, mutation_1.id, filter_column=sample_to_mutation_1.status, filter_list=status)

        sample_query = sample_query.join(
            sample_to_mutation_1, and_(*sample_mutation_join_condition))

        sample_join_condition = build_join_condition(
            sample_1.id, sample_to_mutation_1.sample_id, filter_column=sample_1.name, filter_list=sample)

        sample_query = sample_query.join(
            sample_1, and_(*sample_join_condition))

        if 'patient' in sample_requested:
            sample_query = sample_query.join(
                patient_1, sample_1.patient_id == patient.id)

        order = []
        append_to_order = order.append
        if 'name' in sample_requested:
            append_to_order(sample_1.name)
        if not order:
            append_to_order(sample_1.id)
        sample_query = sample_query.order_by(*order)

        return sample_query.all()

    return []


def request_mutations(requested, gene_requested, mutation_type_requested, data_set=None, entrez=None, feature=None, feature_class=None, mutation_code=None, mutation_id=None, mutation_type=None, related=None, sample=None, status=None, tag=None):
    query = build_mutation_request(
        requested, gene_requested, mutation_type_requested, set(), data_set=dataSet, entrez=entrez, feature=feature, feature_class=featureClass, mutation_code=mutationCode, mutation_id=mutation_id, mutation_type=mutationType, related=related, sample=sample, status=status, tag=tag)
    return query.distinct().all()


def return_mutation_derived_fields(requested, patient_requested, sample_requested, mutation_ids=set(), data_set=None, entrez=None, feature=None, feature_class=None, mutation_code=None, mutation_id=None, mutation_type=None, related=None, sample=None, status=None, tag=None):
    samples = get_samples(
        requested, patient_requested, sample_requested, mutation_ids=mutation_ids, data_set=dataSet,
        entrez=entrez, feature=feature, feature_class=featureClass, mutation_code=mutationCode, mutation_id=mutation_id, mutation_type=mutationType, related=related, sample=sample, status=status, tag=tag)

    sample_dict = dict()
    for key, collection in groupby(samples, key=lambda s: s.mutation_id):
        sample_dict[key] = sample_dict.get(key, []) + list(collection)

    return sample_dict
