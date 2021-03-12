from sqlalchemy import and_, orm
from api import db
from api.db_models import Gene, GeneFamily
from .general_resolvers import build_option_args, get_selection_set


def build_gene_family_core_request(selection_set, name=None):
    """
    Builds a SQL request with just core gene family fields.
    """
    sess = db.session

    gene_family_1 = orm.aliased(GeneFamily, name='g')

    core_field_mapping = {'name': gene_family_1.name.label('name')}

    core = build_option_args(selection_set, core_field_mapping)

    query = sess.query(*core)

    if name:
        query = query.filter(gene_family_1.name.in_(name))

    return query


def build_gene_family_request(_obj, info, name=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(info=info)

    gene_1 = orm.aliased(Gene, name='g')
    gene_family_1 = orm.aliased(GeneFamily, name='m')

    related_field_mapping = {'genes': 'genes'}

    relations = build_option_args(selection_set, related_field_mapping)
    option_args = []

    query = sess.query(gene_family_1)

    if name:
        query = query.filter(gene_family_1.name.in_(name))

    if 'genes' in relations:
        query = query.join((gene_1, gene_family_1.genes), isouter=True)
        option_args.append(orm.contains_eager(
            gene_family_1.genes.of_type(gene_1)))

    if option_args:
        return query.options(*option_args)

    return build_gene_family_core_request(selection_set, name)


def request_gene_families(_obj, info, name=None):
    query = build_gene_family_request(_obj, info, name=name)
    return query.distinct().all()
