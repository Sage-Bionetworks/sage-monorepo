from sqlalchemy import and_, orm
from api import db
from api.db_models import Gene, Pathway
from .general_resolvers import build_option_args, get_selection_set


def build_pathway_core_request(selection_set, name=None):
    """
    Builds a SQL request with just core pathway fields.
    """
    sess = db.session

    pathway_1 = orm.aliased(Pathway, name='p')

    core_field_mapping = {'name': pathway_1.name.label('name')}

    core = build_option_args(selection_set, core_field_mapping)

    query = sess.query(*core)

    if name:
        query = query.filter(pathway_1.name.in_(name))

    return query


def build_pathway_request(_obj, info, name=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)

    gene_1 = orm.aliased(Gene, name='g')
    pathway_1 = orm.aliased(Pathway, name='pw')

    related_field_mapping = {'genes': 'genes'}

    relations = build_option_args(selection_set, related_field_mapping)
    option_args = []

    query = sess.query(pathway_1)

    if name:
        query = query.filter(pathway_1.name.in_(name))

    if 'genes' in relations:
        query = query.join((gene_1, pathway_1.genes), isouter=True)
        option_args.append(orm.contains_eager(
            pathway_1.genes.of_type(gene_1)))

    if option_args:
        return query.options(*option_args)

    return build_pathway_core_request(selection_set, name)


def request_pathways(_obj, info, name=None):
    query = build_pathway_request(_obj, info, name=name)
    return query.distinct().all()
