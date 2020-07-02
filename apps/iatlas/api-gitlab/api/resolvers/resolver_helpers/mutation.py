from sqlalchemy import and_, orm
from api import db
from api.database import return_gene_query
from api.db_models import Gene, Mutation, MutationCode, MutationType, Sample
from .general_resolvers import build_option_args, get_selection_set
from .tag import request_tags


def build_mutation_request(_obj, info, entrez=None, mutation_code=None, mutation_type=None):
    """
    Builds a SQL request and returns values from the DB.
    """
    sess = db.session

    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)

    gene_1 = orm.aliased(Gene, name='g')
    mutation_1 = orm.aliased(Mutation, name='m')
    mutation_code_1 = orm.aliased(MutationCode, name='mc')
    mutation_type_1 = orm.aliased(MutationType, name='mt')
    sample_1 = orm.aliased(Sample, name='s')

    core_field_mapping = {'id': mutation_1.id.label('id')}

    related_field_mapping = {'gene': 'gene',
                             'mutationCode': 'mutation_code',
                             'mutationType': 'mutation_type'}

    core = build_option_args(selection_set, core_field_mapping)
    relations = build_option_args(selection_set, related_field_mapping)
    option_args = []

    query = sess.query(mutation_1)

    if 'gene' in relations or entrez:
        query = query.join((gene_1, mutation_1.gene), isouter=True)
        option_args.append(orm.contains_eager(mutation_1.gene.of_type(gene_1)))

    if 'mutation_code' in relations or mutation_code:
        query = query.join(
            (mutation_code_1, mutation_1.mutation_code), isouter=True)
        option_args.append(orm.contains_eager(
            mutation_1.mutation_code.of_type(mutation_code_1)))

    if 'mutation_type' in relations or mutation_type:
        query = query.join(
            (mutation_type_1, mutation_1.mutation_type), isouter=True)
        option_args.append(orm.contains_eager(
            mutation_1.mutation_type.of_type(mutation_type_1)))

    if 'samples' in relations:
        query = query.join((sample_1, mutation_1.samples), isouter=True)
        option_args.append(orm.contains_eager(
            mutation_1.samples.of_type(sample_1)))

    if option_args:
        query = query.options(*option_args)

    if entrez:
        query = query.filter(gene_1.entrez.in_(entrez))

    if mutation_code:
        query = query.filter(mutation_code_1.code.in_(mutation_code))

    if mutation_type:
        query = query.filter(mutation_type_1.name.in_(mutation_type))

    return query


def request_mutations(_obj, info, entrez=None, mutation_code=None, mutation_type=None):
    query = build_mutation_request(
        _obj, info, entrez=entrez, mutation_code=mutation_code, mutation_type=mutation_type)
    query = query.distinct()
    return query.all()
