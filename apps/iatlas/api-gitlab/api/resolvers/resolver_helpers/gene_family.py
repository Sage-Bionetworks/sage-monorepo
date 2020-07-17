from sqlalchemy import orm
from api import db
from api.db_models import (GeneFamily, Gene)
from .general_resolvers import build_option_args, get_selection_set


def build_gene_family_request(_obj, info, name=None):
    """
    Builds a SQL request and returns values from the DB.
    """
    sess = db.session

    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, True, child_node='gene_families')

    gene_family_1 = orm.aliased(GeneFamily, name='gf')
    gene_1 = orm.aliased(Gene, name='g')

    core_field_mapping = {'name': gene_family_1.name.label('name')}

    related_field_mapping = {'gene': 'gene'}

    core = build_option_args(selection_set, core_field_mapping)
    relations = build_option_args(selection_set, related_field_mapping)
    option_args = []
    entity_args = []

    query = sess.query(gene_family_1)

    if 'gene' in relations:
        query = query.join(
            (gene_family_1.gene, gene_1), isouter=True)
        option_args.append(orm.contains_eager(
            gene_family_1.gene.of_type(gene_1)))

    if option_args:
        query = query.options(*option_args)
    else:
        query = sess.query(*core)

    if name:
        query = query.filter(gene_family_1.name.in_(name))

    return query


def request_gene_family(_obj, info, name=None):
    query = build_gene_family_request(
        _obj, info, name=name)
    query = query.distinct()
    return query.all()
